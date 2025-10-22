
import afds.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Principal {

    public String nomeArquivo = "./test/arquivo.txt";
    public AFD automato = new AFD();
    public Estado corrente;
    public String token;

    public Principal() throws Exception {
        automato.ler("./test/AFD.XML");
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
            if (charLido == 10) { // ignora quebras de linha
                continue;
            }
            return new Simbolo((char) charLido);
        }
        return null; // fim do arquivo
    }


    // Le um token retona 1: se sucesso e 0: se erro -1 se fim
    @SuppressWarnings("empty-statement")
    public String lexico(BufferedReader r) throws IOException {
        String token = "";
        corrente = automato.getEstadoInicial();
        if (automato.getEstadosFinais().pertence(corrente)) {
            return "fim";
        }

        Simbolo p = proximo(r);
        while (p != null) {
            token += p.toString();
            corrente = automato.p(corrente, p);
            if (corrente == null) {
                if (proximo(r)==null){
                    return "erro lexico e não acabou do jeito certo";
                }
                else{
                return "erro lexico"; // erro lexico
                }
            }
            if (automato.getEstadosFinais().pertence(corrente)) {
                return token;
            }
            p = proximo(r);
        }
        return "fim";
    }

    // chama lexico até chegar no final de arquivo ou erro léxico
    public void inicio() {
        System.out.println(("AFD M' = " + automato.toString()));
        // Loop de leitura de tokens  
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String achou = lexico(reader);
            while (!achou.equals("fim") && !achou.equals("automato não finalizado")) {
                System.out.println("Essa palavra está no automato: " + achou);
                achou = lexico(reader);
                //Ele tá ignorando a ultima palavra se não acabar em " " ou /n
            }
            System.out.println(achou);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
