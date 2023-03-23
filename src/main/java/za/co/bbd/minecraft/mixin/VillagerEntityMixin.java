package za.co.bbd.minecraft.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import za.co.bbd.minecraft.Identifiers.ModIdentifiers;
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.chat.VillagerChat;
import za.co.bbd.minecraft.interfaces.VillagerActor;
import za.co.bbd.minecraft.interfaces.VillagerChatHolder;
import za.co.bbd.minecraft.misc.Action;

import java.util.Collections;
import java.util.List;


@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements VillagerActor, VillagerChatHolder {
    private static final double VISION_RADIUS = 100;

    //Injected Class Variables
    private final VillagerChat chat = world.isClient ? null : new VillagerChat((VillagerEntity) (Object) this);


    //Constructor
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


    //Injections
    @Inject(
            method = "beginTradeWith",
            at = @At("TAIL")
    )
    void additionalBeginTradeWith(PlayerEntity customer, CallbackInfo ci){
        if (!world.isClient) {
            if (!chat.isMemorizing()) {
                //Start chat when the client is able to receive
                chat.startChat();

                ServerPlayNetworking.registerGlobalReceiver(
                        new Identifier(ModIdentifiers.CHAT_PLAYER_IDENTIFIER + getCustomer().getUuidAsString()),
                        (server, player2, handler, buf, responseSender) -> chat.respond(buf.readString())
                );

                Mod.LOGGER.info("Started Listening on " + ModIdentifiers.CHAT_PLAYER_IDENTIFIER + getCustomer().getUuidAsString());
            }
        }
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    void additionalTick(CallbackInfo ci){
        if (chat == null){
            return;
        }
        if (chat.isChatting()){
            if (!this.hasCustomer()) {
                chat.endChat();
            }
        }
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("HEAD")
    )
    void writeChatGPTDataToNbt(NbtCompound nbt, CallbackInfo ci){
        nbt.put("bbd.chat_data", chat.generatePersistentChatGPTData());
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("HEAD")
    )
    void readChatGPTDataToNbt(NbtCompound nbt, CallbackInfo ci){
        NbtCompound chatData = nbt.getCompound("bbd.chat_data");
        if (!chatData.isEmpty()){
            chat.parsePersistentChatGPTData(chatData);
        }
    }


    //Custom Methods
    @Override
    public VillagerChat getChat() {
        return chat;
    }

    @Override
    public void performAction(Action action) {
        switch (action) {
            case END_CHAT -> endChat();
            case FOLLOW -> follow();
            case RUN_AWAY -> flee();
            case CALL_IRON_GOLEM -> callGolem();
            case WAIT_REPLY, LISTEN, WAIT, DO_NOTHING -> {}
            case STEAL -> {}
            case GIFT, GIVE, REWARD -> gift();
            case TRADE -> {}
            case DISCOUNT -> {}
            case RAISE_PRICE -> {}
            case QUEST -> {
            }
        }
    }

    private void wait(int seconds){
        long now = world.getTime();
        while (world.getTime() - now <= 20*seconds) {}
    }

    private void endChat(){
        Mod.LOGGER.info("Ending Chat");
        wait(2);
        chat.endChat();
        Mod.LOGGER.info("Chat Ended");
    }

    private void flee(){
        endChat();
        Mod.LOGGER.info("Fleeing Player");
        new Thread(() -> {
            Goal goal = new FleeEntityGoal<>(this, PlayerEntity.class, 20, 1.0, 1.2);
            goalSelector.add(1, goal);
            wait(60);
            goalSelector.remove(goal);
        }).start();

    }

    private void callGolem(){
        flee();
        Mod.LOGGER.info("Calling Iron Golem on " + chat.customer.getEntityName());
        List<Entity> entities = world.getOtherEntities(this, new Box(this.getPos().subtract(VISION_RADIUS, VISION_RADIUS, VISION_RADIUS), this.getPos().add(VISION_RADIUS, VISION_RADIUS, VISION_RADIUS)));
        entities
                .stream()
                .filter(entity -> entity.getType().equals(EntityType.IRON_GOLEM))
                .map(entity -> (IronGolemEntity) entity)
                .forEach(ironGolemEntity -> {
                    ironGolemEntity.tryAttack(chat.customer);
                    ironGolemEntity.setAngryAt(chat.customer.getUuid());
                });
    }

    private void follow(){
        endChat();
        Mod.LOGGER.info("Following Player");
        //                new FollowCustomerTask(1.8f).tryStarting(this.getServer().getWorld(), this, 30000);
        new Thread(() -> {
            Goal goal = new TemptGoal(this, 1.2, Ingredient.ofItems(Items.EMERALD), false);
            goalSelector.add(1, goal);
            wait(60);
            goalSelector.remove(goal);
        }).start();

    }

    private void gift(){
        List<Item> gifts = List.of(Items.EMERALD, Items.COOKIE);
        Collections.shuffle(gifts);

        dropItem(gifts.get(0));
    }
}
