package fr.madu59.ptp.config.configscreen.entries.builders;

import java.util.function.BooleanSupplier;

import fr.madu59.ptp.config.Option;
import fr.madu59.ptp.config.configscreen.MyConfigListWidget;
import fr.madu59.ptp.config.configscreen.MyConfigListWidget.ButtonEntry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ButtonBuilder extends AbstractEntryBuilder{

    private Button button;
    private Option<?> option;
    private String indent = "";

    public ButtonBuilder(MyConfigListWidget list, Option<?> option) {
        this.parent = list;
        this.option = option;
        this.name = option.getName();
    }

    public ButtonBuilder isEnabled(BooleanSupplier supplier) {
        this.isEnabledSupplier = supplier; 
        return this;
    }

    public ButtonBuilder indent(String indent) {
        this.indent = indent; 
        return this;
    }

    public ButtonBuilder indent() {
        this.indent = getDefaultIndent(); 
        return this;
    }

    public void build() {
        button = Button.builder(Component.literal(this.option.getValueAsTranslatedString()), btn -> {option.setToNextValue();}).bounds(0, 0, 100, 20).build();
        parent.addBuiltEntry(new ButtonEntry(parent, button, option, indent, isEnabledSupplier));
    }
}
