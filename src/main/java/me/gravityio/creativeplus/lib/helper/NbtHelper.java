package me.gravityio.creativeplus.lib.helper;

import me.gravityio.creativeplus.MyNbtElementVisitor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.List;

public class NbtHelper {

    /**
     * Gets a compound and splits it into multiple Strings that are less than 256 characters
     */
    public static List<String> splitCompoundIntoStrings(NbtCompound original, int size) {
        List<String> strings = new ArrayList<>();
        StringBuilder current = new StringBuilder("{");

        int i = 0;
        for (String key : original.getKeys()) {
            String temp = key + ":" + MyNbtElementVisitor.toString(original.get(key));
            if (temp.length() + current.length() >= size) {
                i = 0;
                current.append("}");
                strings.add(current.toString());
                current = new StringBuilder("{");
                if (temp.length() < size) {
                    if (i != 0) {
                        current.append(",").append(temp);
                    } else {
                        current.append(temp);
                    }
                }
            } else {
                if (i != 0) {
                    current.append(",").append(temp);
                } else {
                    current.append(temp);
                }
            }
            i++;
        }
        current.append("}");
        strings.add(current.toString());
        return strings;
    }

    public static List<NbtCompound> splitCompound(NbtCompound original, int size) {
        List<NbtCompound> nbts = new ArrayList<>();
        var keys = original.getKeys();
        int totalKeys = keys.size();

        int i = 0;
        NbtCompound current = new NbtCompound();
        int length = 0;
        for (String key : keys) {
            NbtElement elem = original.get(key);
            String str = key + ":" + MyNbtElementVisitor.toString(elem) + (i + 1 >= totalKeys ? "" : ",");
            if (length + str.length() >= size) {
                nbts.add(current);
                length = 0;
                current = new NbtCompound();
            }
            length += str.length();
            current.put(key, elem.copy());
            i++;
        }
                
        return nbts;
    }
}
