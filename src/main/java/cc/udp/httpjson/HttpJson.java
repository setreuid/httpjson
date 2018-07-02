package cc.udp.httpjson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

public class HttpJson
{
    private HttpTask httpTask;
    private HttpJsonTask taskHandler;
    private HttpJsonTaskError taskHandlerError;

    public HttpJson(String url, HashMap<String, String> params, HttpJsonTask func)
    {
        this.taskHandler = func;
        this.httpTask = new HttpTask(url, params, new HttpTaskHandler() {
            @Override
            public void func(Object stringBuilder) {
                taskDone(stringBuilder.toString());
            }
        });
        this.httpTask.setUncaughtExceptionHandler(new ThreadExceptionHandler(this));
    }

    public HttpJson setThreadExceptionHandler(HttpJsonTaskError funcCatch)
    {
        this.taskHandlerError = funcCatch;
        return this;
    }

    public void post()
    {
        this.httpTask.setRequestMode("POST");
        this.httpTask.start();
    }

    public void get()
    {
        this.httpTask.setRequestMode("GET");
        this.httpTask.start();
    }

    public void threadErrorException()
    {
        if (this.taskHandlerError == null) return;
        this.taskHandlerError.error();
    }

    private void taskDone(String response)
    {
        // If just post call then return
        if (this.taskHandler == null) return;

        if (response != null) response = response.replace("\uFEFF", "");
        JSONParser jsonParser = new JSONParser();

        try
        {
            JSONObject root = (JSONObject) jsonParser.parse(response);
            this.taskHandler.done(new HttpJsonObject(root));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            this.taskHandler.done(new HttpJsonObject(response));
        }
    }

    public static String stringify(HashMap<String, Object> params)
    {
        StringBuilder sb = new StringBuilder();
        Object obj;
        int index = 0;

        sb.append("{");

        for (String key : params.keySet())
        {
            index++;
            sb.append("\"" + key + "\":");

            obj = params.get(key);

            if (obj instanceof String)
            {
                sb.append("\"" + (String) obj + "\"");
            }
            else if (obj instanceof Integer)
            {
                sb.append((int) obj);
            }
            else if (obj instanceof Float)
            {
                sb.append((float) obj);
            }
            else if (obj instanceof Boolean)
            {
                sb.append(((boolean) obj) ? "true" : "false");
            }
            else
            {
                sb.append("null");
            }

            if (params.size() > index) sb.append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    public static String stringify(ArrayList<HashMap<String, Object>> params)
    {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        sb.append("[");

        for (HashMap<String, Object> item : params)
        {
            index++;
            sb.append(stringify(item));

            if (params.size() > index) sb.append(",");
        }

        sb.append("]");

        return sb.toString();
    }
}
