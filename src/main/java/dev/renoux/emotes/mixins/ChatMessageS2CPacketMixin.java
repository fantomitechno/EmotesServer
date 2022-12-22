package dev.renoux.emotes.mixins;

import java.time.Instant;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.encryption.NetworkEncryptionUtils.SignatureData;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;

@Mixin(ChatMessageS2CPacket.class)
public class ChatMessageS2CPacketMixin {
  @Shadow
  Text signedContent;

  @Shadow
  Optional<Text> unsignedContent;
  /*
  @Inject(at = @At("TAIL"), method = "<init>")
  private void init(Text text, Optional<Text> optional, int i, MessageSender messageSender, Instant instant,
      SignatureData signatureData) {
    if (messageSender.name() != null) {
      this.signedContent = Text.of(text.getString().replaceAll(":gg:", "\uD83E"));
    }
  } */

  @Inject(at = @At("RETURN"), method = "signedContent")
  private void signedContent(CallbackInfoReturnable<Text> cir) {
    cir.setReturnValue(Text.of(this.signedContent.getString().replaceAll(":gg:", "\uD83E")));
  }

  @Inject(at = @At("RETURN"), method = "unsignedContent")
  private void unsignedContent(CallbackInfoReturnable<Optional<Text>> cir) {
    if (this.unsignedContent.isPresent()) {
      cir.setReturnValue(Optional.of(Text.of(this.unsignedContent.get().getString().replaceAll(":gg:", "\uD83E"))));
    }
  }
}
