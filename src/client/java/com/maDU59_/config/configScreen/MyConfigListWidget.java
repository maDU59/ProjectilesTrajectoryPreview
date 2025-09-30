package com.maDU59_.config.configScreen;

import java.util.List;

import com.maDU59_.config.Option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.sound.PositionedSoundInstance;

public class MyConfigListWidget extends ElementListWidget<MyConfigListWidget.Entry> {

    public MyConfigListWidget(MinecraftClient client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
    }

    @Override
	protected int getScrollbarX() {
		return this.getX() + this.getWidth() - 6;
	}

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public void addCategory(String name) {
        this.addEntry(new CategoryEntry(name));
    }

    public void addButton(String name, ButtonWidget.PressAction onPress) {
        this.addEntry(new ButtonEntry(ButtonWidget.builder(Text.literal(name), onPress).dimensions(0, 0, 100, 20).build(), null, ""));
    }

    public void addButton(Option option, ButtonWidget.PressAction onPress) {
        this.addEntry(new ButtonEntry(ButtonWidget.builder(Text.literal(option.getValueAsString()), onPress).dimensions(0, 0, 100, 20).build(), option, ""));
    }

    public void addButton(Option option, ButtonWidget.PressAction onPress, String indent) {
        this.addEntry(new ButtonEntry(ButtonWidget.builder(Text.literal(option.getValueAsString()), onPress).dimensions(0, 0, 100, 20).build(), option, indent));
    }

    // Base entry
    public abstract static class Entry extends ElementListWidget.Entry<Entry> {}

    // Category header
    public static class CategoryEntry extends Entry {
        private final String name;

        public CategoryEntry(String name) {
            this.name = name;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int textX = getContentX() + getContentWidth() / 2;
            int textY = getContentY() + (getContentHeight() - textRenderer.fontHeight) / 2;
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable(this.name), textX, textY, 0xFFFFFFFF);
        }  

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }
    }

    // Button entry
    public static class ButtonEntry extends Entry{
        private final ButtonWidget button;
        private final String name;
        private final String description;
        private final String indent;
        private final Option option;

        public ButtonEntry(ButtonWidget button, Option option, String indent) {
            this.button = button;
            this.name = option.getName();
            this.description = option.getDescription();
            this.indent = indent;
            this.option = option;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.setY(this.getContentY() + (this.getContentHeight() - this.button.getHeight()) / 2);
            this.button.setX(this.getContentWidth() - this.button.getWidth() - 10);
            this.button.render(context, mouseX, mouseY, tickDelta);

            if(this.description == null) return;

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawText(textRenderer, Text.literal(indent + this.name), 10, this.getContentY() + (this.getContentHeight() - textRenderer.fontHeight) / 2, 0xFFFFFFFF, true);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.button);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.button);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubleClick) {
            if (this.button.mouseClicked(click, doubleClick)) {
                System.out.println(this.name + " clicked");
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                if(this.option != null){
                    this.button.setMessage(Text.literal(this.option.getValueAsString()));
                }
                return true;
            }
            return false;
        }
    }
}