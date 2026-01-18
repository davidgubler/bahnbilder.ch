package i18n;

import entities.*;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoDescriptionGenerator {
    private static final Pattern NR_WILDCARD = Pattern.compile("[^#]*(#+).*");

    private static final String VARIANT_TYPE = " Typ ";
    private static final String BR_PREFIX = "BR ";
    private static final String RH_PREFIX = "RH ";

    public static String getCustomVehicleName(String lang, Operator operator, VehicleClass vehicleClass, Integer nr) {
        if (nr != null && vehicleClass != null && vehicleClass.getNameNumberFormat() != null) {
            Matcher m = NR_WILDCARD.matcher(vehicleClass.getNameNumberFormat());
            if (m.matches()) {
                int minLen = m.group(1).length();
                String formatPattern = "%0" + minLen + "d";
                return vehicleClass.getNameNumberFormat().replace(m.group(1), formatPattern.formatted(nr));
            }
        }

        if (vehicleClass != null && vehicleClass.getName() != null) {
            String name = vehicleClass.getName();
            if (name.contains(VARIANT_TYPE)) {
                name = name.substring(0, name.indexOf(VARIANT_TYPE));
            }
            if (operator != null && name.startsWith(operator.getAbbr() + " ")) {
                name = name.substring(operator.getAbbr().length() + 1);
            }
            if (name.startsWith(BR_PREFIX)) {
                name = name.substring(BR_PREFIX.length());
                if (nr == null) {
                    name = Txt.get(lang, "vclass") + " " + name;
                }
            } else if (name.startsWith(RH_PREFIX)) {
                name = name.substring(RH_PREFIX.length());
                if (nr == null) {
                    name = Txt.get(lang, "vclass") + " " + name;
                }
            }
            if (nr != null) {
                name = name + " " + nr;
            }
            return name;
        }

        return "";
    }

    public static String getCustomDescription(String lang, Photo photo, boolean inclVehicleClass, boolean inclOperator, boolean inclOperatorAbbr, boolean inclLocation, boolean inclCountry) {
        String vehicleName = inclVehicleClass ? getCustomVehicleName(lang, photo.getOperator(), photo.getVehicleClass(), photo.getNr()) : null;
        String operatorName = (inclOperator && photo.getOperator() != null) ? photo.getOperator().getName(lang, photo.getPhotoDate()) : null;
        String operatorAbbr = (inclOperatorAbbr && photo.getOperator() != null) ? photo.getOperator().getAbbr() : null;
        Location location = inclLocation ? photo.getLocation() : null;
        Country country = inclCountry ? photo.getCountry() : null;

        String desc = vehicleName == null ? "" : vehicleName;
        if (operatorName != null || operatorAbbr != null) {
            if (!desc.isBlank()) {
                desc += " " + getOperatorArticle(lang, photo.getOperator()) + " ";
            }
            if (operatorAbbr != null && operatorName != null) {
                desc += operatorAbbr + " (" + operatorName + ")";
            } else if (operatorAbbr != null) {
                desc += operatorAbbr;
            } else {
                desc += operatorName;
            }
        }

        if (location != null) {
            if (!desc.isBlank()) {
                desc += " " + getCustomDescription(lang, location);
            } else {
                desc = getCustomDescription(lang, location);
                desc = desc.substring(0, 1).toUpperCase(Locale.ROOT) + desc.substring(1);
            }
        }

        if (country != null) {
            if (!desc.isBlank()) {
                desc += ", " + country.getName(lang);
            } else {
                desc = country.getName(lang);
            }
        }

        return desc;
    }

    private static String getCustomDescription(String lang, Location location) {
        if (location == null) {
            return null;
        }

        if (location.getName().contains(" - ")) {
            int pos = location.getName().indexOf(" - ");
            return Txt.get(lang, "between") + " " + location.getName().substring(0, pos) + " " + Txt.get(lang, "and") + " " + location.getName().substring(pos + 3);
        }

        return Txt.get(lang, "atLoc") + " " + location.getName();
    }

    private static String getOperatorArticle(String lang, Operator operator) {
        if (operator == null || operator.getName() == null) {
            return null;
        }
        if ("de".equals(lang)) {
            String nameLower = operator.getName().toLowerCase(Locale.GERMAN);
            return (nameLower.contains("verein") || nameLower.contains("club")) ? "des" : "der";
        }
        return "of";
    }
}
