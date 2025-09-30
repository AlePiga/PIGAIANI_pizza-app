import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class App {
    public OkHttpClient client;

    public App() {
        client = new OkHttpClient();
    }

    public void doGet() throws IOException {
        Request request = new Request.Builder().url("https://crudcrud.com/api/1748b029d5de4e4f9106cfaa0e710ab7/pizze")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            System.out.println(response.body().string());
        }
    }

    public void run() {
        try{
            doGet();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}