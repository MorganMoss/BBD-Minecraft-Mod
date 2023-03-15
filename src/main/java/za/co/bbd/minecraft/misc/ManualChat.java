package za.co.bbd.minecraft.misc;

import io.javalin.Javalin;
import za.co.bbd.minecraft.Mod;

public class ManualChat {

    public static void startManualChat(){
        Mod.LOGGER.warning("Starting Manual Chat - development only");
        var app = Javalin.create()
                .get("/{message}", ctx -> ctx.result(
                        ChatGPTMessager
                                .respond(ctx.pathParam("message"))
                                .getBody().toPrettyString()
                    )
                )
                .start();
    }

}
