/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.entidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;

/**
 *
 * @author Douglas
 */
public class Tabuleiro implements Cloneable {

    /*
    * Peças pretas - 2
    * Peças brancas - 1
    *
     */
    private char[][] matriz;
    private static final int TAMANHO = 6;
    private Jogador jogadorAtual;

    public Tabuleiro() {
        this.matriz = new char[TAMANHO][TAMANHO];
        jogadorAtual = Jogador.BRANCAS;
        inicializar();
    }

    private void inicializar() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if ((i + j) % 2 != 0) {
                    if (i < 2) {
                        matriz[i][j] = Peca.PRETA; // Pretas
                    } else if (i > 3) {
                        matriz[i][j] = Peca.BRANCA; // Brancas
                    } else matriz[i][j] = Peca.VAZIA;
                } else matriz[i][j] = Peca.INVALIDA;
            }
        }
    }

    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.matriz = new char[TAMANHO][];
            for (int i = 0; i < TAMANHO; i++) {
                clone.matriz[i] = this.matriz[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    /*
        Implmentação dos métodos - getMovimentosPossiveis(), fazerMovimento(), etc
    */

    public char[][] getMatriz() {
        return matriz;
    }
    public static int getDimensoes()  { return TAMANHO; }
    public char getElemento(int linha, int coluna)  { return matriz[linha][coluna]; }
    public void setElemento(int linha, int coluna, char elemento) { matriz[linha][coluna] = elemento; }

    public boolean movimentoPossivel(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {

        // 2. Peça só se move para casas "Pretas/Rosas" (onde i+j é ímpar no seu código)
        if ((linhaDestino + colunaDestino) % 2 == 0) return false;

        char origem = matriz[linhaOrigem][colunaOrigem];
        char destino = matriz[linhaDestino][colunaDestino];

        if(destino != Peca.VAZIA) return false;


        if(Peca.isDama(origem)) return realizarMovimentoDama(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);

        else return realizarMovimentoPeca(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
    }

    public boolean realizarMovimentoPeca(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        int deltaLinha = linhaDestino - linhaOrigem;
        int deltaColuna = colunaDestino - colunaOrigem;
        int versorLinha = Integer.signum(deltaLinha);
        int versorColuna = Integer.signum(deltaColuna);

        char pecaAtual = matriz[linhaOrigem][colunaOrigem];
        boolean isBranca = Peca.isBranca(pecaAtual);

        if(!(Math.abs(deltaColuna) == Math.abs(deltaLinha))) return false;
        // 1. Movimento Simples (1 casa)
        if (Math.abs(deltaColuna) == 1) {
            // Brancas sobem (delta -1), Pretas descem (delta 1)
            if ((isBranca && deltaLinha == -1) || (!isBranca && deltaLinha == 1)) {
                if (matriz[linhaDestino][colunaDestino] == Peca.VAZIA) {
                    return true;
                }
            }
        }

        // 2. Captura (2 casas)
        if (Math.abs(deltaColuna) == 2) {
            if ((isBranca && deltaLinha == -2) || (!isBranca && deltaLinha == 2)) {
                int lInimigo = linhaOrigem + (deltaLinha / 2);
                int cInimigo = colunaOrigem + (deltaColuna / 2);

                //comerPeca(lInimigo, cInimigo);
                return isEnemy(isBranca, lInimigo, cInimigo) && matriz[linhaDestino][colunaDestino] == Peca.VAZIA;
            }
        }

        return false;
    }

    public boolean realizarMovimentoDama(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        int deltaLinha = linhaDestino - linhaOrigem;
        int deltaColuna = colunaDestino - colunaOrigem;
        int versorLinha = Integer.signum(deltaLinha);
        int versorColuna = Integer.signum(deltaColuna);
        int posLinha = linhaOrigem + versorLinha;
        int posColuna = colunaOrigem + versorColuna;
        int pecasNoCaminho = 0;
        boolean isBranca = Peca.isBranca(matriz[linhaOrigem][colunaOrigem]);
        List<List<Integer>> posicaoPecas = new ArrayList<>();

        if(abs(deltaLinha) != abs(deltaColuna)) return false;

        while (posLinha != linhaDestino && posColuna != colunaDestino) {
            if(matriz[posLinha][posColuna] != Peca.VAZIA) {
                pecasNoCaminho++;
                posicaoPecas.add(Arrays.asList(posLinha, posColuna));
            }
            posLinha += versorLinha;
            posColuna += versorColuna;
        }

        if(pecasNoCaminho == 0) return true;
        else if(pecasNoCaminho >= 1) {
            for (List<Integer> coordenada : posicaoPecas) {
                int l = coordenada.get(0); // Captura a linha
                int c = coordenada.get(1); // Captura a coluna

                // 1. É inimiga? Se não for, para tudo e retorna falso
                if (!isEnemy(isBranca, l, c)) return false;
                if (matriz[l + versorLinha][c + versorColuna] != Peca.VAZIA && (l + versorLinha > 5 && c + versorColuna > 5) && !(l + versorLinha == linhaDestino && c + versorColuna == colunaDestino)) return false;
            }

            //comerPeca(posicaoPecas);
        }

        return true;
    }

    //Lógica para comer peças comuns
    public boolean isEnemy (boolean pecaAtualIsBranca, int linha, int coluna) {
        if(matriz[linha][coluna] == Peca.VAZIA) return false;
        char pecaAdjacente = matriz[linha][coluna];
        return (!Peca.isBranca(pecaAdjacente) && pecaAtualIsBranca) || (Peca.isBranca(pecaAdjacente) && !pecaAtualIsBranca);
    }

    public boolean moverPecaLogica(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        // 1. Antes de tudo, a peça precisa se mover para algum lugar vazio
        if (matriz[linhaDestino][colunaDestino] != Peca.VAZIA) return false;

        // Guardam quem está se movendo para checar promoção depois
        char pecaMovida = matriz[linhaOrigem][colunaOrigem];

        // 2. Lógica de Captura: Percorrer o caminho entre origem e destino
        int versorLinha = Integer.signum(linhaDestino - linhaOrigem);
        int versorColuna = Integer.signum(colunaDestino - colunaOrigem);
        int deltaLinha = linhaOrigem + versorLinha;
        int deltaColuna = colunaOrigem + versorColuna;

        // "limpa" qualquer inimigo que esteja no caminho
        while (deltaLinha != linhaDestino && deltaColuna != colunaDestino) {
            if (matriz[deltaLinha][deltaColuna] != Peca.VAZIA) {
                comerPeca(deltaLinha, deltaColuna);
            }
            deltaLinha += versorLinha;
            deltaColuna += versorColuna;
        }

        // 3. Executa o movimento físico na matriz
        matriz[linhaDestino][colunaDestino] = pecaMovida;
        matriz[linhaOrigem][colunaOrigem] = Peca.VAZIA;

        // 4. Promoção para Dama
        verificarPromocao(linhaDestino, colunaDestino);

        return true;
    }

    public void comerPeca(int linha, int coluna) {
            matriz[linha][coluna] = Peca.VAZIA;
    }

    // comer varias peças de uma vez
    public void comerPeca(List<List<Integer>> coordenadas) {
        for (List<Integer> ponto : coordenadas) {
            matriz[ponto.get(0)][ponto.get(1)] = Peca.VAZIA;
        }
    }


    public void verificarPromocao(int linha, int coluna) {
        char peca = matriz[linha][coluna];
        int ultimaLinha = TAMANHO - 1;

        if (peca == Peca.PRETA && linha == ultimaLinha) {
            matriz[linha][coluna] = Peca.DAMA_PRETA;
        } else if (peca == Peca.BRANCA && linha == 0) {
            matriz[linha][coluna] = Peca.DAMA_BRANCA;
        }
    }


    public boolean dentroDoTabuleiro(int linha, int coluna) {
        return linha >= 0 &&
                linha < Tabuleiro.getDimensoes() &&
                coluna >= 0 &&
                coluna < Tabuleiro.getDimensoes();
    }

    public boolean devePromover(int linhaDestino, char peca) {

        // Branca promove na linha 0
        if (peca == Peca.BRANCA && linhaDestino == 0)
            return true;

        // Preta promove na última linha
        if (peca == Peca.PRETA &&
                linhaDestino == Tabuleiro.getDimensoes() - 1)
            return true;

        return false;
    }

    public void promoverTemporariamente(int linha, int coluna) {

        char pecaAtual = getElemento(linha, coluna);

        if (pecaAtual == Peca.BRANCA)
            setElemento(linha, coluna, Peca.DAMA_BRANCA);

        else if (pecaAtual == Peca.PRETA)
            setElemento(linha, coluna, Peca.DAMA_PRETA);
    }

    public void reverterPromocao(int linha, int coluna) {

        char pecaAtual = getElemento(linha, coluna);

        if (pecaAtual == Peca.DAMA_BRANCA)
            setElemento(linha, coluna, Peca.BRANCA);

        else if (pecaAtual == Peca.DAMA_PRETA)
            setElemento(linha, coluna, Peca.PRETA);
    }

    public List<MovimentoCaptura> obterCapturasObrigatorias(Jogador jogador) {
        GeradorCapturas gerador = new GeradorCapturas(this);
        return gerador.encontrarMelhoresCapturas(jogador);
    }

}
