/* 
 * (C) Copyright Kalidus DBA Skava, 2018 all rights reserved
 * 
 * This code is provided on an as-is basis for the illustration of invoking the Skava API. It is not warranted to be fit for any other purpose. 
 * This code is not optimized for production purposes. 
 *  
 */ 
 
package com.skava.enablement;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Used to generate the JWT 
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.web.client.HttpClientErrorException;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

// Used to call the catalog service
import com.skava.commerce.catalog.client.api.CatalogsApi;
import com.skava.commerce.catalog.client.invoker.ApiClient;
import com.skava.commerce.catalog.client.model.CatalogsAPIResponse;


public class MakeTokenAndGetCatalogs {
	/**
	 * 
	 * This sets up the API to call the proper instance, and populates a default
	 * header with the API key required to access our services through the cloud
	 * deployment Web Application Firewall
	 * 
	 * @param sfDetails
	 * @return
	 */

	private static ApiClient setupApi(StorefrontDetails sfDetails) {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("https://" + sfDetails.getInstanceName() + "/catalogservices");
		apiClient.setDebugging(true);
		apiClient.addDefaultHeader("x-api-key", sfDetails.getApiKey());
		return apiClient;
	}
   /**
    *
    * Use the Skava SDK to fetch the catalogs for a given store. Details are passed in via the 'StorefrontDescription" class
    * 
    * @param sfd
    */
	public static void getCatalogs(StorefrontDetails sfd) {
		ApiClient apiClient = setupApi(sfd);
		CatalogsApi api = new CatalogsApi(apiClient);
		Long xCollectionId = sfd.getCollection();
		String xAuthToken = sfd.getAuthToken();
		String xVersion = null;
		Integer page = 1;
		Integer size = 50;
		String sort = null;
		String filters = null;
		String locale = null;
		CatalogsAPIResponse response = null;
		try {
			response = api.getCatalogs(xCollectionId, xAuthToken, xVersion, page, size, sort, filters, locale);
		} catch (HttpClientErrorException e) {
			System.out.println("Error " + e.getRawStatusCode() + " when calling getCatalogs");
			System.out.println(e.getResponseBodyAsString());
			return;
		} catch (Exception e) {
			System.out.println("Exception while calling getCatalogs " + e.getMessage());
		}
		System.out.println("Catalogs response: " + response.toString());
	}

	/**
	 * 
	 * Create a valid JWT to access the skava microservices
	 * 
	 * @param sharedSecret
	 * @return
	 */
	
	public static String generateJWT(String sharedSecret, String bidString) {

		String userid = "xxx";
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("authorities",
			"[{\"roles\":{\"ROLE_BUSINESS_ADMIN\":{\"business\":{\""+ bidString +"\":{}},\"type\":\"STANDARD\"}}}]");
		claims.put("username", userid);
		claims.put("created", Long.valueOf(System.currentTimeMillis()));
		Gson gson = new Gson();
		String claimsAsJson = gson.toJson(claims, Map.class);
		System.out.println("Claims as json " + claimsAsJson);
		String key = generateKeyWithClaims(sharedSecret, claims, userid);
		System.out.println("admin key with claims " + key);
		return key;
	}

	/**
	 * This code does the hashed signing and calls the jwt.io library top create a valid token 
	 * @param sharedSecret
	 * @param claims
	 * @param userid
	 * @return
	 */
	
	private static String generateKeyWithClaims(String sharedSecret, Map<String, Object> claims, String userid) {
		return Jwts.builder().setSubject(userid).setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)).setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, Sha512DigestUtils.shaHex(sharedSecret)).compact();

	}

	/**
	 * This main solicits the inputs, and calls the methods to:
	 *  	* generate the JWT
	 *      * get the catlogs for this business 
	 * 
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		StorefrontDetails sfd = new StorefrontDetails();
		Scanner scan = new Scanner(System.in);
		String instance = "stratus.skavacommerce.com";
		System.out.println("Enter Shared Secret : ");
		String sharedSecret = scan.nextLine();
		System.out.println("enter business:[50]");
		String businessId = scan.nextLine();
		if (businessId.equals("")) {
			businessId = "50";
		}
		long bid = new Long(businessId);
		sfd.setBusiness(bid);
		String authToken = generateJWT(sharedSecret,businessId);
		sfd.setAuthToken(authToken);
		System.out.println("Enter api key:");
		String apiKey = scan.nextLine();
		
		
		System.out.println("Enter collection[268]:");
		String collection = scan.nextLine();
		if (collection.equals("")) {
			collection = "268";
		}
		long coll = new Long(collection);
		sfd.setBusiness(bid);
		sfd.setCollection(coll);
		sfd.setInstanceName(instance);
		sfd.setApiKey(apiKey);
		scan.close();
		getCatalogs(sfd);

	}

}
