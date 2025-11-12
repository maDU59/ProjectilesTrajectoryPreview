package com.maDU59_.config;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;

public class ClientCommands {
    public static void register() {
        Map<String,String> optionsMap = camelCase(SettingsManager.getAllOptionsId());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                literal("ptpConfig")
                    .then(argument("option", StringArgumentType.string()).suggests((context, builder) -> { return CommandSource.suggestMatching(optionsMap.keySet(), builder);})
                        .then(argument("value", StringArgumentType.string()).suggests((context, builder) -> { return CommandSource.suggestMatching(camelCase(SettingsManager.getOptionPossibleValues(optionsMap.get(StringArgumentType.getString(context, "option")))).keySet(), builder);})
                            .executes(context -> {
                                String option = "null";
                                String value = "null";
                                if(optionsMap != null && !optionsMap.isEmpty()){
                                    option = optionsMap.get(StringArgumentType.getString(context, "option"));
                                    Map<String, String> valuesMap = camelCase(SettingsManager.getOptionPossibleValues(option));
                                    if(valuesMap != null && !valuesMap.isEmpty()){
                                        value = valuesMap.get(StringArgumentType.getString(context, "value"));
                                    }
                                }

                                boolean success = SettingsManager.setOptionValue(option, (Object) value);
                                MinecraftClient.getInstance().player.sendMessage(
                                    Text.literal(success ? "Updated " + StringArgumentType.getString(context, "option") + " to " + StringArgumentType.getString(context, "value") : "Failed to update setting."),
                                    false
                                );
                                if(success){
                                    SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
                                }
                                return success ? 1 : 0;
                            })
                        )
                    )
            );
        });
    }

    private static Map<String,String> camelCase(List<String> list){
        if(list == null) return Collections.emptyMap();
        Map<String,String> result = new LinkedHashMap<String, String>();

        for (String string : list) {
            String lowercaseString = string.toLowerCase();
            StringBuilder sb = new StringBuilder();

            boolean upperNext = false;
            for (int i = 0; i < lowercaseString.length(); i++) {
                char c = lowercaseString.charAt(i);
                if (c == '_') {
                    upperNext = true;
                } else {
                    sb.append(upperNext ? Character.toUpperCase(c) : c);
                    upperNext = false;
                }
            }

            result.put(sb.toString(), string);
        }
        return result;
    }
}

