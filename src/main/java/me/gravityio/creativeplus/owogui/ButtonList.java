package me.gravityio.creativeplus.owogui;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.text.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonList extends ButtonComponent {
    private final List<Text> list = new ArrayList<>();
    private Consumer<Integer> onChanged;
    private int index = 0;
    public ButtonList() {
        super(Text.empty(), (v) -> {});
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.next();
    }

    public void onChanged(Consumer<Integer> onChanged) {
        this.onChanged = onChanged;
    }

    public void next() {
        index++;
        index %= list.size();
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
