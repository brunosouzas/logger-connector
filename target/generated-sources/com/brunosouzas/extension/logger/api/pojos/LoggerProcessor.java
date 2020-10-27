
package com.brunosouzas.extension.logger.api.pojos;

import java.io.InputStream;
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
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.parameter.ParameterResolver;


/**
 * Definition for fields used in the logger message processor
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "correlationId",
    "sourceSystem",
    "errorCode",
    "message",
    "content",
    "tracePoint",
    "priority",
    "category"
})
public class LoggerProcessor {

    @JsonProperty("correlationId")
    @Parameter
    @Optional(defaultValue = "#[correlationId]")
    @Placement(tab = "Advanced")
    private String correlationId;
    @JsonProperty("sourceSystem")
    @Parameter
    @Optional
    @Summary("code used to identify the source system of the error.")
    private SourceSystem sourceSystem;
    @JsonProperty("errorCode")
    @Parameter
    @Optional
    @Summary("error code used to identify the problem in the source system")
    @Example("ZSD-001")
    private String errorCode;
    @JsonProperty("message")
    @Parameter
    @Summary("Message to be logged or Source system error message")
    @Example("Add a log message")
    private String message;
    @JsonProperty("content")
    @Parameter
    @Optional(defaultValue = "#[import modules::loggerModule output application/json ---\n{\n    payload: loggerModule::stringifyNonJSON(payload) \n}]")
    @Summary("NOTE: Writing the entire payload every time across your application can cause serious performance issues")
    @Content
    private ParameterResolver<TypedValue<InputStream>> content;
    @JsonProperty("tracePoint")
    @Parameter
    @Optional(defaultValue = "START")
    @Summary("Current processing stage")
    private TracePoint tracePoint;
    @JsonProperty("priority")
    @Parameter
    @Optional(defaultValue = "INFO")
    @Summary("Logger priority")
    private Priority priority;
    @JsonProperty("category")
    @Parameter
    @Optional
    @Summary("If not set, by default will log to the com.brunosouzas.logger category")
    private String category;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("correlationId")
    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("correlationId")
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @JsonProperty("sourceSystem")
    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    @JsonProperty("sourceSystem")
    public void setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @JsonProperty("errorCode")
    public String getErrorCode() {
        return errorCode;
    }

    @JsonProperty("errorCode")
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("content")
    public ParameterResolver<TypedValue<InputStream>> getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(ParameterResolver<TypedValue<InputStream>> content) {
        this.content = content;
    }

    @JsonProperty("tracePoint")
    public TracePoint getTracePoint() {
        return tracePoint;
    }

    @JsonProperty("tracePoint")
    public void setTracePoint(TracePoint tracePoint) {
        this.tracePoint = tracePoint;
    }

    @JsonProperty("priority")
    public Priority getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
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
        return new HashCodeBuilder().append(correlationId).append(sourceSystem).append(errorCode).append(message).append(content).append(tracePoint).append(priority).append(category).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LoggerProcessor) == false) {
            return false;
        }
        LoggerProcessor rhs = ((LoggerProcessor) other);
        return new EqualsBuilder().append(correlationId, rhs.correlationId).append(sourceSystem, rhs.sourceSystem).append(errorCode, rhs.errorCode).append(message, rhs.message).append(content, rhs.content).append(tracePoint, rhs.tracePoint).append(priority, rhs.priority).append(category, rhs.category).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
