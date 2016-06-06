package test.com.yoosal.mvc;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class TestMVCConcurrent {

    @Test
    public void concurrent() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(1000);
        List<Callable<String>> callables = new LinkedList();
        for (int i = 0; i < 1000; i++) {
            Callable callable = new Callable() {
                @Override
                public String call() throws Exception {
                    String getName = "" + Math.random();
                    String name = doGet(getName);
                    if (getName.equals(name)) {
                        System.out.println("true");
                    } else {
                        throw new Exception("not equal");
                    }
                    return name;
                }
            };
            callables.add(callable);
        }
        List<Future<String>> results = service.invokeAll(callables);
        String name = null;
        for (Future<String> future : results) {
            String cname = future.get();
            System.out.println(cname);
        }
        service.shutdown();

    }

    public static String doGet(String name) throws Exception {
        URL localURL = new URL("http://127.0.0.1:9999/invoke.do?_class=TestApiControllerA&_method=printer&name=" + name);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }
}
