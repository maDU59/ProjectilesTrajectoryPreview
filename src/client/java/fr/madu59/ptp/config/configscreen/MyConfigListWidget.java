package fr.madu59.ptp.config.configscreen;

import java.util.List;
import java.util.function.BooleanSupplier;

import fr.madu59.ptp.config.Option;
import fr.madu59.ptp.config.configscreen.entries.builders.ButtonBuilder;
import fr.madu59.ptp.config.configscreen.entries.builders.CategoryBuilder;
import fr.madu59.ptp.config.configscreen.entries.builders.SliderBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class MyConfigListWidget extends ContainerObjectSelectionList<MyConfigListWidget.Entry> {

    public MyConfigListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
    }

    protected void update(){
        this.setScrollAmount(this.scrollAmount());
    }

    @Override
	protected int scrollBarX() {
		return this.getX() + this.getWidth() - 6;
	}

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public void addBuiltEntry(Entry entry) {
        this.addEntry(entry);
    }

    public CategoryBuilder category(String name){
        return new CategoryBuilder(this, name);
    }

    public ButtonBuilder button(Option<?> option){
        return new ButtonBuilder(this, option);
    }

    public <N extends Number> SliderBuilder<N> slider(Option<N> option){
        return new SliderBuilder<N>(this, option);
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<MyConfigListWidget.Entry> {

        protected MyConfigListWidget parent;
        protected BooleanSupplier isEnabledSupplier;
        private Boolean oldIsEnabled = null;

        public void updateState(){
            Boolean isEnabled = null;
            if(isEnabledSupplier != null) isEnabled = isEnabledSupplier.getAsBoolean();
            if(oldIsEnabled != isEnabled) {
                oldIsEnabled = isEnabled;
                parent.update();
            }
        }

        @Override
        public int getHeight() {
            updateState();
            if(this.isEnabledSupplier != null && !this.isEnabledSupplier.getAsBoolean()) return 0;
            return super.getHeight();
        }

        @Override
        public int getContentHeight() {
            updateState();
            if(this.isEnabledSupplier != null && !this.isEnabledSupplier.getAsBoolean()) return 0;
            return super.getContentHeight();
        }
    }

    public static class CategoryEntry extends MyConfigListWidget.Entry {
        private final String name;

        public CategoryEntry(MyConfigListWidget parent, String name, BooleanSupplier isEnabledSupplier) {
            this.isEnabledSupplier = isEnabledSupplier;
            this.parent = parent;
            this.name = name;
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if(this.isEnabledSupplier != null && !this.isEnabledSupplier.getAsBoolean()) return;
            Font textRenderer = Minecraft.getInstance().font;
            int textX = getContentX() + getContentWidth() / 2;
            int textY = getContentY() + (getContentHeight() - textRenderer.lineHeight) / 2;
            context.drawCenteredString(textRenderer, Component.translatable(this.name).withStyle(ChatFormatting.UNDERLINE), textX, textY, 0xFFFFFFFF);
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

    public static class ButtonEntry extends MyConfigListWidget.Entry{
        private final Button button;
        private final String name;
        private final String indent;
        private final Option<?> option;

        public ButtonEntry(MyConfigListWidget parent, Button button, Option<?> option, String indent, BooleanSupplier isEnabledSupplier) {
            this.parent = parent;
            this.isEnabledSupplier = isEnabledSupplier;
            this.button = button;
            this.name = option.getName();
            this.indent = indent;
            this.option = option;
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if(this.isEnabledSupplier != null && !this.isEnabledSupplier.getAsBoolean()) return;
            this.button.setY(this.getContentY() + (this.getContentHeight() - this.button.getHeight()) / 2);
            this.button.setX(this.getContentWidth() - this.button.getWidth() - 10);
            this.button.render(context, mouseX, mouseY, tickDelta);

            if(this.name == null) return;

            Font textRenderer = Minecraft.getInstance().font;
            context.drawString(textRenderer, Component.literal(indent + this.name), 10, this.getContentY() + (this.getContentHeight() - textRenderer.lineHeight) / 2, 0xFFFFFFFF, true);
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
        public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
            if (this.button.mouseClicked(click, doubleClick)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                if(this.option != null){
                    this.button.setMessage(Component.literal(this.option.getValueAsTranslatedString()));
                }
                return true;
            }
            return false;
        }
    }

    public static class SliderEntry extends MyConfigListWidget.Entry{
        private final AbstractSliderButton slider;
        private final String name;
        private final String indent;

        public SliderEntry(MyConfigListWidget parent, AbstractSliderButton slider, Option<?> option, String indent) {
            this(parent, slider, option, indent, () -> true);
        }

        public SliderEntry(MyConfigListWidget parent, AbstractSliderButton slider, Option<?> option, String indent, BooleanSupplier isEnabledSupplier) {
            this.parent = parent;
            this.isEnabledSupplier = isEnabledSupplier;
            this.slider = slider;
            this.name = option.getName();
            this.indent = indent;
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if(this.isEnabledSupplier != null && !this.isEnabledSupplier.getAsBoolean()) return;
            this.slider.setY(this.getContentY() + (this.getContentHeight() - this.slider.getHeight()) / 2);
            this.slider.setX(this.getContentWidth() - this.slider.getWidth() - 10);
            this.slider.render(context, mouseX, mouseY, tickDelta);

            if(this.name == null) return;

            Font textRenderer = Minecraft.getInstance().font;
            context.drawString(textRenderer, Component.literal(indent + this.name), 10, this.getContentY() + (this.getContentHeight() - textRenderer.lineHeight) / 2, 0xFFFFFFFF, true);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.slider);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.slider);
        }
    }
}