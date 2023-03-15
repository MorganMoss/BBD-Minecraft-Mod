package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;

public class ModItems {

    //Items
    public static final Item REDSTONE_TRANSMITTER = registerItem(
            "redstone_transmitter",
            new Item(new FabricItemSettings())
    );


    //Helper Methods
    public static void addItemsToItemGroup(){
        addToItemGroup(ItemGroups.REDSTONE, REDSTONE_TRANSMITTER);
    }

    public static void registerModItems(){
        LOGGER.info("Registering Mod Items for " + MOD_ID);
        addItemsToItemGroup();
    }

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }

    private static void addToItemGroup(ItemGroup group, Item item){
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }



}
