package me.gravityio.creativeplus.api.custom;

import me.gravityio.creativeplus.lib.idk.DefaultInputListener;
import net.minecraft.nbt.NbtCompound;

public abstract class CustomHandler implements DefaultInputListener {

    private Runnable onCancel;
    private Runnable onComplete;

    protected void cancel() {
        this.onCancel.run();
    }

    protected void complete() {
        this.onComplete.run();
    }

    public abstract void transform(NbtCompound nbt);

    public abstract boolean isAdditive();

    public abstract void onActivated();

    public void onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public void onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}
