package org.avr.epfapi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.avr.epfapi.exceptions.EPFexception;
import org.avr.epfapi.exceptions.EPFloginException;
import org.avr.epfapi.exceptions.KeysMissingException;
import org.avr.epfapi.exceptions.SecurityTokenException;
import org.avr.epfapi.json.ListReq;
import org.avr.epfapi.json.Product;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class EPF_impl implements EPF {
	
	private String epfLogonKey = "";
	private String epfTokenKey = "";
	
	private String domainUser = "";
	private CharSequence domainPWD;
	private static CredentialsProvider credsProvider;
	
	private HttpHost target = new HttpHost("epfws.usps.gov" , -1 , "https");
	private HttpHost proxy = new HttpHost("proxy.aetna.com", 9119 );
	
	private RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	
	
	private int retryAttempts =0;
	
	
	
	/**
	 * 
	 */
	@Override
	public String getVersion() throws EPFexception {
		CloseableHttpClient client = openClient();
		
		HttpGet get = new HttpGet("/ws/resources/epf/version");
		get.setConfig(config);
		
		HttpResponse resp = executeRequest(client, get);
		
		try {
			if ( 200 == resp.getStatusLine().getStatusCode()) {
				JSONObject json = (JSONObject)JSONValue.parse( new InputStreamReader( resp.getEntity().getContent() ) );
				return json.get("version").toString();
			}
			credsProvider = null;		//  MUST get cleared out
			throw new EPFexception("HTTP "+ resp.getStatusLine().getStatusCode() +".  Wrong Password?");
		} catch (IOException ioEx) {
			throw new EPFexception(ioEx, "From getVersion()");
		} finally {
			closeClient(client);
		}
	}
	
	
	
	
	/**
	 * Perform both HTTPGet and HTTPPost from a single method
	 * @param client
	 * @param req
	 * @return
	 */
	private HttpResponse executeRequest(HttpClient client , HttpUriRequest req) throws EPFexception {
		try {
			HttpResponse resp = client.execute( target , req );
			this.retryAttempts = 0;
			return resp;
		} catch ( ClientProtocolException cpEx) {
			cpEx.printStackTrace();
			throw new EPFexception("ClientProtocolException : "+ cpEx.getMessage());
		} catch ( IOException ioEx) {
			ioEx.printStackTrace();			
			throw new EPFexception("IOException : "+ ioEx.getMessage());
		}
	}
	
	
	

	@Override
	public void login(String user, CharSequence pwd) throws EPFloginException {
		JSONObject json = new JSONObject();
		json.put("login" , user);
		json.put("pword" , pwd.toString());
		
		try {
			processResponseAndStoreKeys( makeRequest("/ws/resources/epf/login", json) );
		} catch (EPFexception epfEx) {
			throw new EPFloginException(epfEx.getMessage());
		}
	}
	
	private HttpResponse makeRequest(String uri , JSONObject json) throws EPFexception {
		CloseableHttpClient client = openClient();
		
		HttpPost post = new HttpPost(uri);
		post.setConfig(config);
		
		try {
			StringEntity input = new StringEntity( "obj="+ json);
			input.setContentType("application/x-www-form-urlencoded");
			post.setEntity( input );
		} catch (UnsupportedEncodingException encEx) {
			EPFexception ex = new EPFexception("UnsupportedEncodingException :  check the JSON ["+ json +"]");
			ex.setOriginalException(encEx);
			throw ex;
		}
		
		return executeRequest(client, post);
	}

	@Override
	public void logout() throws EPFloginException {
		JSONObject json = addKeysToJSON( new JSONObject() );
		
		try {
			processResponse( makeRequest("/ws/resources/epf/logout", json) );
		} catch (KeysMissingException kmEx) {
			/* This is not a problem when logging out */
		} catch (EPFexception epfEx) {
			epfEx.printStackTrace();
		}
	}

	@Override
	public List<Product> listFiles( ListReq product ) throws EPFexception {
		JSONObject json = addKeysToJSON( new JSONObject() );
		json.put("productcode" , product.getProductcode());
		json.put("productid" , product.getProductid());
		
		if (product.getStatus() != null &&  product.getStatus().length() > 0)
			json.put("status" , product.getStatus());

		
		try {
			HttpResponse resp = processResponseAndStoreKeys( makeRequest("/ws/resources/download/list", json) );
			JSONObject val = (JSONObject)JSONValue.parse(new InputStreamReader(resp.getEntity().getContent()));
			
			if (val != null) {
				List<Product> products = new ArrayList<Product>();
				if ( val.get("fileList") != null ) {
					JSONArray availableFiles = (JSONArray)JSONValue.parse( val.get("fileList").toString() );
					if (availableFiles.size() == 1 ) {
						products.add( jsonToProduct( (JSONObject)availableFiles.get(0) ) );
					} else {
						for (Object obj : availableFiles) {
							products.add( jsonToProduct( (JSONObject)obj ) );
						}
					}				
				}
				/* if val == null Do nothing.  */
				return products;
			}
			
		} catch (IOException ioEx) {
			throw new EPFexception(ioEx, "Couldnt get JSON from response after listFiles()");
		}

		return null;
	}
	
	
	
	/**
	 * Convert JSON to Product
	 * @param json
	 * @return
	 */
	private Product jsonToProduct(JSONObject json) {
		Product prod = new Product();
		prod.setFileid( json.get("fileid").toString() );
		prod.setStatus( json.get("status").toString() );
		prod.setFilepath( json.get("filepath").toString() );
		prod.setFulfilled( json.get("fulfilled").toString() );
		return prod;
	}
	
	
	
	
	
	@Override
	public void changeStatus(Product prod , String newStatus) throws EPFexception {
		JSONObject json = addKeysToJSON( new JSONObject() );
		json.put("fileid" , prod.getFileid());
		json.put("newstatus" , newStatus);
		
		try {
			HttpResponse resp = processResponseAndStoreKeys( makeRequest("/ws/resources/download/status", json) );
			JSONObject val = (JSONObject)JSONValue.parse(new InputStreamReader(resp.getEntity().getContent()));
			
			if (val != null) {
				if ( val.get("response") != null &&
						"failed".equalsIgnoreCase( val.get("response").toString() ) ) {
					throw new EPFexception("Could not change status to "+ newStatus +" for fileid "+ prod.getFileid() );
				}
			}
			
		} catch (IOException ioEx) {
			throw new EPFexception(ioEx, "Couldnt get JSON from response after listFiles()");
		}
	}

	@Override
	public void requestFile_POST(String epfFileID) throws EPFexception {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestFile_GET(String epfFileID) throws EPFexception {
		// TODO Auto-generated method stub

	}
	
	
	
	/**
	 * HEY .. What if there is a problem with userId/pwd??? this 
	 * @return
	 */
	private CredentialsProvider createCredentials() {
		if (credsProvider  == null) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
					new AuthScope("proxy.aetna.com" , 9119)
					, new UsernamePasswordCredentials(this.domainUser, String.valueOf( this.domainPWD ) ) );
		}
		return credsProvider;
	}
	
	
	
	
	/**
	 * 
	 * @return
	 */
	private CloseableHttpClient openClient() {
		return HttpClientBuilder.create().setDefaultCredentialsProvider( createCredentials() ).build();
	}
	
	/**
	 * Close the HTTPclient
	 * @param client
	 */
	private void closeClient(CloseableHttpClient client) {
		try {
			client.close();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	@Override
	public void setProxyUser(String usr) {
		this.domainUser = usr;
	}

	@Override
	public void setProxyPwd(CharSequence pwd) {
		this.domainPWD = pwd;
	}

	@Override
	public void setProxy(String host , int port) {
		proxy = new HttpHost(host , port);
		config = RequestConfig.custom().setProxy(proxy).build();
	}
	
	
	
	/**
	 * Common to ALL responses - Validate & check for Tokens
	 * @param resp
	 */
	private HttpResponse processResponse(HttpResponse resp) throws EPFexception {
		if (resp == null)
			throw new EPFexception("Response is NULL");
		if ( resp.getStatusLine().getStatusCode() != 200 ) {
			throw new EPFexception("REsponse code: "+ resp.getStatusLine().getStatusCode() );
		}
		if ("failed".equalsIgnoreCase(resp.getFirstHeader("Service-Response").getValue() ) ) {
			Header[] headers = resp.getAllHeaders();
			for (Header header : headers) {
				System.out.println( header.getName() +" : "+ header.getValue() );
			}
			
			/* If failed because of Security Token not matching - Give the code a chance to log in and keep going */
			if ("Security token failed.".equalsIgnoreCase( resp.getFirstHeader("Service-Messages").getValue() ) ) {
				System.out.println("Security token failed.");
				throw new SecurityTokenException();
			}
			throw new EPFexception("Response failed : "+  resp.getFirstHeader("Service-Messages").getValue() );
		}
		return resp;
	}
	
	
	
	
	/**
	 * Store keys provided by EPF, used in the next HTTPRequest.
	 * @param resp
	 * @throws EPFexception
	 */
	private HttpResponse processResponseAndStoreKeys(HttpResponse resp) throws EPFexception {
		processResponse(resp);
		
		try {
			this.epfLogonKey = resp.getFirstHeader("User-Logonkey").getValue();
			this.epfTokenKey = resp.getFirstHeader("User-Tokenkey").getValue();
		} catch (NullPointerException npEx) {
			throw new KeysMissingException();
		}
		return resp;
	}
	
	
	
	
	/**
	 * Add EPF Token & Key to the JSON object and return it.
	 * @param json
	 * @return
	 */
	private JSONObject addKeysToJSON(JSONObject json) {
		json.put("logonkey" , epfLogonKey);
		json.put("tokenkey" , epfTokenKey);
		return json;
	}

}
