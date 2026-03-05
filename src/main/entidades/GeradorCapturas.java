package main.entidades;

import java.util.ArrayList;
import java.util.List;

public class GeradorCapturas {

    private Tabuleiro tabuleiro;
    private List<MovimentoCaptura> melhoresMovimentos;
    private int maxCapturasGlobal = 0;

    public GeradorCapturas(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public List<MovimentoCaptura> encontrarMelhoresCapturas(Jogador jogadorAtual) {
        melhoresMovimentos = new ArrayList<>();
        maxCapturasGlobal = 0;

        for (int i = 0; i < tabuleiro.getDimensoes(); i++) {
            for (int j = 0; j < tabuleiro.getDimensoes(); j++) {
                if ((Peca.vezDe(jogadorAtual, tabuleiro.getElemento(i, j)))) {
                    List<int[]> capturadas = new ArrayList<>();
                    List<int[]> caminho = new ArrayList<>();
                    caminho.add(new int[]{i, j});
                    construirPossibilidadesCapturas(i, j, i, j, capturadas, caminho, false, true);
                }
            }
        }
        return melhoresMovimentos;
    }

    public void construirPossibilidadesCapturas(int linhaAtual, int colunaAtual, int origemLinha, int origemColuna, List<int[]> capturadas, List<int[]> caminho, boolean foiPromovida, boolean ehPrimeiroSalto) {

        boolean encontrouCaptura = false;
        char pecaAtual = tabuleiro.getElemento(linhaAtual, colunaAtual);

        int[][] direcoes;

        // REGRA ESPECIAL:
        // Se for o primeiro salto e for uma peça comum, usa as direções padrão (frente).
        // Se for dama ou se já for o segundo/terceiro salto, libera as 4 direções.
        if (!Peca.isDama(pecaAtual) && ehPrimeiroSalto) {
            direcoes = Peca.obterDirecoesPermitidas(pecaAtual);
        } else {
            // Libera todas as direções para Damas ou saltos subsequentes de peças comuns
            direcoes = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }

        for (int[] dir : direcoes) {
            int meioL = linhaAtual + dir[0];
            int meioC = colunaAtual + dir[1];
            int destinoL = linhaAtual + 2 * dir[0];
            int destinoC = colunaAtual + 2 * dir[1];

            if (ehCapturaValida(linhaAtual, colunaAtual, meioL, meioC, destinoL, destinoC)) {
                encontrouCaptura = true;

                // --- Lógica de Backup e Movimento ---
                char pecaMovida = pecaAtual;
                char pecaInimiga = tabuleiro.getElemento(meioL, meioC);

                tabuleiro.setElemento(linhaAtual, colunaAtual, Peca.VAZIA);
                tabuleiro.setElemento(meioL, meioC, Peca.VAZIA);
                tabuleiro.setElemento(destinoL, destinoC, pecaMovida);

                capturadas.add(new int[]{meioL, meioC});
                caminho.add(new int[]{destinoL, destinoC});

                // RECURSÃO: Agora ehPrimeiroSalto passa a ser FALSE
                construirPossibilidadesCapturas(destinoL, destinoC, origemLinha, origemColuna, capturadas, caminho, foiPromovida, false);

                // --- Backtracking (reverter tudo) ---
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
                MovimentoCaptura mov = new MovimentoCaptura(origemLinha, origemColuna, linhaAtual, colunaAtual, new ArrayList<>(capturadas), new ArrayList<>(caminho));
                melhoresMovimentos.add(mov);
            }
        }
    }

    private boolean ehCapturaValida(int linhaAtual, int colunaAtual, int meioL, int meioC, int destinoL, int destinoC) {

        if (!tabuleiro.dentroDoTabuleiro(destinoL, destinoC))
            return false;

        if (tabuleiro.getElemento(destinoL, destinoC) != Peca.VAZIA)
            return false;

        // 3️⃣ casa do meio dentro do tabuleiro
        if (!tabuleiro.dentroDoTabuleiro(meioL, meioC))
            return false;

        char pecaAtual = tabuleiro.getElemento(linhaAtual, colunaAtual);
        char pecaMeio = tabuleiro.getElemento(meioL, meioC);

        if (!tabuleiro.isEnemy(Peca.isBranca(pecaAtual), meioL, meioC))
            return false;

        return true;
    }




}
