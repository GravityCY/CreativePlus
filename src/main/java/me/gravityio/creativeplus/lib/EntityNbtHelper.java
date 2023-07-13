package me.gravityio.creativeplus.lib;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.EulerAngle;

/**
 * A Class with all the NBT keys of various entity types
 */
public class EntityNbtHelper {

//    ENTITY

    public static class Entity {
        public static String POS = "Pos";
        public static String MOTION = "Motion";
        public static String ROTATION = "Rotation";
        public static String FALL_DISTANCE = "FallDistance";
        public static String FIRE = "Fire";
        public static String AIR = "Air";
        public static String ON_GROUND = "OnGround";
        public static String INVULNERABLE = "Invulnerable";
        public static String PORTAL_COOLDOWN = "PortalCooldown";
        public static String UUID = "UUID";
        public static String CUSTOM_NAME = "CustomName";
        public static String CUSTOM_NAME_VISIBLE = "CustomNameVisible";
        public static String SILENT = "Silent";
        public static String GLOWING = "Glowing";
        public static String NO_GRAVITY = "NoGravity";
        public static String HAS_VISUAL_FIRE = "HasVisualFire";
        public static String TAGS = "Tags";
        public static String PASSENGERS = "Passengers";
    }

    public static class Living {

        public static final String HEALTH = "Health";
        public static final String HURT_TIME = "HurtTime";
        public static final String HURT_BY_TIMESTAMP = "HurtByTimestamp";
        public static final String DEATH_TIME = "DeathTime";
        public static final String ABSORPTION_AMOUNT = "AbsorptionAmount";
        public static final String ATTRIBUTES = "Attributes";
        public static final String ACTIVE_EFFECTS = "ActiveEffects";
        public static final String FALL_FLYING = "FallFlying";
        public static final String SLEEPING_X = "SleepingX";
        public static final String SLEEPING_Y = "SleepingY";
        public static final String SLEEPING_Z = "SleepingZ";
        public static final String BRAIN = "Brain";
    }

    public static class Mob {
        public static final String NO_AI = "NoAI";
        public static final String LEFT_HANDED = "LeftHanded";
        public static final String PERSISTENCE_REQUIRED = "PersistenceRequired";
        public static final String CAN_PICKUP_LOOT = "CanPickUpLoot";
        public static String ARMOR_ITEMS = "ArmorItems";
        public static String HAND_ITEMS = "HandItems";
    }

    public static class Passive {
        public static String AGE = "Age";
        public static String FORCED_AGE = "ForcedAge";
    }

    public static class Animal {
        public static String IN_LOVE = "InLove";
        public static String LOVE_CAUSE = "LoveCause";
    }

    public static class ExperienceOrb {
        public static String HEALTH = "Health";
        public static String AGE = "Age";
        public static String VALUE = "Value";
        public static String COUNT = "Count";
    }

    public static class Allay {
        public static String DUPLICATION_COOLDOWN = "DuplicationCooldown";
        public static String CAN_DUPLICATE = "CanDuplicate";
    }

    public static class ArmorStand {
        private static final EulerAngle DEFAULT_HEAD_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
        private static final EulerAngle DEFAULT_BODY_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
        private static final EulerAngle DEFAULT_LEFT_ARM_ROTATION = new EulerAngle(-10.0f, 0.0f, -10.0f);
        private static final EulerAngle DEFAULT_RIGHT_ARM_ROTATION = new EulerAngle(-15.0f, 0.0f, 10.0f);
        private static final EulerAngle DEFAULT_LEFT_LEG_ROTATION = new EulerAngle(-1.0f, 0.0f, -1.0f);
        private static final EulerAngle DEFAULT_RIGHT_LEG_ROTATION = new EulerAngle(1.0f, 0.0f, 1.0f);
        public static String INVISIBLE = "Invisible";
        public static String SMALL = "Small";
        public static String SHOW_ARMS = "ShowArms";
        public static String DISABLED_SLOTS = "DisabledSlots";
        public static String NO_BASEPLATE = "NoBasePlate";
        public static String MARKER = "Marker";
        public static String POSE = "Pose";

        public static NbtCompound getPoseNbt(ArmorStandEntity stand) {
            NbtCompound nbtCompound = new NbtCompound();
            if (!DEFAULT_HEAD_ROTATION.equals(stand.getHeadRotation())) {
                nbtCompound.put("Head", stand.getHeadRotation().toNbt());
            }
            if (!DEFAULT_BODY_ROTATION.equals(stand.getBodyRotation())) {
                nbtCompound.put("Body", stand.getBodyRotation().toNbt());
            }
            if (!DEFAULT_LEFT_ARM_ROTATION.equals(stand.getLeftArmRotation())) {
                nbtCompound.put("LeftArm", stand.getLeftArmRotation().toNbt());
            }
            if (!DEFAULT_RIGHT_ARM_ROTATION.equals(stand.getRightArmRotation())) {
                nbtCompound.put("RightArm", stand.getRightArmRotation().toNbt());
            }
            if (!DEFAULT_LEFT_LEG_ROTATION.equals(stand.getLeftLegRotation())) {
                nbtCompound.put("LeftLeg", stand.getLeftLegRotation().toNbt());
            }
            if (!DEFAULT_RIGHT_LEG_ROTATION.equals(stand.getRightLegRotation())) {
                nbtCompound.put("RightLeg", stand.getRightLegRotation().toNbt());
            }
            return nbtCompound;
        }
    }

    public static class IronGolem {
        public static String PLAYER_CREATED = "PlayerCreated";
    }

    public static class Angerable {
        public static final String ANGER_TIME = "AngerTime";
        public static final String ANGRY_AT = "AngryAt";
    }

    public static class InventoryOwner {
        public static String INVENTORY = "Inventory";
    }

    public static NbtList toNbtList(float ... values) {
        NbtList nbtList = new NbtList();
        for (float f : values) {
            nbtList.add(NbtFloat.of(f));
        }
        return nbtList;
    }


}
