package main.ai;

import main.ai.avaliacoes.Avaliador;
import main.entidades.Jogador;
import main.entidades.Tabuleiro;
import java.util.List;

public class Tree {
    private final int alturaMaxima;
    private final Jogador jogadorIA;
    private final Avaliador avaliador;

    public Tree(int alturaMaxima, Jogador jogadorIA, Avaliador avaliador) {
        this.alturaMaxima = alturaMaxima;
        this.jogadorIA = jogadorIA;
        this.avaliador = avaliador;
    }

    public Node decidirMelhorJogada(Tabuleiro tabuleiroAtual) {
        // 1. Marca o tempo de início
        long tempoInicio = System.nanoTime();

        char[] estadoVetor = tabuleiroAtual.getVetorCasas();
        List<Node> filhosIniciais = Simulador.gerarEstadosFilhos(estadoVetor, jogadorIA, true);

        if (filhosIniciais.isEmpty()) {
            return null;
        }

        Node raiz = new Node(estadoVetor);

        for (Node filho : filhosIniciais) {
            raiz.addFilho(filho);
            alfabeta(filho, alturaMaxima - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, filho.isTurno());
        }

        Node melhor = selecionarMelhorResposta(raiz);

        // 2. Marca o tempo de fim
        long tempoFim = System.nanoTime();

        // 3. Calcula a diferença e converte para milissegundos
        double tempoExecucaoMs = (tempoFim - tempoInicio) / 1_000_000.0;

        // 4. Exibe o log no console
        System.out.println("--------------------------------------------------");
        System.out.printf("IA FINALIZOU A BUSCA EM: %.2f ms\n", tempoExecucaoMs);
        System.out.println("Profundidade máxima: " + alturaMaxima);
        System.out.println("Melhor jogada: " + melhor);
        System.out.println("--------------------------------------------------");

        return melhor;
    }

    private int alfabeta(Node no, int profundidade, int alpha, int beta, boolean isMaximizing) {
        // Caso Base: Heurística na folha (usando char[])
        if (profundidade == 0) { //chegou na altura maxima definida
            int score = avaliador.avaliar(no.getEstado(), jogadorIA);
            no.setMinMax(score);
            return score;
        }

        Jogador atual = isMaximizing ? jogadorIA : jogadorIA.proximo();

        // Gera sucessores a partir do vetor do nó atual
        List<Node> sucessores = Simulador.gerarEstadosFilhos(no.getEstado(), atual, isMaximizing);

        if (sucessores.isEmpty()) { //nao encontrou mais jogadas possiveis
            // Valor heurístico para estados terminais (Vitória/Derrota)
            return isMaximizing ? -10000 : 10000;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Node filho : sucessores) {
                no.addFilho(filho);
                int eval = alfabeta(filho, profundidade - 1, alpha, beta, filho.isTurno());
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Poda Alfa
            }
            no.setMinMax(maxEval);
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Node filho : sucessores) {
                no.addFilho(filho);
                int eval = alfabeta(filho, profundidade - 1, alpha, beta, filho.isTurno());
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Poda Beta
            }
            no.setMinMax(minEval);
            return minEval;
        }
    }

    private Node selecionarMelhorResposta(Node raiz) {
        if (raiz.getFilhos().isEmpty()) return null;

        Node melhor = null;
        int maiorValor = Integer.MIN_VALUE;

        for (Node n : raiz.getFilhos()) {
            if (n.getMinMax() > maiorValor) {
                maiorValor = n.getMinMax();
                melhor = n;
            }
        }
        return melhor;
    }
}