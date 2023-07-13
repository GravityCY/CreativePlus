package me.gravityio.creativeplus.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import me.gravityio.creativeplus.lib.ClientInputListenerRegistry;
import me.gravityio.creativeplus.gui.ButtonList;
import me.gravityio.creativeplus.handler.entity.ClientEntityPlacementHandler;
import me.gravityio.creativeplus.lib.EntityNbtHelper;
import me.gravityio.creativeplus.lib.TextHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

/**
 * The Current most likely temporary screen for when spawning an entity
 * @param <T>
 */
// TODO: Needs designing and planning
public class EntityScreen<T extends Entity> extends BaseUIModelScreen<FlowLayout> {
    public static EntityScreen<? extends Entity> CURRENT;
    private static final Text CANCEL_MESSAGE = Text.translatable("message.creativeplus.placement.cancel");
    private final ClientEntityPlacementHandler handler;
    private final NbtCompound nbt = new NbtCompound();
    private final EntityType<T> entityType;
    private T screenEntity;

    public static <T extends Entity> EntityScreen<T> open(MinecraftClient client, EntityType<T> type) {
        if (CURRENT == null)
            CURRENT = new EntityScreen<>(client, type);
        return (EntityScreen<T>) CURRENT;
    }

    public EntityScreen(MinecraftClient client, EntityType<T> entityType) {
//        super(FlowLayout.class, DataSource.asset(new Identifier(CreativePlus.MOD_ID, "entity_screen")));
        super(FlowLayout.class, DataSource.file("ui/entity_screen.xml"));

        this.entityType = entityType;
        this.handler = new ClientEntityPlacementHandler(client, entityType);
        ClientInputListenerRegistry.add(this, this.handler);

        this.handler.onPlace(() -> {
            this.handler.discard();
            CURRENT = null;
            this.client.player.sendMessage(Text.translatable("message.creativeplus.placement.success", TextHelper.getLimit(this.screenEntity.getName(), 20)), true);
            ClientInputListenerRegistry.remove(this);
        });
        this.handler.onPlaceError((error) -> {
            if (error == ClientEntityPlacementHandler.PlaceStatus.COMMAND_TOO_LONG) {
                this.client.player.sendMessage(Text.translatable("message.creativeplus.placement.command_too_long"), true);
            }
        });
        this.handler.onCancel(() -> {
            this.handler.discard();
            CURRENT = null;
            this.client.player.sendMessage(CANCEL_MESSAGE, true);
            ClientInputListenerRegistry.remove(this);
        });
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        screenEntity = entityType.create(this.client.world);
        FlowLayout entityHolder = rootComponent.childById(FlowLayout.class, "entity_holder");
        entityHolder
                .child(Components.entity(Sizing.fill(100), screenEntity).scaleToFit(true));

        TextBoxComponent nameTextBox = rootComponent.childById(TextBoxComponent.class, "display_name");
        CheckboxComponent invisibleCheckbox = rootComponent.childById(CheckboxComponent.class, "invisible");
        CheckboxComponent nameVisibleCheckbox = rootComponent.childById(CheckboxComponent.class, "custom_name_visible");
        CheckboxComponent noGravityCheckbox = rootComponent.childById(CheckboxComponent.class, "no_gravity");
        ButtonComponent placeButton = rootComponent.childById(ButtonComponent.class, "place");
        ButtonList modeButton = rootComponent.childById(ButtonList.class, "mode");

        modeButton.onChanged(index -> this.handler.setMode(ClientEntityPlacementHandler.Mode.values()[index]));
        nameTextBox.onChanged().subscribe(this::onDisplayNameChanged);
        invisibleCheckbox.onChanged(this::onInvisibleChanged);
        nameVisibleCheckbox.onChanged(this::onNameVisibleChanged);
        noGravityCheckbox.onChanged(this::onNoGravityChanged);
        placeButton.onPress(this::onPlace);
    }

    private void onDisplayNameChanged(String newString) {
        Text text = Text.translatable(newString);
        if (newString == null || newString.equals("")) {
            nbt.remove(EntityNbtHelper.Entity.CUSTOM_NAME);
        } else {
            nbt.putString(EntityNbtHelper.Entity.CUSTOM_NAME, Text.Serializer.toJson(text));
        }
        screenEntity.setCustomName(text);
        if (handler.entity != null)
            handler.entity.setCustomName(text);
    }

    private void onInvisibleChanged(boolean invisible) {
        screenEntity.setInvisible(invisible);
        if (invisible) {
            nbt.putBoolean(EntityNbtHelper.ArmorStand.INVISIBLE, invisible);
        } else {
            nbt.remove(EntityNbtHelper.ArmorStand.INVISIBLE);
        }
        if (handler.entity != null)
            handler.entity.setInvisible(invisible);
    }

    private void onNameVisibleChanged(boolean isNameVisible) {
        screenEntity.setCustomNameVisible(isNameVisible);
        if (isNameVisible) {
            nbt.putBoolean(EntityNbtHelper.Entity.CUSTOM_NAME_VISIBLE, true);
        } else {
            nbt.remove(EntityNbtHelper.Entity.CUSTOM_NAME_VISIBLE);
        }
        if (handler.entity != null)
            handler.entity.setCustomNameVisible(isNameVisible);
    }

    private void onNoGravityChanged(boolean isNoGravity) {
        screenEntity.setNoGravity(isNoGravity);
        if (isNoGravity) {
            nbt.putBoolean(EntityNbtHelper.Entity.NO_GRAVITY, true);
        } else {
            nbt.remove(EntityNbtHelper.Entity.NO_GRAVITY);
        }
        if (handler.entity != null)
            handler.entity.setNoGravity(isNoGravity);
    }

    private void onPlace(ButtonComponent button) {
        this.close();
        this.handler.setDelay(100);
        handler.startPlacing(nbt);
    }

    public static void clean() {
        if (CURRENT != null) {
            CURRENT.discard();
            CURRENT = null;
        }
    }

    public void discard() {
        this.handler.discard();;
    }

    @Override
    public void close() {
        super.close();
    }

    public ClientEntityPlacementHandler getHandler() {
        return this.handler;
    }
}
