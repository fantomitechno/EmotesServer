package dev.renoux.survival1emotes;

import dev.renoux.survival1emotes.config.ModConfig;
import dev.renoux.survival1emotes.networking.EmotePacket;
import dev.renoux.survival1emotes.networking.ListEmotePacket;
import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.impl.util.FileUtil;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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
                emotesFiles.put(splitEmote[0], FileUtil.readAllBytes(new FileInputStream(file)));
                nameAndHash.append(splitEmote[0]).append(":").append(file.hashCode()).append(",");
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
            Emotes.LOGGER.info(player.getName().getString() + " asked for " + packet.name);
            FriendlyByteBuf newBuf = PacketByteBufs.create();
            new EmotePacket(emotesFiles.get(packet.name), packet.name).write(newBuf);
            responseSender.sendPacket(EmotePacket.PACKET, newBuf);
        });
    }
}
