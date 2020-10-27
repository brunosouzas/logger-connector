package com.brunosouzas.extension.logger.destinations;

import java.util.ArrayList;

import javax.inject.Inject;

import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.client.ExtensionsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSDestination implements Destination {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSDestination.class);

    private final Integer maxBatchOpenMs = 60000;
    private final Integer maxInflightOutboundBatches = 100;
    private AmazonSQSBufferedAsyncClient client;
    
    @Inject
    ExtensionsClient extensionsClient;

    @Parameter
    @Optional
    @DisplayName("AWS Access Key")
    private String awsAccessKey;
    
    @Parameter
    @Optional
    @DisplayName("AWS Secret Key")
    private String awsSecretKey;

    @Parameter
    @Optional
    @Example("us-east-1")
    @DisplayName("AWS Region")
    private String awsRegion;
    
    @Parameter
    @Optional
    @DisplayName("AWS SQS Queue Name")
    private String queueName;
    
    @Parameter
    @Optional
    @DisplayName("AWS SQS Queue URL")
    private String queueUrl;

    @Parameter
    @Optional
    @NullSafe
    @Summary("Indicate which log categories should be send (e.g. [\"my.category\",\"another.category\"]). If empty, all will be send.")
    @DisplayName("Log Categories")
    private ArrayList<String> logCategories;

    @Parameter
    @Optional(defaultValue = "30")
    @Summary("Indicate max quantity of logs entries to be send to the external destination")
    @DisplayName("Max Batch Size")
    private int maxBatchSize;
    
    @Override
    public int getMaxBatchSize() {
        return this.maxBatchSize;
    }

    @Override
    public String getSelectedDestinationType() {
        return "SQS";
    }

    @Override
    public ArrayList<String> getSupportedCategories() {
        return logCategories;
    }

    @Override
    public void sendToExternalDestination(String finalLog) {
        try {
            SendMessageRequest request = new SendMessageRequest(this.queueUrl, finalLog);
            this.getClient().sendMessageAsync(request);
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialise() {

    }

    @Override
    public void dispose() {
    	
    }

     private AmazonSQSBufferedAsyncClient getClient() {
        if (this.client == null) {
            AmazonSQSAsync asyncClient;
            try {
                System.out.println("Initializing SQS Client: " + this.toString());
                AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
                AmazonSQSAsyncClientBuilder clientBuilder = AmazonSQSAsyncClientBuilder.standard();
                asyncClient = clientBuilder.withRegion(awsRegion).withCredentials(credentialsProvider).build();
                QueueBufferConfig config = new QueueBufferConfig().withMaxBatchOpenMs(maxBatchOpenMs).withMaxBatchSize(maxBatchSize).withMaxInflightOutboundBatches(maxInflightOutboundBatches);
                this.client = new AmazonSQSBufferedAsyncClient(asyncClient, config);
                this.queueUrl = this.client.getQueueUrl(this.queueName).getQueueUrl();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Error: " + e.getMessage());
                throw e;
            }
        }

        return this.client;
     }
}
