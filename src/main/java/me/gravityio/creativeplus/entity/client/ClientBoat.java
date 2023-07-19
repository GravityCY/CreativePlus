package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.BoatFrame;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;

public class ClientBoat extends ClientEntity implements BoatFrame {
    private final BoatEntity boat;
    private final BoatEntity transform;

    private BoatEntity.Type type;

    public ClientBoat(BoatEntity entity, BoatEntity transform, NbtCompound realNbt) {
        super(entity, transform, realNbt);

        this.boat = entity;
        this.transform = transform;
        this.init();
    }

    private void init() {

    }

    @Override
    public BoatEntity.Type getType() {
        return this.type;
    }

    @Override
    public void setType(BoatEntity.Type type) {
        this.transform.setVariant(type);
        this.type = type;
    }
}
