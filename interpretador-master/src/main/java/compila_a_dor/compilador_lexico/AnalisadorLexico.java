package compila_a_dor.compilador_lexico;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compila_a_dor.compilador_lexico.enums.ETipoToken;

public class AnalisadorLexico {

    public static final String[] PALAVRAS_RESERVADAS_RUST = {
        "as","async","await","break","const","continue","create","dyn","else","enum",
        "extern","false","fn","for", "if","impl","in","let","loop","match","mod","move",
        "mut","pub","ref","return","Self","self","static","struct","super","trait",
        "true","type","union","unsafe","use","where", "while"
    };

    public static final String[] FUNCOES_ESPECIAIS_RUST ={
        "drop","clone","ccopy","Into","From","Deref","DerefMut","Drop","Default","FnMut",
        "FnOnce","main","panic","unreachable"
    };

    public static final String[] MACRO_ESPECIAIS_RUST ={
        "println!","assert","format!","vec!","panic!","file!", "line!","column!","include!",
        "include_str"
    };

    public static final String[] TYPES_RUST ={
        "i8","i16","i32","i64","i128","u8","u16","u32","u64","u128","f32","f64","bool",
        "char","String"
    };

    private char[] conteudo;
    private int posicao;

    public AnalisadorLexico() {
        try {
            Path caminho = Paths.get("interpretador-master\\src\\main\\resources\\prog.in");
            byte[] bytes = Files.readAllBytes(caminho);
            this.conteudo = new String(bytes).toCharArray();
            this.posicao = 0;
        } catch (IOException ex) {
            System.err.println("Erro ao ler arquivo");
        }
    }

    private boolean fimArquivo() {
        return posicao == conteudo.length;
    }

    private char proximoCaractere() {
        return conteudo[posicao++];
    }

    private boolean ehLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean ehEspacoEmBranco(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    private boolean ehOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '=';
    }

    private boolean ehDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean ehDoisPontos(char c) {
        return c == ':';
    }

    private boolean ehPontuacao(char c) {
        return c == ',' || c == '.' || c == ':' || c == ';' || c == '!';
    }

    private boolean ehDelimitador(char c){
        return c == '{' || c == '}' || c == '(' || c == ')' || c == '[' || c == ']';
    }

    private void retroceder() {
        posicao--;
    }

    private boolean isCaractereEspecial(char c) {
        return c == '!' || c == '\"';
    }

