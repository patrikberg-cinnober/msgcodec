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
import java.io.IOException;

/**
 * Base class for a dynamically generated codec for a specific schema.
 *
 * <p><b>Note: internal use only!</b>
 * 
 * <p>
 * A GeneratedCodec sub class represents a schema.
 * A GeneratedCodec instance is tied to a specific {@link BlinkCodec} instance which holds the encode buffers
 * and any max binary size settings.
 * 
 * @author mikael brannstrom
 */

/* Note: This class should be package private, but cannot since the dynamically generated classes are loaded
   from another class loader, i.e. don't share the package with this class (regardless of package name). */
public abstract class GeneratedCodec {

    protected final int maxBinarySize;

    protected GeneratedCodec(int maxBinarySize) {
        this.maxBinarySize = maxBinarySize;
    }
    
    /**
     * Write a static group and its group id.
     * Method to be generated in a sub class using <b>switch</b> construct
     * (similar to what is generated for a switch on String) based on the group type.
     * 
     * @param out where to write to, not null
     * @param group the group to write, not null.
     * @throws IOException if the underlying stream throws an exception.
     * @throws IllegalArgumentException if an illegal value is encountered, e.g. missing required field value.
     */
    protected abstract void writeStaticGroupWithId(ByteSink out, Object group)
            throws IOException, IllegalArgumentException;
    
    /**
     * Read a static group.
     * Method to be generated in a sub class using <b>switch</b> based on group id.
     * 
     * @param groupId the group id
     * @param in where to read from, not null.
     * @return the decoded group, not null.
     * @throws IOException if the underlying stream throws an exception.
     * @throws DecodeException if the group could not be decoded.
     */
    protected abstract Object readStaticGroup(int groupId, ByteSource in) throws IOException, DecodeException;

    /**
     * Write a dynamic group to the specified output stream.
     * @param out where to write to, not null.
     * @param group the group to encode, not null
     * @throws IOException if the underlying stream throws an exception.
     */
    public abstract void writeDynamicGroup(ByteSink out, Object group) throws IOException, IllegalArgumentException;

    /**
     * Write a nullable dynamic group to the specified output stream.
     * @param out where to write to, or null.
     * @param group the group to encode, not null
     * @throws IOException if the underlying stream throws an exception.
     */
    public abstract void writeDynamicGroupNull(ByteSink out, Object group) throws IOException, IllegalArgumentException;
    
    /** 
     * Read a dynamic group.
     * @param in the stream to read from.
     * @return the group, not null.
     * @throws IOException if the underlying stream throws an exception.
     */
    public abstract Object readDynamicGroup(ByteSource in) throws IOException;
    
    /** 
     * Read a nullable dynamic group.
     * @param in the stream to read from.
     * @return the group, or null.
     * @throws IOException if the underlying stream throws an exception.
     */
    public abstract Object readDynamicGroupNull(ByteSource in) throws IOException;

    protected int getMaxBinarySize() {
        return maxBinarySize;
    }

    // --- UTILITY METHODS FOR CREATING EXCEPTIONS ---

    /**
     * Create an encode exception when a required value is missing.
     * 
     * @param valueName the name of the value, e.g. a field name, not null.
     * @return the exception to be thrown.
     */
    protected static IllegalArgumentException missingRequiredValue(String valueName) {
        return new IllegalArgumentException(valueName + ": Missing required value");
    }

    /**
     * Create a decode exception when an unmappable enum symbol id is read.
     * This means that for a given valid symbol id, there is no Java enum value.
     *
     * @param valueName the name of the value, e.g. a field name, not null.
     * @param symbolId the symbol id
     * @param enumClass the enum class, not null
     * @return the exception to be thrown.
     */
    @SuppressWarnings("rawtypes")
    protected static DecodeException unmappableEnumSymbolId(
            String valueName, int symbolId, Class<? extends Enum> enumClass) {
        return new DecodeException(valueName + ": Cannot map symbol id " + symbolId +
                " to enum value of type " + enumClass);
    }

    /**
     * Create a decode exception when an unknown enum symbol id is read.
     * @param valueName the name of the value, e.g. a field name, not null.
     * @param symbolId the symbol id
     * @return the exception to be thrown.
     */
    protected static DecodeException unknownEnumSymbol(String valueName, int symbolId) {
        return new DecodeException(valueName + ": Unknown enum symbol id " + symbolId);
    }

    /**
     * Create an encode exception when an unmappable enum value is found.
     * This means that for a given Java enum value, there is no mapping to a symbol id.
     * 
     * @param <E> the enum type
     * @param valueName the name of the value, e.g. a field name, not null.
     * @param enumValue the unmappable unum value, not null
     * @return the exception to be thrown.
     */
    protected static <E extends Enum<E>> IllegalArgumentException unmappableEnumSymbolValue(String valueName, E enumValue) {
        return new IllegalArgumentException(valueName + ": Cannot map enum value to symbol " + enumValue);
    }

    /**
     * Create an encode exception when trying to encode a group for an unknown group type.
     * This mean that the group is not present in the schema.
     * 
     * @param groupType the group type (e.g. a java class) of the group to be encoded, not null.
     * @return the exception to be thrown.
     */
    protected static IllegalArgumentException unknownGroupType(Object groupType) {
        return new IllegalArgumentException("Unknown group type: " + groupType);
    }

    /**
     * Create a decode exception when an unknown group id is read.
     *
     * @param groupId the group id.
     * @return the exception to be thrown.
     */
    protected static DecodeException unknownGroupId(int groupId) {
        return new DecodeException("Unknown group id: " + groupId);
    }
}
