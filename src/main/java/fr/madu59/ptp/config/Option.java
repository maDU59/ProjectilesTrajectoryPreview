package fr.madu59.ptp.config;

import java.util.List;

import net.minecraft.client.resources.language.I18n;

public class Option<T> {
    public String id;
    public String name;
    public String description;
    public T value;
    public T defaultValue;

    public Option(String id, String name, String description, T value, T defaultValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        SettingsManager.ALL_OPTIONS.add(this);
    }

    public void resetToDefault() {
        this.value = this.defaultValue;
    }

    public void setValue(T newValue) {
        this.value = newValue;
    }

    public T getValue() {
        return this.value;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return I18n.get(this.name);
    }

    public String getDescription() {
        return I18n.get(this.description);
    }

    public List<?> getPossibleValues(){
        if(this.value instanceof Boolean){
            return List.of(true, false);
        }
        if(this.value instanceof Enum<?> enumValue){
            return List.of(enumValue.getDeclaringClass().getEnumConstants());
        }
        else return List.of();
    }

    public String getValueAsTranslatedString() {
        if( value instanceof Boolean boolValue) {
            return boolValue ? I18n.get("ptp.config.enabled") : I18n.get("ptp.config.disabled");
        }
        return I18n.get(this.value.toString());
    }

    public void setToNextValue() {
        this.value = cycle(this.value);
    }

    @SuppressWarnings("unchecked")
    public T cycle(T value) {
        if (value instanceof Enum<?> enumValue) {
            Enum<?>[] constants = enumValue.getDeclaringClass().getEnumConstants();
            int nextOrdinal = (enumValue.ordinal() + 1) % constants.length;
            return (T) constants[nextOrdinal];
        }
        else if (value instanceof Boolean boolValue) {
           return (T) Boolean.valueOf(!boolValue);
        }
        else return null;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public static enum State{
        ENABLED,
        TARGET_IS_ENTITY,
        DISABLED
    }

    public static enum Opacity{
        OPAQUE,
        TRANSPARENT,
        PULSING
    }

    public static enum Style{
        SOLID,
        DASHED,
        DOTTED
    }

    public static enum Color{
        RED,
        GREEN,
        BLUE,
        YELLOW,
        CYAN,
        MAGENTA,
        WHITE,
        PURPLE,
        BLACK,
        DEPENDS_ON_TARGET
    }
}
