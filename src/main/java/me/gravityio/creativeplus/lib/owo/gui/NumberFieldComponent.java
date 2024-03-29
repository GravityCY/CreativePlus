package me.gravityio.creativeplus.lib.owo.gui;

import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import org.jetbrains.annotations.Nullable;


// TODO: Decide what to do when the number in the text field is null, and
//  whether to keep passing null to the events or 0 etc.
/**
 * A Text Field but only allows for a certain number type, Whole or Decimal.
 */
public class NumberFieldComponent<T extends Number> extends TextBoxComponent {

    protected NumberConverter<T> converter;
    protected final EventStream<OnChanged<T>> changedEvents = OnChanged.newStream();

    public NumberFieldComponent(Sizing horizontalSizing, NumberConverter<T> converter) {
        super(horizontalSizing);
        this.converter = converter;
        super.setTextPredicate(this.converter::pass);
        super.onChanged().subscribe(s -> {
            T converted = this.converter.convert(s);
            changedEvents.sink().onChanged(converted);
        });
    }

    public void max(@Nullable T max) {
        this.converter.setMax(max);
        if (max != null) {
            super.setMaxLength(max.toString().length() + 1);
        }
    }

    public void min(@Nullable T min) {
        this.converter.min = min;
    }

    public void value(T value) {
        super.setText(String.valueOf(value));
    }

    /**
     * The Number returned is {@link Nullable @Nullable}, so make sure to check for that<br>
     * Because the text field can be empty which would be a null Number.
     * @return
     */
    public EventSource<OnChanged<T>> onNumberChanged() {
        return this.changedEvents.source();
    }

    public interface OnChanged<T> {
        void onChanged(@Nullable T type);
        static <T extends Number> EventStream<OnChanged<T>> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }

    public static class WholeConverter extends NumberConverter<Integer> {

        @Override
        public Integer convert(String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        boolean pass(String s) {
            boolean sp = super.pass(s);
            if (sp)
                return true;
            Integer v = this.convert(s);
            if (v == null)
                return false;
            if (max != null && v > max)
                return false;
            if (min != null && v < min)
                return false;
            return true;
        }
    }

    public static class DecimalConverter extends NumberConverter<Double> {

        @Override
        public Double convert(String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        boolean pass(String s) {
            boolean sp = super.pass(s);
            if (sp)
                return true;
            Double v = this.convert(s);
            if (v == null)
                return false;
            if (max != null && v > max)
                return false;
            if (min != null && v < min)
                return false;
            return true;
        }
    }

    public abstract static class NumberConverter<T extends Number> {
        @Nullable T max;
        @Nullable T min;

        abstract T convert(String s);
        boolean pass(String s) {
            return s.equals("") || s.equals("-");
        }
        void setMax(@Nullable T max) {
            this.max = max;
        }
        void setMin(@Nullable T min) {
            this.min = min;
        }
    }
}
