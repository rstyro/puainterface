package top.lrshuai.pua.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	private static HttpUtils instance;
	private static HttpClient httpClient;

	public synchronized static HttpUtils getInstance() {
		if (instance == null) {
			instance = new HttpUtils();
			httpClient = HttpClients.createDefault();
		}
		return instance;
	}

	/**
	 * 
	 * @param path
	 * @param params
	 *            模拟http post
	 * @param encode
	 * @return
	 */
	public String sendPostMethod(String path, Map<String, Object> params, String encoding) {
		HttpPost httpPost = new HttpPost(path);
		String result = "";
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		try {
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					String name = entry.getKey();
					String value = entry.getValue().toString();
					BasicNameValuePair valuePair = new BasicNameValuePair(name, value);
					parameters.add(valuePair);
				}
			}
			// 纯文本表单，不包含文件
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(parameters, encoding);
			httpPost.setEntity(encodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), encoding);
			} else {
				System.out.println("else");
				System.out.println(EntityUtils.toString(httpResponse.getEntity(), encoding));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param path
	 * @param params
	 *            模拟http get
	 * @param encode
	 * @return
	 */
	public static String sendGetMethod2(String path, Map<String, Object> params, String encoding) throws Exception {
		String result = "";
		String parameters = "";

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue().toString();
				parameters = parameters + name + "=" + value + "&";
			}
			parameters = parameters.substring(0, parameters.length() - 1);
		}
		HttpGet httpGet = new HttpGet(path + "?" + parameters);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), encoding);
		}

		return result;
	}

	public void sendGetMethod(String path) throws Exception {
		HttpGet httpGet = new HttpGet(path);
		httpClient.execute(httpGet);
	}

	public String sendGetMethod(String path, Map<String, Object> params, String encoding) throws Exception {
		String result = "";
		String parameters = "";

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue().toString();
				parameters = parameters + name + "=" + value + "&";
			}
			parameters = parameters.substring(0, parameters.length() - 1);
		}
		String resultUrl = path+"?"+parameters;
		System.out.println("resultUrl="+resultUrl);
		HttpGet httpGet = new HttpGet(resultUrl);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), encoding);
		} else {
			System.out.println("else=" + EntityUtils.toString(httpResponse.getEntity(), encoding));
		}

		return result;
	}
	
	public String sendGetMethod(String path, String encoding) throws Exception {
		String result = "";
		HttpGet httpGet = new HttpGet(path);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), encoding);
		} else {
			System.out.println("else=" + EntityUtils.toString(httpResponse.getEntity(), encoding));
		}
		
		return result;
	}
	
	/**
	 * get
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Object getInfo(String url) throws Exception {
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			url = url.replace(" ", "%20");
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
