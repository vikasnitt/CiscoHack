import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtil {

/*
 *   Utility function to convert JSONObject to Map
 */
	public static Map<String,Object> jsonToMap(JSONObject json) throws JSONException
	{
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(json != JSONObject.NULL)
		{
			retMap = toMap(json);
		}
		return retMap;
	}
	
	public static Map<String,Object> toMap(JSONObject object) throws JSONException
	{
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while(keysItr.hasNext())
		{
			String key = keysItr.next();
			Object value = object.get(key);
			
			if(value instanceof JSONArray)
			{
				value = toList((JSONArray) value);
			}
			else if(value instanceof JSONObject)
			{
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}
	
	public static List toList(JSONArray array) throws JSONException
	{
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < array.length(); i++)
		{
			list.add(array.get(i));
		}
		return list;
	}
	
	public static Map<String, String> mapValueObjectToMapValueString(Map<String, Object> paramMap)
	{
		Map<String, String> retMap = new HashMap<String, String>();
		Iterator<Entry<String, Object>> paramsItr = paramMap.entrySet().iterator();
		for(Map.Entry<String, Object> entry : paramMap.entrySet())
		{
			retMap.put(entry.getKey(), (String)entry.getValue());
		}
		return retMap;
	}
	
	public static JSONArray parseJsonArray(String string) throws JSONException
	{
		JSONTokener tokener = new JSONTokener(string);
		JSONArray retArray = new JSONArray(tokener);
		return retArray;
	}
}