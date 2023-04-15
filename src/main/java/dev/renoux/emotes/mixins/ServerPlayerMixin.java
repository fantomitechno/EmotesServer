package dev.renoux.emotes.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.renoux.emotes.Emotes;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {
  @Shadow
  public abstract void sendMessage(Text message, RegistryKey<MessageType> dimension);

  @Shadow
  protected abstract boolean acceptsMessage(RegistryKey<MessageType> typeKey);

  @Shadow
  protected abstract int getMessageTypeId(RegistryKey<MessageType> typeKey);

  @Shadow
  public ServerPlayNetworkHandler networkHandler;

  // Messages from the server (/chat)
  @Inject(method = "sendMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
  private void onSendMessage(Text message, CallbackInfo ci) {
    this.sendMessage(Emotes.processMessage(message.toString()), MessageType.CHAT);
    ci.cancel();
  }

  // Private messages (/msg, /tell, /w)
  @Inject(method = "sendChatMessage(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
  private void sendChatMessage(SignedMessage message, MessageSender sender, RegistryKey<MessageType> typeKey,
      CallbackInfo ci) {
    if (this.acceptsMessage(typeKey)) {
      this.networkHandler.sendPacket(
          new ChatMessageS2CPacket(message.signedContent(), message.unsignedContent(), this.getMessageTypeId(typeKey),
              sender, message.signature().timestamp(), message.signature().saltSignature()));
    }
  }

}
