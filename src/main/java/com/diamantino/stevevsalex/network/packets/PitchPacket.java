package com.diamantino.stevevsalex.network.packets;

import com.diamantino.stevevsalex.entities.base.PlaneEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PitchPacket {

    private final byte pitchUp;

    public PitchPacket(byte pitchUp) {
        this.pitchUp = pitchUp;
    }

    public PitchPacket(FriendlyByteBuf buffer) {
        this.pitchUp = buffer.readByte();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeByte(pitchUp);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender != null && sender.getVehicle() instanceof PlaneEntity planeEntity && planeEntity.getControllingPassenger() == sender) {
                planeEntity.setPitchUp(pitchUp);
            }
        });
        ctx.setPacketHandled(true);
    }
}