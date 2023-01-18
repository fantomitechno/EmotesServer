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
import net.minecraft.text.Text;
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

    this.broadcast(message.raw(), (player) -> {
      return SignedMessage.of(replaceEmotes(message.filtered().getContent()),
          message.filtered().signature());
    }, sender.asMessageSender(), typeKey);
    ci.cancel();
  }

  private Text replaceEmotes(Text content) {
    String message = content.getString();
    message = replaceEmotes(message);
    return Text.Serializer.fromJson("[{\"text\":\"" + message + "\"}]");
  }

  private String replaceEmotes(String message) {
    String returnMessage = message.replaceAll("\"", "\\\"");
    returnMessage = returnMessage.replaceAll(":attentif:", "\"}, {\"translate\": \"mathox1Attentif\"}, {\"text\":\"")
        .replaceAll(":coeur:", "\"}, {\"translate\": \"mathox1Coeur\"}, {\"text\":\"")
        .replaceAll(":D:", "\"}, {\"translate\": \"mathox1D\"}, {\"text\":\"")
        .replaceAll(":diamond:", "\"}, {\"translate\": \"mathox1Diamond\"}, {\"text\":\"")
        .replaceAll(":gg:", "\"}, {\"translate\": \"mathox1Gg\"}, {\"text\":\"")
        .replaceAll(":hug:", "\"}, {\"translate\": \"mathox1Hug\"}, {\"text\":\"")
        .replaceAll(":salut:", "\"}, {\"translate\": \"mathox1Salut\"}, {\"text\":\"")
        .replaceAll(":singe:", "\"}, {\"translate\": \"mathox1Singe\"}, {\"text\":\"")
        .replaceAll(":zzz:", "\"}, {\"translate\": \"mathox1Zzz\"}, {\"text\":\"")
        .replaceAll(":awm:", "\"}, {\"translate\": \"mathox1Awm\"}, {\"text\":\"")
        .replaceAll(":bagarre:", "\"}, {\"translate\": \"mathox1Bagarre\"}, {\"text\":\"")
        .replaceAll(":bingo:", "\"}, {\"translate\": \"mathox1Bingo\"}, {\"text\":\"")
        .replaceAll(":bonk:", "\"}, {\"translate\": \"mathox1Bonk\"}, {\"text\":\"")
        .replaceAll(":chant:", "\"}, {\"translate\": \"mathox1Chant\"}, {\"text\":\"")
        .replaceAll(":clip:", "\"}, {\"translate\": \"mathox1Clip\"}, {\"text\":\"")
        .replaceAll(":dab:", "\"}, {\"translate\": \"mathox1Dab\"}, {\"text\":\"")
        .replaceAll(":fangirl:", "\"}, {\"translate\": \"mathox1Fangirl\"}, {\"text\":\"")
        .replaceAll(":fine:", "\"}, {\"translate\": \"mathox1Fine\"}, {\"text\":\"")
        .replaceAll(":fr:", "\"}, {\"translate\": \"mathox1Fr\"}, {\"text\":\"")
        .replaceAll(":gasm:", "\"}, {\"translate\": \"mathox1Gasm\"}, {\"text\":\"")
        .replaceAll(":gouzi:", "\"}, {\"translate\": \"mathox1Gouzi\"}, {\"text\":\"")
        .replaceAll(":hmm:", "\"}, {\"translate\": \"mathox1Hmm\"}, {\"text\":\"")
        .replaceAll(":hype:", "\"}, {\"translate\": \"mathox1Hype\"}, {\"text\":\"")
        .replaceAll(":karma:", "\"}, {\"translate\": \"mathox1Karma\"}, {\"text\":\"")
        .replaceAll(":kdo:", "\"}, {\"translate\": \"mathox1Kdo\"}, {\"text\":\"")
        .replaceAll(":luck:", "\"}, {\"translate\": \"mathox1Luck\"}, {\"text\":\"")
        .replaceAll(":miam:", "\"}, {\"translate\": \"mathox1Miam\"}, {\"text\":\"")
        .replaceAll(":modeste:", "\"}, {\"translate\": \"mathox1Modeste\"}, {\"text\":\"")
        .replaceAll(":music:", "\"}, {\"translate\": \"mathox1Music\"}, {\"text\":\"")
        .replaceAll(":noluck:", "\"}, {\"translate\": \"mathox1Noluck\"}, {\"text\":\"")
        .replaceAll(":nul:", "\"}, {\"translate\": \"mathox1Nul\"}, {\"text\":\"")
        .replaceAll(":oasis:", "\"}, {\"translate\": \"mathox1Oasis\"}, {\"text\":\"")
        .replaceAll(":ombre:", "\"}, {\"translate\": \"mathox1Ombre\"}, {\"text\":\"")
        .replaceAll(":oops:", "\"}, {\"translate\": \"mathox1Oops\"}, {\"text\":\"")
        .replaceAll(":oula:", "\"}, {\"translate\": \"mathox1Oula\"}, {\"text\":\"")
        .replaceAll(":paint:", "\"}, {\"translate\": \"mathox1Paint\"}, {\"text\":\"")
        .replaceAll(":perdu:", "\"}, {\"translate\": \"mathox1Perdu\"}, {\"text\":\"")
        .replaceAll(":peur:", "\"}, {\"translate\": \"mathox1Peur\"}, {\"text\":\"")
        .replaceAll(":pog:", "\"}, {\"translate\": \"mathox1Pog\"}, {\"text\":\"")
        .replaceAll(":pride:", "\"}, {\"translate\": \"mathox1Pride\"}, {\"text\":\"")
        .replaceAll(":rip:", "\"}, {\"translate\": \"mathox1Rip\"}, {\"text\":\"")
        .replaceAll(":rng:", "\"}, {\"translate\": \"mathox1Rng\"}, {\"text\":\"")
        .replaceAll(":shh:", "\"}, {\"translate\": \"mathox1Shh\"}, {\"text\":\"")
        .replaceAll(":smirk:", "\"}, {\"translate\": \"mathox1Smirk\"}, {\"text\":\"")
        .replaceAll(":soldat:", "\"}, {\"translate\": \"mathox1Soldat\"}, {\"text\":\"")
        .replaceAll(":stonks:", "\"}, {\"translate\": \"mathox1Stonks\"}, {\"text\":\"")
        .replaceAll(":sueur:", "\"}, {\"translate\": \"mathox1Sueur\"}, {\"text\":\"")
        .replaceAll(":timide:", "\"}, {\"translate\": \"mathox1Timide\"}, {\"text\":\"");
    return returnMessage;
  }
}
