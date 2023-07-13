package me.gravityio.creativeplus.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import me.gravityio.creativeplus.MyNbtElementVisitor;
import me.gravityio.creativeplus.entity.nbt.NbtBoundedPiece;
import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.entity.ClientEntity;
import me.gravityio.creativeplus.gui.MutableDiscreteSliderComponent;
import me.gravityio.creativeplus.gui.NumberFieldComponent;
import me.gravityio.creativeplus.lib.ClientServerCommunication;
import me.gravityio.creativeplus.lib.NbtHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * A Screen to edit an entity, currently this is not supported for when
 * you're also placing an entity, since this only works on server sided entities
 */

// TODO: Design needs implementing
public class EditEntityScreen extends BaseUIModelScreen<FlowLayout> {

    private final Entity entity;
    private final CompletableFuture<NbtCompound> realNbt;
    private final ClientEntity clientEntity;
    private final Map<NbtPiece<?>, Component> elements = new HashMap<>();

    public EditEntityScreen(Entity entity) {
        super(FlowLayout.class, DataSource.file("ui/edit_entity.xml"));
        this.entity = entity;
        this.realNbt = ClientServerCommunication.getEntityNbt(this.entity.getUuid(), false);
        this.clientEntity = ClientEntity.create(entity, null);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var applyButton = rootComponent.childById(ButtonComponent.class, "apply");
        applyButton.onPress((v) -> this.onApply());
        createNbt(rootComponent);
    }

    /**
     * Gets the output of the client entity and sends it to the server with the data merge command
     */
    private void onApply() {
        NbtCompound out = clientEntity.getOutput();
        if (out.isEmpty()) {
            super.close();
            return;
        }

        String merged = MyNbtElementVisitor.toString(out);
        String format = "data merge entity %s %s";
        if (merged.length() < 256) {
            String command = format.formatted(entity.getUuidAsString(), merged);
            ClientServerCommunication.sendCommand(command, false);
        } else {
            List<String> split = NbtHelper.splitCompoundIntoStrings(out, 256);
            for (String s : split) {
                ClientServerCommunication.sendCommand(format.formatted(entity.getUuidAsString(), s), false);
            }
        }

        super.close();
    }

    /**
     * Initially set's the UI to all the default values, until the server responds back with the NBT of this entity asynchronously<br><br>
     *
     * Creates the UI from the server provided NBT of the current entity,
     * when any of the inputs are changed the client entity adds it to an output {@link NbtCompound}
     * that will be sent back to the server to modify the entity
     */
    private void createNbt(FlowLayout root) {
        this.realNbt.thenAccept(v -> {
            if (v == null) {
                this.client.player.sendMessage(Text.translatable("message.creativeplus.screen.edit.failed_to_get_nbt"), true);
                super.close();
                return;
            }
            this.clientEntity.setRealNbt(v);
            this.update();
        });

        this.create(root);
    }

