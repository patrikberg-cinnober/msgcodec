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

import com.cinnober.msgcodec.io.ByteSink;
import com.cinnober.msgcodec.io.ByteSource;
import com.cinnober.msgcodec.DecodeException;
import com.cinnober.msgcodec.Schema;
import com.cinnober.msgcodec.MsgCodec;
import com.cinnober.msgcodec.MsgCodecInstantiationException;
import com.cinnober.msgcodec.ObjectInstantiationException;
import com.cinnober.msgcodec.io.ByteBuf;
import com.cinnober.msgcodec.io.InputStreamSource;
import com.cinnober.msgcodec.io.OutputStreamSink;
import com.cinnober.msgcodec.util.Pool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

/**
 * The Blink codec can serialize and deserialize Java objects according to
 * the Blink native binary encoding format.
 *
 * <p><b>Note: Experimental support!</b> E.g. variable length data is not supported for now.
 *
 * Null values are supported in encode and decode.
 *
 * <p>See the <a href="http://blinkprotocol.org/s/BlinkNativeSpec-beta4.pdf">
 * Blink Native Binary Format Specification beta4 - 2013-06-05.</a>
 *
 * <p>This implementation differs from the specification above in the following ways:
 * <ul>
 * <li>The <code>fixed</code> (binary) type is not supported.
 * <li>Message extensions are ignored.
 * <li>The type identifier only uses the lower 32 bits of the 64 bit in the wire format.
 * <li>Support for bigInt is added, encoded as a binary with the integer in little endian.
 * Stored in the data area.
 * <li>Support for bigDecimal is added, encoded as int32 exponent followed by a bigInt mantissa.
 * Stored in the data area.
 * </ul>
 *
 * @author mikael.brannstrom
 * @see BlinkCodecFactory
 *
 */
public class NativeBlinkCodec implements MsgCodec {
    private final GeneratedNativeCodec generatedCodec;
    private final Schema schema;

    private final Pool<byte[]> bufferPool;

    private final int maxBinarySize;
    private final int maxSequenceLength;

    /**
     * Create a Blink codec.
     *
     * @param schema the definition of the messages to be understood by the codec.
     * @param bufferPool the buffer pool, needed for temporary storage while <em>encoding</em>.
     * @param maxBinarySize the maximum binary size (including strings) allowed while decoding, or -1 for no limit.
     * @param maxSequenceLength the maximum sequence length allowed while decoding, or -1 for no limit.
     */
    NativeBlinkCodec(Schema schema, Pool<byte[]> bufferPool,
            int maxBinarySize, int maxSequenceLength) throws MsgCodecInstantiationException {
        if (!schema.isBound()) {
            throw new IllegalArgumentException("Schema not bound");
        }
        this.bufferPool = bufferPool;

        this.maxBinarySize = maxBinarySize;
        this.maxSequenceLength = maxSequenceLength;
        this.schema = schema;

        GeneratedNativeCodec generatedCodecTmp = null;
        try {
            Class<GeneratedNativeCodec> generatedCodecClass =
                    GeneratedCodecClassLoader.getInstance().getGeneratedNativeCodecClass(schema);
            Constructor<GeneratedNativeCodec> constructor =
                    generatedCodecClass.getConstructor(new Class<?>[]{ NativeBlinkCodec.class, Schema.class });
            generatedCodecTmp = constructor.newInstance(this, schema);
        } catch (Exception e) {
            throw new MsgCodecInstantiationException(e);
        }
        generatedCodec = generatedCodecTmp;
    }

    Pool<byte[]> bufferPool() {
        return bufferPool;
    }

    int getMaxBinarySize() {
        return maxBinarySize;
    }

    int getMaxSequenceLength() {
        return maxSequenceLength;
    }

    Schema getSchema() {
        return schema;
    }

    @Override
    public void encode(Object group, OutputStream out) throws IOException {
        encode(group, new OutputStreamSink(out));
    }
    @Override
    public void encode(Object group, ByteSink out) throws IOException {
        generatedCodec.writeDynamicGroup(out, group);
    }

    @Override
    public Object decode(InputStream in) throws IOException {
        return decode(new InputStreamSource(in));
    }

    @Override
    public Object decode(ByteSource in) throws IOException {
        try {
            return generatedCodec.readDynamicGroup(in);
        } catch(GroupDecodeException|FieldDecodeException e) {
            Throwable t = e;
            StringBuilder str = new StringBuilder();
            for (;;) {
                if (t instanceof GroupDecodeException) {
                    str.append('(').append(((GroupDecodeException)t).getGroupName()).append(')');
                    t = t.getCause();
                } else if(t instanceof FieldDecodeException) {
                    str.append('.').append(((FieldDecodeException)t).getFieldName());
                    t = t.getCause();
                } else if(t instanceof ObjectInstantiationException) {
                    throw new DecodeException("Could not create group "+str.toString(), t);
                } else {
                    throw new DecodeException("Could not decode field "+str.toString(), t);
                }
            }
        }
    }
}
