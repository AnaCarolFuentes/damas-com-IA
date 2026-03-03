package main.logicGame;

import java.util.ArrayList;
import java.util.List;

public class MovimentoCaptura {

    private int origemLinha;
    private int origemColuna;

    private int destinoLinha;
    private int destinoColuna;

    private List<int[]> pecasCapturadas;
    private List<int[]> caminho;

    public MovimentoCaptura(int oL, int oC, int dL, int dC, List<int[]> capturadas, List<int[]> caminho) {

        this.origemLinha = oL;
        this.origemColuna = oC;
        this.destinoLinha = dL;
        this.destinoColuna = dC;
        this.pecasCapturadas = capturadas;
        this.caminho = caminho;
    }

    public int getTotalCapturas() {
        return pecasCapturadas.size();
    }
}
