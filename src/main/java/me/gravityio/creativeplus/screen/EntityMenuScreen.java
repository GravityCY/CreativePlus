package me.gravityio.creativeplus.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Sizing;
import me.gravityio.creativeplus.gui.EntityComponentButton;
import me.gravityio.creativeplus.lib.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.List;

/**
 * A Screen to List all Entities
 */
// TODO: Needs designing and planning
public class EntityMenuScreen extends BaseUIModelScreen<FlowLayout> {

    public EntityMenuScreen() {
//        super(FlowLayout.class, DataSource.asset(new Identifier(CreativePlus.MOD_ID, "entity_menu")));
        super(FlowLayout.class, DataSource.file("ui/entity_menu.xml"));
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
            EntityComponentButton<? extends Entity> entityButton = new EntityComponentButton<>(Sizing.fixed(40), type, null);
            entityButton.scaleToFit(true);
            entityButton.onPress(a -> {
                EntityScreen.clean();
                this.client.setScreen(EntityScreen.open(this.client, type));
            });
            entities.child(entityButton, row, column);
            if (column == maxColumns - 1) {
                row++;
                column = 0;
            } else {
                column++;
            }
        }
    }

    @Override
    public void close() {
        super.close();
        if (EntityScreen.CURRENT != null)
            EntityScreen.CURRENT.getHandler().setDelay(100);
    }
}
