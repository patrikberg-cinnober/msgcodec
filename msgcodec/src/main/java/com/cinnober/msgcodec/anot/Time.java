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
package com.cinnober.msgcodec.anot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.cinnober.msgcodec.Epoch;

/**
 * Specifies that an integer or Date field is of a time type.
 * The type of the field may be int, Integer, long, Long or {@link Date}.
 * Date fields are milliseconds since the Unix epoch in UTC by default.
 *
 * <p>When applied to a sequence, the meaning of this annotation is transferred to the element type of the sequence.
 *
 * @author mikael.brannstrom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Time {
    /** 
     * The granularity of the time.
     * @return the granularity of the time.
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
    /** 
     * The epoch defining zero time.
     * @return the epoch defining zero time.
     */
    Epoch epoch() default Epoch.UNIX;
    /** 
     * The time zone, or the empty string for local/unspecified time zone.
     * @return the time zone, or the empty string for local/unspecified time zone.
     */
    String timeZone() default "UTC";
}
