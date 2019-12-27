package cc.udp.httpjson;

/**
 * Created by DMDEV on 2018-07-02.
 */

public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler
{
    HttpJson httpJson;

    public ThreadExceptionHandler(HttpJson _httpJson)
    {
        this.httpJson = _httpJson;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e)
    {
        e.printStackTrace();
        httpJson.threadErrorException(new Exception(e.getMessage(), e));
    }
}
