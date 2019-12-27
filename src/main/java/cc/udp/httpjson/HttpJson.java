package cc.udp.httpjson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class HttpJson {

    private HttpTask httpTask;
    private HttpJsonTask taskJsonHandler;
    private HttpBytesTask taskBinaryHandler;
    private HttpJsonTaskError taskHandlerError;


    public HttpJson(String url) {
        this.httpTask = new HttpTask(url, null, new HttpTaskHandler() {
            @Override
            public void func(Object response) {
                taskDone((byte[]) response);
            }
        });

        this.httpTask.setUncaughtExceptionHandler(new ThreadExceptionHandler(this));
    }

    public HttpJson(String url, HashMap<String, String> params, HttpJsonTask func) {
        this.taskJsonHandler = func;
        this.httpTask = new HttpTask(url, params, new HttpTaskHandler() {
            @Override
            public void func(Object response) {
                taskDone((byte[]) response);
            }
        });

        this.httpTask.setUncaughtExceptionHandler(new ThreadExceptionHandler(this));
    }

    public HttpJson(String url, HashMap<String, String> params, HttpBytesTask func) {
        this.taskBinaryHandler = func;
        this.httpTask = new HttpTask(url, params, new HttpTaskHandler() {
            @Override
            public void func(Object response) {
                taskDone((byte[]) response);
            }
        });

        this.httpTask.setUncaughtExceptionHandler(new ThreadExceptionHandler(this));
    }

    public HttpJson setThreadExceptionHandler(HttpJsonTaskError funcCatch) {
        this.taskHandlerError = funcCatch;
        this.httpTask.setThreadExceptionHandler(funcCatch);
        return this;
    }

    public HttpJson except(HttpJsonTaskError funcCatch) {
        this.taskHandlerError = funcCatch;
        this.httpTask.setThreadExceptionHandler(funcCatch);
        return this;
    }

    public HttpJson then(HttpJsonTask func) {
        this.taskJsonHandler = func;
        return this;
    }

    public HttpJson then(HttpBytesTask func) {
        this.taskBinaryHandler = func;
        return this;
    }

    public HttpJson addField(String key, String value) {
        this.httpTask.addField(key, value);
        return this;
    }

    public HttpJson addHeader(String key, String value) {
        this.httpTask.addHeader(key, value);
        return this;
    }

    public void post() {
        this.httpTask.setRequestMode("POST");
        this.httpTask.start();
    }

    public void get() {
        this.httpTask.setRequestMode("GET");
        this.httpTask.start();
    }

    public void threadErrorException(Exception e) {
        if (this.taskHandlerError == null) return;
        this.taskHandlerError.error(e);
    }

    private void taskDone(byte[] response) {
        // If just post call then return
        if (this.taskJsonHandler == null && this.taskBinaryHandler == null) {
            return;
        }

        if (this.taskJsonHandler != null) {
            try {
                if (response != null) {
                    String jsonString = (new String(response, "UTF-8")).replace("\uFEFF", "");
                    JSONParser jsonParser = new JSONParser();

                    try {
                        JSONObject root = (JSONObject) jsonParser.parse(jsonString);
                        this.taskJsonHandler.done(new HttpJsonObject(root));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        this.taskJsonHandler.done(new HttpJsonObject(response));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                if (this.taskHandlerError == null) {
                    e.printStackTrace();
                } else {
                    this.taskHandlerError.error(e);
                }
            }
        } else if (this.taskBinaryHandler != null) {
            this.taskBinaryHandler.done(response);
        }
    }

    public static String stringify(HashMap<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        Object obj;
        int index = 0;

        sb.append("{");

        for (String key : params.keySet()) {
            index++;
            sb.append("\"" + key + "\":");

            obj = params.get(key);

            if (obj instanceof String) {
                sb.append("\"" + (String) obj + "\"");
            }
            else if (obj instanceof Integer) {
                sb.append((int) obj);
            }
            else if (obj instanceof Float) {
                sb.append((float) obj);
            }
            else if (obj instanceof Boolean) {
                sb.append(((boolean) obj) ? "true" : "false");
            }
            else {
                sb.append("null");
            }

            if (params.size() > index) sb.append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    public static String stringify(ArrayList<HashMap<String, Object>> params) {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        sb.append("[");

        for (HashMap<String, Object> item : params) {
            index++;
            sb.append(stringify(item));

            if (params.size() > index) sb.append(",");
        }

        sb.append("]");

        return sb.toString();
    }
}
