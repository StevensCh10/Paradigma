package compila_a_dor.compilador_semantico;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

import compila_a_dor.compilador_lexico.Token;

public class AnallisadorSemantico {

    public static String analisarSemanticaPrimeiroToken(Token token){
        return "";  
    }

    public static boolean verificarBalanceamentoDelimitadores() throws IOException{
        Stack<Character> pilha = new Stack<>();
        
        for (char caractere : lerArquivo().toCharArray()) {
            if (caractere == '(' || caractere == '{') {
                pilha.push(caractere);
            } else if (caractere == ')' || caractere == '}') {
                if (pilha.isEmpty()) {
                    // Há um fechamento sem correspondência de abertura
                    return false;
                }
                
                char topo = pilha.pop();
                if ((caractere == ')' && topo != '(') || (caractere == '}' && topo != '{')) {
                    // Fechamento sem correspondência de abertura
                    return false;
                }
            }
        }
        return pilha.isEmpty();
    }

    private static String lerArquivo() throws IOException {
        Path caminho = Paths.get("compilador-lexico-master\\src\\main\\resources\\prog.in");
        byte[] bytes = Files.readAllBytes(caminho);
        return new String(bytes);
    }
}
