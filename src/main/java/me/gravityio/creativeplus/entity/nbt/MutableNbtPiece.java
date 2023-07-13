package me.gravityio.creativeplus.entity.nbt;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MutableNbtPiece<T> implements NbtPiece<T>{

    String name;
    Type type;
    Supplier<T> getter;
    Consumer<T> setter;

    public MutableNbtPiece(Supplier<T> getter, Consumer<T> setter, Type type, String name) {
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        this.name = name;
    }

    public MutableNbtPiece<T> setName(String name) {
        this.name = name;
        return this;
    }

    public MutableNbtPiece<T> setType(Type type) {
        this.type = type;
        return this;
    }

    public MutableNbtPiece<T> setGetter(Supplier<T> getter) {
        this.getter = getter;
        return this;
    }

    public MutableNbtPiece<T> setSetter(Consumer<T> setter) {
        this.setter = setter;
        return this;
    }

    public Supplier<T> getGetter() {
        return getter;
    }

    public Consumer<T> getSetter() {
        return setter;
    }


    @Override
    public T get() {
        return this.getter.get();
    }

    @Override
    public void set(T o) {
        this.setter.accept(o);
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
