package fr.madu59.ptp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.madu59.ptp.Ptp;
import fr.madu59.ptp.config.SettingsManager;

import java.lang.Math;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class SettingsManager {

    public static List<Option<?>> ALL_OPTIONS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(Ptp.MOD_ID + ".json");
    private static Map<String, String> loadedSettings = loadSettings();

    public static Option<Option.State> SHOW_TRAJECTORY = loadOptionWithDefaults(
        "SHOW_TRAJECTORY",
        "ptp.config.show_trajectory",
        "Toggle the visibility of projectile trajectories.",
        Option.State.ENABLED
    );

    public static Option<Option.Color> TRAJECTORY_COLOR = loadOptionWithDefaults(
        "TRAJECTORY_COLOR",
        "ptp.config.trajectory_color",
        "ptp.config.trajectory_color_desc",
        Option.Color.DEPENDS_ON_TARGET
    );

    public static Option<Option.Opacity> TRAJECTORY_OPACITY = loadOptionWithDefaults(
        "TRAJECTORY_OPACITY",
        "ptp.config.trajectory_opacity",
        "ptp.config.trajectory_opacity_desc",
        Option.Opacity.OPAQUE
    );

    public static Option<Option.Style> TRAJECTORY_STYLE = loadOptionWithDefaults(
        "TRAJECTORY_STYLE",
        "ptp.config.trajectory_style",
        "ptp.config.trajectory_style_desc",
        Option.Style.SOLID
    );

    public static Option<Option.State> OUTLINE_TARGETS = loadOptionWithDefaults(
        "OUTLINE_TARGETS",
        "ptp.config.outline_targets",
        "ptp.config.outline_targets_desc",
        Option.State.ENABLED
    );

    public static Option<Option.Color> OUTLINE_COLOR = loadOptionWithDefaults(
        "OUTLINE_COLOR",
        "ptp.config.outline_color",
        "ptp.config.outline_color_desc",
        Option.Color.DEPENDS_ON_TARGET
    );

    public static Option<Option.Opacity> OUTLINE_OPACITY = loadOptionWithDefaults(
        "OUTLINE_OPACITY",
        "ptp.config.outline_opacity",
        "ptp.config.outline_opacity_desc",
        Option.Opacity.OPAQUE
    );

    public static Option<Option.State> HIGHLIGHT_TARGETS = loadOptionWithDefaults(
        "HIGHLIGHT_TARGETS",
        "ptp.config.highlight_targets",
        "ptp.config.highlight_targets_desc",
        Option.State.ENABLED
    );

    public static Option<Option.Color> HIGHLIGHT_COLOR = loadOptionWithDefaults(
        "HIGHLIGHT_COLOR",
        "ptp.config.highlight_color",
        "ptp.config.highlight_color_desc",
        Option.Color.DEPENDS_ON_TARGET
    );

    public static Option<Option.Opacity> HIGHLIGHT_OPACITY = loadOptionWithDefaults(
        "HIGHLIGHT_OPACITY",
        "ptp.config.highlight_opacity",
        "ptp.config.highlight_opacity_desc",
        Option.Opacity.TRANSPARENT
    );

    public static Option<Boolean> ENABLE_OFFHAND = loadOptionWithDefaults(
        "ENABLE_OFFHAND",
        "ptp.config.enable_offhand",
        "ptp.config.enable_offhand_desc",
        false
    );

    public static Option<Boolean> TOGGLE_BOW = loadOptionWithDefaults(
        "TOGGLE_BOW",
        "item.minecraft.bow",
        "ptp.config.toggle_bow_desc",
        true
    );

    public static Option<Boolean> TOGGLE_CROSSBOW = loadOptionWithDefaults(
        "TOGGLE_CROSSBOW",
        "item.minecraft.crossbow",
        "ptp.config.toggle_crossbow_desc",
        true
    );

    public static Option<Boolean> TOGGLE_TRIDENT = loadOptionWithDefaults(
        "TOGGLE_TRIDENT",
        "item.minecraft.trident",
        "ptp.config.toggle_trident_desc",
        true
    );

    public static Option<Boolean> TOGGLE_ENDERPEARL = loadOptionWithDefaults(
        "TOGGLE_ENDERPEARL",
        "item.minecraft.ender_pearl",
        "ptp.config.toggle_enderpearl_desc",
        true
    );

    public static Option<Boolean> TOGGLE_SNOWBALL = loadOptionWithDefaults(
        "TOGGLE_SNOWBALL",
        "item.minecraft.snowball",
        "ptp.config.toggle_snowball_desc",
        true
    );

    public static Option<Boolean> TOGGLE_EGG = loadOptionWithDefaults(
        "TOGGLE_EGG",
        "item.minecraft.egg",
        "ptp.config.toggle_egg_desc",
        true
    );

    public static Option<Boolean> TOGGLE_WINDCHARGE = loadOptionWithDefaults(
        "TOGGLE_WINDCHARGE",
        "item.minecraft.wind_charge",
        "ptp.config.toggle_windcharge_desc",
        true
    );

    public static Option<Boolean> TOGGLE_POTION = loadOptionWithDefaults(
        "TOGGLE_POTION",
        "item.minecraft.splash_potion",
        "ptp.config.toggle_potion_desc",
        true
    );

    public static Option<Boolean> TOGGLE_EXPPOTION = loadOptionWithDefaults(
        "TOGGLE_EXPPOTION",
        "item.minecraft.experience_bottle",
        "ptp.config.toggle_exppotion_desc",
        true
    );

    public static Option<Boolean> TOGGLE_FISHINGROD = loadOptionWithDefaults(
        "TOGGLE_FISHINGROD",
        "item.minecraft.fishing_rod",
        "ptp.config.toggle_fishingrod_desc",
        true
    );

    public static List<String> getAllOptionsId(){
        List<String> list = new ArrayList<>();
        for (Option<?> option : ALL_OPTIONS){
            list.add(option.getId());
            }
        return list;
    }

    public static <T> boolean setOptionValue(String optionId, String value){
        for (Option<?> option : ALL_OPTIONS){
            if(option.getId().equalsIgnoreCase(optionId)){
                if (option.value instanceof Float){
                    try{
                        Float floatVal = Float.parseFloat(value);
                        setOptionValueHelper(option, floatVal);
                        return true;
                    }
                    catch(Exception e){ 
                        return false;
                    }
                }
                else if (option.value instanceof Enum<?> en){
                    try{
                        Enum<?> enumValue = Enum.valueOf(en.getDeclaringClass(), value);
                        setOptionValueHelper(option, enumValue);
                        return true;
                    }
                    catch(Exception e){ 
                        return false;
                    }
                }
                else if (option.value instanceof Boolean){
                    Boolean boolValue = Boolean.valueOf(value);
                    setOptionValueHelper(option, boolValue);
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T> void setOptionValueHelper(Option<T> option, Object value) {
        option.setValue((T) value);
    }

    private static final int[] COLOR_RED = new int[] {255, 0, 0};
    private static final int[] COLOR_GREEN = new int[] {0, 255, 0};
    private static final int[] COLOR_BLUE = new int[] {0, 0, 255};
    private static final int[] COLOR_YELLOW = new int[] {255, 255, 0};
    private static final int[] COLOR_CYAN = new int[] {0, 255, 255};
    private static final int[] COLOR_MAGENTA = new int[] {255, 0, 255};
    private static final int[] COLOR_WHITE = new int[] {255, 255, 255};
    private static final int[] COLOR_BLACK = new int[] {0, 0, 0};
    private static final int[] COLOR_PURPLE = new int[] {128, 0, 128};

    private static final float[] FLOAT_COLOR_RED = new float[] {1.0f, 0.0f, 0.0f};
    private static final float[] FLOAT_COLOR_GREEN = new float[] {0.0f, 1.0f, 0.0f};
    private static final float[] FLOAT_COLOR_BLUE = new float[] {0.0f, 0.0f, 1.0f};
    private static final float[] FLOAT_COLOR_YELLOW = new float[] {1.0f, 1.0f, 0.0f};
    private static final float[] FLOAT_COLOR_CYAN = new float[] {0.0f, 1.0f, 1.0f};
    private static final float[] FLOAT_COLOR_MAGENTA = new float[] {1.0f, 0.0f, 1.0f};
    private static final float[] FLOAT_COLOR_WHITE = new float[] {1.0f, 1.0f, 1.0f};
    private static final float[] FLOAT_COLOR_BLACK = new float[] {0.0f, 0.0f, 0.0f};
    private static final float[] FLOAT_COLOR_PURPLE = new float[] {128.0f / 255.0f, 0.0f, 128.0f / 255.0f};

    public static int getARGBColorFromSetting(Option.Color color, Option.Opacity opacitySetting, Entity entity) {
        int[] colors = getColorFromSetting(color, entity);
        return colors[2] | (colors[1] << 8) | (colors[0] << 16) | (getAlphaFromSetting(opacitySetting) << 24);
    }

    public static int getAlphaFromSetting(Option.Opacity opacitySetting){
        int alpha;
        switch (opacitySetting) {
            case OPAQUE:
                alpha = 255;
                break;
            case TRANSPARENT:
                alpha = 100;
                break;
            case PULSING:
                alpha = (int) Math.floor(Math.sin((double)(System.currentTimeMillis() % 2000 / 2000.0 * Math.PI)) * 206) + 50; // Pulsing effect
                break;
            default:
                alpha = 255; // Default to opaque if unknown
        }
        return alpha;
    }

    public static float[] convertColorToFloat(int[] colors){
        if (colors == COLOR_RED) return FLOAT_COLOR_RED;
        if (colors == COLOR_GREEN) return FLOAT_COLOR_GREEN;
        if (colors == COLOR_BLUE) return FLOAT_COLOR_BLUE;
        if (colors == COLOR_YELLOW) return FLOAT_COLOR_YELLOW;
        if (colors == COLOR_CYAN) return FLOAT_COLOR_CYAN;
        if (colors == COLOR_MAGENTA) return FLOAT_COLOR_MAGENTA;
        if (colors == COLOR_WHITE) return FLOAT_COLOR_WHITE;
        if (colors == COLOR_BLACK) return FLOAT_COLOR_BLACK;
        if (colors == COLOR_PURPLE) return FLOAT_COLOR_PURPLE;

        float red = colors[0]/(float)255.0;
        float green = colors[1]/(float)255.0;
        float blue = colors[2]/(float)255.0;
        return new float[] {red, green, blue};
    }

    public static float convertAlphaToFloat(int alpha){
        float alphaFloat = alpha/(float)255.0;
        return alphaFloat;
    }

    public static int[] getColorFromSetting(Option.Color color) {
        return getColorFromSetting(color, null);
    }

    public static int[] getColorFromSetting(Option.Color color, Entity entity) {
        if(color == Option.Color.DEPENDS_ON_TARGET){
            if(entity == null){color = Option.Color.WHITE;}
            else if(entity instanceof  Player){color = Option.Color.BLUE;}
            else if(entity instanceof  NeutralMob){color = Option.Color.YELLOW;}
            else if(entity instanceof  AgeableMob){color = Option.Color.GREEN;}
            else if(entity instanceof  Monster){color = Option.Color.RED;}
            else if(entity instanceof  Mob){color = Option.Color.PURPLE;}
            else if(entity instanceof  LivingEntity){color = Option.Color.CYAN;}
            else{color = Option.Color.MAGENTA;}
        }
        switch (color) {
            case RED:
                return COLOR_RED;
            case GREEN:
                return COLOR_GREEN;
            case BLUE:
                return COLOR_BLUE;
            case YELLOW:
                return COLOR_YELLOW;
            case CYAN:
                return COLOR_CYAN;
            case MAGENTA:
                return COLOR_MAGENTA;
            case WHITE:
                return COLOR_WHITE;
            case BLACK:
                return COLOR_BLACK;
            case PURPLE:
                return COLOR_PURPLE;
            default:
                return COLOR_RED; // Default to red if unknown
        }
    }

    public static void saveSettings(List<Option<?>> options) {
        Map<String, String> map = toMap(options);
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(map, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> toMap(List<Option<?>> options) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Option<?> option : options) {
            map.put(option.getId(), option.value.toString());
        }
        return map;
    }

    private static Map<String, String> loadSettings() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = GSON.fromJson(reader, type);
            return map;
        } catch (Exception e) {
            Ptp.LOGGER.info("[PTP] Config file not found or invalid, using default");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOptionValue(String key, T defaultValue) {
        if (loadedSettings == null || !loadedSettings.containsKey(key)) return null;
        else if (defaultValue instanceof Enum<?> e){
            return (T) Enum.valueOf(e.getDeclaringClass(), loadedSettings.get(key));
        }
        else if (defaultValue instanceof Float){
            return (T) Float.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Boolean){
            return (T) (Boolean) Boolean.parseBoolean(loadedSettings.get(key));
        }
        else return null;
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, String description, T defaultValue) {
        T optionValue= getOptionValue(id, defaultValue);
        if (optionValue == null) optionValue = defaultValue;
        Option<T> option = new Option<T>(
                id,
                name,
                description,
                optionValue,
                defaultValue
        );
        return option;
    }
    
}
