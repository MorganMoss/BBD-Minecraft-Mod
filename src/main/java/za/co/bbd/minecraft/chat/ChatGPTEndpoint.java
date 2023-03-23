package za.co.bbd.minecraft.chat;

import com.google.common.io.Resources;
import kong.unirest.*;
import kong.unirest.json.JSONObject;
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.misc.Message;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ChatGPTEndpoint {
    private static final String URL;
    private static final String API_KEY;
    private static final String MODEL;
    private static final Boolean PRINT_JSON;

    static {
        Properties properties = new Properties();
        try (final InputStream reader = ChatGPTEndpoint.class.getResourceAsStream("chat.properties")){
            properties.load(reader);
        } catch (IOException | NullPointerException e) {
        }

        URL = properties.getProperty("url", "https://api.openai.com/v1/chat/completions");
        String key = properties.getProperty("api-key", "<insert api key>");
        MODEL = properties.getProperty("model", "gpt-3.5-turbo");
        PRINT_JSON = Boolean.parseBoolean(properties.getProperty("print-json", "false"));

        if (key == null || key.equals("<insert api key>")) {
            key  = "";
        }

        API_KEY = key;
    }

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
            if (PRINT_JSON){
                Mod.LOGGER.info(new JSONObject(body).toString(2));
            }

        RequestBodyEntity request = Unirest.post(URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .socketTimeout(6000)
                .body(body);

        for (int i = 0; i < 5; i++){
            try {
                HttpResponse<JsonNode> response = request.asJson();
                if (PRINT_JSON){
                     Mod.LOGGER.info(response.getBody().toPrettyString());
                }

                return response;
            } catch (UnirestException e) {
                if (i < 4){
                    Mod.LOGGER.info("Retrying...");
                } else {
                    Mod.LOGGER.error("ChatGPT Failed", e);
                }
            }
        }
        return null;
    }



    /**
     * This is run in Mod, so that the static initializer is run on start-up
     */
    public static void initialize() {
        Mod.LOGGER.info("Loaded Properties for ChatGPT Endpoint");
    }
}
