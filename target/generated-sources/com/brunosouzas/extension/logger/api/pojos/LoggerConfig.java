
package com.brunosouzas.extension.logger.api.pojos;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Placement;


/**
 * Definition for fields globally defined in the logger config
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "globalSettings",
    "jsonOutput"
})
public class LoggerConfig {

    @JsonProperty("globalSettings")
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Placement(order = 1)
    @ParameterGroup(name = "Global Settings")
    private GlobalSettings globalSettings;
    @JsonProperty("jsonOutput")
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterGroup(name = "JSON Output")
    private JsonOutput jsonOutput;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("globalSettings")
    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }

    @JsonProperty("globalSettings")
    public void setGlobalSettings(GlobalSettings globalSettings) {
        this.globalSettings = globalSettings;
    }

    @JsonProperty("jsonOutput")
    public JsonOutput getJsonOutput() {
        return jsonOutput;
    }

    @JsonProperty("jsonOutput")
    public void setJsonOutput(JsonOutput jsonOutput) {
        this.jsonOutput = jsonOutput;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(globalSettings).append(jsonOutput).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LoggerConfig) == false) {
            return false;
        }
        LoggerConfig rhs = ((LoggerConfig) other);
        return new EqualsBuilder().append(globalSettings, rhs.globalSettings).append(jsonOutput, rhs.jsonOutput).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
