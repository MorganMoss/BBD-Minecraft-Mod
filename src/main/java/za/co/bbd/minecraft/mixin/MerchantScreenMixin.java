package za.co.bbd.minecraft.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import za.co.bbd.minecraft.misc.ChatGPTMessenger;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private TextFieldWidget nameField;
    private String playerResponse;

    // Source code injections and overrides
    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(
            method = "init()V",
            at = @At("TAIL")
    )
    void additionalInit(CallbackInfo ci) {
        setupTextBox();
    }

    @Inject(
            method = "drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V",
            at = @At("TAIL")
    )
    void drawAdditionalForegroundElements(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci){
        drawChatGPT(matrices);
        drawTextBox(matrices, mouseX, mouseY);
        drawSendButton(matrices);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.nameField.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER){
            ChatGPTMessenger.respond(this.playerResponse);
            this.nameField.setText("");
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.player.closeHandledScreen();
        }
        if (this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    //Draw Helper Methods
    private void drawChatGPT(MatrixStack matrices){
        MutableText line1 = Text.literal(ChatGPTMessenger.current_message);

        if (this.textRenderer.getWidth(line1) > this.backgroundWidth - 10){
            int size = ChatGPTMessenger.current_message.length();
            int index = ChatGPTMessenger.current_message.indexOf(" ", size/2);
            line1 = Text.literal(ChatGPTMessenger.current_message.substring(0, index));
            Text line2 = Text.literal(ChatGPTMessenger.current_message.substring(index + 1));
            this.textRenderer.draw(matrices, line2, 5f, -12.0f, 0xFFFFFF);
        }

        this.textRenderer.draw(matrices, line1, 5f, -24.0f, 0xFFFFFF);
    }

    private void drawTextBox(MatrixStack matrices, int mouseX, int mouseY){
        this.nameField.render(matrices, mouseX, mouseY, (float) 0.0);
    }

    private void drawSendButton(MatrixStack matrices){
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);

        MerchantScreen.drawTexture(matrices, x + 5 + 35 + 20, y + 3, this.getZOffset(), this.backgroundWidth-15, this.backgroundHeight + 12, 10, 9, 512, 256);
    }

    //Events
    private void onUpdatePlayerResponse(String response){
        this.playerResponse = response;
    }

    //Init Helper Methods
    private void setupTextBox() {
        int i = 5;
        int j = this.backgroundHeight + 12;
        this.nameField = new TextFieldWidget(this.textRenderer, i, j , this.backgroundWidth - 25, 12, Text.translatable("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setDrawsBackground(false);
        this.nameField.setMaxLength(50);
        this.nameField.setChangedListener(this::onUpdatePlayerResponse);
        this.nameField.setText("");
        this.addSelectableChild(this.nameField);
        this.setInitialFocus(this.nameField);
        this.nameField.setEditable(true);
    }


}
