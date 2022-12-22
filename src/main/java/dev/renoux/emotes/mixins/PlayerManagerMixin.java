package dev.renoux.emotes.mixins;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
  @Shadow
  public abstract void broadcast(SignedMessage message,
      Function<ServerPlayerEntity, SignedMessage> playerMessageFactory, MessageSender sender,
      RegistryKey<MessageType> typeKey);

  @Inject(method = "broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
  private void onSendChatMessage(FilteredMessage<SignedMessage> message, ServerPlayerEntity sender,
      RegistryKey<MessageType> typeKey, CallbackInfo ci) {

    this.broadcast((SignedMessage) message.raw(), (player) -> {
      return SignedMessage.of(replaceEmotes(message.filtered().getContent().getString()),
          message.filtered().signature());
    }, sender.asMessageSender(), typeKey);
    ci.cancel();
  }

  private String replaceEmotes(String message) {
    String returnMessage = message;
    returnMessage = returnMessage.replaceAll(":attentif:", "\uF801");
    returnMessage = returnMessage.replaceAll(":coeur:", "\uF802");
    returnMessage = returnMessage.replaceAll(":D:", "\uF803");
    returnMessage = returnMessage.replaceAll(":diamond:", "\uF804");
    returnMessage = returnMessage.replaceAll(":gg:", "\uF805");
    returnMessage = returnMessage.replaceAll(":hug:", "\uF806");
    returnMessage = returnMessage.replaceAll(":salut:", "\uF807");
    returnMessage = returnMessage.replaceAll(":singe:", "\uF808");
    returnMessage = returnMessage.replaceAll(":zzz:", "\uF809");
    return returnMessage;
  }
}
