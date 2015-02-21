
import java.util.Map;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class DoHttpRequest {
	
	private static final Logger LOGGER = Logger.getLogger(DoHttpRequest.class.getName());

	public enum HttpMethod{
		GET,
		POST,
		PUT,
		DELETE
	}
	
	public enum ContentType{
		XML("application/xml"),//No I18N
		JSON("application/json"),//No I18N
		PROPERTIES("application/octet-stream"),//No I18N
		HTML("application/html");//No I18N
		
		private String contentType;
		ContentType(String contentType)
		{
			this.contentType = contentType;
		}
		
		public String getContentType()
		{
			return this.contentType;
		}
	}

	private String url;
	private HttpMethod method;
	private String contentTypeString;
	private String requestBody;

	private Map<String, String> params;
	private Map<String, String> headers;
	private Map<String,String> proxyParams;
	
	
	public DoHttpRequest(String url, HttpMethod method, Map<String,String> params)
	{
		this.url = url;
		this.method = method;
		this.params = params;
		this.headers = null;
		this.proxyParams = null;
	}
	
	public String getUrl()
	{
		return url;
	}
	public String getMethod()
	{
		return method.toString();
	}
	
	public void setHeaders(Map<String,String> headers)
	{
		this.headers = headers;
	}
	
	public void setRequestBody(String requestBody,ContentType contentType)
	{
		this.requestBody = requestBody;
		this.contentTypeString = contentType.getContentType();
	}
	
	public void setProxyParams(Map<String,String> proxyParams)
	{
		this.proxyParams = proxyParams;
	}
	
	public HttpMethodBase execute() throws Exception
	{
		if(url == null || (!url.startsWith("http:") && !url.startsWith("https:"))){
			//TODO : throw exception
			return null;
		}
		HttpMethodBase requestMethod = null;
		
		switch(method)
		{
			case POST :		
				PostMethod postMethod = new PostMethod(url);
				if(params != null)
				{
					Iterator<String> ie = params.keySet().iterator();
					while(ie.hasNext())
					{
						String key = (String)ie.next();
						postMethod.addParameter(key,params.get(key));
					}
				}
				if(requestBody != null && !requestBody.isEmpty())
				{
					postMethod.setRequestEntity(new StringRequestEntity(requestBody,contentTypeString, null));//No I18N
				}
				requestMethod = postMethod;
				break;
			case PUT :		
				PutMethod putMethod = new PutMethod(url);
				if(requestBody != null && !requestBody.isEmpty())
				{
					putMethod.setRequestEntity(new StringRequestEntity(requestBody,contentTypeString, null));//No I18N
				}
				requestMethod = putMethod;
				if(params != null)
				{
					String paramString = getQueryParamString(params);
					requestMethod.setQueryString(paramString);
				}	
				break;
			case DELETE :	
				requestMethod = new DeleteMethod(url);
				if(params != null)
				{
					String paramString = getQueryParamString(params);
					requestMethod.setQueryString(paramString);
				}
				break;
			default :		
				requestMethod = new GetMethod(url);
				if(params != null)
				{
					String paramString = getQueryParamString(params);
					requestMethod.setQueryString(paramString);
				}	
		}
		if(headers != null)
		{
			for(Entry<String, String> entry : headers.entrySet())
			{	
				requestMethod.setRequestHeader(entry.getKey(),entry.getValue());
			}
		}
		
		HttpClient httpclient = new HttpClient();
		if(proxyParams != null)
		{
			HostConfiguration config = httpclient.getHostConfiguration();
			config.setProxy(proxyParams.get("host").toString(), Integer.parseInt(proxyParams.get("port")));
			AuthScope authScope = new AuthScope(proxyParams.get("host").toString(), Integer.parseInt(proxyParams.get("port")));
			httpclient.getState().setProxyCredentials(authScope, new UsernamePasswordCredentials(proxyParams.get("username").toString(), proxyParams.get("password").toString()));// No I18N
		}
		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		httpclient.getHttpConnectionManager().getParams().setSoTimeout(5000);
		try
		{
			httpclient.executeMethod(requestMethod);
			return requestMethod;
		}
		catch(Exception e){
			
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static String getQueryParamString(Map<String, String> params) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		Iterator<Entry<String, String>> entries = params.entrySet().iterator();
		while(entries.hasNext())
		{
			Entry<String, String> entry = entries.next();
			buffer.append(URLEncoder.encode(entry.getKey(), "UTF-8"));// No I18N
			buffer.append("=");
			buffer.append(URLEncoder.encode(entry.getValue(), "UTF-8"));// No I18N
			if(entries.hasNext())
			{
				buffer.append("&");
			}
			
		}
		return buffer.toString();
	}

}