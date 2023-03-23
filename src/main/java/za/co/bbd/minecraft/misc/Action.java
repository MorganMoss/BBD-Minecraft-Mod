package za.co.bbd.minecraft.misc;

public enum Action {
    END_CHAT ("stop talking"),
    WAIT_REPLY ("wait for reply"),
    WAIT ("wait"),
    FOLLOW ("follow player"),
    STEAL ("steal from player"),
    GIFT ("give gift"),
    TRADE("trade"),
    REWARD ("give reward"),
    GIVE ("give something"),
    DISCOUNT ("discount"),
    RAISE_PRICE ("raise prices"),
    RUN_AWAY ("flee player"),
    DO_NOTHING ("do nothing"),
    CALL_IRON_GOLEM ("call guard"),
    LISTEN ("listen"),
    QUEST ("give quest");
    public final String action;

    Action(String action) {
        this.action = action;
    }
}
