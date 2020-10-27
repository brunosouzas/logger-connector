package com.brunosouzas.extension.logger.destinations;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.client.ExtensionsClient;
import org.mule.runtime.http.api.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPDestination implements Destination {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPDestination.class);
    
    @Inject
    ExtensionsClient extensionsClient;

    @Inject
    protected HttpService httpService;
    
    private CloseableHttpClient client;
    
    @Parameter
    @Summary("URL used to log API with POST method")
    @DisplayName("URL from Log API")
    private String url;

    @Parameter
    @Optional
    @NullSafe
    @Summary("Password to use in Authentication your API (Cripty Password to Basic Authentication). If empty, all will be send.")
    @DisplayName("Password Authentication")
    private String passwordAuthentication;
    
    @Parameter
    @Optional
    @NullSafe
    @Summary("Indicate which log categories should be send (e.g. [\"my.category\",\"another.category\"]). If empty, all will be send.")
    @DisplayName("Log Categories")
    private ArrayList<String> logCategories;
    
    @Override
    public String getSelectedDestinationType() {
        return "HTTP";
    }
    
    @Override
    public ArrayList<String> getSupportedCategories() {
        return logCategories;
    }

    @Override
    public void sendToExternalDestination(String finalLog) {
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			StringEntity entity = new StringEntity(finalLog);
	    	
	        HttpPost post = new HttpPost(this.url);
	    	post.setEntity(entity);
	    	
	    	if (passwordAuthentication != "" || passwordAuthentication != null)
	    		post.setHeader("Authentication", this.passwordAuthentication);
	    	
	    	client.execute(post);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			LOGGER.error("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }

    @Override
    public void initialise() {
    	
    }

    @Override
    public void dispose() {
    	try {
			client.close();
		} catch (IOException e) {
			LOGGER.error("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }

	@Override
	public int getMaxBatchSize() {
		return 0;
	}
}
