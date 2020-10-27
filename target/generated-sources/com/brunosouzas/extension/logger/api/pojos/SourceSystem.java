
package com.brunosouzas.extension.logger.api.pojos;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SourceSystem {

    GENERAL("GENERAL");
    private final String value;
    private final static Map<String, SourceSystem> CONSTANTS = new HashMap<String, SourceSystem>();

    static {
        for (SourceSystem c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private SourceSystem(String value) {
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
    public static SourceSystem fromValue(String value) {
        SourceSystem constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
