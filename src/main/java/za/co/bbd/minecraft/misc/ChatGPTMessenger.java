package za.co.bbd.minecraft.misc;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONException;
import net.minecraft.village.Merchant;
import za.co.bbd.minecraft.Mod;

import java.util.logging.Level;

public class ChatGPTMessenger {
    private static final String URL = "https://chatgpt-api.shn.hk/v1/";

    private static final String BASE_PROMPT =  "Assume you're a villager in Minecraft and a player is talking to you. Respond in 50 words or less.";

    private static final String END_PROMPT = "This is what the player says: ";

    public static String current_message = "'Say Hi to me OwO'";

//    public static void respond(String response, Merchant merchant){
//
//    }

    public static void respond(String response){
        current_message = "'I'm thinking...'";
        Thread thread = new Thread(()->{
            try {

                //TODO: Add an in-between prompt that specifies details about that villager.
                String content = BASE_PROMPT + END_PROMPT + response;

                HttpResponse<JsonNode> result = Unirest.post(URL)
                        .header("content-type", "application/json")
                        .body("{"
                                + "  \"model\": \"gpt-3.5-turbo\",\n"
                                + "  \"messages\": [{\"role\": \"user\", \"content\": \""
                                + content
                                + "\"}]\n"
                                + "}"
                        ).asJson();

                Mod.LOGGER.info(result.getBody().toPrettyString());

                current_message = result.getBody().getObject().getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").strip();
            } catch (JSONException | UnirestException | NullPointerException e){
                current_message = "'Sadness... My internet brain has left me... ;w;'";
                Mod.LOGGER.log(Level.SEVERE, "ChatGPT Service has failed.", e);
            }
        });
        thread.setName("ChatGPT Request");
        thread.start();
    }


}
