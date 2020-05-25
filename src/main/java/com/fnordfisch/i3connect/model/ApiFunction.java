package com.fnordfisch.i3connect.model;

/**
 * Supported API functions
 */
public enum ApiFunction {
    EFFICIENCY("efficiency"),
    DYNAMIC("dynamic"),
    NAVIGATION("navigation"),
    CHARGINGPROFILE("chargingprofile");

    private final String functionName;

    ApiFunction(final String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}