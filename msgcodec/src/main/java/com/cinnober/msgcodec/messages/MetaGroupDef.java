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
package com.cinnober.msgcodec.messages;

import java.util.ArrayList;
import java.util.List;

import com.cinnober.msgcodec.FieldDef;
import com.cinnober.msgcodec.GroupDef;
import com.cinnober.msgcodec.anot.Id;
import com.cinnober.msgcodec.anot.Name;
import com.cinnober.msgcodec.anot.Required;
import com.cinnober.msgcodec.anot.Sequence;

/**
 * A group definition.
 *
 * @author mikael.brannstrom
 *
 */
@Name("GroupDef")
@Id(16001)
public class MetaGroupDef extends MetaAnnotated {
    /**
     * The group name.
     */
    @Required
    @Id(1)
    public String name;
    /**
     * The numeric group identifier.
     */
    @Id(2)
    public Integer id;
    /**
     * The name of the super group, or null if there is no super group.
     */
    @Id(3)
    public String superGroup;
    /**
     * The declared fields of this group.
     * Inherited fields are not included here.
     */
    @Required
    @Sequence(MetaFieldDef.class)
    @Id(4)
    public List<MetaFieldDef> fields;

    public MetaGroupDef() {}

    public MetaGroupDef(String name, Integer id, String superGroup,
            List<MetaFieldDef> fields) {
        this.name = name;
        this.id = id;
        this.superGroup = superGroup;
        this.fields = fields;
    }

    public GroupDef toGroupDef() {
        List<FieldDef> fieldDefs = new ArrayList<>(fields.size());
        for (MetaFieldDef field : fields) {
            fieldDefs.add(field.toFieldDef());
        }
        return new GroupDef(name, id != null ? id.intValue() : -1, superGroup, fieldDefs, toAnnotationsMap(), null);
    }


}
