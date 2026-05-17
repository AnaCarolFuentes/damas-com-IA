package main.ai;

import main.logicGame.Tradutor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {

    private int origemIndice;
    private int destinoIndice;
    private char[] estado; // Vetor compacto de 18 posições
    private int minMax;     // Valor da avaliação heurística (MinMax)
    private List<Node> filhos;
    private boolean captura;
    private List<Integer> pecasCapturadasIndices;
    private List<Integer> caminhoIndices;

    // --- NOVO CAMPO ---
    // true se for o turno da IA (Maximizing), false se for o turno do Humano (Minimizing)
    private boolean turno;

    /**
     * Construtor para o estado inicial ou nós de busca.
     */
    public Node(char[] estado) {
        this.origemIndice = -1;
        this.destinoIndice = -1;
        this.estado = estado.clone();
        this.filhos = new ArrayList<>();
        this.minMax = 0;
        this.turno = true; // Valor padrão inicial
        this.captura = false;
        this.pecasCapturadasIndices = new ArrayList<>();
        this.caminhoIndices = new ArrayList<>();
    }

    /**
     * Construtor para movimentos específicos.
     */
    public Node(int origemIndice, int destinoIndice, char[] estado) {
        this.origemIndice = origemIndice;
        this.destinoIndice = destinoIndice;
        this.estado = (estado != null) ? estado.clone() : null;
        this.filhos = new ArrayList<>();
        this.minMax = 0;
        this.captura = false;
        this.pecasCapturadasIndices = new ArrayList<>();
        this.caminhoIndices = new ArrayList<>();
    }

    public Node(int origemIndice, int destinoIndice, char[] estado, boolean captura,
                List<Integer> pecasCapturadasIndices, List<Integer> caminhoIndices) {
        this(origemIndice, destinoIndice, estado);
        this.captura = captura;
        this.pecasCapturadasIndices = (pecasCapturadasIndices == null)
                ? new ArrayList<>()
                : new ArrayList<>(pecasCapturadasIndices);
        this.caminhoIndices = (caminhoIndices == null)
                ? new ArrayList<>()
                : new ArrayList<>(caminhoIndices);
    }

    // --- MÉTODOS DE TURNO ---

    public boolean isTurno() {
        return turno;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }


    public int getOrigemLinha() {
        return (origemIndice != -1) ? Tradutor.converteParaCoordenada(origemIndice)[0] : -1;
    }

    public int getOrigemColuna() {
        return (origemIndice != -1) ? Tradutor.converteParaCoordenada(origemIndice)[1] : -1;
    }

    public int getDestinoLinha() {
        return (destinoIndice != -1) ? Tradutor.converteParaCoordenada(destinoIndice)[0] : -1;
    }

    public int getDestinoColuna() {
        return (destinoIndice != -1) ? Tradutor.converteParaCoordenada(destinoIndice)[1] : -1;
    }

    public boolean isCaptura() {
        return captura;
    }

    public List<int[]> getPecasCapturadas() {
        return pecasCapturadasIndices.stream()
                .map(Tradutor::converteParaCoordenada)
                .collect(Collectors.toList());
    }

    public List<int[]> getCaminho() {
        return caminhoIndices.stream()
                .map(Tradutor::converteParaCoordenada)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS DE LÓGICA ---

    public char[] getEstado() {
        return estado;
    }

    public char[] getMatriz() {
        return estado;
    }

    public int getMinMax() {
        return minMax;
    }

    public void setMinMax(int minMax) {
        this.minMax = minMax;
    }

    public List<Node> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<Node> filhos) {
        this.filhos = (filhos == null) ? new ArrayList<>() : filhos;
    }

    public void addFilho(Node filho) {
        this.filhos.add(filho);
    }

    public void clear() {
        if (filhos != null) filhos.clear();
        estado = null;
    }

    public int getOrigemIndice() {
        return origemIndice;
    }

    public int getDestinoIndice() {
        return destinoIndice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return origemIndice == node.origemIndice && destinoIndice == node.destinoIndice;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(origemIndice);
        result = 31 * result + Integer.hashCode(destinoIndice);
        return result;
    }

    @Override
    public String toString() {
        if (origemIndice != -1) {
            int[] ori = Tradutor.converteParaCoordenada(origemIndice);
            int[] des = Tradutor.converteParaCoordenada(destinoIndice);
            return "(" + ori[0] + "," + ori[1] + ") -> (" + des[0] + "," + des[1] + ") | Score: " + minMax + " | Turno IA: " + turno;
        }
        return "Root Node | Score: " + minMax + " | Turno IA: " + turno;
    }
}
