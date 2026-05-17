package main.ai.avaliacoes;

import main.entidades.Jogador;

public interface Avaliador {
    /**
     * @return Pontuação do tabuleiro (Positivo para vantagem da IA, Negativo para vantagem do Humano)
     */
    int avaliar(char[] vetor, Jogador jogadorIA);
}
