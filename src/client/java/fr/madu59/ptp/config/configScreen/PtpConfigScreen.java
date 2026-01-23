package fr.madu59.ptp.config.configScreen;

import fr.madu59.ptp.config.SettingsManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PtpConfigScreen extends Screen {
    private MyConfigListWidget list;

    private final String INDENT = " ⤷  ";

    public PtpConfigScreen(Screen parent) {
        super(Component.literal("Projectile Trajectory Preview Config"));
        this.parent = parent;
    }

    public static void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                literal("ptpConfig")
                    .executes(context -> {
                        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new PtpConfigScreen(null)));
                        return 1;
                    })
            );
        });
    }

    private final Screen parent;

    @Override
    protected void init() {
        super.init();
        // Create the scrolling list
        this.list = new MyConfigListWidget(this.minecraft, this.width, this.height - 80, 40, 26);

        // Example: Add categories + buttons
        list.addCategory("ptp.config.trajectory-previsualization");
        list.addButton(SettingsManager.SHOW_TRAJECTORY, btn -> {
            SettingsManager.SHOW_TRAJECTORY.setToNextValue();
        });
        list.addButton(SettingsManager.TRAJECTORY_COLOR, btn -> {
            SettingsManager.TRAJECTORY_COLOR.setToNextValue();
        }, INDENT);
        list.addButton(SettingsManager.TRAJECTORY_OPACITY, btn -> {
            SettingsManager.TRAJECTORY_OPACITY.setToNextValue();
        }, INDENT);
        list.addButton(SettingsManager.TRAJECTORY_STYLE, btn -> {
            SettingsManager.TRAJECTORY_STYLE.setToNextValue();
        }, INDENT);
        list.addButton(SettingsManager.ENABLE_OFFHAND, btn -> {
            SettingsManager.ENABLE_OFFHAND.setToNextValue();
        }, INDENT);
        list.addCategory("ptp.config.target-outlining");
        list.addButton(SettingsManager.OUTLINE_TARGETS, btn -> {
            SettingsManager.OUTLINE_TARGETS.setToNextValue();
        });
        list.addButton(SettingsManager.OUTLINE_COLOR, btn -> {
            SettingsManager.OUTLINE_COLOR.setToNextValue();
        }, INDENT);
        list.addButton(SettingsManager.OUTLINE_OPACITY, btn -> {
            SettingsManager.OUTLINE_OPACITY.setToNextValue();
        }, INDENT);
        list.addCategory("ptp.config.target-highlighting");
        list.addButton(SettingsManager.HIGHLIGHT_TARGETS, btn -> {
            SettingsManager.HIGHLIGHT_TARGETS.setToNextValue();
        });
        list.addButton(SettingsManager.HIGHLIGHT_COLOR, btn -> {
            SettingsManager.HIGHLIGHT_COLOR.setToNextValue();
        }, INDENT);
        list.addButton(SettingsManager.HIGHLIGHT_OPACITY, btn -> {
            SettingsManager.HIGHLIGHT_OPACITY.setToNextValue();
        }, INDENT);

        Button doneButton = Button.builder(Component.literal("Done"), b -> {
            this.minecraft.setScreen(this.parent);
            SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
        }).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build();

        this.addRenderableWidget(this.list);
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
        SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.list.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}