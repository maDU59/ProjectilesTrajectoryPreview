package com.example.config;

import java.util.List;

public class Option {
    public String id;
    public String name;
    public String description;
    public Object value;
    public Object defaultValue;
    public List<Object> possibleValues;

    public Option(String id, String name, String description, Object value, Object defaultValue, List<Object> possibleValues) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.defaultValue = defaultValue;
        this.possibleValues = possibleValues;
        SettingsManager.ALL_OPTIONS.add(this);
    }

    public void resetToDefault() {
        this.value = this.defaultValue;
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }

    public Object getValue() {
        return this.value;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getValueAsString() {
        if( value instanceof Boolean) {
            return (Boolean) value ? "Enabled" : "Disabled";
        }
        return this.value.toString();
    }

    public void setToNextValue() {
        if (possibleValues != null && !possibleValues.isEmpty()) {
            int currentIndex = possibleValues.indexOf(value);
            int nextIndex = (currentIndex + 1) % possibleValues.size();
            value = possibleValues.get(nextIndex);
        }
    }
}
