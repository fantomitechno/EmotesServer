package dev.renoux.survival1emotes.utils;

import dev.renoux.survival1emotes.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.quiltmc.config.api.values.ValueList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EmoteProcessor {

    private static Map<String, String> EMOTES = new HashMap<>();

    public static void init() {
        EMOTES = new HashMap<>();
        ValueList<String> emotes = ModConfig.getConfig().getEmotes();
        for (String emote : emotes) {
            String[] splitedEmote = emote.split(":");
            EMOTES.put(splitedEmote[0], splitedEmote[1]);
        }
    }

    /*
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
            "stonks", "sueur", "timide",
            "attentif");
            */

    public static Component processMessage(String message, Style format) {
        char[] chars = message.toCharArray();
        StringBuilder textBuilder = new StringBuilder();
        MutableComponent rootElement = null;
        MutableComponent currentElement = null;

        int index = 0;
        int emoteStartIndex = 0;
        boolean readingEmoteName = false;
        while (index < chars.length) {
            char token = chars[index++];

            textBuilder.append(token);

            if (token == ':') { // Found start/end of emote pattern
                if (!readingEmoteName) { // Not currently reading emote name, means this *may* be the beginning of an
                    // emote
                    readingEmoteName = true;
                    emoteStartIndex = textBuilder.length() - 1;
                } else { // Currently reading emote name, meaning that this is the end of an emote and
                    // should be processed as an emote
                    readingEmoteName = false;

                    String emoteName = textBuilder.substring(emoteStartIndex + 1, textBuilder.length() - 1);
                    String emote = EMOTES.get(emoteName);
                    if (emote != null) { // Valid emote name, proceeding translation
                        // Strip emote from text builder
                        textBuilder.delete(emoteStartIndex, textBuilder.length());

                        if (!textBuilder.isEmpty()) {
                            MutableComponent text = Component.literal(textBuilder.toString());

                            if (currentElement != null) {
                                currentElement.append(text);
                            }

                            currentElement = text;

                            if (rootElement == null) {
                                rootElement = currentElement;
                            }
                        }

                        MutableComponent emoteText = Component.translatable(emote);

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
            }
        }

        if (!textBuilder.isEmpty()) {
            MutableComponent text = Component.literal(textBuilder.toString());

            if (currentElement != null) {
                currentElement.append(text);
            }

            currentElement = text;

            if (rootElement == null) {
                rootElement = currentElement;
            }
        }

        assert rootElement != null;
        return rootElement.setStyle(format);
    }
}
