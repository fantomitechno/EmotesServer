package dev.renoux.survival1emotes.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

import static dev.renoux.survival1emotes.Emotes.metadata;

public class EmotePacket implements Packet<ClientGamePacketListener> {
    public static final ResourceLocation PACKET = new ResourceLocation(metadata.id(), "emote");

    private final byte[] emoteFile;
    public final String name;
    public EmotePacket(byte[] emoteFile, String emoteName) {
        this.emoteFile = emoteFile;
        this.name = emoteName;
    }

    public EmotePacket(FriendlyByteBuf buf) {
        this.emoteFile = null;
        this.name = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeByteArray(this.emoteFile);
        buf.writeUtf(this.name);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
    }
}
