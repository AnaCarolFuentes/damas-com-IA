package main.ui;

import main.entidades.MovimentoCaptura;
import main.entidades.Peca;
import main.entidades.Tabuleiro;

import java.awt.*;
import java.util.List;

public class PintarTabuleiro {

    private final Tabuleiro tabuleiroLogico;
    private final CasaBotao [][] tabuleiroInterface;

    public PintarTabuleiro(Tabuleiro tabuleiroLogico, CasaBotao [][] tabuleiroInterface) {
        this.tabuleiroLogico = tabuleiroLogico;
        this.tabuleiroInterface = tabuleiroInterface;
    }

    // --- Métodos de cor mantidos ---
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

    /**
     * Agora recebe a lista de capturas obrigatórias calculadas pelo Controller.
     */
    public void destacarMovimentosPossiveis(int linhaOrigem, int colOrigem, List<MovimentoCaptura> capturasObrigatorias) {

        // 1. Se existem capturas obrigatórias, destacamos APENAS elas
        if (!capturasObrigatorias.isEmpty()) {
            for (MovimentoCaptura mov : capturasObrigatorias) {
                // Só destaca se o movimento partir da peça que o usuário clicou
                if (mov.getOrigemLinha() == linhaOrigem && mov.getOrigemColuna() == colOrigem) {

                    // Pinta o destino final do salto de Roxo
                    setBackgroundPurple(mov.getDestinoLinha(), mov.getDestinoColuna());

                    // Pinta todas as peças inimigas que serão comidas no trajeto de Vermelho
                    for (int[] pos : mov.getPecasCapturadas()) {
                        setBackgroundRed(pos[0], pos[1]);
                    }
                }
            }
        }
        // 2. Se NÃO existem capturas, mostra movimentos simples normais
        else {
            for (int i = 0; i < Tabuleiro.getDimensoes(); i++) {
                for (int j = 0; j < Tabuleiro.getDimensoes(); j++) {
                    if (tabuleiroLogico.movimentoPossivel(linhaOrigem, colOrigem, i, j)) {
                        setBackgroundPurple(i, j);
                    }
                }
            }
        }
    }

    public void resetarCoresPadrao() {
        for (int i = 0; i < Tabuleiro.getDimensoes(); i++) {
            for (int j = 0; j < Tabuleiro.getDimensoes(); j++) {
                if ((i + j) % 2 == 0) setBackgroundBeige(i, j);
                else setBackgroundPink(i, j);
            }
        }
    }
}