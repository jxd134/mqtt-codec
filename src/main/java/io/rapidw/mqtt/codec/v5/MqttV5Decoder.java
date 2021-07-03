package io.rapidw.mqtt.codec.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.rapidw.mqtt.codec.utils.DecoderUtils;
import io.rapidw.mqtt.codec.v3_1_1.*;

import java.util.List;

public class MqttV5Decoder extends ReplayingDecoder<MqttV5Decoder.DecoderState> {

    enum DecoderState {
        READ_FIXED_HEADER,
        READ_VARIABLE_HEADER,
        READ_PAYLOAD
    }

    private MqttV5Packet packet;
    private short flags;
    private int remainingLength;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case READ_FIXED_HEADER:
                short b1 = in.readUnsignedByte();
                this.flags = (short) (b1 & 0x0F);
                this.remainingLength = DecoderUtils.readRemainingLength(in);
                switch (MqttV5PacketType.of(b1 >> 4)) {
                    case CONNECT:
                        this.packet = new MqttV5ConnectPacket();
                        break;

                }
                checkpoint(MqttV5Decoder.DecoderState.READ_VARIABLE_HEADER);
                out.add(this.packet);
        }
    }
}
