import com.google.gson.Gson;
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
        Request request = new Request.Builder()
                .url("https://crudcrud.com/api/c9c9b1d0790c432d86f8cc0d5dec94c5/pizze")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            Gson gson = new Gson();
            Pizza[] pizze = gson.fromJson(response.body().string(), Pizza[].class);
            for (Pizza p : pizze) {
                System.out.println(p.toString());
            }
        }
    }

    public void run() {
        try {
            doGet();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}