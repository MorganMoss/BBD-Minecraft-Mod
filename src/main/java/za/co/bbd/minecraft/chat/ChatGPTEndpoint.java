package za.co.bbd.minecraft.chat;

import kong.unirest.*;
import com.google.gson.GsonBuilder;
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.misc.Message;

import java.util.List;
import java.util.stream.Collectors;

public class ChatGPTEndpoint {

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "";

    private static final String MODEL = "gpt-3.5-turbo";

    /**
     * Takes a list of messages as context to formulate the chatbots next response
     * @param messages
     * @return the chatbots response
     */
    public static HttpResponse<JsonNode> post(List<Message> messages){
        String messagesJson = messages
                .stream()
                .map(message -> "{\"role\":\"" + message.role().toString().toLowerCase() + "\", \"content\":\""+ message.content() + "\"}")
                .collect(Collectors.joining(","));

        String body = "{\"model\":\"" + MODEL + "\",\"messages\":[" + messagesJson + "]}";

        //TODO: Logging could be removed later if desired
        Mod.LOGGER.info(new GsonBuilder().setPrettyPrinting().create().toJson(body));

        RequestBodyEntity request = Unirest.post(URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .body(body);


        HttpResponse<JsonNode> response = request.asJson();

        Mod.LOGGER.info(response.getBody().toPrettyString());

        return response;
    }

}
