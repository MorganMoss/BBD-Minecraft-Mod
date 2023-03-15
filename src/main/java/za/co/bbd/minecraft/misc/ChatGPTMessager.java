package za.co.bbd.minecraft.misc;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;

public class ChatGPTMessager {
    public static String current_message = "I have added text OWO. Omg this line is so long, what happens now >w<?! Yeet rawr uwu";

    public static HttpResponse<JsonNode> respond(String response){

        HttpResponse<JsonNode> result = Unirest.post("https://chatgpt-api.shn.hk/v1/")
                .header("content-type", "application/json")
                .body("{"
                        + "  \"model\": \"gpt-3.5-turbo\",\n"
                        + "  \"messages\": [{\"role\": \"user\", \"content\": \""
                        + response
                        + "\"}]\n"
                        + "}"
                ).asJson();

        try {
            current_message = result.getBody().getObject().getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (JSONException e){}

        return result;
    }


}
