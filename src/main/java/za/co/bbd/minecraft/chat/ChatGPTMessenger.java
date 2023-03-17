package za.co.bbd.minecraft.chat;

import kong.unirest.UnirestException;
import kong.unirest.json.JSONException;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerProfession;
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.misc.Message;
import za.co.bbd.minecraft.misc.Role;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ChatGPTMessenger {

    //Constants
    private static final List<String> personalities = new ArrayList<>(Arrays.asList(
            "Friendly", "Introverted", "Confident", "Creative", "Analytical",
            "Adventurous", "Empathetic", "Ambitious", "Outgoing", "Humorous",
            "Reserved", "Assertive", "Reflective", "Reliable", "Open-minded",
            "Arrogant", "Stubborn", "Neurotic", "Machiavellian", "Narcissistic"
    ));

    //Active Chat
    //TODO: Find a better way to do this
    private static ChatGPTMessenger currentMessenger = null;

    //Villager
    private final VillagerEntity villager;

    //Persistent Data
    private HashMap<String, List<Message>> messages;
    private String personality = generatePersonality();

    //Flags
    private boolean isProcessing = false;

    //Constructor
    public ChatGPTMessenger(VillagerEntity villager){
        messages  = new HashMap<>();
        this.villager = villager;
    }

    //Changing Active Villager Chat
    //TODO: Find a better way to do this
    public static ChatGPTMessenger getCurrentMessenger() {
        return currentMessenger;
    }

    public static void setCurrentMessenger(ChatGPTMessenger currentMessenger) {
        ChatGPTMessenger.currentMessenger = currentMessenger;
    }

    //Chat Handling

    /**
     * Gets the history of the player currently interacting with this messengers' villager.
     * @return Players chat history
     */
    public List<Message> getCurrentMessages(){
        PlayerEntity customer = villager.getCustomer();
        return messages.getOrDefault(customer.getEntityName(), new ArrayList<>());
    }

    /**
     * The lock for the Endpoint call. When False, it is currently waiting for chatGPT to respond.
     */
    public boolean isWaitingForResponse(){
        return isProcessing;
    }

    /**
     * The player interacting with this messenger's villager says something to that villager.
     * @param content the message from the player.
     */
    public void respond(String content){

        if (isProcessing){
            return;
        }

        isProcessing = true;

        Thread thread = new Thread(()->{

            try {
                List<Message> system_messages = new ArrayList<>();
                system_messages.addAll(getBaseInfoAsLang());
                system_messages.addAll(getProfessionAsLang());
                system_messages.addAll(getTradesAsLang());
                system_messages.addAll(getCustomerInfoAsLang());

                List<Message> customer_messages = getCurrentMessages();
                customer_messages.add(new Message(Role.USER, content));

                List<Message> combined_messages = new ArrayList<>();
                combined_messages.addAll(system_messages);
                combined_messages.addAll(customer_messages);

                var response = ChatGPTEndpoint.post(combined_messages);
                Mod.LOGGER.info(response.getBody().toPrettyString());

                String result = response
                        .getBody()
                        .getObject()
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .replace("\n", " ")
                        .replace("\"", "'")
                        .strip();

                Message message = new Message(Role.ASSISTANT, result);

                var current_messages = getCurrentMessages();
                current_messages.add(message);

                this.messages.put(villager.getCustomer().getEntityName(), current_messages);

                current_messages.stream().map(message1 -> message1.content()).forEach(Mod.LOGGER::info);
            } catch (JSONException | UnirestException | NullPointerException e){
                getCurrentMessages().add(new Message(Role.ASSISTANT,"'Sadness... My internet brain has left me... ;w;'"));
                this.isProcessing = true; //forever :(
                this.messages.put(villager.getCustomer().getEntityName(), getCurrentMessages());
                Mod.LOGGER.log(Level.SEVERE, "ChatGPT Service has failed.", e);
            } finally {
                isProcessing = false;
            }
        });
        thread.setName("ChatGPT Request");
        thread.start();
    }

    //System Instructions as Natural Language
    private List<Message> getTradesAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        String trades = villager
                .getOffers()
                .stream()
                .map(tradeOffer ->
                        tradeOffer.getAdjustedFirstBuyItem().getCount()
                        + " " +tradeOffer.getAdjustedFirstBuyItem().getName().getString()
                        + (
                            (!tradeOffer.getSecondBuyItem().isOf(Items.AIR)) ?
                                    " and "
                                    + tradeOffer.getSecondBuyItem().getCount()
                                    + " "
                                    + tradeOffer.getSecondBuyItem().getName().getString()
                            :
                                    ""
                        )
                        + " in return for "
                        + tradeOffer.getSellItem().getName().getString()
                )
                .collect(Collectors.joining("; "));

        initialMessages.add(new Message(role,"You have the following trades: " + trades));

        return initialMessages;
    }

    private List<Message> getProfessionAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        if (villager.getVillagerData().getProfession() == VillagerProfession.NONE){
            initialMessages.add(new Message(role, "You have no profession"));
        } else {
            initialMessages.add(new Message(role, "You have the profession of a/an " + villager.getVillagerData().getProfession().toString()));
        }

        switch (villager.getVillagerData().getLevel()){
            case 1: initialMessages.add(new Message(role, "You a novice in your profession")); break;
            case 2: initialMessages.add(new Message(role, "You an apprentice in your profession")); break;
            case 3: initialMessages.add(new Message(role, "You a journeyman in your profession")); break;
            case 4: initialMessages.add(new Message(role, "You an expert in your profession")); break;
            case 5: initialMessages.add(new Message(role, "You a master in your profession")); break;
        }

        return initialMessages;
    }

    private List<Message> getCustomerInfoAsLang(){
        PlayerEntity player = villager.getCustomer();
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        initialMessages.add(new Message(role, "You are talking to the Player named " + player.getEntityName()));
        initialMessages.add(new Message(role, "The Player is wanting to trade with you"));

        final int reputation = villager.getReputation(villager.getCustomer());

        if (reputation >= 15) {
            initialMessages.add(new Message(role, "This player as a hero, you love them"));
        } else if (reputation <= -15) {
            initialMessages.add(new Message(role, "This player as a villain, you hate them"));
        } else {
            initialMessages.add(new Message(role, "You don't know this player"));
        }

        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        initialMessages.add(new Message(role, "The player is holding " + itemStack.getName().getString()));

        return initialMessages;
    }

    private List<Message> getBaseInfoAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();
        initialMessages.add(new Message(role, "Respond only in a single paragraph."));
        initialMessages.add(new Message(role, "You are a villager in Minecraft. You must pretend to have emotions and opinions that a villager would have. "));
        initialMessages.add(new Message(role, "You are " + personality));
        initialMessages.add(new Message(role, "You have a name of your choosing"));

        return initialMessages;
    }

    //Generators
    private String generatePersonality(){
        Collections.shuffle(personalities);
        return personalities.get(0);
    }


    //Minecraft Nbt Serialization Helpers
    public NbtCompound generatePersistentChatGPTData(){

        NbtCompound data = new NbtCompound();
        data.put("personality" , NbtString.of(personality));


        NbtCompound nbtPlayerChats = new NbtCompound();
        messages
                .keySet()
                .stream()
                .forEach(
                        player -> {
                                NbtList nbtChat = new NbtList();
                                nbtChat.addAll(
                                        messages
                                                .get(player)
                                                .stream()
                                                .map(message -> {
                                                        NbtCompound nbtMessage = new NbtCompound();
                                                        nbtMessage.put("role", NbtString.of(message.role().name()));
                                                        nbtMessage.put("content", NbtString.of(message.content()));
                                                        return nbtMessage;
                                                })
                                                .collect(Collectors.toList())
                                );
                                nbtPlayerChats.put(player, nbtChat);
                        }
                );
        data.put("player_chats", nbtPlayerChats);


        return data;
    }
    public void parsePersistentChatGPTData(NbtCompound data){
        this.personality = data.getString("personality");

        NbtCompound nbtPlayerChats = data.getCompound("player_chats");
        nbtPlayerChats
                .getKeys()
                .stream()
                .forEach(
                        player -> {
                                NbtList nbtChat = nbtPlayerChats.getList(player, NbtElement.COMPOUND_TYPE);
                                messages.put(
                                        player,
                                        nbtChat
                                                .stream()
                                                .map(message -> new Message(
                                                        Role.valueOf(((NbtCompound) message).getString("role")),
                                                        ((NbtCompound) message).getString("content")
                                                ))
                                                .collect(Collectors.toList())
                                );

                        }
                );
    }
}
