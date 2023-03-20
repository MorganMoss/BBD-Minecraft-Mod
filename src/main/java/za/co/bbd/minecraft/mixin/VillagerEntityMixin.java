package za.co.bbd.minecraft.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.chat.VillagerChat;
import za.co.bbd.minecraft.interfaces.VillagerActor;
import za.co.bbd.minecraft.interfaces.VillagerChatHolder;
import za.co.bbd.minecraft.misc.Action;

import javax.swing.*;
import java.util.List;


@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements VillagerActor, VillagerChatHolder {
    @Shadow private @Nullable PlayerEntity lastCustomer;
    @Shadow private long lastGossipDecayTime;
    private static final double VISION_RADIUS = 100;

    //Injected Class Variables
    private final VillagerChat chat = new VillagerChat((VillagerEntity) (Object) this);


    //Constructor
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


    //Injections
    @Inject(
            method = "interactMob",
            at = @At("HEAD")
    )
    void additionalInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        VillagerChat.setCurrentMessenger(this.chat);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    void additionalTick(CallbackInfo ci){
        if (!this.hasCustomer() && chat.isChatting()){
            chat.endChat();
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
            case STEAL -> {
            }
            case GIFT -> {
            }
            case TRADE -> {
            }
            case REWARD -> {
            }
            case GIVE -> {
            }
            case DISCOUNT -> {
            }
            case RAISE_PRICE -> {
            }
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
        wait(10);
        chat.endChat();
        Mod.LOGGER.info("Chat Ended");
    }

    private void flee(){
        endChat();
        Mod.LOGGER.info("Fleeing Player");
        new Thread(() -> {
            Goal goal = new FleeEntityGoal<>(this, PlayerEntity.class, 20, 1.0, 1.5);
            goalSelector.add(4, goal);
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
            Goal goal = new TemptGoal(this, 1.5, Ingredient.ofItems(Items.EMERALD), false);
            goalSelector.add(4, goal);
            wait(60);
            goalSelector.remove(goal);
        }).start();

    }
}
