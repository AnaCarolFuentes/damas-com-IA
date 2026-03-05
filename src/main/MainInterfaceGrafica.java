package main;

import main.entidades.Jogador;
import main.entidades.MovimentoCaptura;
import main.entidades.Peca;
import main.entidades.Tabuleiro;
import main.logicGame.*;
import main.ui.CasaBotao;
import main.ui.PintarTabuleiro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class MainInterfaceGrafica extends JFrame {

    private final int TAMANHO = 6;
    private final CasaBotao[][] tabuleiroInterface = new CasaBotao[TAMANHO][TAMANHO];
    private final Jogo controller;
    private final PintarTabuleiro paint;

    private int linhaOrigem = -1;
    private int colOrigem = -1;

    public MainInterfaceGrafica() {

        this.controller = new Jogo();
        this.paint = new PintarTabuleiro(controller.getTabuleiro(), tabuleiroInterface);

        configurarJanela();
        inicializarComponentes();
        sincronizarInterface();

        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Damas 6x6 - IA Academy");
        setSize(700, 700);
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

                // Adiciona o evento de clique
                tabuleiroInterface[i][j].addActionListener(e -> tratarClique(linha, coluna));
                add(tabuleiroInterface[i][j]);
            }
        }
        paint.resetarCoresPadrao();
    }

    private void tratarClique(int linha, int col) {
        // 1. Seleção da peça de origem
        if (linhaOrigem == -1) {
            preSelecionarPeca(linha, col);
        }
        // 2. Tentativa de movimento para o destino
        else {
            executarMovimento(linha, col);
        }
    }

    private void preSelecionarPeca(int linha, int col) {
        char pecaClicada = controller.getTabuleiro().getElemento(linha, col);
        Jogador atual = controller.getJogadorAtual();

        if (Peca.vezDe(atual, pecaClicada)) {
            // Busca as capturas obrigatórias UMA VEZ
            List<MovimentoCaptura> obrigatorias = controller.getTabuleiro().obterCapturasObrigatorias(atual);

            if (!obrigatorias.isEmpty()) {
                boolean podeCapturar = obrigatorias.stream()
                        .anyMatch(m -> m.getOrigemLinha() == linha && m.getOrigemColuna() == col);

                if (!podeCapturar) {
                    JOptionPane.showMessageDialog(this, "Atenção: Você deve realizar a captura máxima!");
                    return;
                }
            }

            linhaOrigem = linha;
            colOrigem = col;
            paint.resetarCoresPadrao();
            tabuleiroInterface[linha][col].setBackground(Color.YELLOW);

            // PASSE A LISTA AQUI:
            paint.destacarMovimentosPossiveis(linha, col, obrigatorias);
        }
    }

    private void executarMovimento(int linhaDestino, int colDestino) {
        // Se clicar na mesma peça, cancela a seleção
        if (linhaOrigem == linhaDestino && colOrigem == colDestino) {
            cancelarSelecao();
            return;
        }

        // Tenta realizar a jogada através do Controller
        boolean sucesso = controller.tentarJogada(linhaOrigem, colOrigem, linhaDestino, colDestino);

        if (sucesso) {
            sincronizarInterface();
            verificarEstadoJogo();

            // Se o próximo turno for das PRETAS (IA), você pode chamar o método aqui
            /* if (controller.getJogadorAtual() == Jogador.PRETAS) {
                 executarJogadaIA();
            } */
        } else {
            JOptionPane.showMessageDialog(this, "Movimento inválido ou não permitido pelas regras.");
        }

        cancelarSelecao();
    }

    private void cancelarSelecao() {
        linhaOrigem = -1;
        colOrigem = -1;
        paint.resetarCoresPadrao();
    }

    public void sincronizarInterface() {
        Tabuleiro tab = controller.getTabuleiro();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j].setTipoPeca(tab.getElemento(i, j));
            }
        }
    }

    // Implementar a lógica de fim de jogo futuramente
    private void verificarEstadoJogo() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainInterfaceGrafica::new);
    }
}