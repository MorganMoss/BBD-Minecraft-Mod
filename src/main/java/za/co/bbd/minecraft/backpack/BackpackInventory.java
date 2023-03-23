package za.co.bbd.minecraft.backpack;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class BackpackInventory implements ImplementedInventory{
    private final ItemStack stack;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(54, ItemStack.EMPTY);

    public BackpackInventory(ItemStack stack) {
        this.stack = stack;
        NbtCompound tag = stack.getSubNbt("backpack");

        if (tag != null) {
            Inventories.readNbt(tag, items);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void markDirty() {
        NbtCompound tag = stack.getOrCreateSubNbt("backpack");
        Inventories.writeNbt(tag, items);
    }
}
