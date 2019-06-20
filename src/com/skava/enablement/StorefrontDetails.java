package com.skava.enablement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class StorefrontDetails {
	
	private String instanceName; 
	private long business;
	private long store;
	private long collection;
	private String authToken;
	private String sessionId;
	private String sharedSecret;
	private String catalog;
	private String project;
	private String apiKey;
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public long getBusiness() {
		return business;
	}
	public void setBusiness(long business) {
		this.business = business;
	}
	public long getStore() {
		return store;
	}
	public void setStore(long store) {
		this.store = store;
	}
	public long getCollection() {
		return collection;
	}
	public void setCollection(long collection) {
		this.collection = collection;
	}
	
	public String toString()
	{
	Gson gson = new Gson();
	String jsonString = gson.toJson(this);
	return jsonString;	
	}

	private static String readFromInputStream(InputStream inputStream)
			  throws IOException {
			    StringBuilder resultStringBuilder = new StringBuilder();
			    try (BufferedReader br
			      = new BufferedReader(new InputStreamReader(inputStream))) {
			        String line;
			        while ((line = br.readLine()) != null) {
			            resultStringBuilder.append(line).append("\n");
			        }
			    }
			  return resultStringBuilder.toString();
			}
	
	public static StorefrontDetails buildFromFile(String filename)
	{
	Class clazz = StorefrontDetails.class;
    InputStream inputStream = clazz.getResourceAsStream(filename);
    String jsonFromFile;
	try {
		jsonFromFile = readFromInputStream(inputStream);
	} catch (IOException e) {
		System.out.println("Error reading from file "+filename);
		return null;
	}
    Gson gson = new Gson();
    StorefrontDetails constructedSfd = gson.fromJson(jsonFromFile, StorefrontDetails.class);
	return constructedSfd;	
	}
	
	
}
