package cc.udp.httpjson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by DMDEV on 2017-11-23.
 */

public class HttpJsonObject
{
    private Object item;

    HttpJsonObject(Object item)
    {
        this.item = item;
    }

    @Override
    public String toString()
    {
        return ((JSONObject) item).toJSONString();
    }

    public HttpJsonObject getObject(String key)
    {
        return new HttpJsonObject(((JSONObject)item).get(key));
    }

    public ArrayList<HttpJsonObject> getArray(String key)
    {
        JSONArray jsonArray = (JSONArray)((JSONObject)item).get(key);
        Iterator<JSONObject> iterJsonArray = jsonArray.iterator();

        ArrayList<HttpJsonObject> list = new ArrayList<HttpJsonObject>();

        while (iterJsonArray.hasNext())
        {
            JSONObject jsonObject = iterJsonArray.next();
            list.add(new HttpJsonObject(jsonObject));
        }

        return list;
    }

    public Object get(String key)
    {
        return ((JSONObject)item).get(key);
    }

    public String getString(String key)
    {
        return this.get(key).toString();
    }

    public int getInt(String key)
    {
        return Integer.parseInt(this.getString(key));
    }

    public float getFloat(String key)
    {
        return Float.parseFloat(this.getString(key));
    }

    public double getDouble(String key)
    {
        return Double.parseDouble(this.getString(key));
    }

    public byte[] getBytes(String key)
    {
        return (this.getString(key)).getBytes();
    }
}
