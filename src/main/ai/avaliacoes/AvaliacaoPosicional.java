package main.ai.avaliacoes;

import main.entidades.Jogador;
import main.entidades.Peca;

public class AvaliacaoPosicional implements Avaliador {

    // Mapa de calor achatado para o vetor de 18 posições (A-R)
    // Reflete os valores da matriz 6x6 original
    private final int[] PESOS_POSICAO_VETOR = {
            2, 2, 2, // Row 0 (Indices 0, 1, 2)
            2, 4, 4, // Row 1 (Indices 3, 4, 5)
            4, 5, 2, // Row 2 (Indices 6, 7, 8)
            2, 5, 4, // Row 3 (Indices 9, 10, 11)
            4, 4, 2, // Row 4 (Indices 12, 13, 14)
            2, 2, 2  // Row 5 (Indices 15, 16, 17)
    };

    @Override
    public int avaliar(char[] estadoVetor, Jogador jogadorIA) {
        int scoreIA = 0;
        int scoreHumano = 0;

        for (int i = 0; i < 18; i++) {
            char p = estadoVetor[i];
            if (p == Peca.VAZIA) continue;

            // 1. Valor base (Material)
            int valorBase = Peca.isDama(p) ? 50 : 10;

            // 2. Bônus de posição (Vetor de pesos)
            int bonusPosicao = PESOS_POSICAO_VETOR[i];

            // 3. Bônus de avanço (incentiva virar dama)
            // No vetor de 18, a linha pode ser obtida por (i / 3)
            int linha = i / 3;
            int bonusAvanco = calcularBonusAvanco(p, linha);

            int totalPeca = valorBase + bonusPosicao + bonusAvanco;

            if (isPecaDaIA(p, jogadorIA)) scoreIA += totalPeca;
            else scoreHumano += totalPeca;
        }
        return scoreIA - scoreHumano;
    }

    private int calcularBonusAvanco(char p, int linha) {
        if (Peca.isDama(p)) return 0;

        // Brancas (IA ou Humano) querem chegar na linha 0
        if (p == Peca.BRANCA) return (5 - linha) * 2;

        // Pretas (IA ou Humano) querem chegar na linha 5
        if (p == Peca.PRETA) return linha * 2;

        return 0;
    }

    private boolean isPecaDaIA(char p, Jogador ia) {
        return (ia == Jogador.BRANCAS && (p == Peca.BRANCA || p == Peca.DAMA_BRANCA)) ||
                (ia == Jogador.PRETAS && (p == Peca.PRETA || p == Peca.DAMA_PRETA));
    }
}