package cc.udp.httpjson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by DMDEV on 2017-11-23.
 */

public class HttpTask extends Thread
{
    private String targetUrl;
    private String params;
    private HttpTaskHandler doneHandler;

    public HttpTask(String url, HashMap<String, String> p, HttpTaskHandler func)
    {
        this.targetUrl = url;
        this.doneHandler = func;

        if (p == null)
        {
            this.params = "";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for (String key : p.keySet())
            {
                if (sb.length() > 0) sb.append("&");
                sb.append(key + "=" + p.get(key));
            }

            this.params = sb.toString();
        }
    }

    @Override
    public void run()
    {
        super.run();
        StringBuilder contents = new StringBuilder();
        String tempContents;

        try
        {
            URL url = new URL(targetUrl);
            URLConnection conn = (URLConnection)url.openConnection();
            HttpURLConnection hconn = (HttpURLConnection) conn;

            hconn.setRequestMethod("POST");
            hconn.setDoOutput(true);
            hconn.setDoInput(true);
            hconn.setUseCaches(true);
            hconn.setDefaultUseCaches(true);

            if (this.params.length() > 0)
            {
                OutputStream writer = hconn.getOutputStream();
                writer.write(this.params.getBytes());
                writer.flush();
                writer.close();
            }

            BufferedReader buff = new BufferedReader(new InputStreamReader (hconn.getInputStream(), "utf-8"));

            while((tempContents = buff.readLine()) != null)
            {
                contents.append(tempContents);
            }

            buff.close();
            doneHandler.func(contents);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}