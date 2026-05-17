package main.entidades;

import main.logicGame.Tradutor;
import java.util.List;
import java.util.stream.Collectors;

public class MovimentoCaptura {


    private int origemIndice;
    private int destinoIndice;

    private List<Integer> pecasCapturadasIndices;
    private List<Integer> caminhoIndices;

    public MovimentoCaptura(int oI, int dI, List<Integer> capturadas, List<Integer> caminho) {
        this.origemIndice = oI;
        this.destinoIndice = dI;
        this.pecasCapturadasIndices = capturadas;
        this.caminhoIndices = caminho;
    }


    public int getOrigemIndice() { return origemIndice; }
    public int getDestinoIndice() { return destinoIndice; }
    public List<Integer> getPecasCapturadasIndices() { return pecasCapturadasIndices; }
    public List<Integer> getCaminhoIndices() { return caminhoIndices; }

    public int getOrigemLinha() { return Tradutor.converteParaCoordenada(origemIndice)[0]; }
    public int getOrigemColuna() { return Tradutor.converteParaCoordenada(origemIndice)[1]; }

    public int getDestinoLinha() { return Tradutor.converteParaCoordenada(destinoIndice)[0]; }
    public int getDestinoColuna() { return Tradutor.converteParaCoordenada(destinoIndice)[1]; }

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

    public int getTotalCapturas() {
        return pecasCapturadasIndices.size();
    }
}
