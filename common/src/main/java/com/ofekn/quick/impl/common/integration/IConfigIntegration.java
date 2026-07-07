package com.ofekn.quick.impl.common.integration;

import java.util.Arrays;

public interface IConfigIntegration {
    String getWheelType();
    void setWheelType(String wheelType);

    boolean getStoreItems();

    default String getActionAssignment(int index) { return ""; }
    default void setActionAssignment(int index, String assignment) {}

    IConfigIntegration DEFAULT = new IConfigIntegration() {
        String wheelType = "round";
        final String[] actions = new String[10];
        {
            Arrays.fill(actions, "");
        }

        @Override
        public String getWheelType() {
            return wheelType;
        }

        @Override
        public void setWheelType(String wheelType) {
            this.wheelType = wheelType;
        }

        @Override
        public boolean getStoreItems() {
            return true;
        }

        @Override
        public String getActionAssignment(int index) {
            return actions[index];
        }

        @Override
        public void setActionAssignment(int index, String assignment) {
            actions[index] = assignment;
        }
    };
}
