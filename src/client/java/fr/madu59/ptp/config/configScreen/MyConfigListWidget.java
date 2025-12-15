package fr.madu59.ptp.config.configScreen;

import java.util.List;

import fr.madu59.ptp.config.configScreen.MyConfigListWidget;

import fr.madu59.ptp.config.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class MyConfigListWidget extends ContainerObjectSelectionList<MyConfigListWidget.Entry> {

    public MyConfigListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
    }

    @Override
	protected int getScrollbarPosition() {
		return this.getX() + this.getWidth() - 6;
	}

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public void addCategory(String name) {
        this.addEntry(new CategoryEntry(name));
    }

    public void addButton(String name, Button.OnPress onPress) {
        this.addEntry(new ButtonEntry(Button.builder(Component.literal(name), onPress).bounds(0, 0, 100, 20).build(), null, ""));
    }

    public void addButton(Option option, Button.OnPress onPress) {
        this.addEntry(new ButtonEntry(Button.builder(Component.literal(option.getValueAsTranslatedString()), onPress).bounds(0, 0, 100, 20).build(), option, ""));
    }

    public void addButton(Option option, Button.OnPress onPress, String indent) {
        this.addEntry(new ButtonEntry(Button.builder(Component.literal(option.getValueAsTranslatedString()), onPress).bounds(0, 0, 100, 20).build(), option, indent));
    }

    // Base entry
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<MyConfigListWidget.Entry> {}

    // Category header
    public static class CategoryEntry extends MyConfigListWidget.Entry {
        private final String name;

        public CategoryEntry(String name) {
            this.name = name;
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Font font = Minecraft.getInstance().font;
            int textX = x + entryWidth / 2;
            int textY = y + (entryHeight - font.lineHeight) / 2;
            context.drawCenteredString(font, Component.translatable(this.name), textX, textY, 0xFFFFFFFF);
        }  

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

    // Button entry
    public static class ButtonEntry extends MyConfigListWidget.Entry{
        private final Button button;
        private final String name;
        private final String description;
        private final String indent;
        private final Option option;

        public ButtonEntry(Button button, Option option, String indent) {
            this.button = button;
            this.name = option.getName();
            this.description = option.getDescription();
            this.indent = indent;
            this.option = option;
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.setY(y + (entryHeight - this.button.getHeight()) / 2);
            this.button.setX(entryWidth - this.button.getWidth() - 10);
            this.button.render(context, mouseX, mouseY, tickDelta);

            if(this.description == null) return;

            Font textRenderer = Minecraft.getInstance().font;
            context.drawString(textRenderer, Component.literal(indent + this.name), 10, y + (entryHeight - textRenderer.lineHeight) / 2, 0xFFFFFFFF, true);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.button);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.button);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.button.mouseClicked(mouseX, mouseY, button)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                if(this.option != null){
                    this.button.setMessage(Component.literal(this.option.getValueAsTranslatedString()));
                }
                return true;
            }
            return false;
        }
    }
}