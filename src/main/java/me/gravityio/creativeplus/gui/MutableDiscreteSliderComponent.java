package me.gravityio.creativeplus.gui;

import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.core.Sizing;

/**
 * A Slider that reveals setting the min and max values. <br><br>
 * The OWO slider doesn't allow for that, afaik.
 */
public class MutableDiscreteSliderComponent extends DiscreteSliderComponent {
    public MutableDiscreteSliderComponent(Sizing horizontalSizing, double min, double max) {
        super(horizontalSizing, min, max);
    }

    public void min(double min) {
        super.min = min;
    }

    public void max(double max) {
        super.max = max;
    }

}
