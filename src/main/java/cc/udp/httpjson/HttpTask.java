package cc.udp.httpjson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

/**
 * Created by DMDEV on 2017-11-23.
 */

public class HttpTask extends Thread  {

    private String targetUrl;
    private HashMap<String, String> params;
    private HashMap<String, String> headers;
    private HttpTaskHandler doneHandler;
    private HttpJsonTaskError taskHandlerError;

    private String requestMode = "POST";


    public HttpTask(String url, HashMap<String, String> p, HttpTaskHandler func) {
        this.targetUrl = url;
        this.doneHandler = func;
        this.headers = new HashMap<String, String>();

        if (p == null) {
            this.params = new HashMap<String, String>();
        } else {
            this.params = p;
        }
    }

    @Override
    public void run() {
        super.run();

        if (this.requestMode.equals("GET") && this.params.size() > 0) {
            this.targetUrl += "?" + this.getEncodedParams();
        }

        try {
            URL url = new URL(this.targetUrl);
            URLConnection conn = (URLConnection) url.openConnection();
            HttpURLConnection hconn = (HttpURLConnection) conn;

            hconn.setRequestMethod(this.requestMode);
            hconn.setDoInput(true);
            hconn.setUseCaches(true);
            hconn.setDefaultUseCaches(true);

            if (this.requestMode.toUpperCase().equals("POST")) {
                hconn.setDoOutput(true);
            } else {
                hconn.setDoOutput(false);
            }

            boolean isJsonRequest = false;

            for (String key : this.headers.keySet()) {
                if (key.toUpperCase().equals("CONTENT-TYPE") && this.headers.get(key).toUpperCase().startsWith("APPLICATION/JSON")) {
                    isJsonRequest = true;
                    break;
                }
            }

            for (String key : this.headers.keySet()) {
                hconn.setRequestProperty(key, this.headers.get(key));
            }

            if (this.params.size() > 0 && this.requestMode.equals("POST")) {
                byte[] postParams;

                if (isJsonRequest) {
                    HashMap<String, Object> soParams = new HashMap<String, Object>();
                    for (String key : this.params.keySet()) {
                        soParams.put(key, this.params.get(key));
                    }

                    postParams = HttpJson.stringify(soParams).getBytes("UTF-8");
                    hconn.setRequestProperty("Content-Type", "application/json");
                    hconn.setRequestProperty("Content-Length", String.valueOf(postParams.length));
                } else {
                    postParams = this.getEncodedParams().getBytes("UTF-8");
                    hconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    hconn.setRequestProperty("Content-Length", String.valueOf(postParams.length));
                }

                OutputStream writer = hconn.getOutputStream();
                writer.write(postParams);
                writer.flush();
                writer.close();
            }

            String status = String.valueOf(hconn.getResponseCode());

            if (status.startsWith("4") || status.startsWith("5")) {
                InputStream is = hconn.getErrorStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                byte[] contents = buffer.toByteArray();

                buffer.close();
                buffer = null;
                is.close();
                is = null;

                this.taskHandlerError.error(new Exception(new String(contents, "UTF-8")));
            } else {
                InputStream is = hconn.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                byte[] contents = buffer.toByteArray();

                buffer.close();
                buffer = null;
                is.close();
                is = null;

                this.doneHandler.func(contents);
            }
        } catch (Exception e) {
            if (this.taskHandlerError == null) {
                e.printStackTrace();
            } else {
                this.taskHandlerError.error(e);
            }
        }
    }

    public String getEncodedParams() {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            if (sb.length() > 0) sb.append("&");
            try {
                sb.append(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                if (this.taskHandlerError == null) {
                    e.printStackTrace();
                } else {
                    this.taskHandlerError.error(e);
                }
            }
        }

        return sb.toString();
    }

    public void addField(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<String, String>();
        }

        this.params.put(key, value);
    }

    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<String, String>();
        }

        this.headers.put(key, value);
    }

    public void setRequestMode(String mode) {
        this.requestMode = mode;
    }

    public void setThreadExceptionHandler(HttpJsonTaskError funcCatch) {
        this.taskHandlerError = funcCatch;
    }
}