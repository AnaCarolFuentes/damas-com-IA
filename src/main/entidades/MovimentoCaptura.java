package main.entidades;

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

    public int getOrigemLinha() {
        return origemLinha;
    }

    public int getOrigemColuna() {
        return origemColuna;
    }

    public int getDestinoLinha() {
        return destinoLinha;
    }

    public int getDestinoColuna() {
        return destinoColuna;
    }

    public List<int[]> getPecasCapturadas() {
        return pecasCapturadas;
    }

    public List<int[]> getCaminho() {
        return caminho;
    }

    public int getTotalCapturas() {
        return pecasCapturadas.size();
    }
}
