
import afds.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Principal {

    public String nomeArquivo = "./test/arquivo.txt";
    public AFD a = new AFD();
    public Estado corrente;
    public String token;

    public Principal() throws Exception {
        a.ler("./test/AFD.XML");
    }

    public static void main(String[] args) {
        Principal t;
        try {
            t = new Principal();
            t.inicio();
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Simbolo proximo(BufferedReader reader) throws IOException {
        int charLido;
        while ((charLido = reader.read()) != -1) {
            if (charLido == 10) {
                return new Simbolo(' ');
            }
            return new Simbolo((char) charLido);
        }
        return null;
    }

    // Le um token retona 1: se sucesso e 0: se erro -1 se fim
    @SuppressWarnings("empty-statement")
    public String lexico(BufferedReader r) throws IOException {
        String token = "";
        corrente = a.getEstadoInicial();
        if (a.getEstadosFinais().pertence(corrente)) {
            return "fim";
        }

        Simbolo p = proximo(r);
        while (p != null) {
            token = token + p.toString();
            corrente = a.p(corrente, p);
            if (corrente == null) {
                return "erro"; // erro lexico
            }
            if (a.getEstadosFinais().pertence(corrente)) {
                return token;
            }
            p = proximo(r);
        }
        return "fim";
    }

    // chama lexico até chegar no final de arquivo ou erro léxico
    public void inicio() {
        System.out.println(("AFD M' = " + a.toString()));
        // Loop de leitura de tokens  
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String achou = lexico(reader);
            while (!(achou.equals("erro")||achou.equals("fim"))) {
                System.out.println("Achou: "+achou);
                achou = lexico(reader);
            };
            System.out.println(achou);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
