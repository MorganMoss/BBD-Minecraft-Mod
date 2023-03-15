package za.co.bbd.minecraft.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import za.co.bbd.minecraft.misc.ChatGPTMessager;


@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {
    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(
            method = "drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V",
            at = @At("TAIL"))
    private void addChatGPT(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci){
        MutableText line1 = Text.literal(ChatGPTMessager.current_message);

        if (this.textRenderer.getWidth(line1) > this.backgroundWidth - 10){
            int size = ChatGPTMessager.current_message.length();
            int index = ChatGPTMessager.current_message.indexOf(" ", size/2);
            line1 = Text.literal(ChatGPTMessager.current_message.substring(0, index));
            Text line2 = Text.literal(ChatGPTMessager.current_message.substring(index + 1));
            this.textRenderer.draw(matrices, line2, 5f, -12.0f, 0xFFFFFF);
        }

        this.textRenderer.draw(matrices, line1, 5f, -24.0f, 0xFFFFFF);
    }
}
