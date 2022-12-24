/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package pl.hackshield.auth.api.quest.condition.base;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Block {
    @SerializedName(value="material")
    private String material;
    @SerializedName(value="type")
    private int type;

    private Block(String material, int type) {
        this.material = material;
        this.type = type;
    }

    public static Block fromBukkit(org.bukkit.block.Block block) {
        return new Block(block.getType().name(), block.getData());
    }

    public String getMaterial() {
        return this.material;
    }

    public int getType() {
        return this.type;
    }

    public boolean isValid(Block block) {
        return Objects.equals(this.material, block.getMaterial()) && Objects.equals(this.type, block.getType());
    }
}

