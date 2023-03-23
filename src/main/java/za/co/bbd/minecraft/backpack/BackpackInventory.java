package za.co.bbd.minecraft.backpack;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.collection.DefaultedList;
import za.co.bbd.minecraft.database.Database;

public class BackpackInventory implements ImplementedInventory{
    private final ItemStack stack;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(54, ItemStack.EMPTY);

    public BackpackInventory(ItemStack stack, Database db) {
        this.stack = stack;
        NbtCompound tag = stack.getSubNbt("backpack");
        if (tag != null) {
            //Need to fetch from DB
            try {
                String name = stack.getName().getString();
                if (!name.equals("Backpack"))
                    tag.copyFrom(StringNbtReader.parse(db.getNbt(name)));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
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