    public Token proximoToken() {
        int s = 0;
        int pontoInicial = posicao;
        ETipoToken tokenParaRetorno = ETipoToken.CONTINUA;
        String valorToken = "";

        while (tokenParaRetorno == ETipoToken.CONTINUA) {
            switch (s) {
                case 0:
                    char c = proximoCaractere();
                    if (ehDoisPontos(c)) {
                        s = 3;
                    } else if (ehLetra(c)) {
                        s = 1;
                    } else if (ehOperador(c)) {
                        s = 2;
                    }else if (ehDelimitador(c)) {
                        s = 7;
                    }else if (ehEspacoEmBranco(c)) {
                        s = 0;
                    } else if (ehPontuacao(c)) {
                        tokenParaRetorno = ETipoToken.PONTUACAO;
                    } else if (ehDigito(c)) {
                        s = 4;
                    } else if (c == '\"' || c == '\''){
                        s = 6;
                    } else {
                        tokenParaRetorno = ETipoToken.ERRO;
                    }
                    break;
                case 1:
                    c = proximoCaractere();
                    if (ehLetra(c) || ehDigito(c)) {
                        s = 1;
                    } else if (ehEspacoEmBranco(c)) {
                        tokenParaRetorno = ETipoToken.IDENTIFICADOR;
                    } else if (ehOperador(c) || ehPontuacao(c) || ehDelimitador(c)) {
                        
                        int pontoFinal = posicao; //melhorar
                        valorToken = new String(conteudo);
                        valorToken = valorToken.substring(pontoInicial, pontoFinal-1).trim() + c;

                        if(Arrays.asList(MACRO_ESPECIAIS_RUST).contains(valorToken)){
                            tokenParaRetorno = ETipoToken.MACRO_ESPECIAL;
                        }else{
                            retroceder();
                            tokenParaRetorno = ETipoToken.IDENTIFICADOR;
                        }
                    }else {
                        tokenParaRetorno = ETipoToken.ERRO;
                    }
                    break;
                case 2:
                    tokenParaRetorno = ETipoToken.OPERADOR;
                    break;
                case 3:
                    c = proximoCaractere();
                    if (ehOperador(c))
                        tokenParaRetorno = ETipoToken.OPERADOR;
                    if (ehEspacoEmBranco(c)) {
                        retroceder();
                        tokenParaRetorno = ETipoToken.PONTUACAO;
                    } else if (isCaractereEspecial(c)) {
                        tokenParaRetorno = ETipoToken.PONTUACAO;
                        StringBuilder strBuilder = new StringBuilder();
                        strBuilder.append(c);
                        while (isCaractereEspecial(c)) {
                            strBuilder.append(c);
                            c = proximoCaractere();
                        }
                        valorToken = strBuilder.toString();
                        retroceder();
                    } else
                        tokenParaRetorno = ETipoToken.ERRO;
                    break;
                case 4:
                    c = proximoCaractere();
                    if (ehDigito(c)) {
                        s = 4;
                    } else if (ehEspacoEmBranco(c)) {
                        tokenParaRetorno = ETipoToken.NUMERO;
                    } else if (ehOperador(c) || ehPontuacao(c)) {
                        if(c == '.'){
                            s=4;
                            if(proximoCaractere() >= '0' && c <= '9'){
                                retroceder();
                            }else{
                                retroceder();
                                tokenParaRetorno = ETipoToken.ERRO;
                            }
                        }else{
                            retroceder();
                            tokenParaRetorno = ETipoToken.NUMERO;
                        }
                    }else if(ehDelimitador(c)){
                        retroceder();
                        tokenParaRetorno = ETipoToken.NUMERO;
                    }else if(ehLetra(c)){    
                        s = 4;
                    }else {
                        tokenParaRetorno = ETipoToken.ERRO;
                    }
                    break;
                case 5:
                    c = proximoCaractere();
                    if (ehEspacoEmBranco(c)) {
                        tokenParaRetorno = ETipoToken.PONTUACAO;
                    } else {
                        tokenParaRetorno = ETipoToken.ERRO;
                    }
                    break;
                case 6:
                    c = proximoCaractere();
                    while(c != '\"'){
                        c = proximoCaractere();
                    }
                    tokenParaRetorno = ETipoToken.STRING;
                    break;
                case 7:
                    tokenParaRetorno = ETipoToken.DELIMITADOR;
                    break;        
            }
        }

        if (tokenParaRetorno == ETipoToken.ERRO) {
            while (true) {
                char c = proximoCaractere();
                if (ehEspacoEmBranco(c) || ehPontuacao(c) || ehOperador(c))
                    break;
            }
            retroceder();
        }

        int pontoFinal = posicao;
        valorToken = new String(conteudo);
        valorToken = valorToken.substring(pontoInicial, pontoFinal).trim();

        if (tokenParaRetorno == ETipoToken.IDENTIFICADOR) {
            if (Arrays.asList(PALAVRAS_RESERVADAS_RUST).contains(valorToken)){
                tokenParaRetorno = ETipoToken.RESERVADO;
            } else if(Arrays.asList(FUNCOES_ESPECIAIS_RUST).contains(valorToken)){
                tokenParaRetorno = ETipoToken.FUNCAO_ESPECIAL;
            } else if(Arrays.asList(MACRO_ESPECIAIS_RUST).contains(valorToken)){
                tokenParaRetorno = ETipoToken.MACRO_ESPECIAL;
            }else if(Arrays.asList(TYPES_RUST).contains(valorToken)){
                tokenParaRetorno = ETipoToken.TIPO;
            }else{
                tokenParaRetorno = ETipoToken.VARIAVEL;
            }    
        }
        if(tokenParaRetorno == ETipoToken.NUMERO){
            if(contemLetra(valorToken)){
                tokenParaRetorno = ETipoToken.VALOR_TIPO;
            }else if(valorToken.contains(".")){
                tokenParaRetorno = ETipoToken.DECIMAL;
            }else{
                tokenParaRetorno = ETipoToken.INTEIRO;
            }
        }

        return new Token(tokenParaRetorno, valorToken);
    }

     public static boolean contemLetra(String texto) {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(texto);

        return matcher.find();
    }
}