package dev.renoux.survival1emotes.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

import static dev.renoux.survival1emotes.Emotes.metadata;

public class ListEmotePacket  implements Packet<ClientGamePacketListener> {
    public static final ResourceLocation PACKET = new ResourceLocation(metadata.id(), "emote_list");

    private final String nameAndHashArray;
    public ListEmotePacket(String nameAndHashArray) {
        this.nameAndHashArray = nameAndHashArray;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(nameAndHashArray);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
    }
}
