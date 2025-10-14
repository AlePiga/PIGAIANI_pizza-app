
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.vandermeer.asciitable.AsciiTable;
import okhttp3.*;

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
            System.out.println("3: Modifica pizza");
            System.out.println("4: Elimina pizze");
            System.out.println("5: Crea pizza");

            int operation = -1;

            try {
                System.out.print("\nDigita il numero dell'operazione: ");
                operation = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException ex) {
                System.out.print("Digita il numero dell'operazione: ");
                sc.nextLine();
                continue;
            }

            switch (operation) {
                case 1:
                try {
                    Pizza[] pizze = getPizze();
                    AsciiTable asciiTable = new AsciiTable();
                    asciiTable.addRule();
                    asciiTable.addRow(" NOME", " INGREDIENTI", " PREZZO", " ID");
                    asciiTable.addRule();
                    for (Pizza pizza : pizze) {
                        asciiTable.addRow(
                                " " + pizza.nome,
                                " " + pizza.ingredienti,
                                " €" + pizza.prezzo,
                                " " + pizza._id
                        );
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
                            asciiTable.addRow(" NOME", " INGREDIENTI", " PREZZO", " ID");
                            asciiTable.addRule();
                            asciiTable.addRow(
                                    " " + pizza.nome,
                                    " " + pizza.ingredienti,
                                    " €" + pizza.prezzo,
                                    " " + pizza._id
                            );
                            asciiTable.addRule();
                            System.out.println(asciiTable.render());
                        } else {
                            System.out.println("Pizza non trovata!");
                        }
                    } catch (IOException e) {
                        System.out.println("> Errore durante la richiesta: \n" + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.print("Inserisci ID della pizza da modificare: ");
                    String idModifica = sc.nextLine();
                    System.out.print("Nuovo nome: ");
                    String nuovoNome = sc.nextLine();
                    System.out.print("Nuovi ingredienti: ");
                    String nuoviIngredienti = sc.nextLine();
                    System.out.print("Nuovo prezzo: ");
                    double nuovoPrezzo = sc.nextDouble();
                    sc.nextLine();

                    try {
                        modificaPizza(idModifica, nuovoNome, nuoviIngredienti, nuovoPrezzo);
                        System.out.println("> Pizza modificata con successo!\n");
                    } catch (IOException e) {
                        System.out.println("> Errore nella modifica: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Inserisci ID della pizza da eliminare: ");
                    String idElimina = sc.nextLine();
                    try {
                        eliminaPizza(idElimina);
                        System.out.println("> Pizza eliminata con successo!\n");
                    } catch (IOException e) {
                        System.out.println("> Errore nell'eliminazione: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.print("Nome pizza: ");
                    String nome = sc.nextLine();
                    System.out.print("Ingredienti: ");
                    String ingredienti = sc.nextLine();
                    System.out.print("Prezzo: ");
                    double prezzo = sc.nextDouble();
                    sc.nextLine();

                    try {
                        creaPizza(nome, ingredienti, prezzo);
                        System.out.println("> Pizza creata con successo!\n");
                    } catch (IOException e) {
                        System.out.println("Errore nella creazione: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("> Operazione non valida\n");
                    break;
            }
        }
    }

    public Pizza[] getPizze() throws IOException {
        Request request = new Request.Builder()
                .url("https://crudcrud.com/api/e75f8557bdc147fabb6a8bbe5f01b4db/pizze")
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
        String url = "https://crudcrud.com/api/e75f8557bdc147fabb6a8bbe5f01b4db/pizze/" + id;

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

    public void creaPizza(String nome, String ingredienti, double prezzo) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("nome", nome);
        json.addProperty("ingredienti", ingredienti);
        json.addProperty("prezzo", prezzo);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://crudcrud.com/api/e75f8557bdc147fabb6a8bbe5f01b4db/pizze")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("> Errore creazione: " + response.code() + "\n");
            }
        }
    }

    public void modificaPizza(String id, String nome, String ingredienti, double prezzo) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("nome", nome);
        json.addProperty("ingredienti", ingredienti);
        json.addProperty("prezzo", prezzo);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://crudcrud.com/api/e75f8557bdc147fabb6a8bbe5f01b4db/pizze/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Errore modifica: " + response.code());
            }
        }
    }

    public void eliminaPizza(String id) throws IOException {
        Request request = new Request.Builder()
                .url("https://crudcrud.com/api/e75f8557bdc147fabb6a8bbe5f01b4db/pizze/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Errore eliminazione: " + response.code());
            }
        }
    }

    public void run() {
        visualizzaMenu();
    }
}
