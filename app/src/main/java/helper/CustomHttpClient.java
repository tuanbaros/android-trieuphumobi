package helper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class CustomHttpClient {
	private String url;

	private HttpClient client;

	public static enum RequestMethod {
		POST, GET
	};

	protected ArrayList<NameValuePair> params;
	protected ArrayList<NameValuePair> headers;

	private int responseCode;
	private String response;

	public CustomHttpClient(String url) {
		this.url = url;
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		client = new DefaultHttpClient(httpParams);
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void setHttpParameters(HttpParams httpParameters) {
		client = new DefaultHttpClient(httpParameters);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getResponse() {
		return response;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public String getParam(String name) {
		for (NameValuePair p : params) {
			if (p.getName() != null && p.getName().equals(name)) {
				return p.getValue();
			}
		}

		return null;
	}

	public void clearParam() {
		params.clear();
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void clearHeader() {
		headers.clear();
	}

	public String getUrl() throws UnsupportedEncodingException {
		String combinedParams = "";
		if (!params.isEmpty()) {
			combinedParams += "?";
			for (NameValuePair p : params) {
				String paramString;
				if (p.getValue() != null) {
					paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");

				} else {
					paramString = p.getName() + "=";
				}

				if (combinedParams.length() > 1) {
					combinedParams += "&" + paramString;
				} else {
					combinedParams += paramString;
				}
			}
		}
		return url + combinedParams;
	}

	/**
	 * on pre-request event
	 * 
	 * @param method
	 */
	public void onPreRequest(RequestMethod method) {

	}

	private void buildAndExecuteRequest(RequestMethod method) throws Exception {
		onPreRequest(method);
		switch (method) {
			case GET: {   //Ban đầu là GET
			// add parameters
			// String combinedParams = "";
			// if (!params.isEmpty()) {
			// combinedParams += "?";
			// for (NameValuePair p : params) {
			// if (p.getValue() != null) {
			// String paramString = p.getName() + "="
			// + URLEncoder.encode(p.getValue(), "UTF-8");
			// if (combinedParams.length() > 1) {
			// combinedParams += "&" + paramString;
			// } else {
			// combinedParams += paramString;
			// }
			// }
			// }
			// }
			// HttpGet request = new HttpGet(URLEncoder.encode(url
			// + combinedParams));
			HttpGet request = new HttpGet(getUrl());
			// Log.i("Lib", "Request service: " + url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request);
			break;
		}
			case POST: {//POST

			HttpPost request = new HttpPost(url);

			getUrl();

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}

			executeRequest(request);
			break;
		}
		}
	}

	private void executeRequest(HttpUriRequest request) throws Exception {
		// if (Utils.DEBUG_MODE)
		// Log.i("Cafe24", "Request:" + getUrl());
		InputStream instream = null;
		HttpResponse httpResponse;

		try {

			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				instream = entity.getContent();
				response = convertStreamToString(instream);
				// if (Utils.DEBUG_MODE)
				// Log.i("Cafe24", "Response:" + response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// if (client != null)
			// client.getConnectionManager().shutdown();
			// if (instream != null) {
			// instream.close();
			// instream = null;
			// }
		}
	}

	@Override
	protected void finalize() throws Throwable {
		closeConnection();
		super.finalize();
	}

	public void closeConnection() {
		if (client != null) {
			client.getConnectionManager().shutdown();
			client = null;
		}
	}

	/**
	 * Chạy request
	 * 
	 * @return
	 * @throws Exception
	 */
	public String request() throws Exception {
		buildAndExecuteRequest(RequestMethod.GET);
		return getResponse().trim();
	}

	public String request(RequestMethod method) throws Exception {
		buildAndExecuteRequest(method);
		return getResponse().trim();
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Log.w("TerraBookChannel", "String:" + sb.toString());
		return sb.toString();
	}

}
