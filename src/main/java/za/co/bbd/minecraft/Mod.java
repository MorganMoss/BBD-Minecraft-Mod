package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.bbd.minecraft.chat.ChatGPTEndpoint;
import za.co.bbd.minecraft.database.Database;
import za.co.bbd.minecraft.registry.ModBlocks;
import za.co.bbd.minecraft.registry.ModItemGroups;
import za.co.bbd.minecraft.registry.ModItems;


public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Mod.class.getName());
    public static final String MOD_ID = "bbd";
    Database db = new Database();

    //This should move but lets see how it goes
    

    @Override
    public void onInitialize() {
        LOGGER.info( "Initializing Mod for " + MOD_ID);
        ModItemGroups.registerModItemGroups();
        ModItems.registerModItems();
        ModItems.registerBackpackItem();
        ModBlocks.registerModBlocks();
        ChatGPTEndpoint.initialize();
    }
}
