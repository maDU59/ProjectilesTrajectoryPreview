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

public class SettingsManager {

    public static List<Option> ALL_OPTIONS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ptp.json");
    public static List<Object> ENABLING_OPTION_VALUES = List.of(true, false);
    public static List<Object> COLOR_OPTION_VALUES = List.of("Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "White", "Black");
    public static List<Object> OPACITY_OPTION_VALUES = List.of("Opaque", "Transparent", "Pulsing");

    public static Option SHOW_TRAJECTORY = loadOptionWithDefaults(
        "SHOW_TRAJECTORY",
        "Trajectory Preview Visualization",
        "Toggle the visibility of projectile trajectories.",
        true,
        true,
        ENABLING_OPTION_VALUES
    );

    public static Option TRAJECTORY_COLOR = loadOptionWithDefaults(
        "TRAJECTORY_COLOR",
        "Color",
        "Select the color of the trajectory line.",
        "Green",
        "Green",
        COLOR_OPTION_VALUES
    );

    public static Option TRAJECTORY_OPACITY = loadOptionWithDefaults(
        "TRAJECTORY_OPACITY",
        "Opacity",
        "Set the opacity level of the trajectory line.",
        "Opaque",
        "Opaque",
        OPACITY_OPTION_VALUES
    );

    public static Option TRAJECTORY_STYLE = loadOptionWithDefaults(
        "TRAJECTORY_STYLE",
        "Style",
        "Choose the style of the trajectory line.",
        "Solid",
        "Solid",
        List.of("Solid", "Dashed", "Dotted")
    );

    public static Option OUTLINE_TARGETS = loadOptionWithDefaults(
        "OUTLINE_TARGETS",
        "Outline Targets",
        "Toggle outlining of potential targets.",
        true,
        true,
        ENABLING_OPTION_VALUES
    );

    public static Option OUTLINE_COLOR = loadOptionWithDefaults(
        "OUTLINE_COLOR",
        "Color",
        "Select the color for outlining targets.",
        "Green",
        "Green",
        COLOR_OPTION_VALUES
    );

    public static Option OUTLINE_OPACITY = loadOptionWithDefaults(
        "OUTLINE_OPACITY",
        "Opacity",
        "Set the opacity level of the outline.",
        "Opaque",
        "Opaque",
        OPACITY_OPTION_VALUES
    );

    public static Option HIGHLIGHT_TARGETS = loadOptionWithDefaults(
        "HIGHLIGHT_TARGETS",
        "Highlight Targets",
        "Toggle highlightning of potential targets.",
        true,
        true,
        ENABLING_OPTION_VALUES
    );

    public static Option HIGHLIGHT_COLOR = loadOptionWithDefaults(
        "HIGHLIGHT_COLOR",
        "Color",
        "Select the color for highlighting targets.",
        "Green",
        "Green",
        COLOR_OPTION_VALUES
    );

    public static Option HIGHLIGHT_OPACITY = loadOptionWithDefaults(
        "HIGHLIGHT_OPACITY",
        "Opacity",
        "Set the opacity level of the highlight.",
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

    public static int getARGBColorFromSetting(String colorName, String opacitySetting) {
        int[] colors = getColorFromSetting(colorName);
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
            SettingsManager.ALL_OPTIONS.add(loadedOption);
            return loadedOption;
        }
    }
    
}
