package cc.udp.httpjson;

import java.io.BufferedReader;
import java.io.IOException;
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

public class HttpTask extends Thread
{
    private String targetUrl;
    private HashMap<String, String> params;
    private HttpTaskHandler doneHandler;

    private String requestMode = "POST";

    public HttpTask(String url, HashMap<String, String> p, HttpTaskHandler func)
    {
        this.targetUrl = url;
        this.doneHandler = func;

        if (p == null)
        {
            this.params = new HashMap<String, String>();
        }
        else
        {
            this.params = p;
        }
    }

    @Override
    public void run()
    {
        super.run();
        StringBuilder contents = new StringBuilder();
        String tempContents;

        if (this.requestMode.equals("GET") && this.params.size() > 0)
        {
            this.targetUrl += "?" + this.getEncodedParams();
        }

        try
        {
            URL url = new URL(this.targetUrl);
            URLConnection conn = (URLConnection) url.openConnection();
            HttpURLConnection hconn = (HttpURLConnection) conn;

            hconn.setRequestMethod(this.requestMode);
            hconn.setDoOutput(true);
            hconn.setDoInput(true);
            hconn.setUseCaches(true);
            hconn.setDefaultUseCaches(true);

            if (this.params.size() > 0 && this.requestMode.equals("POST"))
            {
                byte[] postParams = this.getEncodedParams().getBytes("UTF-8");
                hconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                hconn.setRequestProperty("Content-Length", String.valueOf(postParams.length));

                OutputStream writer = hconn.getOutputStream();
                writer.write(postParams);
                writer.flush();
                writer.close();
            }

            BufferedReader buff = new BufferedReader(new InputStreamReader (hconn.getInputStream(), "UTF-8"));

            while((tempContents = buff.readLine()) != null)
            {
                contents.append(tempContents);
            }

            buff.close();
            this.doneHandler.func(contents);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String getEncodedParams()
    {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet())
        {
            if (sb.length() > 0) sb.append("&");
            try
            {
                sb.append(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public void setRequestMode(String mode)
    {
        this.requestMode = mode;
    }
}