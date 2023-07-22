package me.gravityio.creativeplus.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Sizing;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.api.placement.ClientEntitySummonHandler;
import me.gravityio.creativeplus.entity.client.ClientEntity;
import me.gravityio.creativeplus.lib.owo.gui.EntityComponentButton;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * A Screen to List all Entities
 */
// TODO: Needs designing and planning
public class EntityMenuScreen extends BaseUIModelScreen<FlowLayout> {
    public EntityMenuScreen() {
        super(FlowLayout.class, DataSource.asset(new Identifier(CreativePlus.MOD_ID, "entity_menu")));
//        super(FlowLayout.class, DataSource.file("ui/entity_menu.xml"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        GridLayout entities = rootComponent.childById(GridLayout.class, "entities");
        int maxColumns = 5;
        int row = 0;
        int column = 0;

        List<EntityType<?>> types = Helper.getOrderedEntities(this.client);
        for (EntityType<?> type : types) {
            if (row * maxColumns + column >= 200) break;
            entities.child(createEntity(type), row, column);
            if (column == maxColumns - 1) {
                row++;
                column = 0;
            } else {
                column++;
            }
        }
    }

    private EntityComponentButton<?> createEntity(EntityType<?> type) {
        EntityComponentButton<? extends Entity> entityButton = new EntityComponentButton<>(Sizing.fixed(40), type, null);
        entityButton.tooltip(type.getName());
        entityButton.scaleToFit(true);

        if (type == EntityType.BLOCK_DISPLAY) {
            entityButton.scale(0.5f);
            var block = ((DisplayEntity.BlockDisplayEntity) entityButton.entity());
            block.setBlockState(Blocks.DIRT.getDefaultState());
        } else if (type == EntityType.ITEM_DISPLAY) {
            entityButton.scale(0.5f);
            var item = ((DisplayEntity.ItemDisplayEntity) entityButton.entity());
            item.setItemStack(Items.STONE_SWORD.getDefaultStack());
        } else if (type == EntityType.TEXT_DISPLAY) {
            entityButton.scale(0.5f);
            var text = ((DisplayEntity.TextDisplayEntity) entityButton.entity());
            text.setText(Text.translatable("placeholder.entity_menu.text_display"));
        }

        entityButton.onPress(this::onClickEntity);
        return entityButton;
    }

    private void onClickEntity(EntityComponentButton<? extends Entity> entityComponentButton) {
        Entity screenEntity = entityComponentButton.entity().getType().create(this.client.world);
        screenEntity.copyFrom(entityComponentButton.entity());
        ClientEntity clientEntity = ClientEntity.create(screenEntity, screenEntity, new NbtCompound());
        EditEntityScreen editEntityScreen = new EditEntityScreen(clientEntity);
        editEntityScreen.setFinishButtonText(Text.translatable("button.creativeplus.create_entity"));
        editEntityScreen.onFinish((data) -> {
            var summonHandler = new ClientEntitySummonHandler(this.client);
            CreativePlus.setCurrentHandler(summonHandler);
            summonHandler.onFinish(CreativePlus::unsetCurrentHandler);
            summonHandler.setupEntity(screenEntity.getType(), clientEntity.getOutput());
            summonHandler.setMode(data.mode());
            summonHandler.start();
            editEntityScreen.close();
        });
        this.client.setScreen(editEntityScreen);
    }
}
