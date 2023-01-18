package dev.renoux.emotes.mixins;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.text.MutableText;
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

    private static final Set<String> EMOTES = Set.of(
            "coeur", "D", "diamond", "gg",
            "hug", "salut", "singe", "zzz",
            "awm", "bagarre", "bingo", "bonk",
            "chant", "clip", "dab", "fangirl",
            "fine", "fr", "gasm", "gouzi",
            "hmm", "hype", "karma", "kdo",
            "luck", "miam", "modeste", "music",
            "noluck", "nul", "oasis", "ombre",
            "oops", "oula", "paint", "perdu",
            "peur", "pog", "pride", "rip",
            "rng", "shh", "smirk", "soldat",
            "stonks", "sueur", "timide"
    );

    @Shadow
    public abstract void broadcast(SignedMessage message,
                                   Function<ServerPlayerEntity, SignedMessage> playerMessageFactory, MessageSender sender,
                                   RegistryKey<MessageType> typeKey);

    @Inject(method = "broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(FilteredMessage<SignedMessage> message, ServerPlayerEntity sender,
                                   RegistryKey<MessageType> typeKey, CallbackInfo ci) {

        this.broadcast(message.raw(), (player) -> {
            return SignedMessage.of(this.processMessage(message.filtered().getContent()),
                    message.filtered().signature());
        }, sender.asMessageSender(), typeKey);
        ci.cancel();
    }

    private Text processMessage(String message) {
        char[] chars = message.toCharArray();
        StringBuilder textBuilder = new StringBuilder();
        Text rootElement = null;
        MutableText currentElement = null;

        int index = 0;
        int emoteStartIndex = 0;
        boolean readingEmoteName = false;
        while (index < chars.length) {
            char token = chars[index++];

            textBuilder.append(token);

            if (token == ':') { // Found start/end of emote pattern
                if (!readingEmoteName) { // Not currently reading emote name, means this *may* be the beginning of an emote
                    readingEmoteName = true;
                    emoteStartIndex = textBuilder.length() - 1;
                } else { // Currently reading emote name, meaning that this is the end of an emote and should be processed as an emote
                    readingEmoteName = false;

                    String emoteName = textBuilder.substring(emoteStartIndex + 1, textBuilder.length() - 1);
                    if (PlayerManagerMixin.EMOTES.contains(emoteName)) { // Valid emote name, proceeding translation
                        // Strip emote from text builder
                        textBuilder.delete(emoteStartIndex, textBuilder.length());

                        if (!textBuilder.isEmpty()) {
                            MutableText text = Text.literal(textBuilder.toString());

                            if (currentElement != null) {
                                currentElement.append(text);
                            }

                            currentElement = text;

                            if (rootElement == null) {
                                rootElement = currentElement;
                            }
                        }

                        char[] emoteChars = emoteName.toCharArray();

                        emoteChars[0] = Character.toUpperCase(emoteChars[0]); // Capitalize first character

                        MutableText emoteText = Text.translatable("mathox1" + new String(emoteChars));

                        if (currentElement != null) {
                            currentElement.append(emoteText);
                        }

                        currentElement = emoteText;

                        if (rootElement == null) {
                            rootElement = currentElement;
                        }

                        textBuilder.delete(0, textBuilder.length()); // Clear string builder
                    }
                }
            } else {
                if (readingEmoteName && ((token < 'A' || token > 'Z') && (token < 'a' || token > 'z'))) { // Not a letter, this ain't an emote
                    readingEmoteName = false;
                }
            }
        }

        if (!textBuilder.isEmpty()) {
            MutableText text = Text.literal(textBuilder.toString());

            if (currentElement != null) {
                currentElement.append(text);
            }

            currentElement = text;

            if (rootElement == null) {
                rootElement = currentElement;
            }
        }

        return rootElement;
    }
}
