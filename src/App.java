
import com.google.gson.Gson;
import de.vandermeer.asciitable.AsciiTable;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {

    public OkHttpClient client;

    public App() {
        client = new OkHttpClient();
    }

    public void visualizzaMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("OPERAZIONI DISPONIBILI");
            System.out.println("1: Leggi pizze");
            System.out.println("2: Seleziona pizza");
            System.out.println("3: Aggiorna pizze");
            System.out.println("4: Elimina pizze");

            int operation = -1;

            try {
                System.out.println("\nDigita il numero dell'operazione: ");
                operation = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException ex) {
                System.out.println("Digita il numero dell'operazione: ");
                sc.nextLine();
                continue;
            }

            switch (operation) {
                case 1:
                    try {
                        Pizza[] pizze = getPizze();
                        AsciiTable asciiTable = new AsciiTable();
                        asciiTable.addRule();
                        asciiTable.addRow(" ID", " NOME", " PREZZO", " INGREDIENTI");
                        asciiTable.addRule();
                        for (Pizza pizza : pizze) {
                            asciiTable.addRow(" " + pizza._id, " " + pizza.nome, " " + pizza.prezzo, " " + pizza.ingredienti);
                            asciiTable.addRule();
                        }
                        System.out.println(asciiTable.render());
                    } catch (IOException e) {
                        System.out.println("Errore: " + e.getClass());
                    }
                    break;

                case 2:
                    System.out.println("Inserisci ID della pizza:");
                    String id = sc.nextLine();

                    try {
                        Pizza pizza = getPizzaById(id);
                        if (pizza != null) {
                            AsciiTable asciiTable = new AsciiTable();
                            asciiTable.addRule();
                            asciiTable.addRow(" ID", " NOME", " PREZZO", " INGREDIENTI");
                            asciiTable.addRule();
                            asciiTable.addRow(" " + pizza._id, " " + pizza.nome, " " + pizza.prezzo, " " + pizza.ingredienti);
                            asciiTable.addRule();
                            System.out.println(asciiTable.render());
                        } else {
                            System.out.println("Pizza non trovata!");
                        }
                    } catch (IOException e) {
                        System.out.println("Errore durante la richiesta: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Operazione non valida");
                    break;
            }
        }
    }

    public Pizza[] getPizze() throws IOException {
        Request request = new Request.Builder()
                .url("https://crudcrud.com/api/c9c9b1d0790c432d86f8cc0d5dec94c5/pizze")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), Pizza[].class);
        }
    }

    public Pizza getPizzaById(String id) throws IOException {
        String url = "https://crudcrud.com/api/c9c9b1d0790c432d86f8cc0d5dec94c5/pizze/" + id;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Errore: " + response.code());
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), Pizza.class);
        }
    }

    public void run() {
        visualizzaMenu();
    }
}
