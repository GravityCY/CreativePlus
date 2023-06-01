package me.gravityio.creativeplus.client.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import me.gravityio.creativeplus.client.ArmorStandScreenHandler;
import me.gravityio.creativeplus.owogui.ButtonList;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandScreen extends BaseUIModelScreen<FlowLayout> {

    private final ArmorStandScreenHandler handler;
    private ArmorStandEntity screenEntity;

    public ArmorStandScreen(ArmorStandScreenHandler handler) {
        super(FlowLayout.class, DataSource.file("my_screen.xml"));
        this.handler = handler;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        screenEntity = (ArmorStandEntity) rootComponent.childById(EntityComponent.class, "armor_stand").entity();

        TextBoxComponent nameTextBox = rootComponent.childById(TextBoxComponent.class, "display_name");
        CheckboxComponent invisibleCheckbox = rootComponent.childById(CheckboxComponent.class, "invisible");
        CheckboxComponent nameVisibleCheckbox = rootComponent.childById(CheckboxComponent.class, "custom_name_visible");
        CheckboxComponent noGravityCheckbox = rootComponent.childById(CheckboxComponent.class, "no_gravity");
        ButtonComponent placeButton = rootComponent.childById(ButtonComponent.class, "place");
        ButtonList modeButton = rootComponent.childById(ButtonList.class, "mode");

        modeButton.onChanged(index -> this.handler.setMode(ArmorStandScreenHandler.Mode.values()[index]));
        nameTextBox.onChanged().subscribe(this::onDisplayNameChanged);
        invisibleCheckbox.onChanged(this::onInvisibleChanged);
        nameVisibleCheckbox.onChanged(this::onNameVisibleChanged);
        noGravityCheckbox.onChanged(this::onNoGravityChanged);
        placeButton.onPress(this::onPlace);
    }



    private void onDisplayNameChanged(String newString) {
        Text text = Text.translatable(newString);
        screenEntity.setCustomName(text);
        if (handler.entity != null)
            handler.entity.setCustomName(text);
    }

    private void onInvisibleChanged(boolean invisible) {
        screenEntity.setInvisible(invisible);
        if (handler.entity != null)
            handler.entity.setInvisible(invisible);
    }

    private void onNameVisibleChanged(Boolean aBoolean) {
        screenEntity.setCustomNameVisible(aBoolean);
        if (handler.entity != null)
            handler.entity.setCustomNameVisible(aBoolean);
    }

    private void onNoGravityChanged(Boolean aBoolean) {
        screenEntity.setNoGravity(aBoolean);
        if (handler.entity != null)
            handler.entity.setNoGravity(aBoolean);
    }


    private void onPlace(ButtonComponent button) {
        this.close();
        handler.spawn(this.screenEntity);
    }

    @Override
    public void close() {
        super.close();
    }
}
