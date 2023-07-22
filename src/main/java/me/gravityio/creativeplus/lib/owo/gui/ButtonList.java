package me.gravityio.creativeplus.lib.owo.gui;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A Button List that allows for when clicking to just iterate through some options.
 */
public class ButtonList extends ButtonComponent {
    protected final List<Text> list = new ArrayList<>();
    protected int index = 0;
    private Consumer<Integer> onChanged;

    public ButtonList() {
        super(Text.empty(), (v) -> {});
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.clicked(mouseX, mouseY)) {
            return false;
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            this.next();
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
            this.back();
        } else {
            return false;
        }
        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
        this.onClick(mouseX, mouseY);
        return true;
    }

    public void onChanged(Consumer<Integer> onChanged) {
        this.onChanged = onChanged;
    }

    public Text current() {
        return list.get(index);
    }

    public void next() {
        this.next(1);
    }

    public void back() {
        this.next(-1);
    }

    protected void next(int increment) {
        var size = list.size();
        var temp = (index + increment) % size;
        temp = temp < 0 ? temp + size : temp;

        this.set(temp);
    }

    protected void set(int index) {
        this.index = index;
        super.setMessage(list.get(index));
        super.inflate(Size.zero());
        super.parent().layout(super.parent().fullSize());
        if (this.onChanged != null)
            this.onChanged.accept(index);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "entries", Function.identity(), this::parseAndApplyEntries);
        this.setMessage(this.list.get(0));
        this.inflate(Size.zero());
    }

    protected void parseAndApplyEntries(Element container) {
        for (var node : UIParsing.allChildrenOfType(container, Node.ELEMENT_NODE)) {
            var entry = (Element) node;
            this.list.add(UIParsing.parseText(entry));
        }
    }
}
