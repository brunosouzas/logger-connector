package com.brunosouzas.extension.logger.internal;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.metadata.DataType.TEXT_STRING;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.operation.FlowListener;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.parameter.ParameterResolver;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brunosouzas.extension.logger.api.pojos.LoggerProcessor;
import com.brunosouzas.extension.logger.internal.datamask.JsonMasker;
import com.brunosouzas.extension.logger.singleton.ConfigsSingleton;
import com.brunosouzas.extension.logger.singleton.LogEventSingleton;
import com.brunosouzas.extension.logger.singleton.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class LoggerOperations {

    private static final String CATEGORY_DEFAULT = "com.brunosouzas.logger";
	/**
     * jsonLogger: JSON Logger output log
     * log: Connector internal log
     */
    protected transient Logger customLogger;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerOperations.class);

    // Void Result for NIO
    private final Result<Void, Void> VOID_RESULT = Result.<Void, Void>builder().build();

    // JSON Object Mapper
    @Inject
    ObjectMapperSingleton om;

    // Log Event for External Destination
    @Inject
    LogEventSingleton logEvent;

    // Global definition of logger configs so that it's available for scope processor (SDK scope doesn't support passing configurations)
    @Inject
    ConfigsSingleton configs;

    // Transformation Service
    @Inject
    private TransformationService transformationService;

    /**
     * Log a new entry
     */
    @Execution(ExecutionType.BLOCKING)
    public void logger(@ParameterGroup(name = "Logger") @Expression(value = NOT_SUPPORTED) LoggerProcessor loggerProcessor,
                       CorrelationInfo correlationInfo,
                       ComponentLocation location,
                       @Config LoggerConfiguration config,
                       FlowListener flowListener,
                       CompletionCallback<Void, Void> callback) {

        Long initialTimestamp, loggerTimestamp;
        initialTimestamp = loggerTimestamp = System.currentTimeMillis();

        initLoggerCategory(loggerProcessor.getCategory());

        LOGGER.debug("correlationInfo.getEventId(): " + correlationInfo.getEventId());
        LOGGER.debug("correlationInfo.getCorrelationId(): " + correlationInfo.getCorrelationId());

        try {
            // Add cache entry for initial timestamp based on unique EventId
            initialTimestamp = config.getCachedTimerTimestamp(correlationInfo.getCorrelationId(), initialTimestamp);
        } catch (Exception e) {
            LOGGER.error("initialTimestamp could not be retrieved from the cache config. Defaulting to current System.currentTimeMillis()", e);
        }

        // Calculate elapsed time based on cached initialTimestamp
        Long elapsed = loggerTimestamp - initialTimestamp;

        //config.printTimersKeys();
        if (elapsed == 0) {
            LOGGER.debug("configuring flowListener....");
            flowListener.onComplete(new TimerRemoverRunnable(correlationInfo.getCorrelationId(), config));
        } else {
            LOGGER.debug("flowListener already configured");
        }

        /**
         * Avoid Logger logic execution based on log priority
         */
        if (isLogEnabled(loggerProcessor.getPriority().toString())) {
            // Load disabledFields
            List<String> disabledFields = (config.getJsonOutput().getDisabledFields() != null) ? Arrays.asList(config.getJsonOutput().getDisabledFields().split(",")) : new ArrayList<>();
            LOGGER.debug("The following fields will be disabled for logging: " + disabledFields);

            // Logic to disable fields and/or parse TypedValues as String for JSON log printing
            //Map<String, String> typedValuesAsString = new HashMap<>();
            Map<String, String> typedValuesAsString = new HashMap<>();
            Map<String, JsonNode> typedValuesAsJsonNode = new HashMap<>();
            try {
                PropertyUtils.describe(loggerProcessor).forEach((k, v) -> {
                    if (disabledFields.stream().anyMatch(k::equals)) {
                        try {
                            BeanUtils.setProperty(loggerProcessor, k, null);
                        } catch (Exception e) {
                            LOGGER.error("Failed disabling field: " + k, e);
                        }
                    } else {
                        if (v != null) {
                            try {
                                if (v instanceof ParameterResolver) {
                                    v = ((ParameterResolver) v).resolve();
                                }
                                if (v.getClass().getCanonicalName().equals("org.mule.runtime.api.metadata.TypedValue")) {
                                    LOGGER.debug("org.mule.runtime.api.metadata.TypedValue type was found for field: " + k);
                                    TypedValue<InputStream> typedVal = (TypedValue<InputStream>) v;
                                    LOGGER.debug("Parsing TypedValue for field " + k);

                                    LOGGER.debug("TypedValue MediaType: " + typedVal.getDataType().getMediaType());
                                    LOGGER.debug("TypedValue Type: " + typedVal.getDataType().getType().getCanonicalName());
                                    LOGGER.debug("TypedValue Class: " + typedVal.getValue().getClass().getCanonicalName());

                                    // Remove unparsed field
                                    BeanUtils.setProperty(loggerProcessor, k, null);

                                    // Evaluate if typedValue is null
                                    if (typedVal.getValue() != null) {
                                        // Should content type field be parsed as part of JSON log?
                                        if (config.getJsonOutput().isParseContentFieldsInJsonOutput()) {
                                            // Is content type application/json?
                                            if (typedVal.getDataType().getMediaType().getPrimaryType().equals("application") && typedVal.getDataType().getMediaType().getSubType().equals("json")) {
                                                // Apply masking if needed
                                                List<String> dataMaskingFields = (config.getJsonOutput().getContentFieldsDataMasking() != null) ? Arrays.asList(config.getJsonOutput().getContentFieldsDataMasking().split(",")) : new ArrayList<>();
                                                LOGGER.debug("The following JSON keys/paths will be masked for logging: " + dataMaskingFields);
                                                if (!dataMaskingFields.isEmpty()) {
                                                    JsonNode tempContentNode = om.getObjectMapper().readTree((InputStream)typedVal.getValue());
                                                    JsonMasker masker = new JsonMasker(dataMaskingFields, true);
                                                    JsonNode masked = masker.mask(tempContentNode);
                                                    typedValuesAsJsonNode.put(k, masked);
                                                } else {
                                                    typedValuesAsJsonNode.put(k, om.getObjectMapper().readTree((InputStream)typedVal.getValue()));
                                                }
                                            } else {
                                                typedValuesAsString.put(k, (String) transformationService.transform(typedVal.getValue(), typedVal.getDataType(), TEXT_STRING));
                                            }
                                        } else {
                                            typedValuesAsString.put(k, (String) transformationService.transform(typedVal.getValue(), typedVal.getDataType(), TEXT_STRING));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("Failed parsing field: " + k, e);
                                typedValuesAsString.put(k, "Error parsing expression. See logs for details.");
                            }
                        }
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Unknown error while processing the logger object", e);
            }

            // Aggregate Logger data into mergedLogger
            ObjectNode mergedLogger = om.getObjectMapper().createObjectNode();
            mergedLogger.setAll((ObjectNode) om.getObjectMapper().valueToTree(loggerProcessor));

            /**
             * Custom field ordering for Logger Operation
             * ==========================================
             * This will take place after LoggerProcessor ordering which is defined by the field sequence in loggerProcessor.json
             **/
            // 1. Elapsed Time
            mergedLogger.put("elapsed", elapsed);
            // 2. Location Info: Logger location within Mule application
            if (config.getJsonOutput().isLogLocationInfo()) {
                Map<String, String> locationInfo = locationInfoToMap(location);
                mergedLogger.putPOJO("locationInfo", locationInfo);
            }
            // 3. Timestamp: Add formatted timestamp entry to the logger
            mergedLogger.put("timestamp", getFormattedTimestamp(loggerTimestamp));
            // 4. Content fields: String based fields
            if (!typedValuesAsString.isEmpty()) {
                JsonNode typedValuesNode = om.getObjectMapper().valueToTree(typedValuesAsString);
                mergedLogger.setAll((ObjectNode) typedValuesNode);
            }
            // 5. Content fields: JSONNode based fields
            if (!typedValuesAsJsonNode.isEmpty()) {
                mergedLogger.setAll(typedValuesAsJsonNode);
            }
            // 6. Global info from config
            mergedLogger.setAll((ObjectNode) om.getObjectMapper().valueToTree(config.getGlobalSettings()));
            // 7. Thread Name
            mergedLogger.put("threadName", Thread.currentThread().getName());
            /** End field ordering **/

            /** Print Logger **/
            String finalLog = printObjectToLog(mergedLogger, loggerProcessor.getPriority().toString(), config.getJsonOutput().isPrettyPrint());

            /** Forward Log to External Destination **/
            if (config.getExternalDestination() != null) {
                LOGGER.debug("config.getExternalDestination().getSupportedCategories().isEmpty(): " + config.getExternalDestination().getSupportedCategories().isEmpty());
                LOGGER.debug("config.getExternalDestination().getSupportedCategories().contains(jsonLogger.getName()): " + config.getExternalDestination().getSupportedCategories().contains(customLogger.getName()));
                if (configs.getConfig(config.getConfigName()).getExternalDestination().getSupportedCategories().isEmpty() || config.getExternalDestination().getSupportedCategories().contains(customLogger.getName())) {
                    LOGGER.debug(customLogger.getName() + " is a supported category for external destination");
                    logEvent.publishToExternalDestination(correlationInfo.getEventId(), finalLog, config.getConfigName());
                }
            }
        } else {
            LOGGER.debug("Avoiding logger operation logic execution due to log priority not being enabled");
        }
        callback.success(VOID_RESULT);
    }

    private Map<String, String> locationInfoToMap(ComponentLocation location) {
        Map<String, String> locationInfo = new HashMap<String, String>();
        //locationInfo.put("location", location.getLocation());
        locationInfo.put("rootContainer", location.getRootContainerName());
        locationInfo.put("component", location.getComponentIdentifier().getIdentifier().toString());
        locationInfo.put("fileName", location.getFileName().orElse(""));
        locationInfo.put("lineInFile", String.valueOf(location.getLineInFile().orElse(null)));
        return locationInfo;
    }

    private String getFormattedTimestamp(Long loggerTimestamp) {
    /*
        Define timestamp:
        - DateTime: Defaults to ISO format
        - TimeZone: Defaults to UTC. Refer to https://en.wikipedia.org/wiki/List_of_tz_database_time_zones for valid timezones
    */
        DateTime dateTime = new DateTime(loggerTimestamp).withZone(org.joda.time.DateTimeZone.forID(System.getProperty("json.logger.timezone", "UTC")));
        String timestamp = dateTime.toString();
        if (System.getProperty("json.logger.dateformat") != null && !System.getProperty("json.logger.dateformat").equals("")) {
            timestamp = dateTime.toString(System.getProperty("json.logger.dateformat"));
        }
        return timestamp;
    }

    private String printObjectToLog(ObjectNode loggerObj, String priority, boolean isPrettyPrint) {
        ObjectWriter ow = (isPrettyPrint) ? om.getObjectMapper().writer().withDefaultPrettyPrinter() : om.getObjectMapper().writer();
        String logLine = "";
        try {
            logLine = ow.writeValueAsString(loggerObj);
        } catch (Exception e) {
            LOGGER.error("Error parsing log data as a string", e);
        }
        doLog(priority.toString(), logLine);

        return logLine;
    }

    private void doLog(String priority, String logLine) {
        switch (priority) {
            case "TRACE":
                customLogger.trace(logLine);
                break;
            case "DEBUG":
                customLogger.debug(logLine);
                break;
            case "INFO":
                customLogger.info(logLine);
                break;
            case "WARN":
                customLogger.warn(logLine);
                break;
            case "ERROR":
                customLogger.error(logLine);
                break;
        }
    }

    private Boolean isLogEnabled(String priority) {
        switch (priority) {
            case "TRACE":
                return customLogger.isTraceEnabled();
            case "DEBUG":
                return customLogger.isDebugEnabled();
            case "INFO":
                return customLogger.isInfoEnabled();
            case "WARN":
                return customLogger.isWarnEnabled();
            case "ERROR":
                return customLogger.isErrorEnabled();
        }
        return false;
    }

    protected void initLoggerCategory(String category) {
        if (category != null) {
            customLogger = LoggerFactory.getLogger(category);
        } else {
            customLogger = LoggerFactory.getLogger(CATEGORY_DEFAULT);
        }
        LOGGER.debug("category set: " + customLogger.getName());
    }

    // Allows executing timer cleanup on flowListener onComplete events
    private static class TimerRemoverRunnable implements Runnable {

        private final String key;
        private final LoggerConfiguration config;

        public TimerRemoverRunnable(String key, LoggerConfiguration config) {
            this.key = key;
            this.config = config;
        }

        @Override
        public void run() {
            LOGGER.debug("Removing key: " + key);
            config.removeCachedTimerTimestamp(key);
        }
    }
}
