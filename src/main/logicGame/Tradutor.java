package main.logicGame;

public class Tradutor {

    // Converte (Linha, Coluna) 0-5 para Índice 0-17
    public static int converteParaIndice(int linha, int coluna) {
        // Cada linha tem 3 casas válidas.
        // Se a linha é 0, o índice é 0, 1, 2. Se a linha é 1, é 3, 4, 5...
        return (linha * 3) + (coluna / 2);
    }

    // Converte Índice 0-17 para (Linha, Coluna) 0-5
    public static int[] converteParaCoordenada(int indice) {
        int linha = indice / 3;
        int coluna;

        // Linhas pares (0, 2, 4) começam a casa válida na coluna 1, 3, 5
        // Linhas ímpares (1, 3, 5) começam a casa válida na coluna 0, 2, 4

        //Ó ceus, preciso corrigir isso
        if (linha % 2 == 0) {
            coluna = (indice % 3) * 2 + 1;
        } else {
            coluna = (indice % 3) * 2;
        }
        return new int[]{linha, coluna};
    }
}