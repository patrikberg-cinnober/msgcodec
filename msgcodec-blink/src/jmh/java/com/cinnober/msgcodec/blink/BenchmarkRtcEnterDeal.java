/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 The MsgCodec Authors
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
package com.cinnober.msgcodec.blink;

import com.cinnober.msgcodec.Epoch;
import com.cinnober.msgcodec.Schema;
import com.cinnober.msgcodec.SchemaBuilder;
import com.cinnober.msgcodec.blink.rtcmessages.EnterDeal;
import com.cinnober.msgcodec.blink.rtcmessages.IncomingTradeSide;
import com.cinnober.msgcodec.blink.rtcmessages.Request;
import com.cinnober.msgcodec.blink.rtcmessages.SessionToken;
import com.cinnober.msgcodec.blink.rtcmessages.TradeDestination;
import com.cinnober.msgcodec.blink.rtcmessages.TradeExternalData;
import com.cinnober.msgcodec.io.ByteArrayBuf;
import com.cinnober.msgcodec.io.ByteBuf;
import com.cinnober.msgcodec.io.ByteBufferBuf;
import com.cinnober.msgcodec.io.ByteBuffers;
import com.cinnober.msgcodec.util.TimeFormat;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkRtcEnterDeal {

    public static enum BufferType {
        ARRAY,
        BUFFER,
        DIRECT_BUFFER,
    }

//    @Param({"ARRAY", "BUFFER", "DIRECT_BUFFER"})
    @Param({"ARRAY"})
    public BufferType bufType;

    private EnterDeal msg;
    private BlinkCodec codec;
    private int encodedSize;

    private ByteBuf buf;

    public BenchmarkRtcEnterDeal() {
    }

    @Setup
    public void setup() throws IOException {
        Schema dict = new SchemaBuilder(true).build(
                EnterDeal.class,
                Request.class,
                IncomingTradeSide.class,
                SessionToken.class,
                TradeDestination.class,
                TradeExternalData.class
        ).assignGroupIds();
        BlinkCodecFactory factory = new BlinkCodecFactory(dict);
        codec = factory.createCodec();
        final int bufferSize = 1024;
        switch (bufType) {
            case ARRAY:
                buf = new ByteArrayBuf(new byte[bufferSize]);
                break;
            case BUFFER:
                buf = new ByteBufferBuf(ByteBuffer.allocate(bufferSize));
                break;
            case DIRECT_BUFFER:
                buf = new ByteBufferBuf(ByteBuffer.allocateDirect(bufferSize));
                break;
            default:
                throw new RuntimeException("Unhandled case: " + bufType);
        }

        msg = createRtcEnterDeal();

        encodedSize = benchmarkEncode();
        System.out.println("Encoded size: " + encodedSize);
        System.out.println("Encoded hex: " + ByteBuffers.toHex(buf.getByteBuffer(), 0, encodedSize, 1, 100, 100));
    }

    public static EnterDeal createRtcEnterDeal() {
        EnterDeal msg = new EnterDeal();
        msg.requestId = 123456;
        msg.clientDealId = "qwerty";
        msg.instrumentKey = 1000_001;
        msg.price = BigDecimal.valueOf(1234, 2);
        msg.quantity = BigDecimal.valueOf(10_000);
        try {
            msg.tradeBusinessDate = (int) TimeFormat.getTimeFormat(TimeUnit.DAYS, Epoch.UNIX).parse("2015-03-30");
        } catch (ParseException ex) {
            throw new RuntimeException("Should not happen", ex);
        }
        msg.buy = createTradeSide(msg.quantity);
        msg.sell = createTradeSide(msg.quantity);
        msg.tradeExternalData = null;
        return msg;
    }

    private static IncomingTradeSide createTradeSide(BigDecimal quantity) {
        IncomingTradeSide side = new IncomingTradeSide();
        side.clientOrderId = "abc-123";
        TradeDestination alloc = new TradeDestination(456789, quantity);
        side.allocations = Arrays.asList(alloc);
        return side;
    }

    @Benchmark
    public Object benchmarkDecode() throws IOException {
        buf.position(0).limit(encodedSize);
        return codec.decode(buf);
    }
    @Benchmark
    public int benchmarkEncode() throws IOException {
        buf.clear();
        codec.encode(msg, buf);
        return buf.position();
    }
}
