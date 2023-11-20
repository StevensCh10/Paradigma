package compila_a_dor;

import java.io.IOException;

import compila_a_dor.compilador_lexico.AnalisadorLexico;
import compila_a_dor.compilador_lexico.Token;
import compila_a_dor.compilador_semantico.AnallisadorSemantico;

public class Main {
    public static void main(String[] args) throws IOException {
        AnalisadorLexico analisadorLexico = new AnalisadorLexico("prog.in");

        if(!AnallisadorSemantico.verificarBalanceamentoDelimitadores()){
            System.err.println("\nErro na sintaxe de parenteses/chaves\n");
            return;
        }
        while (true) {
            try {
                Token token = analisadorLexico.proximoToken();
                System.out.println(token.getTexto() + " - " + token.getId().toString());
            } catch (Exception e) {
                break;
            }
        }
    }
}
