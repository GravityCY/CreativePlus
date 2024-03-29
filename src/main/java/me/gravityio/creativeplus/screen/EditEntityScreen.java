package me.gravityio.creativeplus.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.api.nbt.pieces.NbtBoundedPiece;
import me.gravityio.creativeplus.api.nbt.pieces.NbtEnumPiece;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.entity.client.ClientEntity;
import me.gravityio.creativeplus.lib.owo.gui.*;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * A Screen to edit an entity, currently this is not supported for when
 * you're also placing an entity, since this only works on server sided entities
 */

public class EditEntityScreen extends BaseUIModelScreen<FlowLayout> {

    protected final Entity screenEntity;
    protected final ClientEntity clientEntity;
    private final Map<NbtPiece<?>, Component> elements = new HashMap<>();
    private OnFinish onFinish;
    private Text finishButtonText;

    public EditEntityScreen(ClientEntity clientEntity) {
        super(FlowLayout.class, DataSource.asset(new Identifier(CreativePlus.MOD_ID, "edit_entity")));
//        super(FlowLayout.class, DataSource.file("ui/edit_entity.xml"));
        this.clientEntity = clientEntity;
        this.screenEntity = this.clientEntity.getWriteEntity();
    }

    public void onFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
    }

    public void setTitleText(Text text) {
        // TODO: Implement
    }

    public void setFinishButtonText(Text text) {
        this.finishButtonText = text;
    }

    /**
     * Initially set's the UI to all the default values, until the server responds back with the NBT of this entity asynchronously<br><br>
     *
     * Creates the UI from the server provided NBT of the current entity,
     * when any of the inputs are changed the client entity adds it to an output {@link NbtCompound}
     * that will be sent back to the server to modify the entity
     */
    @Override
    protected void build(FlowLayout root) {
        var entityName = root.childById(LabelComponent.class, "entity_name");
        var entityRoot = root.childById(FlowLayout.class, "entity");

        entityName.text(this.screenEntity.getType().getName());

        var modeButton = root.childById(ButtonList.class, "mode");

        var finishButton = root.childById(ButtonComponent.class, "apply");
        finishButton.onPress((btn) -> {
            var mode = Mode.valueOf(modeButton.current().getString());
            var output = new OutputData(mode);
            this.onFinish.onFinish(output);
        });
        if (finishButtonText != null) {
            finishButton.setMessage(finishButtonText);
        }
        this.create(root);

        var entityComp = new ScrollableEntityComponent<>(Sizing.fill(50), this.screenEntity);
        entityComp.transform(s -> {
            entityComp.showNametag(entityComp.entity().isCustomNameVisible());
        });
        entityComp.sizing(Sizing.fill(80), Sizing.fill(55));
        entityComp.showNametag(this.screenEntity.isCustomNameVisible());
        entityComp.scaleToFit(true);
        entityComp.allowMouseRotation(true);
        entityRoot.child(entityComp);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    /**
     * Creates all the UI elements, sets their default values and their callbacks for when they are modified
     */
    private void create(FlowLayout root) {
        var inputFlow = root.childById(FlowLayout.class, "input_data");
        var checkboxRoot = root.childById(FlowLayout.class, "checkbox_data");
        var section = getCheckboxSection("Entity");
        var sectionRoot = section.root;
        var sectionContentRoot = section.contentRoot;

        for (NbtPiece<?> nbtPiece : clientEntity.getNbt()) {
            NbtPiece.Type type = nbtPiece.getType();

            Component comp = null;

            if (type == NbtPiece.Type.BOOLEAN) {
                var boolPiece = (NbtPiece<Boolean>) nbtPiece;
                var checkbox = getCheckbox(boolPiece.getName(), boolPiece.get());
                checkbox.checkbox.onChanged(boolPiece::set);
                sectionContentRoot.child(checkbox.root);
                comp = checkbox.checkbox;

            } else if (type == NbtPiece.Type.BYTE) {
                var bytePiece = (NbtPiece<Byte>) nbtPiece;
                var numberInput = getNumberInput(bytePiece.getName(), new NumberFieldComponent.WholeConverter(), -256, 256);
                numberInput.numberField.onNumberChanged().subscribe(v -> {
                    if (v == null) return;
                    setIfNotEqual(v.byteValue(), bytePiece);
                });
                inputFlow.child(numberInput.root);
                comp = numberInput.numberField;

            } else if (type == NbtPiece.Type.INT) {
                if (nbtPiece.isBounded()) {
                    var intPiece = (NbtBoundedPiece<Integer>) nbtPiece;
                    var slider = getNumberSlider(intPiece.getName(), intPiece.getMin(), intPiece.getMax(), intPiece.get(), 0);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual((int) v, intPiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {
                    var intPiece = (NbtPiece<Integer>) nbtPiece;
                    NumberInput<Integer> input = getNumberInput(intPiece.getName(), new NumberFieldComponent.WholeConverter(), null, null);
                    input.numberField.onNumberChanged().subscribe(v -> {
                        if (v == null) return;
                        setIfNotEqual(v, intPiece);
                    });
                    inputFlow.child(input.root);
                    comp = input.numberField;
                }

            } else if (type == NbtPiece.Type.SHORT) {
                if (nbtPiece.isBounded()) {
                    var shortPiece = (NbtBoundedPiece<Short>) nbtPiece;
                    var slider = getNumberSlider(shortPiece.getName(), shortPiece.getMin(), shortPiece.getMax(), shortPiece.get(), 0);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual((short) v, shortPiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {
                    var shortPiece = (NbtPiece<Short>) nbtPiece;
                    NumberInput<Integer> input = getNumberInput(shortPiece.getName(), new NumberFieldComponent.WholeConverter(), null,null);
                    input.numberField.onNumberChanged().subscribe(v -> {
                        if (v == null) return;
                        setIfNotEqual(v.shortValue(), shortPiece);
                    });
                    inputFlow.child(input.root);
                    comp = input.numberField;
                }

            } else if (type == NbtPiece.Type.FLOAT) {
                if (nbtPiece.isBounded()) {
                    var floatPiece = (NbtBoundedPiece<Float>) nbtPiece;
                    var slider = getNumberSlider(floatPiece.getName(), floatPiece.getMin(), floatPiece.getMax(), floatPiece.get(), 1);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual((float) v, floatPiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {
                    var floatPiece = (NbtPiece<Float>) nbtPiece;
                    NumberInput<Double> input = getNumberInput(floatPiece.getName(), new NumberFieldComponent.DecimalConverter(), null, null);
                    input.numberField.onNumberChanged().subscribe(v -> {
                        if (v == null) return;
                        setIfNotEqual(v.floatValue(), floatPiece);
                    });
                    inputFlow.child(input.root);
                    comp = input.numberField;
                }

            } else if (type == NbtPiece.Type.DOUBLE) {
                if (nbtPiece.isBounded()) {
                    var doublePiece = (NbtBoundedPiece<Double>) nbtPiece;
                    var slider = getNumberSlider(doublePiece.getName(), doublePiece.getMin(), doublePiece.getMax(), doublePiece.get(), 1);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual(v, doublePiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {
                    var doublePiece = (NbtPiece<Double>) nbtPiece;
                    NumberInput<Double> input = getNumberInput(doublePiece.getName(), new NumberFieldComponent.DecimalConverter(), null, null);
                    input.numberField.onNumberChanged().subscribe(v -> {
                        if (v == null) return;
                        setIfNotEqual(v, doublePiece);
                    });
                    inputFlow.child(input.root);
                    comp = input.numberField;
                }

            } else if (type == NbtPiece.Type.LONG) {
                if (nbtPiece.isBounded()) {
                    var longPiece = (NbtBoundedPiece<Long>) nbtPiece;
                    var slider = getNumberSlider(longPiece.getName(), longPiece.getMin(), longPiece.getMax(), longPiece.get(), 0);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual((long) v, longPiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {
                    // TODO: implement

                }

            } else if (type == NbtPiece.Type.STRING) {
                if (nbtPiece.isBounded()) {
                    var stringPiece = (NbtBoundedPiece<String>) nbtPiece;
                    var textbox = getTextBox(stringPiece.getName(), stringPiece.get(), (int) stringPiece.getMax());
                    textbox.textBox.onChanged().subscribe(v -> setIfNotEqual(v, stringPiece));
                    inputFlow.child(textbox.root);
                    comp = textbox.textBox;
                } else {
                    // TODO: implement

                }

            } else if (type == NbtPiece.Type.ENUM) {
                var enumPiece = (NbtEnumPiece) nbtPiece;
                var data = getEnumButton(enumPiece.getName(), enumPiece.getEnumClass());
                data.enumButton.onEnumChanged(enumPiece::set);
                inputFlow.child(data.root);
                comp = data.enumButton;
            } else if (type == NbtPiece.Type.TEXT) {
                if (nbtPiece.isBounded()) {
                    var textPiece = (NbtBoundedPiece<Text>) nbtPiece;
                    Text text = textPiece.get();
                    var textbox = getTextBox(textPiece.getName(), text == null ? "" : text.getString(), (int) textPiece.getMax());
                    textbox.textBox.onChanged().subscribe(s -> {
                        if (s.equals("")) {
                            textPiece.set(null);
                            return;
                        }

                        textPiece.set(Text.literal(s));
                    });
                    inputFlow.child(textbox.root);
                    comp = textbox.textBox;
                } else {
                    // TODO: implement
                    
                }

            }
            elements.put(nbtPiece, comp);
        }

        checkboxRoot.child(sectionRoot);
    }

    /**
     * Update's all the UI elements with the server provided NBT of the entity
     */
    public void updateNBT() {

        for (NbtPiece<?> nbtPiece : this.clientEntity.getNbt()) {
            NbtPiece.Type type = nbtPiece.getType();
            Component comp = this.elements.get(nbtPiece);

            if (type == NbtPiece.Type.BOOLEAN) {
                var boolPiece = (NbtPiece<Boolean>) nbtPiece;
                var checkbox = (CheckboxComponent) comp;
                checkbox.checked(boolPiece.get());

            } else if (type == NbtPiece.Type.BYTE) {
                var bytePiece = (NbtPiece<Byte>) nbtPiece;
                var numberField = (NumberFieldComponent<Integer>) comp;
                numberField.value((int) bytePiece.get());

            } else if (type == NbtPiece.Type.INT) {
                if (nbtPiece.isBounded()) {
                    var intPiece = (NbtBoundedPiece<Integer>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(intPiece.get());
                    slider.min(intPiece.getMin());
                    slider.max(intPiece.getMax());
                } else {
                    var intPiece = (NbtPiece<Integer>) nbtPiece;
                    var numberField = (NumberFieldComponent<Integer>) comp;
                    numberField.value(intPiece.get());
                }

            } else if (type == NbtPiece.Type.SHORT) {
                if (nbtPiece.isBounded()) {
                    var shortPiece = (NbtBoundedPiece<Short>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(shortPiece.get());
                    slider.min(shortPiece.getMin());
                    slider.max(shortPiece.getMax());
                } else {
                    var shortPiece = (NbtPiece<Integer>) nbtPiece;
                    var numberField = (NumberFieldComponent<Integer>) comp;
                    numberField.value(shortPiece.get());
                }
                
            } else if (type == NbtPiece.Type.FLOAT) {
                if (nbtPiece.isBounded()) {
                    var floatPiece = (NbtBoundedPiece<Float>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(floatPiece.get());
                    slider.min(floatPiece.getMin());
                    slider.max(floatPiece.getMax());
                } else {
                    var floatPiece = (NbtPiece<Float>) nbtPiece;
                    var numberField = (NumberFieldComponent<Float>) comp;
                    numberField.value(floatPiece.get());
                }

            } else if (type == NbtPiece.Type.DOUBLE) {
                if (nbtPiece.isBounded()) {
                    var doublePiece = (NbtBoundedPiece<Double>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(doublePiece.get());
                    slider.min(doublePiece.getMin());
                    slider.max(doublePiece.getMax());
                } else {
                    var doublePiece = (NbtPiece<Double>) nbtPiece;
                    var numberField = (NumberFieldComponent<Double>) comp;
                    numberField.value(doublePiece.get());
                }

            } else if (type == NbtPiece.Type.LONG) {
                if (nbtPiece.isBounded()) {
                    var longPiece = (NbtBoundedPiece<Integer>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(longPiece.get());
                    slider.min(longPiece.getMin());
                    slider.max(longPiece.getMax());
                } else {
                    var longPiece = (NbtPiece<Long>) nbtPiece;
                    var numberField = (NumberFieldComponent<Long>) comp;
                    numberField.value(longPiece.get());
                }

            } else if (type == NbtPiece.Type.STRING) {
                var stringPiece = (NbtPiece<String>) nbtPiece;
                var textbox = (TextBoxComponent) comp;
                textbox.text(stringPiece.get());

            } else if (type == NbtPiece.Type.ENUM) {
                var enumPiece = (NbtEnumPiece) nbtPiece;
                var enumButton = (EnumButtonList) comp;
                enumButton.setEnum(enumPiece.get());
            } else if (type == NbtPiece.Type.TEXT) {
                var textPiece = (NbtPiece<Text>) nbtPiece;
                var textbox = (TextBoxComponent) comp;
                Text text = textPiece.get();
                textbox.text(text == null ? null : text.getString());
            }
        }

    }

    private static <T> void setIfNotEqual(@Nullable T v, NbtPiece<T> piece) {
        if (!piece.get().equals(v))
            piece.set(v);
    }

    private static EnumButton getEnumButton(String name, Class<? extends Enum<?>> enumClass) {
        var root = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        root.horizontalAlignment(HorizontalAlignment.CENTER);
        root.verticalAlignment(VerticalAlignment.CENTER);
        root.gap(5);

        var label = Components.label(Text.translatable("label." + name));
        label.tooltip(Text.translatable("tooltip." + name));
        var enumButton = new EnumButtonList(enumClass);

        root.child(label);
        root.child(enumButton);

        return new EnumButton(root, label, enumButton);
    }
    private static <I extends Number> NumberInput<I> getNumberInput(String name, NumberFieldComponent.NumberConverter<I> converter, @Nullable I min, @Nullable I max) {
        var row = getInputLayoutRow();
        var rowFlow = row.root;
        var contentFlow = row.contentRoot;

        var labelFlow = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

        var numberInput = new NumberFieldComponent<>(Sizing.fill(100), converter);
        numberInput.min(min);
        numberInput.max(max);

        var label = Components.label(Text.translatable("label." + name));
        label.tooltip(Text.translatable("tooltip." + name));

        labelFlow.child(label);
        contentFlow.child(labelFlow);
        contentFlow.child(numberInput);

        return new NumberInput<>(rowFlow, label, numberInput);
    }
    private static Slider getNumberSlider(String name, double min, double max, double def, int decimals) {
        var row = getInputLayoutRow();
        var rowFlow = row.root;
        var contentFlow = row.contentRoot;

        var labelFlow = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

        var slider = new MutableDiscreteSliderComponent(Sizing.fill(100), min, max);
        var label = Components.label(Text.translatable("label." + name));
        label.tooltip(Text.translatable("tooltip." + name));
        slider.decimalPlaces(decimals);
        slider.setFromDiscreteValue(def);

        labelFlow.child(label);
        contentFlow.child(labelFlow);
        contentFlow.child(slider);

        return new Slider(rowFlow, label, slider);
    }
    private static TextBox getTextBox(String name, String def, int maxLength) {
        var row = getInputLayoutRow();
        var rowFlow = row.root;
        var contentFlow = row.contentRoot;

        var labelFlow = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

        var label = Components.label(Text.translatable("label." + name));
        label.tooltip(Text.translatable("tooltip." + name));
        var box = Components.textBox(Sizing.fill(100));
        box.setPlaceholder(Text.translatable("placeholder.edit_entity.empty_name").formatted(Formatting.GRAY));
        box.setMaxLength(maxLength);
        box.text(def);

        labelFlow.child(label);
        contentFlow.child(labelFlow);
        contentFlow.child(box);

        return new TextBox(rowFlow, label, box);
    }
    private static Checkbox getCheckbox(String name, boolean def) {
        FlowLayout layout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        layout.gap(3);
        layout.alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        var label = Components.label(Text.translatable("label."+name));
        label.tooltip(Text.translatable("tooltip."+name));
        var checkbox = Components.checkbox(Text.translatable(""));
        checkbox.checked(def);

        layout.child(label);
        layout.child(checkbox);

        return new Checkbox(layout, label, checkbox);
    }
    private static CheckboxSection getCheckboxSection(String name) {
        var root = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        root.horizontalAlignment(HorizontalAlignment.CENTER);
        root.gap(3);

        var contentRoot = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        contentRoot.gap(1);

        var labelRoot = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        labelRoot.horizontalAlignment(HorizontalAlignment.CENTER);
        labelRoot.surface(Surface.flat(0x99666666));
        labelRoot.padding(Insets.of(4));

        var label = Components.label(Text.literal(name));

        labelRoot.child(label);

        root.child(labelRoot);
        root.child(contentRoot);

        return new CheckboxSection(root, contentRoot);
    }
    private static Row getInputLayoutRow() {
        var flow = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        flow.horizontalAlignment(HorizontalAlignment.CENTER);
        flow.verticalAlignment(VerticalAlignment.CENTER);

        var contentFlow = Containers.verticalFlow(Sizing.fill(65), Sizing.content());
        contentFlow.horizontalAlignment(HorizontalAlignment.CENTER);
        contentFlow.verticalAlignment(VerticalAlignment.CENTER);

        flow.child(contentFlow);
        return new Row(flow, contentFlow);
    }

    public interface OnFinish {
        void onFinish(OutputData data);
    }

    public enum Mode {
        EXECUTE, COPY
    }

    public record CheckboxSection(FlowLayout root, FlowLayout contentRoot) {}
    public record Row(FlowLayout root, FlowLayout contentRoot) {}
    public record OutputData(Mode mode) {}
    public record NumberInput<T extends Number>(FlowLayout root, LabelComponent label, NumberFieldComponent<T> numberField) {}
    public record Slider(FlowLayout root, LabelComponent label, DiscreteSliderComponent slider) {}
    public record Checkbox(FlowLayout root, LabelComponent label, CheckboxComponent checkbox) {}
    public record TextBox(FlowLayout root, LabelComponent label, TextBoxComponent textBox) {}
    private record EnumButton(FlowLayout root, LabelComponent label, EnumButtonList enumButton) {}
}
