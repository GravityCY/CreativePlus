package me.gravityio.creativeplus;

import com.google.common.collect.Lists;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.NbtElementVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class MyNbtElementVisitor implements NbtElementVisitor {
    private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
    private final StringBuilder result = new StringBuilder();

    public static String toString(NbtElement element) {
        return new MyNbtElementVisitor().apply(element);
    }

    /**
     * {@return the stringified NBT {@code element}}
     */
    public String apply(NbtElement element) {
        element.accept(this);
        return this.result.toString();
    }


    @Override
    public void visitString(NbtString element) {
        this.result.append(NbtString.escape(element.asString()));
    }

    @Override
    public void visitByte(NbtByte element) {
        this.result.append(element.numberValue()).append('b');
    }

    @Override
    public void visitShort(NbtShort element) {
        this.result.append(element.numberValue()).append('s');
    }

    @Override
    public void visitInt(NbtInt element) {
        this.result.append(element.numberValue());
    }

    @Override
    public void visitLong(NbtLong element) {
        this.result.append(element.numberValue()).append('L');
    }

    @Override
    public void visitFloat(NbtFloat element) {
        this.result.append("%.2ff".formatted(element.floatValue()));
    }

    @Override
    public void visitDouble(NbtDouble element) {
        this.result.append("%.2fd".formatted(element.doubleValue()));
    }

    @Override
    public void visitByteArray(NbtByteArray element) {
        this.result.append("[B;");
        byte[] bs = element.getByteArray();
        for (int i = 0; i < bs.length; ++i) {
            if (i != 0) {
                this.result.append(',');
            }
            this.result.append(bs[i]).append('B');
        }
        this.result.append(']');
    }

    @Override
    public void visitIntArray(NbtIntArray element) {
        this.result.append("[I;");
        int[] is = element.getIntArray();
        for (int i = 0; i < is.length; ++i) {
            if (i != 0) {
                this.result.append(',');
            }
            this.result.append(is[i]);
        }
        this.result.append(']');
    }

    @Override
    public void visitLongArray(NbtLongArray element) {
        this.result.append("[L;");
        long[] ls = element.getLongArray();
        for (int i = 0; i < ls.length; ++i) {
            if (i != 0) {
                this.result.append(',');
            }
            this.result.append(ls[i]).append('L');
        }
        this.result.append(']');
    }

    @Override
    public void visitList(NbtList element) {
        this.result.append('[');
        for (int i = 0; i < element.size(); ++i) {
            if (i != 0) {
                this.result.append(',');
            }
            this.result.append(new MyNbtElementVisitor().apply(element.get(i)));
        }
        this.result.append(']');
    }

    @Override
    public void visitCompound(NbtCompound compound) {
        this.result.append('{');
        ArrayList<String> list = Lists.newArrayList(compound.getKeys());
        Collections.sort(list);
        for (String string : list) {
            if (this.result.length() != 1) {
                this.result.append(',');
            }
            this.result.append(MyNbtElementVisitor.escapeName(string)).append(':').append(new MyNbtElementVisitor().apply(compound.get(string)));
        }
        this.result.append('}');
    }

    @Override
    public void visitEnd(NbtEnd element) {
        this.result.append("END");
    }

    protected static String escapeName(String name) {
        if (SIMPLE_NAME.matcher(name).matches()) {
            return name;
        }
        return NbtString.escape(name);
    }
}
