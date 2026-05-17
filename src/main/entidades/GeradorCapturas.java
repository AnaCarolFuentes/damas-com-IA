package main.entidades;

import main.logicGame.Tradutor;
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

        // Varre os 18 índices do vetor compacto
        for (int i = 0; i < 18; i++) {
            // Traduz índice para coordenada apenas para buscar o elemento e manter a lógica
            int[] coord = Tradutor.converteParaCoordenada(i);
            char peca = tabuleiro.getElemento(coord[0], coord[1]);

            if (Peca.vezDe(jogadorAtual, peca)) {
                List<Integer> capturadas = new ArrayList<>();
                List<Integer> caminho = new ArrayList<>();
                caminho.add(i); // Adiciona o índice de origem ao caminho

                construirPossibilidadesCapturas(coord[0], coord[1], coord[0], coord[1], capturadas, caminho, false, true);
            }
        }
        return melhoresMovimentos;
    }

    public void construirPossibilidadesCapturas(int linhaAtual, int colunaAtual, int origemLinha, int origemColuna, List<Integer> capturadas, List<Integer> caminho, boolean foiPromovida, boolean ehPrimeiroSalto) {

        boolean encontrouCaptura = false;
        char pecaAtual = tabuleiro.getElemento(linhaAtual, colunaAtual);

        int[][] direcoes;

        if (!Peca.isDama(pecaAtual) && ehPrimeiroSalto) {
            direcoes = Peca.obterDirecoesPermitidas(pecaAtual);
        } else {
            direcoes = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }

        for (int[] dir : direcoes) {
            int meioL = linhaAtual + dir[0];
            int meioC = colunaAtual + dir[1];
            int destinoL = linhaAtual + 2 * dir[0];
            int destinoC = colunaAtual + 2 * dir[1];

            if (ehCapturaValida(linhaAtual, colunaAtual, meioL, meioC, destinoL, destinoC)) {
                encontrouCaptura = true;

                char pecaMovida = pecaAtual;
                char pecaInimiga = tabuleiro.getElemento(meioL, meioC);

                int indiceMeio = Tradutor.converteParaIndice(meioL, meioC);
                int indiceDestino = Tradutor.converteParaIndice(destinoL, destinoC);

                tabuleiro.setElemento(linhaAtual, colunaAtual, Peca.VAZIA);
                tabuleiro.setElemento(meioL, meioC, Peca.VAZIA);
                tabuleiro.setElemento(destinoL, destinoC, pecaMovida);

                capturadas.add(indiceMeio);
                caminho.add(indiceDestino);

                construirPossibilidadesCapturas(destinoL, destinoC, origemLinha, origemColuna, capturadas, caminho, foiPromovida, false);

                tabuleiro.setElemento(linhaAtual, colunaAtual, pecaMovida);
                tabuleiro.setElemento(meioL, meioC, pecaInimiga);
                tabuleiro.setElemento(destinoL, destinoC, Peca.VAZIA);
                caminho.remove(caminho.size() - 1);
                capturadas.remove(capturadas.size() - 1);
            }
        }

        // FIM DO CAMINHO
        if (!encontrouCaptura && capturadas.size() > 0) {
            int total = capturadas.size();

            if (total > maxCapturasGlobal) {
                melhoresMovimentos.clear();
                maxCapturasGlobal = total;
            }

            if (total == maxCapturasGlobal) {
                // Instancia usando os índices
                int oI = Tradutor.converteParaIndice(origemLinha, origemColuna);
                int dI = Tradutor.converteParaIndice(linhaAtual, colunaAtual);

                MovimentoCaptura mov = new MovimentoCaptura(oI, dI, new ArrayList<>(capturadas), new ArrayList<>(caminho));
                melhoresMovimentos.add(mov);
            }
        }
    }

    private boolean ehCapturaValida(int linhaAtual, int colunaAtual, int meioL, int meioC, int destinoL, int destinoC) {
        if (!tabuleiro.dentroDoTabuleiro(destinoL, destinoC))
            return false;

        if (tabuleiro.getElemento(destinoL, destinoC) != Peca.VAZIA)
            return false;

        if (!tabuleiro.dentroDoTabuleiro(meioL, meioC))
            return false;

        char pecaAtual = tabuleiro.getElemento(linhaAtual, colunaAtual);
        return tabuleiro.isEnemy(Peca.isBranca(pecaAtual), meioL, meioC);
    }

    public boolean podeContinuarCapturando(int linha, int coluna) {
        char peca = tabuleiro.getElemento(linha, coluna);
        if (peca == Peca.VAZIA || peca == Peca.INVALIDA) return false;

        int[][] direcoes = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : direcoes) {
            int meioL = linha + dir[0];
            int meioC = coluna + dir[1];
            int destinoL = linha + 2 * dir[0];
            int destinoC = coluna + 2 * dir[1];

            if (ehCapturaValida(linha, coluna, meioL, meioC, destinoL, destinoC)) {
                return true;
            }
        }
        return false;
    }
}