package za.co.bbd.minecraft.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.chat.VillagerChat;
import za.co.bbd.minecraft.interfaces.VillagerActor;
import za.co.bbd.minecraft.misc.Action;


@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements VillagerActor {
    //Injected Class Variables
    private final VillagerChat messenger = new VillagerChat((VillagerEntity) (Object) this);


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
        VillagerChat.setCurrentMessenger(this.messenger);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    void additionalTick(CallbackInfo ci){
        if (!this.hasCustomer() && messenger.isChatting()){
            messenger.endChat();
        }
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("HEAD")
    )
    void writeChatGPTDataToNbt(NbtCompound nbt, CallbackInfo ci){
        nbt.put("bbd.chat_data", messenger.generatePersistentChatGPTData());
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("HEAD")
    )
    void readChatGPTDataToNbt(NbtCompound nbt, CallbackInfo ci){
        NbtCompound chatData = nbt.getCompound("bbd.chat_data");
        if (!chatData.isEmpty()){
            messenger.parsePersistentChatGPTData(chatData);
        }
    }

    //Custom Methods
    @Override
    public void performAction(Action action) {
        Mod.LOGGER.info(action.action);
    }
}
