package com.ofekn.quick.impl.common.integration;

public interface IConfigIntegration {
    String getWheelType();
    void setWheelType(String wheelType);

    boolean getStoreItems();

    // Format: "id:minecraft:stick", "name:Stick", "slot:9", or "" if unset
    default String getActionAssignment(int index) { return ""; }
    default void setActionAssignment(int index, String assignment) {}

    IConfigIntegration DEFAULT = new IConfigIntegration() {
        String wheelType = "round";
        final String[] actions = new String[10];

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
            String v = actions[index];
            return v != null ? v : "";
        }

        @Override
        public void setActionAssignment(int index, String assignment) {
            actions[index] = assignment;
        }
    };
}
