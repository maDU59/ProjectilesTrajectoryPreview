package fr.madu59.ptp.config.configscreen;

import fr.madu59.ptp.config.SettingsManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PtpConfigScreen extends Screen {
    
    private MyConfigListWidget list;
    private final Screen parent;

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

    @Override
    protected void init() {
        super.init();
        this.list = new MyConfigListWidget(this.minecraft, this.width, this.height - 80, 40, 26);

        list.category("ptp.config.trajectory-previsualization").build();
        list.button(SettingsManager.SHOW_TRAJECTORY).build();
        list.button(SettingsManager.TRAJECTORY_COLOR).indent().build();
        list.button(SettingsManager.TRAJECTORY_OPACITY).indent().build();
        list.button(SettingsManager.TRAJECTORY_STYLE).indent().build();
        list.button(SettingsManager.ENABLE_OFFHAND).indent().build();
        list.category("ptp.config.target-outlining").build();
        list.button(SettingsManager.OUTLINE_TARGETS).build();
        list.button(SettingsManager.OUTLINE_COLOR).indent().build();
        list.button(SettingsManager.OUTLINE_OPACITY).indent().build();
        list.category("ptp.config.target-highlighting").build();
        list.button(SettingsManager.HIGHLIGHT_TARGETS).build();
        list.button(SettingsManager.HIGHLIGHT_COLOR).indent().build();
        list.button(SettingsManager.HIGHLIGHT_OPACITY).indent().build();
        list.category("ptp.config.projectile-toggle").build();
        list.button(SettingsManager.TOGGLE_BOW).build();
        list.button(SettingsManager.TOGGLE_CROSSBOW).build();
        list.button(SettingsManager.TOGGLE_TRIDENT).build();
        list.button(SettingsManager.TOGGLE_ENDERPEARL).build();
        list.button(SettingsManager.TOGGLE_SNOWBALL).build();
        list.button(SettingsManager.TOGGLE_EGG).build();
        list.button(SettingsManager.TOGGLE_WINDCHARGE).build();
        list.button(SettingsManager.TOGGLE_POTION).build();
        list.button(SettingsManager.TOGGLE_EXPPOTION).build();
        list.button(SettingsManager.TOGGLE_FISHINGROD).build();


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
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.list.extractRenderState(context, mouseX, mouseY, delta);
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}