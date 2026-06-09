package fr.madu59.ptp.config.configscreen.entries.builders;

import java.util.function.BooleanSupplier;

import fr.madu59.ptp.config.Option;
import fr.madu59.ptp.config.configscreen.MyConfigListWidget;
import fr.madu59.ptp.config.configscreen.MyConfigListWidget.SliderEntry;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class SliderBuilder<N extends Number> extends AbstractEntryBuilder{

    private AbstractSliderButton button;
    private Option<N> option;
    private String indent = "";
    private N min;
    private N max;
    private N step;

    @SuppressWarnings("unchecked")
    public SliderBuilder(MyConfigListWidget list, Option<N> option) {
        this.parent = list;
        this.option = option;
        this.name = option.getName();

        N sample = option.getDefaultValue();
        if (sample instanceof Integer) {
            this.min = (N) Integer.valueOf(0);
            this.max = (N) Integer.valueOf(100);
            this.step = (N) Integer.valueOf(1);
        } else if (sample instanceof Float) {
            this.min = (N) Float.valueOf(0.0f);
            this.max = (N) Float.valueOf(1.0f);
            this.step = (N) Float.valueOf(0.1f);
        } else {
            this.min = (N) Double.valueOf(0.0);
            this.max = (N) Double.valueOf(1.0);
            this.step = (N) Double.valueOf(0.1);
        }
    }

    public SliderBuilder<N> isEnabled(BooleanSupplier supplier) {
        this.isEnabledSupplier = supplier; 
        return this;
    }

    public SliderBuilder<N> indent(String indent) {
        this.indent = indent; 
        return this;
    }

    public SliderBuilder<N> indent() {
        this.indent = getDefaultIndent(); 
        return this;
    }

    public SliderBuilder<N> range(N min, N max) {
        this.min = min;
        this.max = max;
        return this;
    }

    public SliderBuilder<N> step(N step) {
        this.step = step;
        return this;
    }

    public void build() {
        double dMin = min.doubleValue();
        double dMax = max.doubleValue();
        double dCurrent = option.getValue().doubleValue();

        double initialPosition = (dMax <= dMin) ? 0 : (dCurrent - dMin) / (dMax - dMin);

        button = new AbstractSliderButton(0, 0, 100, 20, 
        Component.literal(option.getValue().toString()), initialPosition){

            @Override
            protected void updateMessage() {

                N value = option.getValue();

                String formattedValue;
                if (value instanceof Integer || value instanceof Long) {
                    formattedValue = String.format(java.util.Locale.ROOT, "%d", value);
                } else {
                    String stepStr = step.toString();
                    int decimalPlaces = 0;
                    if (stepStr.contains(".")) {
                        decimalPlaces = stepStr.length() - stepStr.indexOf('.') - 1;
                    }

                    String format = "%." + decimalPlaces + "f";
                    formattedValue = String.format(java.util.Locale.ROOT, format, option.getValue());
                }

                this.setMessage(Component.literal(formattedValue));
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void applyValue() {
                if (option.getValue() instanceof Integer) {

                    int imax = max.intValue();
                    int imin = min.intValue();
                    int istep = step.intValue();
                    int newValue = imin + Math.round((imax - imin) * (float)this.value / istep) * istep;
                    option.setValue((N)(Object) Math.round(newValue));

                } else if (option.getValue() instanceof Double) {
                    
                    double dmax = max.doubleValue();
                    double dmin = min.doubleValue();
                    double dstep = step.doubleValue();
                    double newValue = dmin + (double)Math.round((dmax - dmin) * this.value / dstep) * dstep;
                    option.setValue((N)(Object) newValue);

                } else if (option.getValue() instanceof Float) {

                    float fmax = max.floatValue();
                    float fmin = min.floatValue();
                    float fstep = step.floatValue();
                    float newValue = fmin + (float)Math.round((fmax - fmin) * (float)this.value / fstep) * fstep;
                    option.setValue((N)(Object) newValue);
                }
            }
        };
        parent.addBuiltEntry(new SliderEntry(parent, button, option, indent, isEnabledSupplier));
    }
}
