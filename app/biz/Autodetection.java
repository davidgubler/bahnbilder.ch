package biz;

import entities.Operator;
import entities.Photo;
import entities.VehicleClass;
import entities.tmp.AutodetectionStatus;
import entities.tmp.Confidence;
import utils.Config;
import utils.Context;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Autodetection {

    private static final Pattern CONTAINS_LETTERS = Pattern.compile("[a-zA-Z]+");

    private List<Confidence<Operator>> extractOperatorsFromLocation(Context context, Photo photo) {
        Map<Integer, Double> operatorCount = new HashMap<>();
        for (int operatorId : context.getPhotosModel().getOperatorIdsByCountryAndCoordinates(photo, Config.PHOTO_SPOT_RADIUS_KM * 10.0)) {
            operatorCount.put(operatorId, operatorCount.containsKey(operatorId) ? (operatorCount.get(operatorId)) + 1 : 1);
        }
        List<Confidence<Operator>> operators = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : operatorCount.entrySet()) {
            operators.add(new Confidence<>(context.getOperatorsModel().get(e.getKey()), e.getValue()));
        }
        return normalizeEntityConfidence(operators);
    }

    private List<Confidence<Operator>> extractOperatorsFromTexts(Context context, Photo photo) {
        Map<Integer, Double> operatorMatches = new HashMap<>();
        for (String text : photo.getTexts()) {
            Map<Integer, Long> om = context.getPhotosModel().getOperatorMatchesByText(photo, text);
            if (om.isEmpty()) {
                continue;
            }

            double weight = 1.0 / om.keySet().size(); // weight of the result is lower for less specific matches
            weight *= Math.pow(2, text.length());     // longer words are more relevant (very short strings are more likely to match randomly)
            for (int operatorId : om.keySet()) {
                double points = om.get(operatorId) * weight;
                operatorMatches.put(operatorId, operatorMatches.containsKey(operatorId) ? (operatorMatches.get(operatorId) + points) : points);
            }
        }

        List<Confidence<Operator>> operators = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : operatorMatches.entrySet()) {
            operators.add(new Confidence<>(context.getOperatorsModel().get(e.getKey()), e.getValue()));
        }
        return normalizeEntityConfidence(operators);
    }

    private List<Confidence<Operator>> extractOperatorsFromAbbreviations(Context context, Photo photo) {
        Map<Integer, Double> operatorCount = new HashMap<>();
        for (String text : photo.getTexts()) {
            if (!CONTAINS_LETTERS.matcher(text).find()) {
                continue;
            }
            // try the way it's written...
            for (Operator operator : context.getOperatorsModel().getByAbbr(text).toList()) {
                int count = (int)context.getPhotosModel().countPhotosByOperator(photo, operator.getId());
                if (count > 0) {
                    operatorCount.put(operator.getId(), operatorCount.containsKey(operator.getId()) ? (operatorCount.get(operator.getId()) + count) : count);
                }
            }
            // and try uppercase
            for (Operator operator : context.getOperatorsModel().getByAbbr(text.toUpperCase(Locale.ROOT)).toList()) {
                int count = (int)context.getPhotosModel().countPhotosByOperator(photo, operator.getId());
                if (count > 0) {
                    operatorCount.put(operator.getId(), operatorCount.containsKey(operator.getId()) ? (operatorCount.get(operator.getId()) + count) : count);
                }
            }
        }
        List<Confidence<Operator>> operators = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : operatorCount.entrySet()) {
            operators.add(new Confidence<>(context.getOperatorsModel().get(e.getKey()), e.getValue()));
        }
        return normalizeEntityConfidence(operators);
    }

    private List<Confidence<Operator>> getOperators(
            List<Confidence<Operator>> operatorsFromLocation,
            List<Confidence<Operator>> operatorsFromTexts,
            List<Confidence<Operator>> operatorsFromAbbreviations) {

        Set<Operator> candidates = new HashSet<>();

        candidates.addAll(operatorsFromLocation.stream().map(Confidence::getEntity).collect(Collectors.toUnmodifiableList()));
        candidates.addAll(operatorsFromTexts.stream().map(Confidence::getEntity).collect(Collectors.toUnmodifiableList()));
        candidates.addAll(operatorsFromAbbreviations.stream().map(Confidence::getEntity).collect(Collectors.toUnmodifiableList()));

        List<Confidence<Operator>> operatorConfidence = new ArrayList<>();
        for (Operator operator : candidates) {
            double locationConfidence = operatorsFromLocation.stream().filter(ec -> ec.getEntity().equals(operator)).map(Confidence::getConfidence).findFirst().orElse(0.0d);
            double textConfidence = operatorsFromTexts.stream().filter(ec -> ec.getEntity().equals(operator)).map(Confidence::getConfidence).findFirst().orElse(0.0d);
            double abbrConfidence = operatorsFromAbbreviations.stream().filter(ec -> ec.getEntity().equals(operator)).map(Confidence::getConfidence).findFirst().orElse(0.0d);
            operatorConfidence.add(new Confidence<>(operator, (locationConfidence + 0.01d) * (textConfidence + 0.01d) * (abbrConfidence + 0.01d)));
        }

        operatorConfidence = normalizeEntityConfidence(operatorConfidence).stream().filter(ec -> ec.getConfidence() >= 0.05).collect(Collectors.toUnmodifiableList());
        return normalizeEntityConfidence(operatorConfidence);
    }

    private static <T> List<Confidence<T>> normalizeEntityConfidence(Collection<Confidence<T>> entityConfidences) {
        entityConfidences = entityConfidences.stream().sorted().limit(10).collect(Collectors.toUnmodifiableList());
        double totalConfidence = entityConfidences.stream().mapToDouble(Confidence::getConfidence).sum();
        return entityConfidences.stream().map(e -> new Confidence<>(e.getEntity(), e.getConfidence() / totalConfidence)).collect(Collectors.toUnmodifiableList());
    }

    private static final Pattern UIC_FULL = Pattern.compile("^([0-9]{3,4})[^0-9]?([0-9]{3})(-[0-9])?$");
    private static final Pattern UIC_CLASS = Pattern.compile("^([0-9]{3,4})$");
    private static final Pattern UIC_VEHICLENR = Pattern.compile("^([0-9]{3})(-[0-9])?$");
    private static final Pattern CUSTOM_VEHICLENR = Pattern.compile("^([0-9]{4,})?$");

    private Set<UicNr> extractUicNrsFromTexts(List<String> texts) {
        Set<UicNr> uicNrs = new HashSet<>();
        if (texts == null) {
            return uicNrs;
        }
        for (int i = 0; i < texts.size() - 1; i++) {
            Matcher cm = UIC_CLASS.matcher(texts.get(i));
            Matcher nm = UIC_VEHICLENR.matcher(texts.get(i+1));
            if (cm.matches() && nm.matches()) {
                uicNrs.add(new UicNr(cm.group(1), nm.group(1)));
            }
        }
        for (String text : texts) {
            Matcher fm = UIC_FULL.matcher(text);
            if (fm.matches()) {
                uicNrs.add(new UicNr(fm.group(1), fm.group(2)));
            }
        }
        return uicNrs;
    }

    private Set<Integer> extractVehicleNrsFromTexts(List<String> texts) {
        Set<Integer> vehicleNrs = new HashSet<>();
        if (texts == null) {
            return vehicleNrs;
        }
        for (String text : texts) {
            Matcher nm = UIC_VEHICLENR.matcher(text);
            if (nm.matches()) {
                try {
                    vehicleNrs.add(Integer.parseInt(nm.group(1)));
                } catch (Exception e) {
                    // shouldn't happen
                }
            }
            nm = CUSTOM_VEHICLENR.matcher(text);
            if (nm.matches()) {
                try {
                    vehicleNrs.add(Integer.parseInt(nm.group(1)));
                } catch (Exception e) {
                    // shouldn't happen
                }
            }
        }
        return vehicleNrs;
    }

    private boolean containsWord(String text, String word) {
        return Arrays.stream(text.split(" ")).map(String::trim).collect(Collectors.toSet()).contains(word);
    }

    private double nrConfidence(Context context, Operator operator, VehicleClass vehicleClass, int nr) {
        List<Integer> nrs = context.getPhotosModel().getNrsByOperatorAndVehicleClassId(operator.getId(), vehicleClass.getId());
        nrs.remove(null);
        if (nrs.isEmpty()) {
            return 0.0;
        }
        if (nrs.contains(nr)) {
            return 1.0;
        }
        if (nrs.get(0) <= nr && nrs.get(nrs.size() - 1) >= nr) {
            // number is in the known range of numbers but that doesn't necessarily mean that it's valid
            return 0.5;
        }
        return 0.0;
    }

    private List<Confidence<AutodetectionStatus.Solution>> extractFullSolutions(Context context, Operator operator, Photo photo) {
        List<Confidence<AutodetectionStatus.Solution>> solutions = new ArrayList<>();
        List<VehicleClass> vehicleClasses = context.getVehicleClassesModel().getByIds(context.getPhotosModel().getVehicleClassIdsByOperatorId(operator.getId())).collect(Collectors.toUnmodifiableList());
        for (UicNr uicNr : extractUicNrsFromTexts(photo.getTexts())) {
            for (VehicleClass vehicleClass : vehicleClasses) {
                if (containsWord(vehicleClass.getName(), uicNr.vclass + "")) {
                    double nrConfidence = nrConfidence(context, operator, vehicleClass, uicNr.nr);
                    if (nrConfidence > 0.0) {
                        solutions.add(new Confidence(new AutodetectionStatus.Solution(operator, vehicleClass, uicNr.nr), nrConfidence));
                    }
                }
            }
        }
        return solutions;
    }

    private List<Confidence<AutodetectionStatus.Solution>> extractNrOnlySolutions(Context context, Operator operator, Photo photo) {
        List<Confidence<AutodetectionStatus.Solution>> solutions = new ArrayList<>();
        List<VehicleClass> vehicleClasses = context.getVehicleClassesModel().getByIds(context.getPhotosModel().getVehicleClassIdsByOperatorId(operator.getId())).collect(Collectors.toUnmodifiableList());
        for (Integer nr : extractVehicleNrsFromTexts(photo.getTexts())) {
            for (VehicleClass vehicleClass : vehicleClasses) {
                double nrConfidence = nrConfidence(context, operator, vehicleClass, nr);
                if (nrConfidence > 0.0) {
                    solutions.add(new Confidence(new AutodetectionStatus.Solution(operator, vehicleClass, nr), nrConfidence * 0.8)); // we have lower confidence for vehicle numbers without class
                }
            }
        }
        return solutions;
    }

    public AutodetectionStatus autodetect(Context context, Photo photo) {
        List<Confidence<Operator>> operatorsFromLocation = extractOperatorsFromLocation(context, photo);
        List<Confidence<Operator>> operatorsFromTexts = extractOperatorsFromTexts(context, photo);
        List<Confidence<Operator>> operatorsFromAbbreviations = extractOperatorsFromAbbreviations(context, photo);

        List<Confidence<Operator>> operators = getOperators(operatorsFromLocation, operatorsFromTexts, operatorsFromAbbreviations);

        List<Confidence<AutodetectionStatus.Solution>> solutions = new ArrayList<>();
        for (Confidence<Operator> operatorConfidence : operators) {
            solutions.addAll(
                    extractFullSolutions(context, operatorConfidence.getEntity(), photo)
                    .stream()
                    .map(c -> new Confidence<>(c.getEntity(), c.getConfidence() * operatorConfidence.getConfidence()))
                    .collect(Collectors.toSet())
            );
        }

        if (solutions.isEmpty()) {
            // We haven't found any matches with the full UIC nr, try the vehicle nr only instead
            for (Confidence<Operator> operatorConfidence : operators) {
                solutions.addAll(
                        extractNrOnlySolutions(context, operatorConfidence.getEntity(), photo)
                        .stream()
                        .map(c -> new Confidence<>(c.getEntity(), c.getConfidence() * operatorConfidence.getConfidence()))
                        .collect(Collectors.toSet())
                );
            }
        }

        Collections.sort(solutions);
        return new AutodetectionStatus(operatorsFromLocation, operatorsFromTexts, operatorsFromAbbreviations, operators, solutions);
    }

    private static final class UicNr {
        int vclass;
        int nr;
        public UicNr(int vclass, int nr) {
            this.vclass = vclass;
            this.nr = nr;
        }
        public UicNr(String vclass, String nr) {
            try {
                this.vclass = Integer.parseInt(vclass);
                this.nr = Integer.parseInt(nr);
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            UicNr uicNr = (UicNr) o;
            return vclass == uicNr.vclass && nr == uicNr.nr;
        }

        @Override
        public int hashCode() {
            return Objects.hash(vclass, nr);
        }

        @Override
        public String toString() {
            return vclass + " " + nr;
        }
    }
}
