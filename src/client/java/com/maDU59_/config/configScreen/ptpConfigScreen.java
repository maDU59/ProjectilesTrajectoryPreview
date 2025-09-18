package com.maDU59_.config.configScreen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import com.maDU59_.config.SettingsManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ptpConfigScreen extends Screen {
    private MyConfigListWidget list;

    private final String INDENT = " ⤷  ";

    protected ptpConfigScreen(Screen parent) {
        super(Text.literal("Projectile Trajectory Preview Config"));
        this.parent = parent;
    }

    private final Screen parent;

    @Override
    protected void init() {
        super.init();
        // Create the scrolling list
        this.list = new MyConfigListWidget(this.client, this.width, this.height - 80, 40, 26);

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

        ButtonWidget doneButton = ButtonWidget.builder(Text.literal("Done"), b -> {
            this.client.setScreen(this.parent);
            SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
        }).dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build();

        this.addDrawableChild(this.list);
        this.addDrawableChild(doneButton);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.list.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}