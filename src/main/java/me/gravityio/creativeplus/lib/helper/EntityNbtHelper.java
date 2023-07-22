package me.gravityio.creativeplus.lib.helper;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
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

    public static class AbstractMinecart {
        public static String CUSTOM_DISPLAY_TILE_PRESENT = "CustomDisplayTile";
        public static String DISPLAY_STATE = "DisplayState";
        public static String DISPLAY_OFFSET = "DisplayOffset";
    }

    public static class ExperienceOrb {
        public static String HEALTH = "Health";
        public static String AGE = "Age";
        public static String VALUE = "Value";
        public static String COUNT = "Count";
    }

    public static class AreaEffectCloud {
        public static String AGE = "Age";
        public static String DURATION = "Duration";
        public static String WAIT_TIME = "WaitTime";
        public static String REAPPLICATION_DELAY = "ReapplicationDelay";
        public static String DURATION_ON_USE = "DurationOnUse";
        public static String RADIUS_ON_USE = "RadiusOnUse";
        public static String RADIUS_PER_TICK = "RadiusPerTick";
        public static String RADIUS = "Radius";
        public static String OWNER = "Owner";
        public static String PARTICLE = "Particle";
        public static String COLOR = "Color";
        public static String POTION = "Potion";
        public static String EFFECTS = "Effects";
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

        public static NbtCompound mergePose(NbtCompound nbt, EulerAngle head, EulerAngle body, EulerAngle leftArm, EulerAngle rightArm, EulerAngle leftLeg, EulerAngle rightLeg) {
            if (!DEFAULT_HEAD_ROTATION.equals(head)) {
                nbt.put("Head", head.toNbt());
            }
            if (!DEFAULT_BODY_ROTATION.equals(body)) {
                nbt.put("Body", body.toNbt());
            }
            if (!DEFAULT_LEFT_ARM_ROTATION.equals(leftArm)) {
                nbt.put("LeftArm", leftArm.toNbt());
            }
            if (!DEFAULT_RIGHT_ARM_ROTATION.equals(rightArm)) {
                nbt.put("RightArm", rightArm.toNbt());
            }
            if (!DEFAULT_LEFT_LEG_ROTATION.equals(leftLeg)) {
                nbt.put("LeftLeg", leftLeg.toNbt());
            }
            if (!DEFAULT_RIGHT_LEG_ROTATION.equals(rightLeg)) {
                nbt.put("RightLeg", rightLeg.toNbt());
            }
            return nbt;
        }

        public static NbtCompound poseToNbt(EulerAngle head, EulerAngle leftArm, EulerAngle body, EulerAngle rightArm, EulerAngle leftLeg, EulerAngle rightLeg) {
            return mergePose(new NbtCompound(), head, leftArm, body, rightArm, leftLeg, rightLeg);
        }

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

    public static class Display {
        public static final String BILLBOARD_NBT_KEY = "billboard";
    }

    public static class TextDisplay {
        public static final String TEXT = "text";
        private static final String LINE_WIDTH = "line_width";
        private static final String TEXT_OPACITY = "text_opacity";
        private static final String BACKGROUND = "background";
        private static final String SHADOW = "shadow";
        private static final String SEE_THROUGH = "see_through";
        private static final String DEFAULT_BACKGROUND = "default_background";
        private static final String ALIGNMENT = "alignment";

        public static void putText(NbtCompound nbt, Text text) {
            nbt.putString(TEXT, Text.Serializer.toJson(text));
        }

        public static void putLineWidth(NbtCompound nbt, int lineWidth) {
            nbt.putInt(LINE_WIDTH, lineWidth);
        }

        public static void putBackground(NbtCompound nbt, int background) {
            nbt.putInt(BACKGROUND, background);
        }

        public static void putTextOpacity(NbtCompound nbt, int opacity) {
            nbt.putByte(TEXT_OPACITY, (byte)opacity);
        }

        public static void putShouldCastShadow(NbtCompound nbt, boolean shouldCastShadow) {
            nbt.putBoolean(SHADOW, shouldCastShadow);
        }

        public static void putIsSeeThrough(NbtCompound nbt, boolean isSeeThrough) {
            nbt.putBoolean(SEE_THROUGH, isSeeThrough);
        }

        public static void putShouldUseDefaultBackground(NbtCompound nbt, boolean shouldUseDefaultBackground) {
            nbt.putBoolean(DEFAULT_BACKGROUND, shouldUseDefaultBackground);
        }

        public static void putAlignment(NbtCompound nbt, TextDisplayEntity.TextAlignment alignment) {
            TextDisplayEntity.TextAlignment.CODEC.encodeStart(NbtOps.INSTANCE, alignment).result().ifPresent(element -> nbt.put(ALIGNMENT, element));
        }

        public static Text getText(NbtCompound nbt) {
            return nbt.contains(TEXT) ? Text.Serializer.fromJson(nbt.getString(TEXT)) : null;
        }

        public static Integer getLineWidth(NbtCompound nbt) {
            return nbt.contains(LINE_WIDTH) ? nbt.getInt(LINE_WIDTH) : null;
        }

        public static Integer getBackground(NbtCompound nbt) {
            return nbt.contains(BACKGROUND) ? nbt.getInt(BACKGROUND) : null;
        }

        public static Byte getTextOpacity(NbtCompound nbt) {
            return nbt.contains(TEXT_OPACITY) ? nbt.getByte(TEXT_OPACITY) : null;
        }

        public static Boolean getShouldCastShadow(NbtCompound nbt) {
            return nbt.contains(SHADOW) ? nbt.getBoolean(SHADOW) : null;
        }

        public static Boolean getIsSeeThrough(NbtCompound nbt) {
            return nbt.contains(SEE_THROUGH) ? nbt.getBoolean(SEE_THROUGH) : null;
        }

        public static Boolean getShouldUseDefaultBackground(NbtCompound nbt) {
            return nbt.contains(DEFAULT_BACKGROUND) ? nbt.getBoolean(DEFAULT_BACKGROUND) : null;
        }

        public static TextDisplayEntity.TextAlignment getAlignment(NbtCompound nbt) {
            return nbt.contains(ALIGNMENT) ? TextDisplayEntity.TextAlignment.valueOf(nbt.getString(ALIGNMENT)) : null;
        }
    }

    public static NbtList toNbtList(float ... values) {
        NbtList nbtList = new NbtList();
        for (float f : values) {
            nbtList.add(NbtFloat.of(f));
        }
        return nbtList;
    }

    public static NbtList toNbtList(double ... values) {
        NbtList nbtList = new NbtList();
        for (double d : values) {
            nbtList.add(NbtDouble.of(d));
        }
        return nbtList;
    }
}
