import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class testMapToXML {

	private static Document document;
	private static DocumentBuilderFactory documentFactory;
	private static DocumentBuilder documentBuilder;
	
	public static void main(String[] args) throws Exception
	{
		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> n = new HashMap<String, Object>();
		Map<String, Object> o = new HashMap<String, Object>();
		List<Object> l = new ArrayList<Object>();
		l.add(m);
		l.add(n);
		o.put("Contacts", l);
		
		MaptoXML(o);
	}
    
    public static String MaptoXML(Map<String, Object> param) throws Exception
	{
		 documentFactory = DocumentBuilderFactory.newInstance();
		 documentBuilder = documentFactory.newDocumentBuilder();
		 document = documentBuilder.newDocument();

		 Document response = null;
		 MaptoXML2(param, document, null);
		 System.out.println(toString(document));
		 return "";
	}
    
    public static void MaptoXML2(Object obj, Node parentEl, String str) throws Exception
	{
    	if(obj instanceof Map)
    	{
    		Map<String, Object> obj2 = (Map<String, Object>) obj;
    		Set<String> set = obj2.keySet();
    		Iterator<String> it = set.iterator();
    		while(it.hasNext())
    		{
    			String key = it.next();
    			Object ob = ((Map) obj).get(key);
    			Element element = document.createElement(key);
    			parentEl.appendChild(element);
    			MaptoXML2(ob, element, key);
    		}
    	}
    	
    	else if(obj instanceof List)
    	{
    		List obj2 = (List) obj;
    		String temp = str;
    		 if(str.equals("Contacts"))
    			 temp = "Contact";
    		for(int i = 0; i< obj2.size(); i++)
    		{
    			Element element = document.createElement(temp);
    			parentEl.appendChild(element);
    			MaptoXML2(obj2.get(i), element, temp);
    		}
    	}
    	else if(obj instanceof String)
    	{
    		parentEl.setTextContent(String.valueOf(obj));
    	}
	}
    
    public static Document convertMaptoXML(Document document,Map<String,Object> params, String root,Element rootofRoot) throws Exception
    {
        
        Element	rootElement = document.createElement(root);
    	if(rootofRoot != null )
    	{
    		rootofRoot.appendChild(rootElement);
    	}
    	Iterator<String> it = params.keySet().iterator();
    	while(it.hasNext())
    	{
    		String key = it.next();
    		Object obj = params.get(key);
    		if(obj instanceof Map)
    		{
    			
    			document = convertMaptoXML(document,(Map<String,Object>)obj,key,rootElement);
    		}
    		else if(obj instanceof List)
    		{
    			document = convertListtoXML(document,(List<Object>)obj,key,rootElement);
    		}
    		else
    		{
    			Element element = document.createElement(key);  
			    element.appendChild(document.createTextNode(params.get(key).toString()));  
			    rootElement.appendChild(element);
    		}
    	}
    	return document;
    }
    
    public static Document convertListtoXML(Document document,List<Object> params, String root,Element rootofRoot) throws Exception
    {
    	Map<String, Object> element = new HashMap<String, Object>();
		String r = (String) element.get(root);
    	Element rootElement = document.createElement((String) element.get(r));
    	if(rootofRoot != null)
    	{
    		rootofRoot.appendChild(rootElement);
    	}

    	Iterator<Object> it = params.iterator();
    	while(it.hasNext())
    	{ 
    		Object obj = it.next();
    		if(obj instanceof Map)
    		{
    			document = convertMaptoXML(document,(Map<String,Object>)obj,r,rootElement);
    		}
    		else if(obj instanceof List)
    		{
    			document = convertListtoXML(document,(List<Object>)obj,r,rootElement);
    		}
    	}
    	return document;
    }
    
    public static String toString(Document doc) throws TransformerException 
	{
          StringWriter sw = new StringWriter();
          TransformerFactory tf = TransformerFactory.newInstance();
          Transformer transformer = tf.newTransformer();
          transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
          transformer.setOutputProperty(OutputKeys.METHOD, "xml"); 
          transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
          transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); 

          transformer.transform(new DOMSource(doc), new StreamResult(sw));
          return sw.toString();
    }
}

