package main.ai;

import main.entidades.*;
import main.logicGame.Tradutor;
import java.util.ArrayList;
import java.util.List;

public class Simulador {

    /**
     * Gera os estados sucessores a partir de um estado vetorial.
     */
    public static List<Node> gerarEstadosFilhos(char[] estadoVetor, Jogador atual, boolean isMaximizingIA) {
        List<Node> sucessores = new ArrayList<>();

        // tabuleiro virtual
        Tabuleiro tab = new Tabuleiro(estadoVetor, atual);

        // 1. Regra: Capturas são obrigatórias
        List<MovimentoCaptura> capturas = tab.obterCapturasObrigatorias(atual);

        if (!capturas.isEmpty()) {
            for (MovimentoCaptura mc : capturas) {
                // Simula o movimento e obtém o novo vetor de 18 posições
                char[] novoEstado = simularMovimento(estadoVetor, mc);

                // Cria o Node usando os índices (0-17) que o MovimentoCaptura agora possui
                Node filho = new Node(
                        mc.getOrigemIndice(),
                        mc.getDestinoIndice(),
                        novoEstado,
                        true,
                        mc.getPecasCapturadasIndices(),
                        mc.getCaminhoIndices()
                );

                // Checa se há captura múltipla pendente no destino
                GeradorCapturas gc = new GeradorCapturas(new Tabuleiro(novoEstado, atual));
                boolean podeContinuar = gc.podeContinuarCapturando(mc.getDestinoLinha(), mc.getDestinoColuna());

                // Define o turno do próximo nível da árvore
                filho.setTurno(podeContinuar ? isMaximizingIA : !isMaximizingIA);
                sucessores.add(filho);
            }
        } else {
            // 2. Se não há capturas, busca movimentos simples
            List<Node> simples = tab.obterMovimentosSimples(atual);
            for (Node n : simples) {
                // n já vem com o vetor de 18 posições do método obterMovimentosSimples
                n.setTurno(!isMaximizingIA);
                sucessores.add(n);
            }
        }
        return sucessores;
    }

    /**
     * Executa a lógica de movimentação no vetor compacto.
     */
    private static char[] simularMovimento(char[] estadoOriginal, MovimentoCaptura mc) {
        // tabuleiro temporário (apenas 18 posições)
        Tabuleiro tabVirtual = new Tabuleiro(estadoOriginal, Jogador.BRANCAS);

        // Executa a lógica de mover peça (que já limpa o caminho e promove)
        tabVirtual.moverPecaLogica(
                mc.getOrigemLinha(), mc.getOrigemColuna(),
                mc.getDestinoLinha(), mc.getDestinoColuna()
        );

        // Garante a limpeza das peças capturadas usando os índices
        for (Integer indice : mc.getPecasCapturadasIndices()) {
            int[] coord = Tradutor.converteParaCoordenada(indice);
            tabVirtual.setElemento(coord[0], coord[1], Peca.VAZIA);
        }

        return tabVirtual.getVetorCasas();
    }
}
