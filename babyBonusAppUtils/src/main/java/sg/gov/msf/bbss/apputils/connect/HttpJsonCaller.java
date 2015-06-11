package sg.gov.msf.bbss.apputils.connect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sg.gov.msf.bbss.apputils.AppConstants;

/**
 * Created by bandaray on 18/1/2015.
 */
public class HttpJsonCaller {
    private String authToken = AppConstants.EMPTY_STRING;
    private HashMap<String, String> httpHeaders;
    private HttpURLConnection urlConnection = null;

    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";

    public HttpJsonCaller(String authToken){
        this.authToken = authToken;
        this.httpHeaders = new HashMap<String, String>(0);
    }

    public HttpJsonCaller(HashMap<String, String> httpHeaders){
        this.httpHeaders = httpHeaders;
    }

    public <T> T get(String url, Class<T> entityClass) throws Exception{
        String jsonString = null;
        T data = null;

        try{
            urlConnection = createHttpConnection(url, HTTP_GET);
            jsonString = readResponse(urlConnection.getInputStream());
            data = toEntity(jsonString, entityClass);
        } finally {
            closeConnection();
        }

        return data;
    }

    public String get(String url) throws Exception{
        String jsonString = null;

        try{
            urlConnection = createHttpConnection(url, HTTP_GET);
            jsonString = readResponse(urlConnection.getInputStream());
        } finally {
            closeConnection();
        }
        return jsonString;
    }

    public String post(String url, String jsonData) throws Exception {
        BufferedWriter bufferedWriter;
        String jsonString = null;

        try {
            urlConnection = createHttpConnection(url, HTTP_POST);
            bufferedWriter = new BufferedWriter (new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            bufferedWriter.write(jsonData);
            bufferedWriter.flush ();
            jsonString = readResponse(urlConnection.getInputStream());
        } finally {
            closeConnection();
        }

        return jsonString;
    }

    private HttpURLConnection createHttpConnection(String getUrl,
                                                   String requestMethod) throws Exception {
        URL url = new URL(getUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(requestMethod);
        urlConnection.setUseCaches (false);

        if(requestMethod.equals(HTTP_POST)){
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.addRequestProperty("Content-type", "application/json");
            if (!authToken.equals(AppConstants.EMPTY_STRING)) {
                urlConnection.addRequestProperty("auth_token", authToken);
            }
        } else if(requestMethod.equals(HTTP_GET)) {
            if (!authToken.equals(AppConstants.EMPTY_STRING)) {
                urlConnection.addRequestProperty("auth_token", authToken);
            }
        }

        for (Map.Entry<String,String> entry : httpHeaders.entrySet())
            urlConnection.addRequestProperty(entry.getKey(), entry.getValue());

        urlConnection.connect();

        return urlConnection;
    }

    private String readResponse(InputStream inputStream) throws Exception {
        InputStream in = new BufferedInputStream(inputStream);
        BufferedReader bufferReader = new BufferedReader( new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferReader.readLine()) != null)
            stringBuilder.append(line);

        return stringBuilder.toString();
    }

    private <T> T toEntity(String jsonString, Class<T> entityClass) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class,
                new JsonDateDeserializer(AppConstants.APP_DATE_TIME_FORMAT));
        Gson gson = builder.create();

        return gson.fromJson(jsonString, entityClass);
    }

    private void closeConnection() {
        if(urlConnection != null){
            urlConnection.disconnect();
        }
    }
}
