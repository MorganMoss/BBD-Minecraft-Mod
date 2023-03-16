package za.co.bbd.minecraft.misc;

import com.google.gson.JsonArray;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import za.co.bbd.minecraft.Mod;

import java.util.List;
import java.util.stream.Collectors;

public class ChatGPTEndpoint {

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

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

        Mod.LOGGER.info("ChatGPT Conversation: " + messagesJson);

        return Unirest.post(URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .body("{\"model\":\"gpt-3.5-turbo\",\"messages\":[" + messagesJson + "]}")
                .asJson();
    }

}
