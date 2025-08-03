package entities.tmp;

import entities.Operator;
import entities.VehicleClass;

import java.util.*;

public class AutodetectionStatus {
    private final List<Confidence<Operator>> operatorsFromLocation;
    private final List<Confidence<Operator>> operatorsFromTexts;
    private final List<Confidence<Operator>> operatorsFromAbbreviations;
    private final List<Confidence<Operator>> operators;
    private final List<Confidence<Solution>> solutions;

    public AutodetectionStatus(
            List<Confidence<Operator>> operatorsFromLocation,
            List<Confidence<Operator>> operatorsFromTexts,
            List<Confidence<Operator>> operatorsFromAbbreviations,
            List<Confidence<Operator>> operators,
            List<Confidence<Solution>> solutions) {
        this.operatorsFromLocation = operatorsFromLocation;
        this.operatorsFromTexts = operatorsFromTexts;
        this.operatorsFromAbbreviations = operatorsFromAbbreviations;
        this.operators = operators;
        this.solutions = solutions;
    }

    public List<Confidence<Operator>> getOperatorsFromLocation() {
        return operatorsFromLocation;
    }

    public List<Confidence<Operator>> getOperatorsFromTexts() {
        return operatorsFromTexts;
    }

    public List<Confidence<Operator>> getOperatorsFromAbbreviations() {
        return operatorsFromAbbreviations;
    }

    public List<Confidence<Operator>> getOperators() {
        return operators;
    }

    public List<Confidence<Solution>> getSolutions() {
        return solutions;
    }

    public static class Solution {
        private final Operator operator;
        private final VehicleClass vehicleClass;
        private final int nr;

        public Solution(Operator operator, VehicleClass vehicleClass, int nr) {
            this.operator = operator;
            this.vehicleClass = vehicleClass;
            this.nr = nr;
        }

        public Operator getOperator() {
            return operator;
        }

        public VehicleClass getVehicleClass() {
            return vehicleClass;
        }

        public int getNr() {
            return nr;
        }

        @Override
        public String toString() {
            return operator.getName() + " " + vehicleClass.getName() + " " + nr;
        }

        public String toIds() {
            return operator.getId() + "|" + vehicleClass.getId() + "|" + nr;
        }
    }
}
