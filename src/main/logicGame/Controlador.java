package main.logicGame;

import main.ai.Node;
import main.ai.Tree;
import main.ai.avaliacoes.AvaliacaoOtimizada;
import main.ai.avaliacoes.AvaliacaoPosicional;
import main.entidades.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Controlador {

    private Tabuleiro tabuleiro;
    private Jogador jogadorAtual;
    private GeradorCapturas geradorCapturas;
    // Configurações da IA
    private final Jogador jogadorIA;
    private final Jogador jogadorHumano;
    private final int nivelIA; // Profundidade da Tree
    private boolean ultimaJogadaCapturou;
    private boolean ultimaJogadaPromoveu;

    public Controlador(Jogador jogador, int profundidade) {
        this.tabuleiro = new Tabuleiro();
        this.jogadorHumano = jogador;
        this.geradorCapturas = new GeradorCapturas(tabuleiro);
        this.nivelIA = profundidade;
        this.jogadorAtual = Jogador.BRANCAS;
        this.jogadorIA = (jogadorHumano == Jogador.BRANCAS) ? Jogador.PRETAS : Jogador.BRANCAS;;
    }

    public boolean tentarJogada(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        limparEventosUltimaJogada();
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

                char pecaQueMoveu = tabuleiro.getElemento(linhaOrigem, colunaOrigem);
                ultimaJogadaCapturou = true;
                ultimaJogadaPromoveu = tabuleiro.devePromover(linhaDestino, pecaQueMoveu);
                tabuleiro.setElemento(linhaOrigem, colunaOrigem, Peca.VAZIA);
                tabuleiro.setElemento(linhaDestino, colunaDestino, pecaQueMoveu);

                for (int[] pos : movimentoEscolhido.getPecasCapturadas()) {
                    tabuleiro.setElemento(pos[0], pos[1], Peca.VAZIA);
                }

                tabuleiro.verificarPromocao(linhaDestino, colunaDestino);

                proximoTurno();
                return true;
            } else {
                return false;
            }
        }

        if (tabuleiro.movimentoPossivel(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
            char pecaQueMoveu = tabuleiro.getElemento(linhaOrigem, colunaOrigem);
            if (tabuleiro.moverPecaLogica(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
                ultimaJogadaPromoveu = tabuleiro.devePromover(linhaDestino, pecaQueMoveu);
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


    public void executarJogadaIA() {

        if(!isVezIA()) return;
        limparEventosUltimaJogada();
        System.out.println("DEBUG: IA tentando jogar. Turno de: " + jogadorAtual);
        if (jogadorAtual == jogadorIA) {
            int pecasAntes = contarPecas(tabuleiro.getVetorCasas());
            int damasAntes = contarDamas(tabuleiro.getVetorCasas());

            Tree engine = new Tree(nivelIA, jogadorIA, new AvaliacaoOtimizada()); // Posso escolher qual heuristica de ganhador eu posso aplicar

            Node melhorMovimento = engine.decidirMelhorJogada(tabuleiro);

            if (melhorMovimento == null) {
                System.out.println("DEBUG: A Tree retornou NULL! Investigar Simulador.");
                return;
            }
            tabuleiro.importarVetor(melhorMovimento.getEstado());
            ultimaJogadaCapturou = contarPecas(tabuleiro.getVetorCasas()) < pecasAntes;
            ultimaJogadaPromoveu = contarDamas(tabuleiro.getVetorCasas()) > damasAntes;
            proximoTurno();
            //verificarEstadoJogo();
        }
    }

    public void verificarEstadoJogo() {
        int brancasNormais = 0, brancasDamas = 0;
        int pretasNormais = 0, pretasDamas = 0;

        for (int i = 0; i < Tabuleiro.getDimensoes(); i++) {
            for (int j = 0; j < Tabuleiro.getDimensoes(); j++) {
                char p = tabuleiro.getElemento(i, j);
                if (p == Peca.BRANCA) brancasNormais++;
                else if (p == Peca.DAMA_BRANCA) brancasDamas++;
                else if (p == Peca.PRETA) pretasNormais++;
                else if (p == Peca.DAMA_PRETA) pretasDamas++;
            }
        }

        int totalBrancas = brancasNormais + brancasDamas;
        int totalPretas = pretasNormais + pretasDamas;

        // REGRA 1: Extermínio
        if (totalBrancas == 0) {
            finalizarPartida("Vitória das Pretas por extermínio!");
            return;
        }
        if (totalPretas == 0) {
            finalizarPartida("Vitória das Brancas por extermínio!");
            return;
        }

        // REGRA 2: Impossibilidade de efetuar jogadas (Travamento)
        boolean temCapturas = !tabuleiro.obterCapturasObrigatorias(jogadorAtual).isEmpty();
        boolean temMovimentos = !tabuleiro.obterMovimentosSimples(jogadorAtual).isEmpty();

        if (!temCapturas && !temMovimentos) {
            String vencedor = (jogadorAtual == Jogador.BRANCAS) ? "Pretas" : "Brancas";
            finalizarPartida("Vitória das " + vencedor + " por travamento do oponente!");
            return;
        }

        // REGRA 3: Somente duas damas e sem captura imediata
        if (totalBrancas == 1 && brancasDamas == 1 && totalPretas == 1 && pretasDamas == 1) {
            if (!temCapturas) {
                finalizarPartida("Empate: Apenas duas damas em jogo e sem captura imediata.");
                return;
            }
        }
    }

    private void finalizarPartida(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    public Jogador getJogadorIA() {
        return jogadorIA;
    }

    public Jogador getJogadorHumano() {
        return jogadorHumano;
    }

    public boolean isVezIA() {
        return jogadorAtual == jogadorIA;
    }

    public boolean ultimaJogadaCapturou() {
        return ultimaJogadaCapturou;
    }

    public boolean ultimaJogadaPromoveu() {
        return ultimaJogadaPromoveu;
    }

    private void limparEventosUltimaJogada() {
        ultimaJogadaCapturou = false;
        ultimaJogadaPromoveu = false;
    }

    private int contarPecas(char[] estado) {
        int total = 0;
        for (char p : estado) {
            if (p != Peca.VAZIA) total++;
        }
        return total;
    }

    private int contarDamas(char[] estado) {
        int total = 0;
        for (char p : estado) {
            if (Peca.isDama(p)) total++;
        }
        return total;
    }

    public List<int[]> obterCasasComMovimentosPossiveis() {
        List<int[]> casas = new ArrayList<>();

        if (jogadorAtual != jogadorHumano) return casas;

        List<Node> movimentosLegais = obterTodosOsMovimentosLegais(jogadorHumano);

        for (Node n : movimentosLegais) {
            int[] coord = Tradutor.converteParaCoordenada(n.getOrigemIndice());
            casas.add(coord);
        }
        return casas;
    }

    public List<Node> obterTodosOsMovimentosLegais(Jogador jogador) {
        List<Node> movimentos = new ArrayList<>();

        // 1. Tenta capturas (Obrigatório)
        List<MovimentoCaptura> capturas = tabuleiro.obterCapturasObrigatorias(jogador);
        if (!capturas.isEmpty()) {
            // Converte MovimentoCaptura para Node para a IA entender
            return converterCapturasParaNodes(capturas);
        }

        // 2. Se não houver, tenta movimentos simples
        return tabuleiro.obterMovimentosSimples(jogador);
    }

    private List<Node> converterCapturasParaNodes(List<MovimentoCaptura> capturas) {
        List<Node> nodes = new ArrayList<>();
        char[] estadoAtual = tabuleiro.getVetorCasas();

        for (MovimentoCaptura c : capturas) {
            int oI = Tradutor.converteParaIndice(c.getOrigemLinha(), c.getOrigemColuna());
            int dI = Tradutor.converteParaIndice(c.getDestinoLinha(), c.getDestinoColuna());

            nodes.add(new Node(oI, dI, estadoAtual, true, c.getPecasCapturadasIndices(), c.getCaminhoIndices()));
        }
        return nodes;
    }


}
