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

import com.cinnober.msgcodec.MsgObject;
import com.cinnober.msgcodec.anot.Id;
import com.cinnober.msgcodec.anot.MaxSize;
import com.cinnober.msgcodec.anot.Name;
import com.cinnober.msgcodec.anot.Required;
import com.cinnober.msgcodec.anot.Unsigned;

/**
 *
 * @author mikael.brannstrom
 */
@Id('O')
@Name("EnterOrder")
public class Ouch42EnterOrder extends MsgObject {
    @Id(1)
    @MaxSize(14)
    @Required
    public String token;
    @Id(2)
    @Unsigned
    public byte buySell;
    @Id(3)
    @Unsigned
    public int shares;
    @Id(4)
    @MaxSize(8)
    @Required
    public String stock;
    @Id(5)
    @Unsigned
    public int price;
    @Id(6)
    @Unsigned
    public int timeInForce;
    @Id(7)
    @MaxSize(4)
    @Required
    public String firm;
    @Id(8)
    @Unsigned
    public byte display;
    @Id(9)
    @Unsigned
    public byte capacity;
    @Id(10)
    @Unsigned
    public byte intermarketSweep;
    @Id(11)
    @Unsigned
    public int minimumQuantity;
    @Id(12)
    @Unsigned
    public byte crossType;
    @Id(13)
    @Unsigned
    public byte customerType;


}
