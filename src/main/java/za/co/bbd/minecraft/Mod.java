package za.co.bbd.minecraft;

import net.fabricmc.api.ModInitializer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Mod implements ModInitializer {

    Logger logger = Logger.getLogger(Mod.class.getName());

    @Override
    public void onInitialize() {
        logger.log(Level.INFO, "Initializing Mod");
    }
}
