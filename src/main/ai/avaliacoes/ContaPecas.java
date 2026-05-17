package main.ai.avaliacoes;

import main.entidades.Jogador;
import main.entidades.Peca;

public class ContaPecas implements Avaliador {

    @Override
    public int avaliar(char[] estadoVetor, Jogador jogadorIA) {
        int pontuacaoIA = 0;
        int pontuacaoHumano = 0;

        // apenas as 18 casas válidas do vetor
        for (int i = 0; i < 18; i++) {
            char p = estadoVetor[i];

            if (p == Peca.VAZIA) continue;

            // Damas valem 3x mais (30 vs 10)
            int peso = Peca.isDama(p) ? 30 : 10;

            if (isPecaDoJogador(p, jogadorIA)) {
                pontuacaoIA += peso;
            } else {
                pontuacaoHumano += peso;
            }
        }

        // Retorno positivo se a IA está ganhando, negativo se está perdendo
        return pontuacaoIA - pontuacaoHumano;
    }

    private boolean isPecaDoJogador(char peca, Jogador jogador) {
        if (jogador == Jogador.BRANCAS) {
            return peca == Peca.BRANCA || peca == Peca.DAMA_BRANCA;
        } else {
            return peca == Peca.PRETA || peca == Peca.DAMA_PRETA;
        }
    }
}