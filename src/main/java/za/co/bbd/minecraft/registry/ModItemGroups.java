package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;

import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;

public class ModItemGroups {
    // Item Groups
    public static ItemGroup BBD;

    // Initializer
    public static void registerModItemGroups() {
        LOGGER.info("Registering Mod Item Groups for " + MOD_ID);
        BBD = FabricItemGroup
                .builder(new Identifier(MOD_ID, "bbd"))
                .displayName(Text.translatable("itemgroup.bbd"))
                // TODO: Change this to a custom icon rather than the icon of an item
                .icon(() -> new ItemStack(ModItems.REDSTONE_LINKER_ITEM))
                .build();
    }

    // Helper Methods (Package Private)
    static void addToItemGroup(Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));

    }

    static void addToItemGroup(Item item, ItemGroup... groups) {
        Arrays.stream(groups).forEach(itemGroup -> addToItemGroup(item, itemGroup));
    }
}
