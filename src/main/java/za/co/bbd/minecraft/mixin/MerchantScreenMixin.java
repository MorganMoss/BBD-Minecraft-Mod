package za.co.bbd.minecraft.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import za.co.bbd.minecraft.Identifiers.ModIdentifiers;
import za.co.bbd.minecraft.Mod;

import java.util.List;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    //Button Texture
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");

    //This is the edit box
    private TextFieldWidget nameField;

    //These are constantly updated.
    private String villagerResponse = "I is thinking ... owo";
    private String playerResponse = "";

    private boolean isReplying = true;

    // Source code injections and overrides
    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

    }

    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    void additionalInit(CallbackInfo ci) {
        isReplying = true;
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                if (!ClientPlayNetworking.canSend(
                        new Identifier(ModIdentifiers.CHAT_PLAYER_IDENTIFIER + this.client.player.getUuidAsString()))
                ) {
                    if (i == 99){
                        client.execute(this::close);
                        Mod.LOGGER.info(List.of(ClientPlayNetworking.getSendable()).toString());
                        return;
                    }

                } else {
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier(ModIdentifiers.CHAT_VILLAGER_IDENTIFIER + this.client.player.getUuidAsString()),
                (client1, handler1, buf, responseSender) -> {
                    villagerResponse = buf.readString();
                    isReplying = false;
                }
        );
        Mod.LOGGER.info("Started Listening on " + ModIdentifiers.CHAT_VILLAGER_IDENTIFIER + this.client.player.getUuidAsString());

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

        if (!isReplying && !ClientPlayNetworking.canSend(new Identifier(ModIdentifiers.CHAT_PLAYER_IDENTIFIER + this.client.player.getUuidAsString()))) {
            client.execute(this::close);

        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER){
            if (!isReplying){
                isReplying = true;
                PacketByteBuf packet = PacketByteBufs.create();
                packet.writeString(playerResponse);
                ClientPlayNetworking.send(
                        new Identifier(ModIdentifiers.CHAT_PLAYER_IDENTIFIER + this.client.player.getUuidAsString()),
                        packet);
                this.nameField.setText("");
            }
        }
        if (isReplying) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
        }
        if (this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public void close() {
        super.close();
        ClientPlayNetworking.unregisterGlobalReceiver(
                new Identifier(ModIdentifiers.CHAT_VILLAGER_IDENTIFIER + this.client.player.getUuidAsString())
        );
        isReplying = true;
    }


    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
        String text = this.nameField.getText();
        setupTextBox();
        this.nameField.setText(text);
    }

    //Draw Helper Methods
    private void drawChatGPT(MatrixStack matrices){
        MutableText line = Text.literal(villagerResponse);

        String lineStr = line.getString();

        int textWidth = this.textRenderer.getWidth(line);
        int textLength = lineStr.length();
        int lineCount = textWidth / (this.backgroundWidth-10) + (textWidth % (this.backgroundWidth-10) != 0 ? 1 : 0);
        int lineLength = textLength / lineCount;


        //TODO: Add a scrollbar x3
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
        if (isReplying){
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
