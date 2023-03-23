package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.bbd.minecraft.backpack.BackpackItem;
import za.co.bbd.minecraft.chat.ChatGPTEndpoint;
import za.co.bbd.minecraft.registry.ModBlocks;
import za.co.bbd.minecraft.registry.ModItemGroups;
import za.co.bbd.minecraft.registry.ModItems;


public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Mod.class.getName());
    public static final String MOD_ID = "bbd";


    //This should move but lets see how it goes
    

    @Override
    public void onInitialize() {
        LOGGER.info( "Initializing Mod for " + MOD_ID);
        ModItemGroups.registerModItemGroups();
        ModItems.registerModItems();
        ModItems.registerBackpackItem();
        ModBlocks.registerModBlocks();
        ChatGPTEndpoint.initialize();
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://172.23.122.70:5433/postgres", "postgres", "postgres")) {}
        catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }
}
