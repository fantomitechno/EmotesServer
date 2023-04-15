package dev.renoux.emotes.mixins;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.renoux.emotes.Emotes;
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

        this.broadcast(message.raw(), (player) -> {
            return SignedMessage.of(Emotes.processMessage(message.filtered().getContent().getString()),
                    message.filtered().signature());
        }, sender.asMessageSender(), typeKey);
        ci.cancel();
    }
}
