package main.entidades;

//Clase final peça para aumentar a legibilidade

public final class Peca {

    public static final char VAZIA = ' ';
    public  static final char BRANCA = 'b';
    public  static final char PRETA = 'p';
    public static final char DAMA_BRANCA = 'B';
    public  static final char DAMA_PRETA = 'P';
    public  static final char INVALIDA = '$';

    //uma peca nunca será instanciada
    private Peca() {}

    public static boolean vezDe(Jogador jogadorAtual, char casa) {
        if (jogadorAtual == Jogador.BRANCAS) {
            return casa == BRANCA || casa == DAMA_BRANCA;
        } else {
            return casa == PRETA || casa == DAMA_PRETA;
        }
    }

    public static boolean isPeca(char pecaAtual) {
        return pecaAtual == BRANCA || pecaAtual == PRETA;
    }

    public static boolean isDama(char pecaAtual) {
        return pecaAtual == DAMA_BRANCA || pecaAtual == DAMA_PRETA;
    }

    public static boolean isBranca(char pecaAtual) {
        return pecaAtual == BRANCA || pecaAtual == DAMA_BRANCA;
    }

    public static int[][] obterDirecoesPermitidas(char pecaAtual) {

        if (pecaAtual == DAMA_BRANCA || pecaAtual == DAMA_PRETA) {
            return new int[][]{
                    {-1, -1},
                    {-1, 1},
                    {1, -1},
                    {1, 1}
            };
        }

        // Peça comum
        if (pecaAtual == BRANCA) {
            return new int[][]{
                    {-1, -1},
                    {-1, 1}
            };
        } else {
            return new int[][]{
                    {1, -1},
                    {1, 1}
            };
        }
    }
}
