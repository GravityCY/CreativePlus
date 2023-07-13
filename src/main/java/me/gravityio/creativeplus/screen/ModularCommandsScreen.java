package me.gravityio.creativeplus.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;

/**
 * A Screen that will allow for multi line writing of callback commands instead of the cumbersome commands way.
 */
public class ModularCommandsScreen extends BaseUIModelScreen<FlowLayout> {
    protected ModularCommandsScreen() {
        super(FlowLayout.class, DataSource.file("ui/callback_commands.xml"));
    }

    @Override
    protected void build(FlowLayout rootComponent) {

    }
}
