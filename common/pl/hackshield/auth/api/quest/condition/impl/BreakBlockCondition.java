/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 */
package pl.hackshield.auth.api.quest.condition.impl;

import com.google.gson.annotations.SerializedName;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import pl.hackshield.auth.api.quest.condition.ConditionRegistry;
import pl.hackshield.auth.api.quest.condition.base.Condition;
import pl.hackshield.auth.api.quest.condition.base.Item;

public class BreakBlockCondition
extends Condition {
    @SerializedName(value="item")
    private Item item;
    @SerializedName(value="block")
    private pl.hackshield.auth.api.quest.condition.base.Block block;

    public BreakBlockCondition() {
        this.setEvent(ConditionRegistry.CONDITION_BY_CLASS.get(this.getClass()));
    }

    public void setBlock(Block block) {
        this.block = pl.hackshield.auth.api.quest.condition.base.Block.fromBukkit(block);
    }

    public void setItem(ItemStack itemStack) {
        this.item = Item.fromBukkit(itemStack);
    }

    public pl.hackshield.auth.api.quest.condition.base.Block getBlock() {
        return this.block;
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public boolean isValid(Condition event) {
        if (!super.isValid(event)) {
            return false;
        }
        if (!(event instanceof BreakBlockCondition)) {
            return false;
        }
        BreakBlockCondition e = (BreakBlockCondition)event;
        if (this.item != null && !this.item.isValid(e.getItem())) {
            return false;
        }
        return this.block == null || this.block.isValid(e.getBlock());
    }
}

