
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
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "prettyPrint",
    "logLocationInfo",
    "parseContentFieldsInJsonOutput",
    "disabledFields",
    "contentFieldsDataMasking"
})
public class JsonOutput {

    @JsonProperty("prettyPrint")
    @Parameter
    @Optional(defaultValue = "true")
    @Summary("Indicate if log entries should be formatted or single line")
    @Expression(ExpressionSupport.SUPPORTED)
    private boolean prettyPrint;
    @JsonProperty("logLocationInfo")
    @Parameter
    @Optional(defaultValue = "true")
    @Summary("Indicate if location information should be logged")
    @Expression(ExpressionSupport.SUPPORTED)
    private boolean logLocationInfo;
    @JsonProperty("parseContentFieldsInJsonOutput")
    @Parameter
    @Optional(defaultValue = "true")
    @Summary("Indicate if Content fields should be parsed as part of the JSON logger output")
    @Expression(ExpressionSupport.SUPPORTED)
    private boolean parseContentFieldsInJsonOutput;
    @JsonProperty("disabledFields")
    @Parameter
    @Optional
    @Summary("Indicate which fields (from JSON output) should be disabled from logging separated by comma. They should match the exact name given in loggerProcessor.json schema")
    @Example("message,content")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private String disabledFields;
    @JsonProperty("contentFieldsDataMasking")
    @Parameter
    @Optional
    @Summary("Indicate which fields (inside a content type with JSON output only) should be masked before from logging separated by comma. They can be JSON keys or JSON paths. If empty, no masking will be applied. Recommendation: This value should be based on external property")
    @Example("client_secret,password,$.myArray[1].someField,$..path.to.a.field")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private String contentFieldsDataMasking;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("prettyPrint")
    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    @JsonProperty("prettyPrint")
    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    @JsonProperty("logLocationInfo")
    public boolean isLogLocationInfo() {
        return logLocationInfo;
    }

    @JsonProperty("logLocationInfo")
    public void setLogLocationInfo(boolean logLocationInfo) {
        this.logLocationInfo = logLocationInfo;
    }

    @JsonProperty("parseContentFieldsInJsonOutput")
    public boolean isParseContentFieldsInJsonOutput() {
        return parseContentFieldsInJsonOutput;
    }

    @JsonProperty("parseContentFieldsInJsonOutput")
    public void setParseContentFieldsInJsonOutput(boolean parseContentFieldsInJsonOutput) {
        this.parseContentFieldsInJsonOutput = parseContentFieldsInJsonOutput;
    }

    @JsonProperty("disabledFields")
    public String getDisabledFields() {
        return disabledFields;
    }

    @JsonProperty("disabledFields")
    public void setDisabledFields(String disabledFields) {
        this.disabledFields = disabledFields;
    }

    @JsonProperty("contentFieldsDataMasking")
    public String getContentFieldsDataMasking() {
        return contentFieldsDataMasking;
    }

    @JsonProperty("contentFieldsDataMasking")
    public void setContentFieldsDataMasking(String contentFieldsDataMasking) {
        this.contentFieldsDataMasking = contentFieldsDataMasking;
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
        return new HashCodeBuilder().append(prettyPrint).append(logLocationInfo).append(parseContentFieldsInJsonOutput).append(disabledFields).append(contentFieldsDataMasking).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof JsonOutput) == false) {
            return false;
        }
        JsonOutput rhs = ((JsonOutput) other);
        return new EqualsBuilder().append(prettyPrint, rhs.prettyPrint).append(logLocationInfo, rhs.logLocationInfo).append(parseContentFieldsInJsonOutput, rhs.parseContentFieldsInJsonOutput).append(disabledFields, rhs.disabledFields).append(contentFieldsDataMasking, rhs.contentFieldsDataMasking).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
