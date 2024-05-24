/*
 * MIT License
 *
 * Copyright (c) 2024 Simon RENOUX aka fantomitechno
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
package dev.renoux.emotes_server;

import dev.renoux.emotes_server.config.ModConfig;
import dev.renoux.emotes_server.networking.EmotePacket;
import dev.renoux.emotes_server.networking.ListEmotePacket;
import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static dev.renoux.emotes_server.EmotesServer.LOGGER;

public class Events {
    private static String nameAndHashArray;
    private static HashMap<String, byte[]> emotesFiles;

    public static void init() throws IOException {
        initPlayerJoin();
        initCustomPayload();

        ValueList<String> emotes = ModConfig.getConfig().getEmotes();
        emotesFiles = new HashMap<>();
        StringBuilder nameAndHash = new StringBuilder();
        for (String emote : emotes) {
            String[] splitEmote = emote.split(":");
            String path = ModConfig.getPath() + "/emotes/";
            if (splitEmote[0].equals("nul")) {
                path += "nul_.png";
            } else {
                path += splitEmote[0] + ".png";
            }
            File file = new File(path);
            if (file.exists()) {
                try {
                    emotesFiles.put(splitEmote[0], new FileInputStream(file).readAllBytes());
                    nameAndHash.append(splitEmote[0]).append(":").append(file.hashCode()).append(",");
                } catch (Exception exception) {
                    LOGGER.info("An error occured while loading " + emote + " emote: " + exception.getMessage());
                }
            }
        }
        nameAndHashArray = nameAndHash.toString();
    }

    private static void initPlayerJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            FriendlyByteBuf buf = PacketByteBufs.create();
            new ListEmotePacket(nameAndHashArray).write(buf);
            sender.sendPacket(ListEmotePacket.PACKET, buf);
        });
    }

    private static void initCustomPayload() {
        ServerPlayNetworking.registerGlobalReceiver(EmotePacket.PACKET, (server, player, handler, buf, responseSender) -> {
            EmotePacket packet = new EmotePacket(buf);
            LOGGER.info(player.getName().getString() + " asked for " + packet.name);
            FriendlyByteBuf newBuf = PacketByteBufs.create();
            new EmotePacket(emotesFiles.get(packet.name), packet.name).write(newBuf);
            responseSender.sendPacket(EmotePacket.PACKET, newBuf);
        });
    }
}
