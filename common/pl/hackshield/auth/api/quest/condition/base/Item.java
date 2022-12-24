/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package pl.hackshield.auth.api.quest.condition.base;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import org.bukkit.inventory.ItemStack;

public class Item {
    @SerializedName(value="material")
    private String material;
    @SerializedName(value="type")
    private int type;

    private Item(String material, int type) {
        this.material = material;
        this.type = type;
    }

    public static Item fromBukkit(ItemStack block) {
        return new Item(block.getType().name(), block.getData().getData());
    }

    public String getMaterial() {
        return this.material;
    }

    public int getType() {
        return this.type;
    }

    public boolean isValid(Item item) {
        return Objects.equals(this.material, item.getMaterial()) && Objects.equals(this.type, item.getType());
    }
}

