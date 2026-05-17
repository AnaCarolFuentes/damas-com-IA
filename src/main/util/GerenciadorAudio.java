package main.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;
import java.net.URL;

public final class GerenciadorAudio {

    private static final String PASTA_SONS = "/res/sons/";

    private GerenciadorAudio() {
    }

    public static void tocarSom(String caminho) {
        try {
            URL url = localizarSom(caminho);
            if (url == null) {
                System.err.println("Som nao encontrado: " + PASTA_SONS + caminho);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioIn.close();
                    } catch (Exception ignored) {
                    }
                }
            });
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("Erro ao tocar som: " + e.getMessage());
        }
    }

    private static URL localizarSom(String caminho) throws Exception {
        URL recurso = GerenciadorAudio.class.getResource(PASTA_SONS + caminho);
        if (recurso != null) return recurso;

        File[] candidatos = {
                new File("res/sons", caminho),
                new File("src/main/res/sons", caminho),
                new File("src/main/resources/res/sons", caminho)
        };

        for (File arquivo : candidatos) {
            if (arquivo.isFile()) {
                return arquivo.toURI().toURL();
            }
        }
        return null;
    }
}
