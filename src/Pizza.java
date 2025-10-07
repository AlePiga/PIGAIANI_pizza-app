public class Pizza {

    public String nome;
    public String ingredienti;
    public double prezzo;
    public String _id;

    @Override
    public String toString() {
        return nome + "\t\t" + ingredienti + "\t\t\t€" + prezzo;
    }
}
