package main.ui;

import main.logicGame.Peca;
import main.logicGame.Tabuleiro;

import java.awt.*;

public class PintarTabuleiro {

    private final Tabuleiro tabuleiroLogico;

    private final CasaBotao [][] tabuleiroInterface;

    public PintarTabuleiro(Tabuleiro tabuleiroLogico, CasaBotao [][] tabuleiroInterface) {
        this.tabuleiroLogico = tabuleiroLogico;
        this.tabuleiroInterface = tabuleiroInterface;
    }


    public void setBackgoundBorrow(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color(139, 69, 19));
    }

    public void setBackgroundPink(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color(255, 20, 147));
    }

    public void setBackgroundBeige(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color(245, 245, 220));
    }
    public void setBackgroundPurple(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color (128, 0, 128));
    }
    public void setBackgroundRed(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color (255, 0, 0));
    }


    public void destacarMovimentosPossiveis(int linhaOrigem, int colunaOrigem) {
        char pecaDestaque = tabuleiroLogico.getMatriz()[linhaOrigem][colunaOrigem];
        boolean branca = Peca.isBranca(pecaDestaque);

        for (int i = 0; i < tabuleiroLogico.getDimensoes(); i++) {
            for (int j = 0; j < tabuleiroLogico.getDimensoes(); j++) {

                if (tabuleiroLogico.movimentoPossivel(linhaOrigem, colunaOrigem, i, j)) {
                    // 1. Pinta o destino possível de roxo
                    setBackgroundPurple(i, j);

                    // 2. Lógica para achar e pintar o inimigo de Roxo
                    int deltaLinha = i - linhaOrigem;
                    int deltaColuna = j - colunaOrigem;

                    // Se o movimento for de captura (distância > 1)
                    if (Math.abs(deltaLinha) > 1) {
                        int versorLinha = Integer.signum(deltaLinha);
                        int versorColuna = Integer.signum(deltaColuna);
                        int checkL = linhaOrigem + versorLinha;
                        int checkC = colunaOrigem + versorColuna;

                        // Percorre o caminho até o destino para achar a peça capturada
                        while (checkL != i && checkC != j) {
                            if (tabuleiroLogico.isEnemy(branca, checkL, checkC)) setBackgroundRed(checkL, checkC);

                            checkL += versorLinha;
                            checkC += versorLinha;
                        }
                    }
                }
            }
        }
    }

    public void resetarCoresPadrao() {
        for (int i = 0; i < tabuleiroLogico.getDimensoes(); i++) {
            for (int j = 0; j < tabuleiroLogico.getDimensoes(); j++) {
                // Cores do tabuleiro
                if ((i + j) % 2 == 0) {
                    setBackgroundBeige(i, j);
                } else {
                    setBackgroundPink(i, j);
                }

            }
        }
    }

}
