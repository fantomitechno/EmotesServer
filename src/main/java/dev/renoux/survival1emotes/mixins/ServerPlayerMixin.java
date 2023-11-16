package dev.renoux.survival1emotes.mixins;

import com.mojang.authlib.GameProfile;
import dev.renoux.survival1emotes.utils.EmoteProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

  public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
    super(world, pos, yaw, gameProfile);
  }

  @Shadow public abstract void sendSystemMessage(Component message, boolean overlay);

  @Shadow protected abstract boolean acceptsChatMessages();

  // Messages from the server (/chat)
  @Inject(method = "sendSystemMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
  private void onSendMessage(Component message, CallbackInfo ci) {
    if (message.getString().startsWith("message.voicechat"))
        return;
    this.sendSystemMessage(EmoteProcessor.processMessage(message.getString(), message.getStyle()), false);
    ci.cancel();
  }

  // Private messages (/msg, /tell, /w) and normal chat messages
  @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
  private void sendChatMessage(OutgoingChatMessage message, boolean filterMaskEnabled, ChatType.Bound parameters, CallbackInfo ci) {
    ci.cancel();

    if (!this.acceptsChatMessages()) return;
    Component messageProcessed = EmoteProcessor.processMessage(message.content().getString(), message.content().getStyle());

    PlayerChatMessage newMessage = PlayerChatMessage.system(message.content().getString()).withUnsignedContent(messageProcessed);

    OutgoingChatMessage finalMessage = OutgoingChatMessage.create(newMessage.withUnsignedContent(messageProcessed));

    finalMessage.sendToPlayer((ServerPlayer) (Object) this, filterMaskEnabled, parameters);
  }
}
