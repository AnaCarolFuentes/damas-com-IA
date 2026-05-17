package main.ai.avaliacoes;

import main.entidades.*;
import main.logicGame.Tradutor;

public class AvaliacaoOtimizada implements Avaliador {

    private static final int PESO_PECA = 100;
    private static final int PESO_DAMA = 300;
    private static final int PESO_POSICAO = 5;

    // Mapeamento A-R do vetor de 18 posições para os pesos
    // Linha 0 (Indices 0,1,2), Linha 1 (3,4,5), etc.
    private static final int[] TABELA_PESOS_VETOR = {
            1, 1, 1, // Row 0 (A, B, C) - Bordas/Base
            1, 3, 3, // Row 1 (D, E, F)
            4, 5, 2, // Row 2 (G, H, I) - Centro Superior
            2, 5, 4, // Row 3 (J, K, L) - Centro Inferior
            3, 3, 1, // Row 4 (M, N, O)
            1, 1, 1  // Row 5 (P, Q, R) - Bordas/Base
    };

    @Override
    public int avaliar(char[] estadoVetor, Jogador jogadorIA) {
        int score = 0;

        // Loop rápido: apenas 18 iterações
        for (int i = 0; i < 18; i++) {
            char p = estadoVetor[i];

            if (p == Peca.VAZIA) continue;

            // Identifica se a peça pertence à IA
            boolean ehIA = Peca.vezDe(jogadorIA, p);

            int valor = Peca.isDama(p) ? PESO_DAMA : PESO_PECA;

            // Bônus Posicional usando o índice direto do vetor
            valor += TABELA_PESOS_VETOR[i] * PESO_POSICAO;

            // Bônus de Defesa da Base (Linha 0 para Brancas, Linha 5 para Pretas)
            // No vetor: 0-2 é Row 0, 15-17 é Row 5
            // Pra ela entender que é importante nao sair afobada para nao deixar o humano fazer dama
            if (!Peca.isDama(p)) {
                if (ehIA) {
                    if (jogadorIA == Jogador.BRANCAS && i >= 15) valor += 30; // Defesa Branca
                    if (jogadorIA == Jogador.PRETAS && i <= 2) valor += 30;   // Defesa Preta
                }
            }

            if (ehIA) score += valor;
            else score -= valor;
        }
        return score;
    }
}