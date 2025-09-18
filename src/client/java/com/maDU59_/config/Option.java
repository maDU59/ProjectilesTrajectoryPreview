package com.maDU59_.config;

import java.util.List;

import net.minecraft.client.resource.language.I18n;

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
        return I18n.translate(this.name);
    }

    public String getDescription() {
        return I18n.translate(this.description);
    }

    public List<Object> getPossibleValues(){
        return this.possibleValues;
    }

    public String getValueAsString() {
        if( value instanceof Boolean boolValue) {
            return boolValue ? I18n.translate("ptp.config.enabled") : I18n.translate("ptp.config.disabled");
        }
        return I18n.translate(this.value.toString());
    }

    public void setToNextValue() {
        if (possibleValues != null && !possibleValues.isEmpty()) {
            int currentIndex = possibleValues.indexOf(value);
            int nextIndex = (currentIndex + 1) % possibleValues.size();
            value = possibleValues.get(nextIndex);
        }
    }

    public void setPossibleValues(List<Object> possibleValues){
        this.possibleValues = possibleValues;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }
}
