package main.logicGame;

public enum Jogador {
    BRANCAS,
    PRETAS;

    private int pontos;

    Jogador() {
        this.pontos = 0;
    }

    public Jogador proximo() {
        return (this == BRANCAS) ? PRETAS : BRANCAS;
    }

    public void incrementarPontuacao () {
        pontos++;
    }

    public int getPontuacao () {
        return pontos;
    }

}


