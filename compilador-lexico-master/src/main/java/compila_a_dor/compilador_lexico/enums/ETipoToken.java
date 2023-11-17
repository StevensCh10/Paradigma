package compila_a_dor.compilador_lexico.enums;

public enum ETipoToken {
    IDENTIFICADOR,
    DECIMAL,
    ERRO,
    OPERADOR,
    PONTUACAO,
    INTEIRO,
    RESERVADO,
    STRING,
    VARIAVEL,
    FUNCAO_ESPECIAL,
    MACRO_ESPECIAL,
    DELIMITADOR,
    VALOR_TIPO,
    NUMERO,
    TIPO,
    LIFETIME,       // Exemplo de um novo tipo para lifetimes
    ATRIBUTO,       // Exemplo de um novo tipo para atributos
    CHAR_LITERAL,   // Exemplo de um novo tipo para literais de caractere
    CONTINUA;
}
