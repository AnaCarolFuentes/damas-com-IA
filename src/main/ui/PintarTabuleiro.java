package main.ui;

import main.ai.Node;
import main.entidades.MovimentoCaptura;
import main.entidades.Tabuleiro;

import java.awt.*;
import java.util.List;

public class PintarTabuleiro {

    private final Tabuleiro tabuleiroLogico;
    private final CasaBotao [][] tabuleiroInterface;
    private static final Color COR_DESTINO_MOVIMENTO = new Color(128, 0, 128);
    private static final Color COR_DESTINO_CAPTURA = new Color(54, 70, 180);
    private static final Color COR_VITIMA = new Color(255, 0, 0, 180);
    private static final Color COR_PASSAGEM_COMBO = new Color(255, 140, 0);
    private static final Color COR_DICA = new Color(173, 216, 230);

    public PintarTabuleiro(Tabuleiro tabuleiroLogico, CasaBotao [][] tabuleiroInterface) {
        this.tabuleiroLogico = tabuleiroLogico;
        this.tabuleiroInterface = tabuleiroInterface;
    }

    // --- Métodos de cor ---
    public void setBackgroundPink(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color(255, 20, 147));
    }

    public void setBackgroundBeige(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(new Color(245, 245, 220));
    }

    public void setBackgroundPurple(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(COR_DESTINO_MOVIMENTO);
    }

    public void setBackgroundRed(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(COR_VITIMA);
    }


    public void destacarMovimentosPossiveis(int linhaOrigem, int colOrigem, List<MovimentoCaptura> capturasObrigatorias) {

        if (!capturasObrigatorias.isEmpty()) {
            for (MovimentoCaptura mov : capturasObrigatorias) {
                if (mov.getOrigemLinha() == linhaOrigem && mov.getOrigemColuna() == colOrigem) {

                    destacarCombo(mov);
                }
            }
        }
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

    // PintarTabuleiro.java

    public void setBackgroundHint(int linha, int coluna) {
        tabuleiroInterface[linha][coluna].setBackground(COR_DICA);
    }

    public void aplicarDicas(List<?> casasOuMovimentos) {
        for (Object item : casasOuMovimentos) {
            if (item instanceof Node) {
                aplicarDicaMovimento((Node) item);
            } else if (item instanceof int[]) {
                int[] pos = (int[]) item;
                setBackgroundHint(pos[0], pos[1]);
            }
        }
    }

    public void destacarCombo(MovimentoCaptura mov) {
        destacarCasasDePassagem(mov.getCaminho(), mov.getDestinoLinha(), mov.getDestinoColuna());

        tabuleiroInterface[mov.getDestinoLinha()][mov.getDestinoColuna()].setBackground(COR_DESTINO_CAPTURA);

        for (int[] vitima : mov.getPecasCapturadas()) {
            tabuleiroInterface[vitima[0]][vitima[1]].setBackground(COR_VITIMA);
        }
    }

    private void aplicarDicaMovimento(Node n) {
        if (!n.isCaptura()) {
            setBackgroundHint(n.getOrigemLinha(), n.getOrigemColuna());
            return;
        }

        destacarCasasDePassagem(n.getCaminho(), n.getDestinoLinha(), n.getDestinoColuna());
        tabuleiroInterface[n.getDestinoLinha()][n.getDestinoColuna()].setBackground(COR_DESTINO_CAPTURA);

        for (int[] vitima : n.getPecasCapturadas()) {
            tabuleiroInterface[vitima[0]][vitima[1]].setBackground(COR_VITIMA);
        }
    }

    private void destacarCasasDePassagem(List<int[]> caminho, int destinoLinha, int destinoColuna) {
        for (int i = 1; i < caminho.size() - 1; i++) {
            int[] passo = caminho.get(i);
            if (passo[0] != destinoLinha || passo[1] != destinoColuna) {
                tabuleiroInterface[passo[0]][passo[1]].setBackground(COR_PASSAGEM_COMBO);
            }
        }
    }
}
