/*
 * MIT License
 *
 * Copyright (c) 2023 Simon RENOUX aka fantomitechno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.renoux.emotes;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class Emotes implements ModInitializer {
  public static final String MODID = "emotes";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

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

  public static Text processMessage(String message, Style format) {
    char[] chars = message.toCharArray();
    StringBuilder textBuilder = new StringBuilder();
    MutableText rootElement = null;
    MutableText currentElement = null;

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
          if (EMOTES.contains(emoteName)) { // Valid emote name, proceeding translation
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
        if (readingEmoteName && ((token < 'A' || token > 'Z') && (token < 'a' || token > 'z'))) { // Not a
                                                                                                  // letter,
                                                                                                  // this ain't
                                                                                                  // an emote
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

    return rootElement.setStyle(format);
  }

  @Override
  public void onInitialize() {
    LOGGER.info("Emotes : LOADING");

    LOGGER.info("Emotes : LOADED");
  }
}
