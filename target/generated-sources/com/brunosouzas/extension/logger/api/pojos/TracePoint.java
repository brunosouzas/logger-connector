
package com.brunosouzas.extension.logger.api.pojos;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TracePoint {

    START("START"),
    BEFORE_TRANSFORM("BEFORE_TRANSFORM"),
    AFTER_TRANSFORM("AFTER_TRANSFORM"),
    BEFORE_REQUEST("BEFORE_REQUEST"),
    AFTER_REQUEST("AFTER_REQUEST"),
    FLOW("FLOW"),
    END("END"),
    EXCEPTION("EXCEPTION");
    private final String value;
    private final static Map<String, TracePoint> CONSTANTS = new HashMap<String, TracePoint>();

    static {
        for (TracePoint c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private TracePoint(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static TracePoint fromValue(String value) {
        TracePoint constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
