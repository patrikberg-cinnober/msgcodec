/*
 * Copyright (c) 2013 Cinnober Financial Technology AB, Stockholm,
 * Sweden. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Cinnober Financial Technology AB, Stockholm, Sweden. You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Cinnober.
 *
 * Cinnober makes no representations or warranties about the suitability
 * of the software, either expressed or implied, including, but not limited
 * to, the implied warranties of merchantibility, fitness for a particular
 * purpose, or non-infringement. Cinnober shall not be liable for any
 * damages suffered by licensee as a result of using, modifying, or
 * distributing this software or its derivatives.
 */
package com.cinnober.msgcodec.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.cinnober.msgcodec.Epoch;
import com.cinnober.msgcodec.TypeDef;
import com.cinnober.msgcodec.TypeDef.Symbol;
import com.cinnober.msgcodec.anot.Id;
import com.cinnober.msgcodec.anot.Name;
import com.cinnober.msgcodec.anot.Required;

/**
 * @author mikael.brannstrom
 *
 */
@Name("TypeDef")
public class MetaTypeDef extends MetaAnnotated {

    public static final MetaInt8 INT8 = new MetaInt8();
    public static final MetaInt16 INT16 = new MetaInt16();
    public static final MetaInt32 INT32 = new MetaInt32();
    public static final MetaInt64 INT64 = new MetaInt64();

    public static final MetaUInt8 UINT8 = new MetaUInt8();
    public static final MetaUInt16 UINT16 = new MetaUInt16();
    public static final MetaUInt32 UINT32 = new MetaUInt32();
    public static final MetaUInt64 UINT64 = new MetaUInt64();

    public static final MetaFloat32 FLOAT32 = new MetaFloat32();
    public static final MetaFloat64 FLOAT64 = new MetaFloat64();

    public static final MetaString STRING = new MetaString();
    public static final MetaBinary BINARY = new MetaBinary();

    public static final MetaDecimal DECIMAL = new MetaDecimal();
    public static final MetaBoolean BOOLEAN = new MetaBoolean();
    public static final MetaBigDecimal BIGDECIMAL = new MetaBigDecimal();
    public static final MetaBigInt BIGINT = new MetaBigInt();

    public TypeDef toTypeDef() { return null; }

    @Name("Ref") @Id(16003)
    public static class MetaRef extends MetaTypeDef {
        @Required
        @Id(1)
        private String type;
        public MetaRef() {}

        /**
         * @param type
         */
        public MetaRef(String type) {
            this.type = type;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }
        @Override
        public TypeDef toTypeDef() {
            return new TypeDef.Reference(type);
        }

    }
    @Name("DynRef") @Id(16004)
    public static class MetaDynRef extends MetaTypeDef {
        @Id(1)
        private String type;
        public MetaDynRef() {}

        /**
         * @param type
         */
        public MetaDynRef(String type) {
            this.type = type;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }
        @Override
        public TypeDef toTypeDef() {
            return new TypeDef.DynamicReference(type);
        }
    }
    @Name("Sequence") @Id(16005)
    public static class MetaSequence extends MetaTypeDef {
        @Required
        @Id(1)
        private MetaTypeDef type;
        public MetaSequence() {}

        /**
         * @param type
         */
        public MetaSequence(MetaTypeDef type) {
            this.type = type;
        }


        /**
         * @return the type
         */
        public MetaTypeDef getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(MetaTypeDef type) {
            this.type = type;
        }
        @Override
        public TypeDef toTypeDef() {
            return new TypeDef.Sequence(type.toTypeDef());
        }
    }

    @Name("Time") @Id(16006)
    public static class MetaTime extends MetaTypeDef {
        @Required
        @Id(1)
        private TimeUnit unit;
        @Required
        @Id(2)
        private Epoch epoch;
        @Id(3)
        private String timeZone;

        public MetaTime() {}

        /**
         * @param unit
         * @param epoch
         * @param timeZone
         */
        public MetaTime(TimeUnit unit, Epoch epoch, String timeZone) {
            this.unit = unit;
            this.epoch = epoch;
            this.timeZone = timeZone;
        }


