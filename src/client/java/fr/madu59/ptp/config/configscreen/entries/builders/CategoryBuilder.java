package fr.madu59.ptp.config.configscreen.entries.builders;

import java.util.function.BooleanSupplier;

import fr.madu59.ptp.config.configscreen.MyConfigListWidget;
import fr.madu59.ptp.config.configscreen.MyConfigListWidget.CategoryEntry;

public class CategoryBuilder extends AbstractEntryBuilder{

    public CategoryBuilder(MyConfigListWidget list, String name) {
        this.parent = list;
        this.name = name;
    }

    public CategoryBuilder isEnabled(BooleanSupplier supplier) {
        this.isEnabledSupplier = supplier; 
        return this;
    }

    public void build() {
        parent.addBuiltEntry(new CategoryEntry(parent, name, isEnabledSupplier));
    }
}
