package th.co.scbprotect.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetConnector implements Runnable {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private String data = null;
    private String url = "";
    private String token = "";
    private DataHandler handler = null;

    public interface DataHandler
    {
        void processData(String data);
    }

    public GetConnector(String url, String token, DataHandler handler) {
        this.url = url;
        this.token = token;
        this.handler = handler;
    }

    public void execute() {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        String accessTokenHeader = token;

        Request request = null;
        if (accessTokenHeader.length() == 0) {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + accessTokenHeader)
                    .get()
                    .build();
        }

        this.data = "";
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            this.data = response.body().string();

            Log.e("TAG",this.data);

            handler.processData(this.data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }
}