        /**
         * @return the unit
         */
        public TimeUnit getUnit() {
            return unit;
        }
        /**
         * @param unit the unit to set
         */
        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }
        /**
         * @return the epoch
         */
        public Epoch getEpoch() {
            return epoch;
        }
        /**
         * @param epoch the epoch to set
         */
        public void setEpoch(Epoch epoch) {
            this.epoch = epoch;
        }
        /**
         * @return the timeZone
         */
        public String getTimeZone() {
            return timeZone;
        }
        /**
         * @param timeZone the timeZone to set
         */
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
        @Override
        public TypeDef toTypeDef() {
            return new TypeDef.Time(unit, epoch, timeZone != null ? TimeZone.getTimeZone(timeZone) : null);
        }
    }

    @Name("Enum") @Id(16007)
    public static class MetaEnum extends MetaTypeDef {
        @Required
        @com.cinnober.msgcodec.anot.Sequence(MetaSymbol.class)
        @Id(1)
        private Collection<MetaSymbol> symbols;

        public MetaEnum() {}

        /**
         * @param symbols
         */
        public MetaEnum(Collection<MetaSymbol> symbols) {
            this.symbols = symbols;
        }

        /**
         * @return the symbols
         */
        public Collection<MetaSymbol> getSymbols() {
            return symbols;
        }
        /**
         * @param symbols the symbols to set
         */
        public void setSymbols(Collection<MetaSymbol> symbols) {
            this.symbols = symbols;
        }
        @Override
        public TypeDef toTypeDef() {
            List<Symbol> enumSymbols = new ArrayList<>(symbols.size());
            for (MetaSymbol symbol : symbols) {
                enumSymbols.add(symbol.toSymbol());
            }
            return new TypeDef.Enum(enumSymbols);
        }
    }

    @Name("String") @Id(16008)
    public static class MetaString extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.STRING; }
    }
    @Name("Binary") @Id(16009)
    public static class MetaBinary extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.BINARY; }
    }

    @Name("I8") @Id(16010)
    public static class MetaInt8 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.INT8; }
    }
    @Name("I16") @Id(16011)
    public static class MetaInt16 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.INT16; }
    }
    @Name("I32") @Id(16012)
    public static class MetaInt32 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.INT32; }
    }
    @Name("I64") @Id(16013)
    public static class MetaInt64 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.INT64; }
    }

    @Name("U8") @Id(16014)
    public static class MetaUInt8 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.UINT8; }
    }
    @Name("U16") @Id(16015)
    public static class MetaUInt16 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.UINT16; }
    }
    @Name("U32") @Id(16016)
    public static class MetaUInt32 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.UINT32; }
    }
    @Name("U64") @Id(16017)
    public static class MetaUInt64 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.UINT64; }
    }

    @Name("F32") @Id(16018)
    public static class MetaFloat32 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.FLOAT32; }
    }
    @Name("F64") @Id(16019)
    public static class MetaFloat64 extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.FLOAT64; }
    }

    @Name("Boolean") @Id(16020)
    public static class MetaBoolean extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.BOOLEAN; }
    }

    @Name("Decimal") @Id(16021)
    public static class MetaDecimal extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.DECIMAL; }
    }

    @Name("BigDecimal") @Id(16022)
    public static class MetaBigDecimal extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.BIGDECIMAL; }
    }
    @Name("BigInt") @Id(16023)
    public static class MetaBigInt extends MetaTypeDef {
        @Override
        public TypeDef toTypeDef() { return TypeDef.BIGINT; }
    }

    @Name("Symbol")
    public static class MetaSymbol extends MetaAnnotated {
        @Required
        @Id(1)
        private String name;
        @Id(2)
        private int id;

        public MetaSymbol() {}

        /**
         * @param name
         * @param id
         */
        public MetaSymbol(String name, int id) {
            this.name = name;
            this.id = id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * @return the id
         */
        public int getId() {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }

        public Symbol toSymbol() {
            return new Symbol(name, id);
        }
    }
}
