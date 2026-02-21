/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
    private final int TAMANHO = 6;

    public Tabuleiro() {
        this.matriz = new char[TAMANHO][TAMANHO];
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

    public void setMatriz(char[][] matriz) {
        this.matriz = matriz;
    }

    public boolean movimentoPossivel(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        char origem = matriz[linhaOrigem][colunaOrigem];
        char destino = matriz[linhaDestino][colunaDestino];

        if(destino != Peca.VAZIA) return false;

        if(Peca.isDama(origem)) return validarMovimentoDama(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);

        else return validarMovimentoPeca(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
    }

    public boolean validarMovimentoPeca(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        int deltaLinha = linhaDestino - linhaOrigem;
        int deltaColuna = colunaDestino - colunaOrigem;
        int versorColuna = Integer.signum(deltaColuna);
        char pecaAtual = matriz[linhaOrigem][colunaOrigem];
        boolean isBranca = Peca.isBranca(pecaAtual);

        if(abs(deltaColuna) > 2 || deltaLinha > 2 || deltaLinha < -2) return false;

        if(abs(deltaColuna) == 1) {
            if(deltaLinha == 1 && !isBranca) return true;

            else return deltaLinha == -1 && isBranca;
        }

        if(abs(deltaColuna) == 2) {
            if(deltaLinha == -2 && isBranca)
                if(versorColuna == 1 )  return comerPeca(isBranca, linhaOrigem - 1, colunaOrigem + 1);
                else return comerPeca(isBranca,linhaOrigem - 1, colunaOrigem - 1);

            else if(deltaLinha == 2 && !isBranca)
                if(versorColuna == 1 )  return comerPeca(isBranca, linhaOrigem + 1, colunaOrigem + 1);
                else return comerPeca(isBranca,linhaOrigem + 1, colunaOrigem - 1);
        }

        return false;
    }

    public boolean validarMovimentoDama(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        int deltaLinha = linhaDestino - linhaOrigem;
        int deltaColuna = colunaDestino - colunaOrigem;
        int versorLinha = Integer.signum(deltaLinha);
        int versorColuna = Integer.signum(deltaColuna);
        int posLinha = linhaOrigem + versorLinha;
        int posColuna = colunaOrigem + versorColuna;

        if(abs(deltaLinha) != abs(deltaColuna)) return false;

        while (posLinha != linhaDestino && posColuna != colunaDestino) {
            if(matriz[posLinha][posColuna] != Peca.VAZIA) return false;

            posLinha += versorLinha;
            posColuna += versorColuna;
        }

        return true;
    }

    public boolean comerPeca (boolean pecaAtualIsBranca, int linha, int coluna) {
        char pecaAdjacente = matriz[linha][coluna];
        if((!Peca.isBranca(pecaAdjacente) && pecaAtualIsBranca)|| (Peca.isBranca(pecaAdjacente) && !pecaAtualIsBranca)){
            matriz[linha][coluna] = Peca.VAZIA;
            return true;
        }
        return false;

    }



}
