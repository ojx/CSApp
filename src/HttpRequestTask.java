import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A class to represent and construct an http request with either a
 * POST of GET methods, with or without handling.
 */
public class HttpRequestTask extends Task<String>  {
    private static final String COOKIES_HEADER = "Set-Cookie";

    private static CookieManager msCookieManager =  new CookieManager();

    private static byte GET = 1;
    private static byte POST = 2;
    private byte type;
    private FormData data;
    private URL url;

    public static void get(String urlText, HttpResponseHandler handler) {
        new HttpRequestTask(GET, urlText, null, handler);
    }

    public static void get(String urlText, FormData data, HttpResponseHandler handler) {
        new HttpRequestTask(GET, urlText, data, handler);
    }

    public static void post(String urlText, FormData data, HttpResponseHandler handler) {
        new HttpRequestTask(POST, urlText, data, handler);
    }

    public HttpRequestTask(byte type, String urlText, FormData data, HttpResponseHandler successHandler) {
        super();
        this.type = type;
        this.data = data;
        try {
            if ((type == GET) && (data != null)) {
                if (urlText.contains("?")) {
                    urlText += "&" + data.toString();
                } else {
                    urlText += "?" + data.toString();
                }
            }

            url = new URL(urlText);

            this.setOnSucceeded(successHandler);
            this.setOnFailed(event -> System.out.println("fail"));
            Thread th = new Thread(this);
            th.setDaemon(true);
            th.start();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    @Override
    protected String call() throws Exception {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setUseCaches(false);


            if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                String cookieString = "";

                for (HttpCookie httpCookie : msCookieManager.getCookieStore().get(url.toURI())) {


                   // System.out.println(msCookieManager.getCookieStore().get+"|"+url.toURI().getHost());

                  //  if (httpCookie.getDomain().equalsIgnoreCase(url.toURI().getHost())) {
                        cookieString += httpCookie.toString() + ";";
                    //}
                }

                if (cookieString.length() > 0) {
                    cookieString = cookieString.substring(0, cookieString.length() - 1);
                    conn.addRequestProperty("Cookie", cookieString );
                    System.out.println(url.toURI().getHost());
                    System.out.println("Cookies: " + cookieString);
                }

            }

            if (type == POST) {
                conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                conn.addRequestProperty("Content-Length", "" +
                        Integer.toString(data.toString().getBytes().length));

                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream (
                        conn.getOutputStream ());
                wr.writeBytes(data.toString());
                wr.flush();
                wr.close();
            }
            try {

                Integer responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                   // CookieManager msCookieManager = new CookieManager();

                    Map<String, List<String>> headerFields = conn.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                    if (cookiesHeader != null) {
                        for (String cookie : cookiesHeader) {
                            System.out.println(cookie);
                            msCookieManager.getCookieStore().add(url.toURI(), HttpCookie.parse(cookie).get(0));
                        }
                    }

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                        sb.append('\n');
                    }

                    return sb.toString();
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
