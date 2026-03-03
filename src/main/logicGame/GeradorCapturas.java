package main.logicGame;

import java.util.ArrayList;
import java.util.List;

public class GeradorCapturas {

    private Tabuleiro tabuleiro;
    private List<MovimentoCaptura> melhoresMovimentos;
    private int maxCapturasGlobal;

    public GeradorCapturas(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public List<MovimentoCaptura> encontrarMelhoresCapturas(Jogador jogadorAtual) {
        melhoresMovimentos = new ArrayList<>();

        for (int i = 0; i < Tabuleiro.getDimensoes(); i++) {
            for (int j = 0; j < Tabuleiro.getDimensoes(); j++) {
                if ((Peca.vezDe(jogadorAtual, Tabuleiro.getElemento(i, j)))) {
                    List<int[]> capturadas = new ArrayList<>();
                    List<int[]> caminho = new ArrayList<>();
                    caminho.add(new int[]{i, j});
                    dfsCaptura(i, j, i, j, capturadas, caminho, false);
                }
            }
        }
        return melhoresMovimentos;
    }

    public void dfsCaptura(int linhaAtual, int colunaAtual,
                           int origemLinha, int origemColuna,
                           List<int[]> capturadas,
                           List<int[]> caminho,
                           boolean foiPromovida) {

        boolean encontrouCaptura = false;

        char pecaAtual = tabuleiro.getElemento(linhaAtual, colunaAtual);
        int[][] direcoes = Peca.obterDirecoesPermitidas(pecaAtual);

        for (int[] dir : direcoes) {

            int meioL = linhaAtual + dir[0];
            int meioC = colunaAtual + dir[1];

            int destinoL = linhaAtual + 2 * dir[0];
            int destinoC = colunaAtual + 2 * dir[1];

            if (ehCapturaValida(meioL, meioC, destinoL, destinoC)) {

                encontrouCaptura = true;

                // --- BACKUP ---
                char pecaMovida = pecaAtual;
                char pecaInimiga = tabuleiro.getElemento(meioL, meioC);

                // --- FAZER MOVIMENTO ---
                tabuleiro.setElemento(linhaAtual, colunaAtual, Peca.VAZIA);
                tabuleiro.setElemento(meioL, meioC, Peca.VAZIA);
                tabuleiro.setElemento(destinoL, destinoC, pecaMovida);

                capturadas.add(new int[]{meioL, meioC});
                caminho.add(new int[]{destinoL, destinoC});

                boolean promoveuAgora = false;

                if (tabuleiro.devePromover(destinoL, pecaMovida)) {
                    tabuleiro.promoverTemporariamente(destinoL, destinoC);
                    promoveuAgora = true;
                }

                // --- RECURSÃO ---
                dfsCaptura(destinoL, destinoC,
                        origemLinha, origemColuna,
                        capturadas, caminho,
                        foiPromovida || promoveuAgora);

                // --- BACKTRACKING ---
                if (promoveuAgora) {
                    tabuleiro.reverterPromocao(destinoL, destinoC);
                }

                tabuleiro.setElemento(linhaAtual, colunaAtual, pecaMovida);
                tabuleiro.setElemento(meioL, meioC, pecaInimiga);
                tabuleiro.setElemento(destinoL, destinoC, Peca.VAZIA);

                caminho.remove(caminho.size() - 1);
                capturadas.remove(capturadas.size() - 1);
            }
        }

        //FIM DO CAMINHO
        if (!encontrouCaptura && capturadas.size() > 0) {

            int total = capturadas.size();

            if (total > maxCapturasGlobal) {
                melhoresMovimentos.clear();
                maxCapturasGlobal = total;
            }

            if (total == maxCapturasGlobal) {
                MovimentoCaptura mov = new MovimentoCaptura(
                        origemLinha,
                        origemColuna,
                        linhaAtual,
                        colunaAtual,
                        new ArrayList<>(capturadas),
                        new ArrayList<>(caminho)
                );
                melhoresMovimentos.add(mov);
            }
        }
    }

    private boolean ehCapturaValida(int meioL, int meioC,
                                    int destinoL, int destinoC) {

        if (!dentroDoTabuleiro(destinoL, destinoC))
            return false;

        if (tabuleiro.getElemento(destinoL, destinoC) != '.')
            return false;

        if (!dentroDoTabuleiro(meioL, meioC))
            return false;

        char pecaMeio = tabuleiro.getElemento(meioL, meioC);
        char pecaAtual = tabuleiro.getElemento(
                meioL - (meioL - destinoL) / 2,
                meioC - (meioC - destinoC) / 2
        );

        if (!tabuleiro.isEnemy(Peca.isBranca(pecaAtual), meioL, meioC))
            return false;

        return true;
    }

    private boolean dentroDoTabuleiro(int linha, int coluna) {
        return linha >= 0 &&
                linha < Tabuleiro.getDimensoes() &&
                coluna >= 0 &&
                coluna < Tabuleiro.getDimensoes();
    }

    private boolean ehCapturaValida(int linhaAtual,
                                    int colunaAtual,
                                    int meioL, int meioC,
                                    int destinoL, int destinoC) {

        if (!tabuleiro.dentroDoTabuleiro(destinoL, destinoC))
            return false;

        if (tabuleiro.getElemento(destinoL, destinoC) != Peca.VAZIA)
            return false;

        // 3️⃣ casa do meio dentro do tabuleiro
        if (!dentroDoTabuleiro(meioL, meioC))
            return false;

        char pecaAtual = tabuleiro.getElemento(linhaAtual, colunaAtual);
        char pecaMeio = tabuleiro.getElemento(meioL, meioC);

        if (!tabuleiro.isEnemy(Peca.isBranca(pecaAtual), meioL, meioC))
            return false;

        return true;
    }




}
