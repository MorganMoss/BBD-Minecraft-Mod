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
import za.co.bbd.minecraft.Mod;
import za.co.bbd.minecraft.chat.ChatGPTMessenger;
import za.co.bbd.minecraft.misc.Message;
import za.co.bbd.minecraft.misc.Role;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    //Button Texture
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");

    //This is the edit box
    private TextFieldWidget nameField;

    //These are constantly updated.
    private String playerResponse = "";
    private String villagerResponse = "";
    private ChatGPTMessenger messenger;


    // Source code injections and overrides
    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    void additionalInit(CallbackInfo ci) {
        while (messenger == null) {
            messenger = ChatGPTMessenger.getCurrentMessenger();
        }
        if (this.messenger.isWaitingForResponse()) {
            this.client.player.closeHandledScreen();
            return;
        }
        messenger.startChat();
        setupTextBox();
    }

    @Inject(
            method = "drawForeground",
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
            if (!messenger.isWaitingForResponse()){
                messenger.respond(this.playerResponse);
                this.nameField.setText("");
            }
        }
        if (this.messenger.isWaitingForResponse()) {
            return true;
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
        villagerResponse = "I'm thinking...";

        //TODO: This is very unoptimized... But... It runs fine?
        List<Message> villagerMessages = (
                messenger.getChat()
                        .stream()
                        .filter(message -> message.role() == Role.ASSISTANT)
                        .collect(Collectors.toList())
        );



        if (!messenger.isWaitingForResponse() && villagerMessages.isEmpty()){
            return;
        } else if (!villagerMessages.isEmpty()){
            villagerResponse = villagerMessages.get(villagerMessages.size() - 1).content();
        }

        MutableText line = Text.literal(villagerResponse);

        String lineStr = line.getString();

        int textWidth = this.textRenderer.getWidth(line);
        int textLength = lineStr.length();
        int lineCount = textWidth / (this.backgroundWidth-10) + (textWidth % (this.backgroundWidth-10) != 0 ? 1 : 0);
        int lineLength = textLength / lineCount;

        int i = 0;
        while (!lineStr.isEmpty()) {
            String shortLine;
            int nextSpace = lineStr.indexOf(" ", lineLength);

            if (nextSpace > 0){
                shortLine = lineStr.substring(0, nextSpace);
                lineStr = lineStr.substring(nextSpace+1);
            } else {
                shortLine = lineStr.substring(0);
                lineStr = "";
            }

            Text newLine = Text.literal(shortLine);
            this.textRenderer.draw(matrices, newLine, 5f, -12.0f*(lineCount+1)+12.0f*(i+1), 0xFFFFFF);
            i++;
        }

    }

    private void drawTextBox(MatrixStack matrices, int mouseX, int mouseY){
        this.nameField.setEditable(true);
        if (messenger.isWaitingForResponse()){
            this.nameField.setEditable(false);
        }

        this.nameField.render(matrices, mouseX, mouseY, (float) 0.0);
    }

    private void drawSendButton(MatrixStack matrices){
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);

        MerchantScreen.drawTexture(matrices, x + 5 + 35 + 20, y + 3, this.getZOffset(), this.backgroundWidth + 5, this.backgroundHeight + 12, 10, 9, 512, 256);
    }


    //Events
    private void onUpdatePlayerResponse(String response){
        this.playerResponse = response;
    }


    //Init Helper Methods
    private void setupTextBox() {
        int i = 5;
        int j = this.backgroundHeight + 12;
        this.nameField = new TextFieldWidget(this.textRenderer, i, j , this.backgroundWidth - 10, 12, Text.translatable("container.repair"));
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
