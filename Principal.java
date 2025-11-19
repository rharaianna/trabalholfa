
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
        automato.ler("test/AFD.XML");
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


    // O proximo agora retorna TUDO, inclusive espaços e quebras de linha.
    // Quem decide o que fazer com eles é o analisador léxico.
    public Simbolo proximo(BufferedReader reader) throws IOException {
        int charLido = reader.read();

        if (charLido == -1){
            return null; //fim de arquivo
        }
        return new Simbolo((char) charLido);
    }

    // Le um token retona 1: se sucesso e 0: se erro -1 se fim
    public String lexico(BufferedReader r) throws IOException {
        String token = "";
        corrente = automato.getEstadoInicial();

        r.mark(1); // Marca a posição atual para poder voltar 1 char se precisar
        Simbolo p = proximo(r);

        // 1. Pular espaços em branco/quebras de linha ANTES do token começar
        while (p != null) {
            if(p.toString().equals(" ")||p.toString().equals("\n") || p.toString().equals("\r") || p.toString().equals("\t")){
                r.mark(1); // Marca novamente após consumir o espaço
                p = proximo(r);
            }
            else{
                break; // Encontrou algo que não é espaço, começa o token
            }
        }

        if (p == null) return "fim"; // Arquivo acabou só com espaços
        while (p != null) {
            Estado proximoEstado = automato.p(corrente, p);

            // CASO 1: Transição Inválida (null)
            if (proximoEstado == null) {
                // Se o caractere lido não é aceito.
                return "erro lexico: caractere '" + p + "' inesperado em " + token;
            }

            // CASO 2: Atingiu o Estado Final (F)
            if (automato.getEstadosFinais().pertence(proximoEstado)) {
                // O "asterisco" diz: o token está pronto.
                // O caractere 'p' (espaço) serviu para finalizar, mas não entra no token numérico.
                return token;
            }

            // CASO 3: Transição Válida para estado não final
            // Consome o caractere e continua
            token += p.toString();
            corrente = proximoEstado;

            // Prepara leitura do próximo
            r.mark(1);
            p = proximo(r);
        }
        return token;
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
                // Ele tá ignorando a ultima palavra se não acabar em " " ou /n
            }
            System.out.println(achou);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
