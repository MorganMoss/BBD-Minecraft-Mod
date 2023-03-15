package za.co.bbd.minecraft.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


import static za.co.bbd.minecraft.Mod.LOGGER;
import static za.co.bbd.minecraft.Mod.MOD_ID;
import static za.co.bbd.minecraft.registry.ModItemGroups.addToItemGroup;

public class ModItems {

    //Items
    public static final Item REDSTONE_TRANSMITTER_ITEM = registerItem(
            "redstone_transmitter_item",
            ItemGroups.REDSTONE, ModItemGroups.BBD
    );

    //Initializer
    public static void registerModItems(){
        LOGGER.info("Registering Mod Items for " + MOD_ID);
    }

    //Helper Methods
    private static Item registerItem(String name,  ItemGroup...groups){
        final Item registeredItem = Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, name),
                new Item(new FabricItemSettings())
        );

        addToItemGroup(registeredItem, groups);

        return registeredItem;
    }


}
