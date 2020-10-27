package com.brunosouzas.extension.logger.internal;

import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

import com.brunosouzas.extension.logger.destinations.AMQDestination;
import com.brunosouzas.extension.logger.destinations.AMQPDestination;
import com.brunosouzas.extension.logger.destinations.Destination;
import com.brunosouzas.extension.logger.destinations.HTTPDestination;
import com.brunosouzas.extension.logger.destinations.JMSDestination;
import com.brunosouzas.extension.logger.destinations.SQSDestination;

/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "logger")
@Extension(name = "Logger")
@Export(resources = {"modules/loggerModule.dwl"})
@Configurations(LoggerConfiguration.class)
@SubTypeMapping(baseType = Destination.class,
        subTypes = {JMSDestination.class, AMQDestination.class, AMQPDestination.class, HTTPDestination.class, SQSDestination.class})
public class LoggerExtension {

}
