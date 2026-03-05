package main.logicGame;

import main.entidades.*;

import java.util.List;

public class Jogo {

    private Tabuleiro tabuleiro;
    private Jogador jogadorAtual;
    private GeradorCapturas geradorCapturas;

    public Jogo() {
        this.tabuleiro = new Tabuleiro();
        this.jogadorAtual = Jogador.BRANCAS;
        this.geradorCapturas = new GeradorCapturas(tabuleiro);
    }

    public boolean tentarJogada(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        List<MovimentoCaptura> melhoresCapturas = geradorCapturas.encontrarMelhoresCapturas(jogadorAtual);

        if (!melhoresCapturas.isEmpty()) {
            MovimentoCaptura movimentoEscolhido = null;
            for (MovimentoCaptura m : melhoresCapturas) {
                if (m.getOrigemLinha() == linhaOrigem && m.getOrigemColuna() == colunaOrigem &&
                        m.getDestinoLinha() == linhaDestino && m.getDestinoColuna() == colunaDestino) {
                    movimentoEscolhido = m;
                    break;
                }
            }

            if (movimentoEscolhido != null) {
                // --- AQUI ESTÁ A CORREÇÃO PARA PEÇAS SUMIREM ---
                // 1. Move a peça fisicamente para o destino final
                char pecaQueMoveu = tabuleiro.getElemento(linhaOrigem, colunaOrigem);
                tabuleiro.setElemento(linhaOrigem, colunaOrigem, Peca.VAZIA);
                tabuleiro.setElemento(linhaDestino, colunaDestino, pecaQueMoveu);

                // 2. Remove TODAS as peças inimigas que o Gerador capturou
                for (int[] pos : movimentoEscolhido.getPecasCapturadas()) {
                    tabuleiro.setElemento(pos[0], pos[1], Peca.VAZIA);
                }

                // 3. Verifica promoção no destino final
                tabuleiro.verificarPromocao(linhaDestino, colunaDestino);

                proximoTurno();
                return true;
            } else {
                return false;
            }
        }

        // Se não houver captura, segue o fluxo normal para movimento simples
        if (tabuleiro.movimentoPossivel(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
            if (tabuleiro.moverPecaLogica(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
                proximoTurno();
                return true;
            }
        }
        return false;
    }


    private void proximoTurno() {
        jogadorAtual = jogadorAtual.proximo();
    }

    public Tabuleiro getTabuleiro(){
        return tabuleiro;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }
}
