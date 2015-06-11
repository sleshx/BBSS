package sg.gov.msf.bbss.apputils.connect;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by bandaray on 11/12/2014.
 */
public class HttpURLConnectionHelper {

	// http://www.xyzws.com/javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
	// http://ihofmann.wordpress.com/2013/01/23/android-sending-post-requests-with-parameters/
	// http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/

	private static final String AUTHENTICATION_URL = "http://192.168.204.82:8080/iframe-cxf-sample/services/itrust/authentication/login/";
	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";

	private HashMap<String, String> httpHeaders;
	private HttpURLConnection httpUrlConn = null;
	private String urlParameters;

	public HttpURLConnectionHelper(String urlParameters){
		this.urlParameters = urlParameters;
		this.httpHeaders = new HashMap<String, String>(0);
	}

	public HttpURLConnectionHelper(HashMap<String, String> httpHeaders){
		this.httpHeaders = httpHeaders;
	}

	//--------------------------------------------------------------------------------------------

	public String iConnectAuthenticate() {	
		// Define HTTP header fields
		httpHeaders.put("Accept", "application/json");
		httpHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		httpHeaders.put("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		httpHeaders.put("Content-Language", "en-US");  

		return executePostForAuthetication();
	}

	//--------------------------------------------------------------------------------------------
	
	public String executePostForAuthetication() {
		String jsonString = null;

		try{
			httpUrlConn = createHttpConnectionForAuthetication();
			sendRequest();
			jsonString = getResponse();
		} finally {
			closeHttpConnection();
		}
		return jsonString;
	}

	//--------------------------------------------------------------------------------------------

	private void sendRequest() {
		DataOutputStream dataOutputStream;
		try {
			dataOutputStream = new DataOutputStream (httpUrlConn.getOutputStream());
			dataOutputStream.writeBytes (urlParameters);
			dataOutputStream.flush ();
			dataOutputStream.close ();
		} catch (IOException e) {
			//--- Handle I/0
			e.printStackTrace();
		}

	}
	
	//--------------------------------------------------------------------------------------------

	private String getResponse() {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			InputStream in = new BufferedInputStream(httpUrlConn.getInputStream());
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			//--- Handle I/0
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}

	//--------------------------------------------------------------------------------------------

	private HttpURLConnection createHttpConnectionForAuthetication(){		
		URL url;
		try {
			url = new URL(AUTHENTICATION_URL);
			httpUrlConn = (HttpURLConnection)url.openConnection();
			httpUrlConn.setRequestMethod(HTTP_POST);

			httpUrlConn.setUseCaches (false);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setConnectTimeout(2000);
			httpUrlConn.setReadTimeout(2000);
		} catch (MalformedURLException e1) {
			//--- Handle invalid URL
			e1.printStackTrace();
		} catch (IOException e) {
			//--- Handle I/0
			e.printStackTrace();
		}
		return httpUrlConn;
	}
	
	private void closeHttpConnection() {
		if(httpUrlConn != null) {
			httpUrlConn.disconnect(); 
		}
	}

	//--------------------------------------------------------------------------------------------
}
