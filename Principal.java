
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

    public static void main(String[] args) {
        Principal t;
        try {
            t = new Principal();
            t.inicio();
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Principal() throws Exception {
        automato.ler("test/AFD1.XML");
        //automato.toXML("test/novoXML");
    }

    public void inicio() {
        System.out.println("AFD M' = " + automato.toString());
        System.out.println("------------------------------------------------");

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {

            // Primeira leitura
            String resultado = lexico(reader);

            // Enquanto não chegar no fim do arquivo
            while (!resultado.equals("fim")) {

                // Verifica se o resultado é uma mensagem de erro
                if (resultado.startsWith("erro")) {
                    System.err.println(resultado); // Imprime o erro
                    break; // PARA a execução se encontrar um erro léxico
                } else {
                    // Se não é erro, é um token válido
                    System.out.println("Token reconhecido: " + resultado);
                }

                // Lê o próximo
                resultado = lexico(reader);
            }

            System.out.println("------------------------------------------------");
            System.out.println("Análise finalizada.");

        } catch (FileNotFoundException ex) {
            System.err.println("Arquivo não encontrado: " + nomeArquivo);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    // Le um token retona 1: se sucesso e 0: se erro -1 se fim
    public String lexico(BufferedReader r) throws IOException {
        String token = "";

        //Corrente ja é declarada no corpo da classe
        corrente = automato.getEstadoInicial();
        ConjuntoSimbolo delimitadores = automato.getDelimitador();

        r.mark(1); // Marca a posição atual para poder voltar se precisar
        Simbolo p = proximo(r);


        // 1. Pular delimitadores iniciais
        while (p != null) {
            if(delimitadores.pertence(p)){
                r.mark(1); // Marca novamente após consumir o espaço
                p = proximo(r);
            }
            else{
                break; // Encontrou algo que não é delimitador, começa o token
            }
        }

        if (p == null) return "fim"; // Arquivo acabou só com delimitadores


        while (p != null) {
            // Antes de transitar, verifica se é um delimitador
            if (delimitadores.pertence(p)) {
                // Encontramos um separador! O token acabou.

                if (automato.getEstadosFinais().pertence(corrente)) {
                    // Se paramos em um estado final (ex: B, D ou E), SUCESSO.
                    r.reset(); // Devolve o delimitador para ser lido na próxima vez (ou ignorado no início)
                    return token;
                } else {
                    // Se paramos no meio do caminho (ex: estado C "0."), ERRO.
                    return "erro lexico: token incompleto ou inválido " + token;
                }
            }

            Estado proximoEstado = automato.p(corrente, p);

            // CASO 1: Transição Inválida (null)
            if (proximoEstado == null) {
                // Se o caractere lido não é aceito.
                return "erro lexico: caractere '" + p + "' inesperado em " + token;
            }

            // CASO 2: Transição Válida para estado não final
            // Consome o caractere e continua
            token += p.toString();
            corrente = proximoEstado;

            // Prepara leitura do próximo
            r.mark(1);
            p = proximo(r);
        }

        //Chegou no final de arquivo mas o leu token valido
        if (automato.getEstadosFinais().pertence(corrente)) {
            return token;
        }
        return "erro lexico: fim de arquivo inesperado";
    }

    // chama lexico até chegar no final de arquivo ou erro léxico
    // Quem decide o que fazer com eles é o analisador léxico.
    public Simbolo proximo(BufferedReader reader) throws IOException {
        int charLido = reader.read();
        if (charLido == -1){
            return null; //fim de arquivo
        }
        return new Simbolo((char) charLido);
    }
}
