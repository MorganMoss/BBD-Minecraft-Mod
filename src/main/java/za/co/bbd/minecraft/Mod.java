package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.bbd.minecraft.chat.ChatGPTEndpoint;
import za.co.bbd.minecraft.registry.ModBlocks;
import za.co.bbd.minecraft.registry.ModItemGroups;
import za.co.bbd.minecraft.registry.ModItems;


public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Mod.class.getName());
    public static final String MOD_ID = "bbd";

    @Override
    public void onInitialize() {
        LOGGER.info( "Initializing Mod for " + MOD_ID);
        ModItemGroups.registerModItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ChatGPTEndpoint.initialize();
    }
}
