package main.entidades;

import main.ai.Node;
import main.logicGame.Tradutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.lang.Math.abs;

/**
 * @author Douglas
 */
public class Tabuleiro implements Cloneable {

    private char[] casas; // Vetor de 18 posições (A-R)
    private static final int TAMANHO = 6;
    private Jogador jogadorAtual;

    public Tabuleiro() {
        this.casas = new char[18];
        this.jogadorAtual = Jogador.BRANCAS;
        inicializar();
    }

    // Adaptado para aceitar a matriz da UI e converter para vetor
    public Tabuleiro(char[][] matriz) {
        this(matriz, Jogador.BRANCAS);
    }

    public Tabuleiro(char[][] matriz, Jogador jogadorAtual) {
        this.casas = new char[18];
        importarMatriz(matriz);
        this.jogadorAtual = jogadorAtual;
    }

    public Tabuleiro(char[] vetor, Jogador jogadorAtual) {
        this.casas = vetor.clone();
        this.jogadorAtual = jogadorAtual;
    }

    private void inicializar() {
        Arrays.fill(casas, Peca.VAZIA);
        // Mapeamento A-C (Linha 0), D-F (Linha 1), etc.
        for (int i = 0; i < 6; i++) casas[i] = Peca.PRETA;   // Linhas 0 e 1
        for (int i = 12; i < 18; i++) casas[i] = Peca.BRANCA; // Linhas 4 e 5
    }

    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.casas = this.casas.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }


    public char[][] getMatriz() {
        return exportarMatriz();
    }

    public char[][] exportarMatriz() {
        char[][] matriz = new char[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if ((i + j) % 2 != 0) {
                    matriz[i][j] = casas[Tradutor.converteParaIndice(i, j)];
                } else {
                    matriz[i][j] = Peca.INVALIDA;
                }
            }
        }
        return matriz;
    }

    public void importarMatriz(char[][] matriz) {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if ((i + j) % 2 != 0) {
                    casas[Tradutor.converteParaIndice(i, j)] = matriz[i][j];
                }
            }
        }
    }
    public void importarVetor(char[] vetor) {
        if (vetor != null) {
            this.casas = vetor.clone();
        }
    }

    // Mantido por compatibilidade de assinatura
    private char[][] copiarMatriz(char[][] origem) {
        char[][] copia = new char[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            copia[i] = origem[i].clone();
        }
        return copia;
    }

    public static int getDimensoes()  { return TAMANHO; }

    public char getElemento(int linha, int coluna) {
        if (!dentroDoTabuleiro(linha, coluna) || (linha + coluna) % 2 == 0) return Peca.INVALIDA;
        return casas[Tradutor.converteParaIndice(linha, coluna)];
    }

    public void setElemento(int linha, int coluna, char elemento) {
        if (dentroDoTabuleiro(linha, coluna) && (linha + coluna) % 2 != 0) {
            casas[Tradutor.converteParaIndice(linha, coluna)] = elemento;
        }
    }


    public boolean movimentoPossivel(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        if (!dentroDoTabuleiro(linhaDestino, colunaDestino) || (linhaDestino + colunaDestino) % 2 == 0) return false;

        char origem = getElemento(linhaOrigem, colunaOrigem);
        char destino = getElemento(linhaDestino, colunaDestino);

        if (destino != Peca.VAZIA) return false;

        if (Peca.isDama(origem)) return realizarMovimentoDama(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
        else return realizarMovimentoPeca(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
    }

    public boolean realizarMovimentoPeca(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        int deltaLinha = linhaDestino - linhaOrigem;
        int deltaColuna = colunaDestino - colunaOrigem;
        char pecaAtual = getElemento(linhaOrigem, colunaOrigem);
        boolean isBranca = Peca.isBranca(pecaAtual);

        if (abs(deltaColuna) != abs(deltaLinha)) return false;

        if (abs(deltaColuna) == 1) {
            if ((isBranca && deltaLinha == -1) || (!isBranca && deltaLinha == 1)) {
                return getElemento(linhaDestino, colunaDestino) == Peca.VAZIA;
            }
        }

        if (abs(deltaColuna) == 2) {
            if ((isBranca && deltaLinha == -2) || (!isBranca && deltaLinha == 2)) {
                int lInimigo = linhaOrigem + (deltaLinha / 2);
                int cInimigo = colunaOrigem + (deltaColuna / 2);
                return isEnemy(isBranca, lInimigo, cInimigo) && getElemento(linhaDestino, colunaDestino) == Peca.VAZIA;
            }
        }
        return false;
    }

    public boolean realizarMovimentoDama(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        int dL = linhaDestino - linhaOrigem;
        int dC = colunaDestino - colunaOrigem;

        // 1. Validação de diagonal
        if (Math.abs(dL) != Math.abs(dC)) return false;

        int vL = Integer.signum(dL);
        int vC = Integer.signum(dC);
        int l = linhaOrigem + vL;
        int c = colunaOrigem + vC;

        int pecasInimigasEncontradas = 0;
        int linhaInimiga = -1;
        int colInimiga = -1;
        boolean isBranca = Peca.isBranca(getElemento(linhaOrigem, colunaOrigem));

        // 2. Percorre o caminho ATÉ o destino
        while (l != linhaDestino && c != colunaDestino) {
            char pecaAtual = getElemento(l, c);

            if (pecaAtual != Peca.VAZIA) {
                // Se encontrar peça própria ou uma segunda peça inimiga, movimento inválido
                if (!isEnemy(isBranca, l, c) || pecasInimigasEncontradas > 0) return false;

                pecasInimigasEncontradas++;
                linhaInimiga = l;
                colInimiga = c;
            }
            l += vL;
            c += vC;
        }

        // 3. LOGICA DE TRAVA:
        if (pecasInimigasEncontradas == 1) {
            // Se houve captura, o destino DEVE ser exatamente a casa seguinte à inimiga
            // Se a distância entre a inimiga e o destino for maior que 1, ela "voou" (inválido)
            return (linhaDestino == linhaInimiga + vL) && (colunaDestino == colInimiga + vC);
        }

        // Se não houve captura, é um movimento simples (livre para voar em casas vazias)
        return pecasInimigasEncontradas == 0;
    }

    public boolean isEnemy (boolean pecaAtualIsBranca, int linha, int coluna) {
        char pecaAlvo = getElemento(linha, coluna);
        if (pecaAlvo == Peca.VAZIA || pecaAlvo == Peca.INVALIDA) return false;
        return pecaAtualIsBranca != Peca.isBranca(pecaAlvo);
    }

    public boolean moverPecaLogica(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        if (getElemento(linhaDestino, colunaDestino) != Peca.VAZIA) return false;

        char pecaMovida = getElemento(linhaOrigem, colunaOrigem);
        int distLinha = abs(linhaDestino - linhaOrigem);

        if (distLinha >= 2) {
            int vL = Integer.signum(linhaDestino - linhaOrigem);
            int vC = Integer.signum(colunaDestino - colunaOrigem);
            int tempL = linhaOrigem + vL;
            int tempC = colunaOrigem + vC;

            while (tempL != linhaDestino && tempC != colunaDestino) {
                if (getElemento(tempL, tempC) != Peca.VAZIA) {
                    setElemento(tempL, tempC, Peca.VAZIA);
                }
                tempL += vL;
                tempC += vC;
            }
        }

        setElemento(linhaDestino, colunaDestino, pecaMovida);
        setElemento(linhaOrigem, colunaOrigem, Peca.VAZIA);
        verificarPromocao(linhaDestino, colunaDestino);
        return true;
    }

    public void comerPeca(int linha, int coluna) {
        setElemento(linha, coluna, Peca.VAZIA);
    }

    public void comerPeca(List<List<Integer>> coordenadas) {
        for (List<Integer> ponto : coordenadas) {
            setElemento(ponto.get(0), ponto.get(1), Peca.VAZIA);
        }
    }

    public void verificarPromocao(int linha, int coluna) {
        char peca = getElemento(linha, coluna);
        if (peca == Peca.PRETA && linha == 5) setElemento(linha, coluna, Peca.DAMA_PRETA);
        else if (peca == Peca.BRANCA && linha == 0) setElemento(linha, coluna, Peca.DAMA_BRANCA);
    }

    public boolean dentroDoTabuleiro(int linha, int coluna) {
        return linha >= 0 && linha < TAMANHO && coluna >= 0 && coluna < TAMANHO;
    }

    public boolean devePromover(int linhaDestino, char peca) {
        if (peca == Peca.BRANCA && linhaDestino == 0) return true;
        return peca == Peca.PRETA && linhaDestino == TAMANHO - 1;
    }

    public void promoverTemporariamente(int linha, int coluna) {
        char peca = getElemento(linha, coluna);
        if (peca == Peca.BRANCA) setElemento(linha, coluna, Peca.DAMA_BRANCA);
        else if (peca == Peca.PRETA) setElemento(linha, coluna, Peca.DAMA_PRETA);
    }

    public void reverterPromocao(int linha, int coluna) {
        char peca = getElemento(linha, coluna);
        if (peca == Peca.DAMA_BRANCA) setElemento(linha, coluna, Peca.BRANCA);
        else if (peca == Peca.DAMA_PRETA) setElemento(linha, coluna, Peca.PRETA);
    }

    public List<MovimentoCaptura> obterCapturasObrigatorias(Jogador jogador) {
        return new GeradorCapturas(this).encontrarMelhoresCapturas(jogador);
    }

    public List<Node> obterMovimentosSimples(Jogador jogador) {
        List<Node> movimentos = new ArrayList<>();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                char peca = getElemento(i, j);

                if (Peca.vezDe(jogador, peca)) {
                    for (int[] dir : Peca.obterDirecoesPermitidas(peca)) {
                        int destL = i + dir[0];
                        int destC = j + dir[1];

                        if (dentroDoTabuleiro(destL, destC) && movimentoPossivel(i, j, destL, destC)) {

                            // 1. Clonar e executar o movimento no tabuleiro temporário
                            Tabuleiro tabTemp = this.clone();
                            tabTemp.moverPecaLogica(i, j, destL, destC);

                            // 2. TRADUÇÃO: Converter (linha, coluna) para Índice (0-17)
                            int oI = Tradutor.converteParaIndice(i, j);
                            int dI = Tradutor.converteParaIndice(destL, destC);

                            Node novoNode = new Node(oI, dI, tabTemp.getVetorCasas());

                            movimentos.add(novoNode);
                        }
                    }
                }
            }
        }
        return movimentos;
    }

    public char[] getVetorCasas() { return casas; }
}