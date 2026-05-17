package main.ui;

import main.entidades.Jogador;
import main.entidades.MovimentoCaptura;
import main.entidades.Peca;
import main.entidades.Tabuleiro;
import main.logicGame.*;
import main.util.GerenciadorAudio;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class InterfaceGrafica extends JFrame {

    private final int TAMANHO = 6;
    private final CasaBotao[][] tabuleiroInterface = new CasaBotao[TAMANHO][TAMANHO];
    private final Controlador controller;
    private final PintarTabuleiro paint;
    private final Color COR_DICA = new Color(173, 216, 230);

    private int linhaOrigem = -1;
    private int colOrigem = -1;

    public InterfaceGrafica(Jogador jogadorHumano, int profundidade) {
        // O controlador já inicializa o tabuleiro compacto internamente
        this.controller = new Controlador(jogadorHumano, profundidade);
        this.paint = new PintarTabuleiro(controller.getTabuleiro(), tabuleiroInterface);

        configurarJanela();
        inicializarComponentes();
        sincronizarInterface();

        // Se o humano escolheu ser as Pretas, as Brancas (IA) começam
        if (controller.getJogadorIA() == Jogador.BRANCAS) {
            processarJogadaIA();
        }

        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Damas 6x6 - Otimização Vetorial (IFSULDEMINAS)");
        setSize(700, 700);
        setResizable(false);
        setLayout(new GridLayout(TAMANHO, TAMANHO));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j] = new CasaBotao();

                final int linha = i;
                final int coluna = j;

                // O Tradutor ajuda a ignorar cliques em casas brancas (inválidas)
                tabuleiroInterface[i][j].addActionListener(e -> tratarClique(linha, coluna));
                add(tabuleiroInterface[i][j]);
            }
        }
        paint.resetarCoresPadrao();
    }

    private void tratarClique(int linha, int col) {

        if (controller.isVezIA()) return;

        if (linhaOrigem == -1) {
            if (tabuleiroInterface[linha][col].getBackground().equals(COR_DICA)) {
                preSelecionarPeca(linha, col);
            } else {
                System.out.println("Clique ignorado: Peça não possui movimentos válidos ou obrigatórios.");
            }
        }
        else {
            if (tabuleiroInterface[linha][col].getBackground().equals(COR_DICA)) {
                preSelecionarPeca(linha, col);
            } else {
                executarMovimento(linha, col);
            }
        }
    }

    private void preSelecionarPeca(int linha, int col) {
        char pecaClicada = controller.getTabuleiro().getElemento(linha, col);
        GerenciadorAudio.tocarSom("clique.wav");
        Jogador atual = controller.getJogadorAtual();

        if (Peca.vezDe(atual, pecaClicada)) {
            List<MovimentoCaptura> obrigatorias = controller.getTabuleiro().obterCapturasObrigatorias(atual);

            if (!obrigatorias.isEmpty()) {
                boolean podeCapturar = obrigatorias.stream()
                        .anyMatch(m -> m.getOrigemLinha() == linha && m.getOrigemColuna() == col);

                if (!podeCapturar) {
                    JOptionPane.showMessageDialog(this, "Atenção: Você deve realizar a captura obrigatória!");
                    return;
                }
            }

            linhaOrigem = linha;
            colOrigem = col;

            paint.resetarCoresPadrao();
            tabuleiroInterface[linha][col].setBackground(Color.YELLOW);
            paint.destacarMovimentosPossiveis(linha, col, obrigatorias);
        }
    }

    private void executarMovimento(int linhaDestino, int colDestino) {

        if (linhaOrigem == linhaDestino && colOrigem == colDestino) {
            cancelarSelecao();
            return;
        }

        boolean sucesso = controller.tentarJogada(linhaOrigem, colOrigem, linhaDestino, colDestino);

        if (sucesso) {
            tocarSonsUltimaJogada();
            cancelarSelecao();
            sincronizarInterface();

            if (controller.isVezIA()) {
                processarJogadaIA();
            }
            if (!verificarFimDeJogo() && controller.isVezIA()) {
                processarJogadaIA();
            }
        } else {
            char pecaDestino = controller.getTabuleiro().getElemento(linhaDestino, colDestino);
            if (Peca.vezDe(controller.getJogadorAtual(), pecaDestino)) {
                preSelecionarPeca(linhaDestino, colDestino);
            } else {
                JOptionPane.showMessageDialog(this, "Movimento inválido.");
                cancelarSelecao();
            }
        }
    }

    private void processarJogadaIA() {
        Timer timer = new Timer(600, e -> {
            controller.executarJogadaIA();
            tocarSonsUltimaJogada();
            sincronizarInterface();
            verificarFimDeJogo();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean verificarFimDeJogo() {
        controller.verificarEstadoJogo();
        return false;
    }

    private void cancelarSelecao() {
        linhaOrigem = -1;
        colOrigem = -1;
        paint.resetarCoresPadrao();
        atualizarDicasVisuais();
    }

    public void sincronizarInterface() {
        Tabuleiro tab = controller.getTabuleiro();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j].setTipoPeca(tab.getElemento(i, j));
            }
        }
        atualizarDicasVisuais();
    }

    private void atualizarDicasVisuais() {

        paint.resetarCoresPadrao();

        if (linhaOrigem != -1) {
            tabuleiroInterface[linhaOrigem][colOrigem].setBackground(Color.YELLOW);
            return;
        }

        List<int[]> dicas = controller.obterCasasComMovimentosPossiveis();


        paint.aplicarDicas(dicas);
    }

    private void tocarSonsUltimaJogada() {
        if (controller.ultimaJogadaCapturou()) {
            GerenciadorAudio.tocarSom("captura.wav");
        }
        if (controller.ultimaJogadaPromoveu()) {
            GerenciadorAudio.tocarSom("dama.wav");
        }
    }
}
