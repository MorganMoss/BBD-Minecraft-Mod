package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import za.co.bbd.minecraft.backpack.BackpackItem;
import za.co.bbd.minecraft.items.RedstoneLinkerItem;
import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;
import static za.co.bbd.minecraft.registry.ModItemGroups.addToItemGroup;

public class ModItems {

    // Items
    // TODO: need to make a custom Item Class with an onUse of some kind
    // maybe it opens up a ui that accepts a code that you can match to a receivers
    // code
    // then will signal to activate receivers.
    // Alternatively, you can make there be an action on the transmitter that when
    // used on a receiver,
    // it handles that for you
    // https://fabricmc.net/wiki/tutorial:items

    // TODO: Needs a tooltip (could do the same for the receiver block)
    // https://fabricmc.net/wiki/tutorial:tooltip
    
    public static final Item BACKPACK = new BackpackItem(new Item.Settings().maxCount(1));

    public static final Item REDSTONE_LINKER_ITEM = registerItem(
            "redstone_linker_item",
            new RedstoneLinkerItem(new FabricItemSettings()),
            ItemGroups.REDSTONE, ModItemGroups.BBD);


    // Initializer
    public static void registerModItems() {
        LOGGER.info("Registering Mod Items for " + MOD_ID);
    }

    // Helper Methods
    // Todo, replace 'new Item(new FabricItemSettings())' with an item param, so
    // that you can have custom item classes
    private static Item registerItem(String name, Item item, ItemGroup... groups) {
        final Item registeredItem = Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, name),
                item);

        addToItemGroup(registeredItem, groups);

        return registeredItem;
    }
    
    public static void registerBackpackItem(){
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.BBD).register(content -> {
            content.add(BACKPACK);
        });

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "backpack"), BACKPACK);
    }
}
