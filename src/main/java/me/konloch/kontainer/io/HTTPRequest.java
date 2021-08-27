package me.konloch.kontainer.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A wrapper for Java SE classes to write/read an HTTP Request
 *
 * @author Konloch
 */

public class HTTPRequest {

    public URL url;
    private int timeout = 30000;
    private String cookie;
    private String referer;
    private String postData;
    private String useragent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private Proxy proxy;
    private boolean setFollowRedirects = true;
    private BufferedReader reader;
    private DataOutputStream writer;
    private HttpURLConnection connection;
    private Set<Entry<String, List<String>>> lastConnectionHeaders;
    private int statusCode;

    /**
     * Creates a new HTTPRequest object
     *
     * @param url
     */
    public HTTPRequest(URL url) {
        this.url = url;
    }

    /**
     * Sets a referer to send to the web server
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

    /**
     * Set a cookie string to send to the web server
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     * Sets post data to send to the web server
     */
    public void setPostData(String postData) {
        this.postData = postData;
    }

    /**
     * Sets a custom useragent, default 'Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0'
     */
    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    /**
     * Sets the seconds till timeout, default 30,000 milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets a proxy to connect through
     */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Used to get the headers the webserver sent on our last connection
     */
    public Set<Entry<String, List<String>>> getLastConnectionHeaders() {
        return lastConnectionHeaders;
    }
    
    public int getStatusCode()
    {
        return statusCode;
    }
    
    /**
     * By default, follow redirects are enabled
     */
    public void setFollowRedirects(boolean setFollowRedirects) {
        this.setFollowRedirects = setFollowRedirects;
    }

    /**
     * Used to set up the connection to read the content.
     */
    private void setup() throws Exception {
        if (proxy != null)
            connection = (HttpURLConnection) url.openConnection(proxy);
        else
            connection = (HttpURLConnection) url.openConnection();

        if (cookie != null)
            connection.setRequestProperty("Cookie", cookie);
        if (referer != null)
            connection.addRequestProperty("Referer", referer);

        connection.setRequestProperty("User-Agent", useragent);
        connection.setReadTimeout(timeout);
        connection.setConnectTimeout(timeout);
        connection.setUseCaches(false);
        HttpURLConnection.setFollowRedirects(setFollowRedirects);

        if (postData != null) {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(postData);
            writer.flush();
        }

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    /**
     * Reads the entire page and returns a string array
     *
     * @return
     * @throws Exception
     */
    public String[] read() throws Exception {
        List<String> st;

        try {
            setup();

            st = new ArrayList<>();
            String s;
            while ((s = reader.readLine()) != null)
                st.add(s);

            lastConnectionHeaders = connection.getHeaderFields().entrySet();
            statusCode = connection.getResponseCode();
        } catch (Exception e) {
            cleanup();
            throw e;
        } finally {
            cleanup();
        }

        return st.toArray(new String[0]);
    }

    /**
     * Reads as many lines as expected unless it reaches the end.
     *
     * @param linesToRead
     * @return
     * @throws Exception
     */
    public String[] read(int linesToRead) throws Exception {
        List<String> st;

        try {
            setup();

            st = new ArrayList<>();
            for (int i = 0; i < linesToRead; i++) {
                String s = reader.readLine();
                if (s != null)
                    st.add(s);
            }

            lastConnectionHeaders = connection.getHeaderFields().entrySet();
            statusCode = connection.getResponseCode();
        } catch (Exception e) {
            cleanup();
            throw e;
        } finally {
            cleanup();
        }

        return st.toArray(new String[0]);
    }

    /**
     * Only reads the first line
     *
     * @return
     * @throws Exception
     */
    public String readSingle() throws Exception {
        String s;

        try {
            setup();

            s = reader.readLine();

            lastConnectionHeaders = connection.getHeaderFields().entrySet();
            statusCode = connection.getResponseCode();
        } catch (Exception e) {
            cleanup();
            throw e;
        } finally {
            cleanup();
        }

        return s;
    }

    /**
     * Reads until it reaches the expected line then it returns it.
     *
     * @param linesToRead
     * @return
     * @throws Exception
     */
    public String readSingle(int linesToRead) throws Exception {
        String s;

        try {
            setup();

            for (int i = 0; i < linesToRead - 1; i++)
                reader.readLine();

            s = reader.readLine();

            lastConnectionHeaders = connection.getHeaderFields().entrySet();
            statusCode = connection.getResponseCode();
        } catch (Exception e) {
            cleanup();
            throw e;
        } finally {
            cleanup();
        }

        return s;
    }

    /**
     * Used to clean up the connection, closes the connections and nulls the objects
     */
    private void cleanup() {
        try {
            reader.close();
        } catch (Exception ignored) {
        }
        try {
            writer.close();
        } catch (Exception ignored) {
        }
        try {
            connection.disconnect();
        } catch (Exception ignored) {
        }
        reader = null;
        writer = null;
        connection = null;
    }

}
