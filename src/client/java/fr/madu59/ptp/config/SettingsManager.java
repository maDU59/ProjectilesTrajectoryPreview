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
import java.util.stream.Collectors;

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

    public static <T> List<String> getOptionPossibleValues(String optionId){
        for (Option<?> option : ALL_OPTIONS){
            if (option.getId().equalsIgnoreCase(optionId)){
                return option.getPossibleValues().stream().map(Object::toString).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public static int getARGBColorFromSetting(Option.Color color, Option.Opacity opacitySetting, Entity entity) {
        int[] colors = getColorFromSetting(color, entity);
        return colors[2] + colors[1] * 256 + colors[0] * 256 * 256 + getAlphaFromSetting(opacitySetting) * 256 * 256 *256;
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
        int red = 0, green = 0, blue = 0;
        switch (color) {
            case RED:
                red = 255;
                break;
            case GREEN:
                green = 255;
                break;
            case BLUE:
                blue = 255;
                break;
            case YELLOW:
                red = 255;
                green = 255;
                break;
            case CYAN:
                green = 255;
                blue = 255;
                break;
            case MAGENTA:
                red = 255;
                blue = 255;
                break;
            case WHITE:
                red = 255;
                green = 255;
                blue = 255;
                break;
            case BLACK:
                red = 0;
                green = 0;
                blue = 0;
                break;
            case PURPLE:
                red = 128;
                green = 0;
                blue = 128;
                break;
            default:
                red = 255; // Default to red if unknown
        }

        return new int[] {red, green, blue};
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
