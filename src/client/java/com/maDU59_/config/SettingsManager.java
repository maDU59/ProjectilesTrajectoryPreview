package com.maDU59_.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.Math;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SettingsManager {

    public static List<Option> ALL_OPTIONS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ptp.json");
    public static List<Object> ENABLING_OPTION_VALUES = List.of(true, false);
    public static List<Object> COLOR_OPTION_VALUES = List.of("Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "White", "Purple", "Black", "Depends on target");
    public static List<Object> OPACITY_OPTION_VALUES = List.of("Opaque", "Transparent", "Pulsing");

    public static Option SHOW_TRAJECTORY = loadOptionWithDefaults(
        "SHOW_TRAJECTORY",
        "ptp.config.show_trajectory",
        "Toggle the visibility of projectile trajectories.",
        true,
        true,
        ENABLING_OPTION_VALUES
    );

    public static Option TRAJECTORY_COLOR = loadOptionWithDefaults(
        "TRAJECTORY_COLOR",
        "ptp.config.trajectory_color",
        "ptp.config.trajectory_color_desc",
        "Green",
        "Green",
        COLOR_OPTION_VALUES
    );

    public static Option TRAJECTORY_OPACITY = loadOptionWithDefaults(
        "TRAJECTORY_OPACITY",
        "ptp.config.trajectory_opacity",
        "ptp.config.trajectory_opacity_desc",
        "Opaque",
        "Opaque",
        OPACITY_OPTION_VALUES
    );

    public static Option TRAJECTORY_STYLE = loadOptionWithDefaults(
        "TRAJECTORY_STYLE",
        "ptp.config.trajectory_style",
        "ptp.config.trajectory_style_desc",
        "Solid",
        "Solid",
        List.of("Solid", "Dashed", "Dotted")
    );

    public static Option OUTLINE_TARGETS = loadOptionWithDefaults(
        "OUTLINE_TARGETS",
        "ptp.config.outline_targets",
        "ptp.config.outline_targets_desc",
        true,
        true,
        ENABLING_OPTION_VALUES
    );

    public static Option OUTLINE_COLOR = loadOptionWithDefaults(
        "OUTLINE_COLOR",
        "ptp.config.outline_color",
        "ptp.config.outline_color_desc",
        "Green",
        "Green",
        COLOR_OPTION_VALUES
    );

    public static Option OUTLINE_OPACITY = loadOptionWithDefaults(
        "OUTLINE_OPACITY",
        "ptp.config.outline_opacity",
        "ptp.config.outline_opacity_desc",
        "Opaque",
        "Opaque",
        OPACITY_OPTION_VALUES
    );

    public static Option HIGHLIGHT_TARGETS = loadOptionWithDefaults(
        "HIGHLIGHT_TARGETS",
        "ptp.config.highlight_targets",
        "ptp.config.highlight_targets_desc",
        true,
        true,
        ENABLING_OPTION_VALUES
    );

    public static Option HIGHLIGHT_COLOR = loadOptionWithDefaults(
        "HIGHLIGHT_COLOR",
        "ptp.config.highlight_color",
        "ptp.config.highlight_color_desc",
        "Green",
        "Green",
        COLOR_OPTION_VALUES
    );

    public static Option HIGHLIGHT_OPACITY = loadOptionWithDefaults(
        "HIGHLIGHT_OPACITY",
        "ptp.config.highlight_opacity",
        "ptp.config.highlight_opacity_desc",
        "Transparent",
        "Transparent",
        OPACITY_OPTION_VALUES
    );

    public static List<String> getAllOptionsId(){
        List<String> list = new ArrayList<>();
        for (Option option : ALL_OPTIONS){
            list.add(option.getId());
            }
        return list;
    }

    public static boolean setOptionValue(String optionId, Object value){
        for (Option option : ALL_OPTIONS){
            System.out.println(optionId + ": " + option.getId() + ", " + option.getId().equalsIgnoreCase(optionId));
            System.out.println(value + ": " + option.getPossibleValues() + ", " + option.getPossibleValues().contains(value));
            if(option.getId().equalsIgnoreCase(optionId)){
                int index = option.getPossibleValues().stream().map(Object::toString).collect(Collectors.toList()).indexOf((String) value);
                if (option.getPossibleValues().contains(value)){
                    option.setValue(value);
                    return true;
                }
                else if(index != -1){
                    option.setValue(option.getPossibleValues().get(index));
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> getOptionPossibleValues(String optionId){
        for (Option option : ALL_OPTIONS){
            if (option.getId().equalsIgnoreCase(optionId)){
                return option.getPossibleValues().stream().map(Object::toString).collect(Collectors.toList());
            }
        }
        return null;
    }

    public static int getARGBColorFromSetting(String colorName, String opacitySetting, Entity entity) {
        int[] colors = getColorFromSetting(colorName, entity);
        return colors[2] + colors[1] * 256 + colors[0] * 256 * 256 + getAlphaFromSetting(opacitySetting) * 256 * 256 *256;
    }

    public static int getAlphaFromSetting(String opacitySetting){
        int alpha;
        switch (opacitySetting) {
            case "Opaque":
                alpha = 255;
                break;
            case "Transparent":
                alpha = 100;
                break;
            case "Pulsing":
                alpha = (int) Math.floor(Math.sin((double)(System.currentTimeMillis() % 2000 / 2000.0 * Math.PI)) * 206) + 50; // Pulsing effect
                break;
            default:
                alpha = 255; // Default to opaque if unknown
        }
        return alpha;
    }

    public static float[] convertColorToFloat(int[] colors){
        float red = colors[0]/(float)255.0;
        float green = colors[1]/(float)255.0;
        float blue = colors[2]/(float)255.0;
        return new float[] {red, green, blue};
    }

    public static float convertAlphaToFloat(int alpha){
        float alphaFloat = alpha/(float)255.0;
        return alphaFloat;
    }

    public static int[] getColorFromSetting(String colorName) {
        return getColorFromSetting(colorName, null);
    }

    public static int[] getColorFromSetting(String colorName, Entity entity) {
        if(colorName.equals("Depends on target")){
            if(entity == null){colorName = "White";}
            else if(entity instanceof  PlayerEntity){colorName = "Blue";}
            else if(entity instanceof  Angerable){colorName = "Yellow";}
            else if(entity instanceof  PassiveEntity){colorName = "Green";}
            else if(entity instanceof  HostileEntity){colorName = "Red";}
            else if(entity instanceof  MobEntity){colorName = "Purple";}
            else if(entity instanceof  LivingEntity){colorName = "Cyan";}
            else{colorName = "Magenta";}
        }
        int red = 0, green = 0, blue = 0;
        switch (colorName) {
            case "Red":
                red = 255;
                break;
            case "Green":
                green = 255;
                break;
            case "Blue":
                blue = 255;
                break;
            case "Yellow":
                red = 255;
                green = 255;
                break;
            case "Cyan":
                green = 255;
                blue = 255;
                break;
            case "Magenta":
                red = 255;
                blue = 255;
                break;
            case "White":
                red = 255;
                green = 255;
                blue = 255;
                break;
            case "Black":
                red = 0;
                green = 0;
                blue = 0;
                break;
            case "Purple":
                red = 128;
                green = 0;
                blue = 128;
                break;
            default:
                red = 255; // Default to red if unknown
        }

        return new int[] {red, green, blue};
    }

    public static void saveSettings(List<Option> options) {
        Map<String, Option> map = toMap(options);
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(map, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Option> toMap(List<Option> options) {
        Map<String, Option> map = new LinkedHashMap<>();
        for (Option option : options) {
            map.put(option.getId(), option);
        }
        return map;
    }

    private static Option loadOption(String key) {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Type type = new TypeToken<Map<String, Option>>() {}.getType();
            Map<String, Option> map = GSON.fromJson(reader, type);
            return map.get(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Option loadOptionWithDefaults(String id, String name, String description, Object value, Object defaultValue, List<Object> possibleValues) {
        Option loadedOption = loadOption(id);
        System.out.println("Loaded option for " + id + ": " + (loadedOption == null ? "null" : loadedOption.getValueAsString()));
        if (loadedOption == null) {
            return new Option(
                    id,
                    name,
                    description,
                    value,
                    defaultValue,
                    possibleValues
            );
        } else {
            loadedOption.setPossibleValues(possibleValues);
            loadedOption.setName(name);
            loadedOption.setDescription(description);
            SettingsManager.ALL_OPTIONS.add(loadedOption);
            return loadedOption;
        }
    }
    
}
