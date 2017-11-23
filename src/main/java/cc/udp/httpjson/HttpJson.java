package cc.udp.httpjson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.HashMap;

public class HttpJson
{
    private HttpTask httpTask;
    private HttpJsonTask taskHandler;

    public HttpJson(String url, HashMap<String, String> params, HttpJsonTask func)
    {
        this.taskHandler = func;
        this.httpTask = new HttpTask(url, params, new HttpTaskHandler() {
            @Override
            public void func(Object stringBuilder) {
                taskDone(stringBuilder.toString());
            }
        });
    }

    public void post()
    {
        this.httpTask.start();
    }

    private void taskDone(String response)
    {
        // If just post call then return
        if (this.taskHandler == null) return;

        JSONParser jsonParser = new JSONParser();

        try
        {
            JSONObject root = (JSONObject) jsonParser.parse(response);
            taskHandler.done(new HttpJsonObject(root));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            taskHandler.done(null);
        }
    }
}
