
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
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "applicationName",
    "applicationVersion",
    "environment"
})
public class GlobalSettings {

    @JsonProperty("applicationName")
    @Parameter
    @Optional(defaultValue = "${api.name}")
    @Summary("Name of the Mule application. Recommendation: This value should be based on pom.xml")
    private String applicationName;
    @JsonProperty("applicationVersion")
    @Parameter
    @Optional(defaultValue = "${api.version}")
    @Summary("Version of the Mule application. Recommendation: This value should be based on pom.xml")
    private String applicationVersion;
    @JsonProperty("environment")
    @Parameter
    @Summary("Name of the Mule Environment where the application is running. Recommendation: This value should be based on external property")
    @Example("${mule.env}")
    private String environment;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("applicationName")
    public String getApplicationName() {
        return applicationName;
    }

    @JsonProperty("applicationName")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @JsonProperty("applicationVersion")
    public String getApplicationVersion() {
        return applicationVersion;
    }

    @JsonProperty("applicationVersion")
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    @JsonProperty("environment")
    public String getEnvironment() {
        return environment;
    }

    @JsonProperty("environment")
    public void setEnvironment(String environment) {
        this.environment = environment;
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
        return new HashCodeBuilder().append(applicationName).append(applicationVersion).append(environment).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GlobalSettings) == false) {
            return false;
        }
        GlobalSettings rhs = ((GlobalSettings) other);
        return new EqualsBuilder().append(applicationName, rhs.applicationName).append(applicationVersion, rhs.applicationVersion).append(environment, rhs.environment).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