    /**
     * Creates all the UI elements, sets their default values and their callbacks for when they are modified
     */
    private void create(FlowLayout root) {
        var entityRoot = root.childById(FlowLayout.class, "entity");
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
                checkbox.checkbox.onChanged(v -> setIfNotEqual(v, boolPiece));
                sectionContentRoot.child(checkbox.root);
                comp = checkbox.checkbox;

            } else if (type == NbtPiece.Type.INT) {
                if (nbtPiece.isBounded()) {
                    var intPiece = (NbtBoundedPiece<Integer>) nbtPiece;
                    var slider = getNumberSlider(intPiece.getName(), intPiece.getMin(), intPiece.getMax(), intPiece.get(), 0);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual((int) v, intPiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {
                    var intPiece = (NbtPiece<Integer>) nbtPiece;
                    NumberInput<Integer> input = getNumberInput(intPiece.getName(), new NumberFieldComponent.WholeConverter(), -24000, 24000);
                    input.numberField.onNumberChanged().subscribe(v -> {
                        setIfNotEqual(v, intPiece);
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

                }

            } else if (type == NbtPiece.Type.DOUBLE) {
                if (nbtPiece.isBounded()) {
                    var doublePiece = (NbtBoundedPiece<Double>) nbtPiece;
                    var slider = getNumberSlider(doublePiece.getName(), doublePiece.getMin(), doublePiece.getMax(), doublePiece.get(), 1);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual(v, doublePiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {

                }

            } else if (type == NbtPiece.Type.LONG) {
                if (nbtPiece.isBounded()) {
                    var longPiece = (NbtBoundedPiece<Long>) nbtPiece;
                    var slider = getNumberSlider(longPiece.getName(), longPiece.getMin(), longPiece.getMax(), longPiece.get(), 0);
                    slider.slider.onChanged().subscribe(v -> setIfNotEqual((long) v, longPiece));
                    inputFlow.child(slider.root);
                    comp = slider.slider;
                } else {

                }

            } else if (type == NbtPiece.Type.STRING) {
                if (nbtPiece.isBounded()) {
                    var stringPiece = (NbtBoundedPiece<String>) nbtPiece;
                    var textbox = getTextBox(stringPiece.getName(), stringPiece.get(), (int) stringPiece.getMax());
                    textbox.textBox.onChanged().subscribe(v -> setIfNotEqual(v, stringPiece));
                    inputFlow.child(textbox.root);
                    comp = textbox.textBox;
                } else {

                }

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
                }

            }
            elements.put(nbtPiece, comp);
        }

        var entityComp = Components.entity(Sizing.fill(50), entity.getType(), null);
        entityComp.sizing(Sizing.fill(80), Sizing.fill(55));
        entityComp.scaleToFit(true);
        entityRoot.child(entityComp);
        checkboxRoot.child(sectionRoot);
    }

    /**
     * Update's all the UI elements with the server provided NBT of the entity
     */
    private void update() {
        for (NbtPiece<?> nbtPiece : this.clientEntity.getNbt()) {
            NbtPiece.Type type = nbtPiece.getType();
            Component comp = this.elements.get(nbtPiece);

            if (type == NbtPiece.Type.BOOLEAN) {
                var boolPiece = (NbtPiece<Boolean>) nbtPiece;
                var checkbox = (CheckboxComponent) comp;
                checkbox.checked(boolPiece.get());

            } else if (type == NbtPiece.Type.INT) {
                if (nbtPiece.isBounded()) {
                    var intPiece = (NbtBoundedPiece<Integer>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(intPiece.get());
                    slider.min(intPiece.getMin());
                    slider.max(intPiece.getMax());
                } else {

                }

            } else if (type == NbtPiece.Type.FLOAT) {
                if (nbtPiece.isBounded()) {
                    var floatPiece = (NbtBoundedPiece<Float>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(floatPiece.get());
                    slider.min(floatPiece.getMin());
                    slider.max(floatPiece.getMax());
                }

            } else if (type == NbtPiece.Type.DOUBLE) {
                if (nbtPiece.isBounded()) {
                    var doublePiece = (NbtBoundedPiece<Double>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(doublePiece.get());
                    slider.min(doublePiece.getMin());
                    slider.max(doublePiece.getMax());
                }

            } else if (type == NbtPiece.Type.LONG) {
                if (nbtPiece.isBounded()) {
                    var longPiece = (NbtBoundedPiece<Integer>) nbtPiece;
                    var slider = (MutableDiscreteSliderComponent) comp;
                    slider.setFromDiscreteValue(longPiece.get());
                    slider.min(longPiece.getMin());
                    slider.max(longPiece.getMax());
                }

            } else if (type == NbtPiece.Type.STRING) {
                var stringPiece = (NbtPiece<String>) nbtPiece;
                var textbox = (TextBoxComponent) comp;
                textbox.text(stringPiece.get());

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

    private static <I extends Number> NumberInput<I> getNumberInput(String name, NumberFieldComponent.NumberConverter<I> converter, I min, I max) {
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
        box.setPlaceholder(Text.translatable("label.empty_name").formatted(Formatting.GRAY));
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

    public record CheckboxSection(FlowLayout root, FlowLayout contentRoot) {}
    public record Row(FlowLayout root, FlowLayout contentRoot) {}

    public record NumberInput<T extends Number>(FlowLayout root, LabelComponent label, NumberFieldComponent<T> numberField) {}
    public record Slider(FlowLayout root, LabelComponent label, DiscreteSliderComponent slider) {}
    public record Checkbox(FlowLayout root, LabelComponent label, CheckboxComponent checkbox) {}
    public record TextBox(FlowLayout root, LabelComponent label, TextBoxComponent textBox) {}
}
