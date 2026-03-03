package main;

import main.logicGame.Jogador;
import main.logicGame.Peca;
import main.logicGame.Tabuleiro;
import main.ui.CasaBotao;
import main.ui.PintarTabuleiro;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Douglas
 */
public final class MainInterfaceGrafica extends JFrame {

    private final int TAMANHO = 6;
    private final CasaBotao[][] tabuleiroInterface = new CasaBotao[TAMANHO][TAMANHO];
    private final PintarTabuleiro paint;
    /*
        Vazio: 0
        Brancas: 1
        Pretas: 2
        Damas: 3 (branca) ou 4 (preta)

        REGRAS DO JOGO
            - DEFINIR QUEM UTILIZARÁ AS PEÇAS BRANCAS (COMEÇA O JOGO) CHECK
            - OBRIGATÓRIO COMER A PEÇA
            - NÃO É PERMITIDO COMER PRA TRÁS CHECK
            - UMA PEÇA PODE COMER MÚLTIPLAS PEÇAS, EM QUALQUER DIREÇÃO, DESDE QUE A PRIMEIRA SEJA PARA FRENTE
            - A DAMA PODE ANDAR INFINITAS CASAS, RESPEITANDO O LIMITE DO TABULEIRO Check (tratar quando tiver peças a serem comidas por ela)
            - A DAMA PODE COMER PRA TRÁS
            - A DAMA PODE COMER MÚLTIPLAS PEÇAS
            - A ÚLTIMA PEÇA A SER COMIDA PELA DAMA INDICA A POSIÇÃO QUE A DAMA DEVERÁ PARAR (POSIÇÃO SUBSEQUENTE NA DIREÇÃO DA COMIDA)
            - NA IMPOSSIBILIDADE DE EFETUAR JOGADAS, O JOGADOR TRAVADO PERDE O JOGO
    */

    private final Tabuleiro tabuleiroLogico;

    private int linhaOrigem = -1, colOrigem = -1;

    public MainInterfaceGrafica() {
        
        /*
            TABULEIRO DO JOGO
        */
        tabuleiroLogico = new Tabuleiro();

        paint = new PintarTabuleiro(tabuleiroLogico, tabuleiroInterface);

        setTitle("DISCIPLINA - IA - MINI JOGO DE DAMA");
        setSize(800, 800);
        setLayout(new GridLayout(TAMANHO, TAMANHO));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarComponentes();
        sincronizarInterface(); 

        setVisible(true);
    }

    private void inicializarComponentes() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j] = new CasaBotao();

                // Cores do tabuleiro
                if ((i + j) % 2 == 0) {
                    paint.setBackgroundBeige(i, j);
                } else {
                    paint.setBackgroundPink(i, j);
                }

                final int linha = i;
                final int coluna = j;
                tabuleiroInterface[i][j].addActionListener(e -> tratarClique(linha, coluna));
                add(tabuleiroInterface[i][j]);
            }
        }
    }

    private void tratarClique(int linha, int col) {

        char casaAtual = tabuleiroLogico.getMatriz()[linha][col];
        // Caso 1: Nenhuma peça selecionada ainda
        if (linhaOrigem == -1) {

            paint.resetarCoresPadrao();
            // Verifica se a casa clicada contém QUALQUER peça (1, 2, 3 ou 4)
            if (casaAtual != Peca.VAZIA && casaAtual != Peca.INVALIDA) {
                linhaOrigem = linha;
                colOrigem = col;
                tabuleiroInterface[linha][col].setBackground(Color.YELLOW); // Destaque do clique
                paint.destacarMovimentosPossiveis(linhaOrigem, colOrigem);
            }
        } 
        // Caso 2: Já existe uma peça selecionada, tentando mover
        else {
            
            // Se clicar na mesma peça, cancela a seleção ou se clicar em uma casa invalida
            if (linhaOrigem == linha && colOrigem == col || casaAtual == Peca.INVALIDA) {
                cancelarSelecao();
                return;
            }

            if(!tabuleiroLogico.movimentoPossivel(linhaOrigem, colOrigem, linha, col)) return;
            boolean sucesso = tabuleiroLogico.moverPecaLogica(linhaOrigem, colOrigem, linha, col);

            if (sucesso) {
                cancelarSelecao();
                sincronizarInterface();
                /*
                    IMPLEMENTAÇÃO DA JOGADA DA IA
                */
            }
            cancelarSelecao(); // Sempre limpa as cores após uma tentativa de movimento
        }
    }

    private void cancelarSelecao() {
        if (linhaOrigem != -1) {
            paint.resetarCoresPadrao();
        }
        linhaOrigem = -1;
        colOrigem = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainInterfaceGrafica::new);
    }
    
    /*
     * Atualiza a interface gráfica com base na matriz lógica do Tabuleiro. Este
     * método será chamado após cada jogada da IA.
     */
    public void sincronizarInterface() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                char peca = tabuleiroLogico.getMatriz()[i][j];
                tabuleiroInterface[i][j].setTipoPeca(peca);
            }
        }
    }

}
