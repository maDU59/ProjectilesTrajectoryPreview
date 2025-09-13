package com.maDU59_.config;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;

public class ClientCommands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                literal("ptpConfig")
                    .then(argument("option", StringArgumentType.string()).suggests((context, builder) -> { return CommandSource.suggestMatching(SettingsManager.getAllOptionsId(), builder);})
                        .then(argument("value", StringArgumentType.string()).suggests((context, builder) -> { return CommandSource.suggestMatching(SettingsManager.getOptionPossibleValues(StringArgumentType.getString(context, "option")), builder);})
                            .executes(context -> {
                                String option = StringArgumentType.getString(context, "option");
                                String value = StringArgumentType.getString(context, "value");

                                boolean success = SettingsManager.setOptionValue(option, (Object) value);
                                MinecraftClient.getInstance().player.sendMessage(
                                    Text.literal(success ? "Updated " + option + " to " + value : "Failed to update setting."),
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
}

