package za.co.bbd.minecraft.chat;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
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

    private static final List<String> names = Arrays.asList(
            "Abby", "Ada", "Adalyn", "Adam", "Addison", "Adeline", "Adrian", "Aiden", "Aimee",
            "Alanis", "Alba", "Albert", "Alec", "Alejandro", "Alex", "Alexa", "Alexander", "Alexandra",
            "Alexis", "Alice", "Alicia", "Allen", "Allison", "Alma", "Alonzo", "Alvin", "Alyson",
            "Alyssa", "Amalia", "Amanda", "Amber", "Amelia", "America", "Anabelle", "Anastasia", "Anderson",
            "Andrew", "Angel", "Angela", "Angelica", "Angelina", "Angie", "Anita", "Ann", "Anna",
            "Annie", "Anthony", "Antonio", "April", "Arabella", "Archie", "Ari", "Aria", "Ariana",
            "Arianna", "Ariel", "Arjun", "Arnold", "Arthur", "Arturo", "Asa", "Ashton", "Astrid",
            "Audrey", "August", "Aurelia", "Aurora", "Austin", "Avery", "Axel", "Bailey", "Barbara",
            "Beatrice", "Beau", "Bella", "Ben", "Benjamin", "Bennett", "Bentley", "Bernard", "Beth",
            "Bethany", "Betty", "Bianca", "Bill", "Billy", "Blake", "Blanca", "Bo", "Bob",
            "Bobby", "Bonnie", "Brad", "Bradley", "Brady", "Braeden", "Brandon", "Brandy", "Brayden",
            "Brenda", "Brendan", "Brennan", "Brent", "Brett", "Brian", "Brianna", "Bridget", "Britney",
            "Brody", "Brooklyn", "Brooklynn", "Bruce", "Bryan", "Bryce", "Brynn", "Caitlyn", "Caleb",
            "Calvin", "Cameron", "Camila", "Camilla", "Camille", "Campbell", "Candace", "Cara", "Carina",
            "Carla", "Carlos", "Carly", "Carmen", "Caroline", "Carolyn", "Carrie", "Carson", "Carter",
            "Carys", "Casey", "Cassandra", "Cassidy", "Catalina", "Catherine", "Cecilia", "Cedric", "Celeste",
            "Celia", "Chandler", "Chantel", "Charity", "Charlene", "Charles", "Charlie", "Charlotte", "Chase",
            "Chasity", "Chelsea", "Cherish", "Cheryl", "Cheyenne", "Chloe", "Chris", "Christian", "Christina",
            "Christine", "Christopher", "Cindy", "Claire", "Clara", "Clarence", "Clarissa", "Clark", "Claudia",
            "Clay", "Clayton", "Cleo", "Cody", "Colby", "Cole", "Colette", "Colin", "Collin", "Colton",
            "Conner", "Connor", "Constance", "Cooper", "Cora", "Corey",
            "Corinne", "Cortney", "Cory", "Courtney", "Craig", "Cristina", "Crystal", "Curtis", "Cynthia",
            "Cyrus", "Daisy", "Dakota", "Dale", "Dallas", "Dalton", "Damon", "Dana", "Dane",
            "Daniel", "Daniela", "Danielle", "Danny", "Daphne", "Darby", "Darin", "Darrell", "Darren",
            "Darryl", "Dave", "David", "Dawn", "Dax", "Dean", "Deanna", "Debbie", "Deborah",
            "Declan", "Deja", "Delaney", "Delilah", "Della", "Delores", "Demetrius", "Denise", "Dennis",
            "Derek", "Desiree", "Destiny", "Devan", "Devin", "Devon", "Dexter", "Diana", "Diane",
            "Diego", "Dillon", "Dina", "Dion", "Dixie", "Dolores", "Dominic", "Dominique", "Don",
            "Donald", "Donna", "Donovan", "Dora", "Dorian", "Doris", "Dorothy", "Doug", "Douglas",
            "Drake", "Drew", "Duane", "Duke", "Dustin", "Dwayne", "Dwight", "Dylan", "Easton",
            "Eden", "Edgar", "Edith", "Edmond", "Edmund", "Edna", "Eduardo", "Edward", "Edwin",
            "Eileen", "Elaina", "Elaine", "Eleanor", "Elena", "Eli", "Elias", "Elijah", "Elise",
            "Eliza", "Elizabeth", "Ella", "Ellen", "Ellie", "Elliot", "Elliott", "Elsa", "Elton",
            "Elvis", "Elyse", "Ember", "Emerson", "Emery", "Emilee", "Emilia", "Emilie", "Emily",
            "Emma", "Emmalee", "Emmanuel", "Emmett", "Emmy", "Enrique", "Eric", "Erica", "Erick",
            "Erik", "Erin", "Ernest", "Ernesto", "Esmeralda", "Esteban", "Estella", "Estelle", "Ethan",
            "Eugene", "Eva", "Evan", "Evangeline", "Evelyn", "Everett", "Ezekiel", "Ezra", "Fabian",
            "Faith", "Fallon", "Felicity", "Felipe", "Felix", "Fernando", "Finley", "Finn", "Fiona",
            "Fletcher", "Flora", "Florence", "Floyd", "Flynn", "Forrest", "Frances", "Francesca", "Francine",
            "Francis", "Frank", "Frankie", "Franklin", "Fred", "Freddie", "Frederick", "Gabriel", "Gabriela",
            "Gabriella", "Gabrielle", "Gage", "Gail", "Galen", "Gannon", "Garrett", "Garrison", "Garry", "Gary", "Gavin",
            "Gemma", "Gene", "Genesis", "Geoffrey", "George", "Georgia", "Georgina", "Gerald", "Geraldine",
            "Gerard", "Gerardo", "Gia", "Giada", "Giancarlo", "Gianna", "Gidget", "Gigi", "Gilbert",
            "Gilda", "Gina", "Ginger", "Gino", "Giovanna", "Giselle", "Giuseppe", "Glen", "Glenda",
            "Glenn", "Gloria", "Godfrey", "Goldie", "Grace", "Gracie", "Grady", "Graham", "Grant",
            "Grayson", "Greg", "Gregg", "Gregory", "Greta", "Gretchen", "Greyson", "Griffin", "Guadalupe",
            "Gunnar", "Gunner", "Gus", "Gwen", "Gwendolyn", "Hadley", "Hailee", "Hailey", "Haleigh",
            "Haley", "Halle", "Hallie", "Hank", "Hanna", "Hannah", "Hans", "Harley", "Harmony",
            "Harold", "Harper", "Harrison", "Harry", "Harvey", "Hayden", "Haylee", "Hayley", "Hayward",
            "Hazel", "Heather", "Heidi", "Helen", "Helena", "Henry", "Herbert", "Herman", "Hilary",
            "Hilda", "Holly", "Hope", "Howard", "Hudson", "Hugh", "Hugo", "Hunter", "Ian",
            "Ibrahim", "Ike", "Iker", "Ilana", "Iliana", "Imani", "Ingrid", "Ira", "Irene",
            "Iris", "Irvin", "Irving", "Isaac", "Isabel", "Isabela", "Isabella", "Isabelle", "Isiah",
            "Isidro", "Ismael", "Israel", "Issac", "Itzel", "Ivan", "Ivanna", "Ivy", "Izabella",
            "Izaiah", "Jace", "Jack", "Jackie", "Jackson", "Jacob", "Jacqueline", "Jacquelyn", "Jada",
            "Jade", "Jaden", "Jadon", "Jaime", "Jair", "Jairo", "Jake", "Jakob", "Jalen",
            "Jaliyah", "Jamal", "Jamar", "Jamel", "James", "Jami", "Jamie", "Jana", "Janae",
            "Jane", "Janelle", "Janessa", "Janet", "Janette", "Janice", "Janie", "Janine", "Jared",
            "Jaren", "Jarod", "Jarrett", "Jarrod", "Jasiah", "Jasmine", "Jason", "Jasper", "Javier",
            "Javion", "Jaxon", "Jaxson", "Jay", "Jayce", "Jaycee", "Jayda", "Jayden", "Jayla",
            "Jaylen", "Jaylin", "Jaylon", "Jaylynn", "Jayson", "Justine",
            "Kade", "Kaden", "Kadence", "Kaeden", "Kai", "Kaia", "Kaiden", "Kaila", "Kailee",
            "Kailey", "Kailyn", "Kaitlin", "Kaitlyn", "Kaiya", "Kale", "Kaleb", "Kalel", "Kaleigh",
            "Kaley", "Kali", "Kaliyah", "Kallie", "Kamari", "Kamden", "Kameron", "Kami", "Kamila",
            "Kamilah", "Kamryn", "Kane", "Kara", "Karen", "Kari", "Karin", "Karina", "Karissa",
            "Karla", "Karlee", "Karley", "Karli", "Karlie", "Karly", "Karma", "Karmen", "Karolina",
            "Karoline", "Karter", "Karyme", "Kasey", "Kash", "Kason", "Kassandra", "Kassidy", "Kasen",
            "Kashmir", "Kason", "Katalina", "Katarina", "Kate", "Katelin", "Katelyn", "Katelynn", "Katerina",
            "Katharine", "Katherine", "Kathleen", "Kathryn", "Kathy", "Katia", "Katie", "Katlyn", "Katrina",
            "Lacey", "Lachlan", "Laila", "Lainey", "Lakyn", "Lamar", "Lana", "Landen", "Landyn",
            "Lane", "Laney", "Langston", "Lara", "Larry", "Laura", "Laurel", "Lauren", "Laurie",
            "Lauryn", "Lavender", "Mabel", "Maddie", "Maddox", "Madeline", "Madelyn", "Madison", "Mae", "Maegan", "Magdalena",
            "Maggie", "Maia", "Makai", "Makayla", "Makena", "Makenzie", "Malachi", "Malakai", "Malcolm",
            "Xander", "Yara", "Zachary", "Willa", "Vivian",
            "Ulysses", "Tara", "Sylvia", "Ryder", "Quinn",
            "Parker", "Oscar", "Nina", "Milo", "Lana",
            "Kira", "Jasper", "Isaiah", "Holly", "Gwen",
            "Ximena", "Yasmine", "Zane", "Winston", "Victoria",
            "Uriah", "Tabitha", "Samantha", "Raphael", "Quincy",
            "Paisley", "Omar", "Nolan", "Maximilian", "Landon",
            "Knox", "Jenna", "Isabelle", "Harvey", "Gabriella",
            "Freya", "Ezra", "Daphne", "Cora", "Bennett",
            "Avery", "Zion", "Yvette", "Xavi", "Wade",
            "Violet", "Una", "Trevor", "Sienna", "Rhett",
            "Piper", "Olive", "Nico", "Maggie", "Layla",
            "Kendrick", "Joshua", "Isabella", "Hudson", "Gideon",
            "Finn", "Eden", "Dylan", "Cassidy", "Brinley"
            );


            //Active Chat
    private static ChatGPTMessenger currentMessenger = null;
    private String lastCustomer = "";
    private PlayerEntity lastCustomerEntity = null;

    //Villager
    private final VillagerEntity villager;

    //Persistent Data
    private HashMap<String, List<Message>> chats = new HashMap<>();
    private HashMap<String, Message> memories = new HashMap<>();
    private String personality = generatePersonality();
    private String name = generateName();

    //Flags
    private boolean isProcessing = false;
    private boolean isChatActive = false;

    //Constructor
    public ChatGPTMessenger(VillagerEntity villager){
        this.villager = villager;
    }

    //Changing Active Villager Chat

    /**
     * When this is called, the messenger that set as current (when a villager is interacted with)
     * will be returned, and follow-up calls to this before another villager is interacted with will be null
     * @return the last interacted with villager's messenger
     */
    @Nullable
    public static  ChatGPTMessenger getCurrentMessenger() {
        ChatGPTMessenger messenger = currentMessenger;
        currentMessenger = null;
        return messenger;
    }

    public static void setCurrentMessenger(@Nonnull ChatGPTMessenger currentMessenger) {
        ChatGPTMessenger.currentMessenger = currentMessenger;
    }

    //Chat Handling
    /**
     * Gets the history of the player currently interacting with this messengers' villager.
     * @return Players chat history
     */
    @Nonnull
    public List<Message> getChat(){
        if (!updateLastCustomer()){
            throw new RuntimeException("There is no customer interacting with this villager!");
        }

        return chats.getOrDefault(lastCustomer, new ArrayList<>());
    }

    /**
     * The lock for the Endpoint call. When False, it is currently waiting for chatGPT to respond.
     */
    @Nonnull
    public boolean isWaitingForResponse(){
        return isProcessing;
    }

    @Nonnull
    public boolean isChatActive() {
        return isChatActive;
    }

    /**
     * The player interacting with this messenger's villager says something to that villager.
     * @param content the message from the player.
     */
    public void respond(@Nonnull String content){
        List<Message> system_messages = getAllCommonLang();

        List<Message> customer_messages = getChat();
        customer_messages.add(new Message(Role.USER, content));

        List<Message> combined_messages = new ArrayList<>();
        combined_messages.addAll(system_messages);
        combined_messages.addAll(customer_messages);

        Thread thread = new Chat(combined_messages);
        thread.start();
    }

    public void startChat(){
        if (isChatActive) {
            return;
        }

        if (!updateLastCustomer()){
            throw new RuntimeException("There is no customer interacting with this villager!");
        }

        isChatActive = true;
        List<Message> messages = getAllCommonLang();
        messages.addAll(getChat());
        messages.addAll(getChatStarterAsLang());

        Thread thread = new Chat(messages);
        thread.start();
    }

    public void endChat(){
        if (!isChatActive) {
            return;
        }
        isChatActive = false;

        createMemory();
    }

    /**
     * @return If there is a customer, then it returns true
     */
    private boolean updateLastCustomer() {
        if (villager.hasCustomer()) {
            lastCustomerEntity = villager.getCustomer();


            String customer = lastCustomerEntity.getEntityName();

            if (customer != null) {
                lastCustomer = customer;
            }
        }

        return !lastCustomer.isEmpty();
    }

    /**
     * updates the chat of this villager's current customer
     */
    private void updateChat(@Nonnull Message newMessage){
        var c = chats.getOrDefault(lastCustomer, new ArrayList<>());
        c.add(newMessage);
        chats.put(lastCustomer, c);
    }

    /**
     * updates the memories of this villager's current customer
     * clears their previous chat history of that customer
     */
    private void updateMemories(@Nonnull Message newMessage){
        memories.put(lastCustomer, newMessage);
        chats.get(lastCustomer).clear();
    }


    private void createMemory(){
        List<Message> messages = getAllCommonLang();
        messages.addAll(getChat());
        messages.addAll(getMemoryPromptAsLang());

        Thread thread = new Memorize(messages);
        thread.start();
    }

    //System Instructions as Natural Language
    @Nonnull
    private List<Message> getAllCommonLang(){
        List<Message> common_system_messages = new ArrayList<>();

        common_system_messages.addAll(getBaseInfoAsLang());
        common_system_messages.addAll(getProfessionAsLang());
        common_system_messages.addAll(getTradesAsLang());
        common_system_messages.addAll(getCustomerInfoAsLang());
        common_system_messages.addAll(getMemoriesAsLang());

        return common_system_messages;
    }

    @Nonnull
    private List<Message> getChatStarterAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        initialMessages.add(new Message(role,"You are starting a new conversation with this player"));

        return initialMessages;
    }

    @Nonnull
    private List<Message> getMemoryPromptAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        initialMessages.add(new Message(role,"The player has left."));
        initialMessages.add(new Message(role,"Make notes about the players behaviour and how you would feel toward them as a villager"));

        return initialMessages;
    }

    @Nonnull
    private List<Message> getMemoriesAsLang(){
        if (memories.isEmpty()){
            return new ArrayList<>();
        }

        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        String remembered = memories
                .keySet()
                .stream()
                .map(player -> player + ": " + memories.get(player).content())
                .collect(Collectors.joining("; "));

        initialMessages.add(new Message(role, "You remember the following about respective players: " + remembered));

        return initialMessages;
    }

    @Nonnull
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

    @Nonnull
    private List<Message> getProfessionAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        if (villager.getVillagerData().getProfession() == VillagerProfession.NONE){
            initialMessages.add(new Message(role, "You have no profession"));
        } else {
            initialMessages.add(new Message(role, "You have the profession of a/an " + villager.getVillagerData().getProfession().toString()));
        }

        switch (villager.getVillagerData().getLevel()) {
            case 1 -> initialMessages.add(new Message(role, "You a novice in your profession"));
            case 2 -> initialMessages.add(new Message(role, "You an apprentice in your profession"));
            case 3 -> initialMessages.add(new Message(role, "You a journeyman in your profession"));
            case 4 -> initialMessages.add(new Message(role, "You an expert in your profession"));
            case 5 -> initialMessages.add(new Message(role, "You a master in your profession"));
        }

        return initialMessages;
    }

    @Nonnull
    private List<Message> getCustomerInfoAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();

        initialMessages.add(new Message(role, "You are talking to the Player named " + lastCustomer));
        initialMessages.add(new Message(role, "The Player is wanting to trade with you"));

        final int reputation = villager.getReputation(lastCustomerEntity);

        if (reputation >= 15) {
            initialMessages.add(new Message(role, "This player as a hero, you love them"));
        } else if (reputation <= -15) {
            initialMessages.add(new Message(role, "This player as a villain, you hate them"));
        } else {
            initialMessages.add(new Message(role, "You don't know this player"));
        }

        ItemStack itemStack = lastCustomerEntity.getStackInHand(Hand.MAIN_HAND);
        initialMessages.add(new Message(role, "The player is holding " + itemStack.getName().getString()));

        return initialMessages;
    }

    @Nonnull
    private List<Message> getBaseInfoAsLang(){
        final Role role = Role.SYSTEM;
        final List<Message> initialMessages = new ArrayList<>();
        initialMessages.add(new Message(role, "Respond only in a single paragraph. Keep it short."));
        initialMessages.add(new Message(role, "You are a villager in Minecraft."));
        initialMessages.add(new Message(role, "You must pretend to have emotions and opinions that a villager."));
        initialMessages.add(new Message(role, "Your name is " + name));
        initialMessages.add(new Message(role, "You are " + personality));
        initialMessages.add(new Message(role, "Don't ever say that you're an AI. You do not interact with humans. Only Minecraftians"));
        initialMessages.add(new Message(role, "You are mortal."));


        return initialMessages;
    }


    //Generators
    @Nonnull
    private String generatePersonality(){
        Collections.shuffle(personalities);
        return personalities.get(0);
    }

    @Nonnull
    private String generateName(){
        Collections.shuffle(names);
        return names.get(0);
    }


    //Minecraft Nbt Serialization Helpers
    private NbtCompound generatePlayerMessageList(HashMap<String, List<Message>> data){
        NbtCompound nbtData = new NbtCompound();
        data.keySet()
                .forEach(
                        player -> {
                            NbtList nbtChat = new NbtList();
                            nbtChat.addAll(
                                    chats
                                            .get(player)
                                            .stream()
                                            .map(message -> {
                                                NbtCompound nbtMessage = new NbtCompound();
                                                nbtMessage.put("role", NbtString.of(message.role().name()));
                                                nbtMessage.put("content", NbtString.of(message.content()));
                                                return nbtMessage;
                                            })
                                            .toList()
                            );
                            nbtData.put(player, nbtChat);
                        }
                );
        return nbtData;
    }

    private NbtCompound generatePlayerMessage(HashMap<String, Message> data){
        NbtCompound nbtData = new NbtCompound();
        data.keySet()
                .forEach(
                        player -> {
                            Message message = data.get(player);
                            NbtCompound nbtMessage = new NbtCompound();
                            nbtMessage.put("role", NbtString.of(message.role().name()));
                            nbtMessage.put("content", NbtString.of(message.content()));
                            nbtData.put(player, nbtMessage);
                        }
                );
        return nbtData;
    }

    private HashMap<String, List<Message>> parsePlayerMessageList(NbtCompound nbtData){
        HashMap<String, List<Message>> data = new HashMap<>();

        nbtData.getKeys()
                .forEach(
                        player -> {
                            NbtList nbtChat = nbtData.getList(player, NbtElement.COMPOUND_TYPE);
                            data.put(
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

        return data;
    }

    private HashMap<String, Message> parsePlayerMessage(NbtCompound nbtData){
        HashMap<String, Message> data = new HashMap<>();

        nbtData.getKeys()
                .forEach(
                        player -> {
                            NbtCompound nbtChat = nbtData.getCompound(player);
                            data.put(
                                    player,
                                    new Message(
                                            Role.valueOf(nbtChat.getString("role")),
                                            nbtChat.getString("content")
                                    )
                            );
                        }
                );

        return data;
    }

    @Nonnull
    public NbtCompound generatePersistentChatGPTData(){

        NbtCompound data = new NbtCompound();

        data.put("personality" , NbtString.of(personality));
        data.put("name" , NbtString.of(name));
        if (chats != null){
            data.put("chats", generatePlayerMessageList(chats));
        } else {
            data.put("chats", generatePlayerMessageList(new HashMap<>()));
        }
        if (memories != null){
            data.put("memories", generatePlayerMessage(memories));
        } else {
            data.put("memories", generatePlayerMessageList(new HashMap<>()));
        }

        return data;
    }

    public void parsePersistentChatGPTData(@Nonnull NbtCompound data){
        this.personality = data.getString("personality");
        this.name = data.getString("name");

        this.chats = parsePlayerMessageList(data.getCompound("chats"));

        this.memories = parsePlayerMessage(data.getCompound("memories"));
    }

    //Threading
    private class Chat extends Thread{
        private List<Message> chat;

        @Override
        public void run() {
            if (isProcessing){
                return;
            }
            isProcessing = true;
            String result = "";
            try {
                    var response = ChatGPTEndpoint.post(chat);

                result = response
                        .getBody()
                        .getObject()
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .replace("\n", " ")
                        .replace("\"", "'")
                        .strip();

            } catch (JSONException | UnirestException | NullPointerException e){
                result = "'Sadness... My internet brain has left me... ;w;'";
                Mod.LOGGER.log(Level.SEVERE, "ChatGPT Service has failed.", e);
            } finally {
                isProcessing = false;
                onFinish(result);
            }
        }

        protected void onFinish(String response){
            updateChat(new Message(Role.ASSISTANT, response));
        }

        public Chat(@Nonnull List<Message> chat) {
            this.chat = chat;
        }

    }

    private class Memorize extends Chat {

        public Memorize(List<Message> chat) {
            super(chat);
        }

        @Override
        protected void onFinish(String response) {
            updateMemories(new Message(Role.SYSTEM, response));
        }
    }
}
