package main;

import main.entidades.Jogador;
import main.ui.InterfaceGrafica;
import main.util.GerenciadorAudio;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {
    private Jogador corSelecionada = Jogador.BRANCAS; // Padrão
    private JSlider sliderNivel;
    private final Color COR_ROSA = new Color(255, 20, 147);
    private final Color COR_BEGE = new Color(245, 245, 220);

    public Main() {
        // Mesma proporção da janela do jogo (700x700)
        setTitle("Damas IA Academy - Configurações");
        setSize(700, 700);
        setResizable(false);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(COR_BEGE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        GerenciadorAudio.tocarSom("inicio.wav");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        // --- TÍTULO ---
        JLabel lblTitulo = new JLabel("IA ACADEMY: CHECKERS");
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 42));
        lblTitulo.setForeground(COR_ROSA);
        gbc.gridy = 0;
        add(lblTitulo, gbc);

        // --- SELEÇÃO DE COR COM PEÇAS ---
        JLabel lblCor = new JLabel("SELECIONE SUA COR");
        lblCor.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 1;
        add(lblCor, gbc);

        JPanel painelPecas = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        painelPecas.setOpaque(false);

        // Botões de Peças Customizados
        JButton btnBrancas = criarBotaoPeca(Color.WHITE, "BRANCAS (Você começa)");
        JButton btnPretas = criarBotaoPeca(Color.BLACK, "PRETAS (IA começa)");

        // Lógica de seleção visual
        btnBrancas.addActionListener(e -> {
            GerenciadorAudio.tocarSom("click.wav");
            corSelecionada = Jogador.BRANCAS;
            btnBrancas.setBorder(new LineBorder(COR_ROSA, 4));
            btnPretas.setBorder(null);
        });
        btnPretas.addActionListener(e -> {
            GerenciadorAudio.tocarSom("click.wav");
            corSelecionada = Jogador.PRETAS;
            btnPretas.setBorder(new LineBorder(COR_ROSA, 4));
            btnBrancas.setBorder(null);
        });

        // Seleção padrão inicial
        btnBrancas.setBorder(new LineBorder(COR_ROSA, 4));

        painelPecas.add(btnBrancas);
        painelPecas.add(btnPretas);
        gbc.gridy = 2;
        add(painelPecas, gbc);

        // --- SLIDER DE DIFICULDADE DIVERTIDO ---
        JLabel lblNivel = new JLabel("NÍVEL DE PROCESSAMENTO DA IA");
        lblNivel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 3;
        add(lblNivel, gbc);

        sliderNivel = new JSlider(1, 15, 6);
        sliderNivel.setPreferredSize(new Dimension(500, 80));
        sliderNivel.setBackground(COR_BEGE);
        sliderNivel.setForeground(Color.DARK_GRAY);
        sliderNivel.setMajorTickSpacing(2);
        sliderNivel.setPaintTicks(true);
        sliderNivel.setPaintLabels(true);
        sliderNivel.setSnapToTicks(true);


        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(1, new JLabel("Bebê"));
        labelTable.put(5, new JLabel("Estudante"));
        labelTable.put(10, new JLabel("Engenheiro"));
        labelTable.put(15, new JLabel("TERMINATOR"));
        sliderNivel.setLabelTable(labelTable);

        gbc.gridy = 4;
        add(sliderNivel, gbc);

        // --- BOTÃO INICIAR ---
        JButton btnIniciar = new JButton("INICIAR BATALHA");
        btnIniciar.setPreferredSize(new Dimension(300, 60));
        btnIniciar.setBackground(COR_ROSA);
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 20));
        btnIniciar.setFocusPainted(false);
        btnIniciar.addActionListener(e -> iniciarJogo());
        btnIniciar.addMouseListener(criarHoverSom());

        gbc.gridy = 5;
        add(btnIniciar, gbc);
    }

    private JButton criarBotaoPeca(Color corPeca, String tooltip) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corPeca);
                g2.fillOval(10, 10, 80, 80);
                g2.setColor(Color.GRAY);
                g2.drawOval(10, 10, 80, 80);
                if (corPeca == Color.BLACK) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(25, 25, 50, 50);
                }
            }
        };
        btn.setPreferredSize(new Dimension(100, 100));
        btn.setContentAreaFilled(false);
        btn.setToolTipText(tooltip);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(criarHoverSom());
        return btn;
    }

    private void iniciarJogo() {
        GerenciadorAudio.tocarSom("inicio.wav");
        int dificuldade = sliderNivel.getValue();
        new InterfaceGrafica(corSelecionada, dificuldade);
        this.dispose();
    }

    private MouseAdapter criarHoverSom() {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                GerenciadorAudio.tocarSom("clique.wav");
            }
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
