package batalhanaval;

import batalhanaval.client.ClienteGUI;
import batalhanaval.server.Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Launcher gráfico da Batalha Naval.
 * Ponto de entrada único — escolhe se quer ser Servidor ou Cliente.
 *
 * Uso: java -jar BatalhaNaval.jar
 */
public class Launcher extends JFrame {

    // Cores iguais ao ClienteGUI
    private static final Color BG     = new Color(10, 28, 58);
    private static final Color ACCENT = new Color(0, 155, 215);
    private static final Color TEXT   = new Color(215, 230, 255);
    private static final Color BTN_BG = new Color(20, 52, 98);

    public static void main(String[] args) {
        // Permitir arranque direto como Servidor ou Cliente via argumento
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "servidor", "server" -> {
                    try { Servidor.main(new String[0]); }
                    catch (IOException e) { e.printStackTrace(); }
                    return;
                }
                case "cliente", "client" -> {
                    SwingUtilities.invokeLater(() -> new ClienteGUI().setVisible(true));
                    return;
                }
            }
        }

        // Janela de escolha
        SwingUtilities.invokeLater(() -> new Launcher().setVisible(true));
    }

    public Launcher() {
        super("Batalha Naval");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(BG);
        painel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);
        c.gridx = 0; c.gridy = 0;

        // Título
        JLabel titulo = new JLabel("⚓  BATALHA NAVAL", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(ACCENT);
        painel.add(titulo, c);

        // Subtítulo
        c.gridy++;
        JLabel sub = new JLabel("Jogo Multijogador via Rede", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(130, 165, 205));
        painel.add(sub, c);

        // Separador
        c.gridy++;
        c.insets = new Insets(16, 0, 16, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(35, 75, 135));
        painel.add(sep, c);
        c.insets = new Insets(8, 0, 8, 0);

        // Botão Servidor
        c.gridy++;
        JButton btnServidor = criarBotao("🖥  Iniciar Servidor", new Color(0, 100, 160));
        btnServidor.setToolTipText("Cria um novo jogo e aguarda dois jogadores");
        btnServidor.addActionListener(e -> {
            dispose();
            // Servidor corre numa thread separada (tem Scanner no main)
            Thread t = new Thread(() -> {
                try { Servidor.main(new String[0]); }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar servidor:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }, "Servidor");
            t.setDaemon(false);
            t.start();
        });
        painel.add(btnServidor, c);

        // Botão Cliente
        c.gridy++;
        JButton btnCliente = criarBotao("🎮  Entrar como Jogador", new Color(0, 130, 80));
        btnCliente.setToolTipText("Abre a interface gráfica do jogador");
        btnCliente.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ClienteGUI().setVisible(true));
        });
        painel.add(btnCliente, c);

        // Botão Dois Jogadores (mesmo PC)
        c.gridy++;
        JButton btnDois = criarBotao("👥  Dois Jogadores (mesmo PC)", new Color(100, 60, 150));
        btnDois.setToolTipText("Abre Servidor + dois clientes para testar localmente");
        btnDois.addActionListener(e -> {
            dispose();
            // Servidor em background
            Thread t = new Thread(() -> {
                try { Servidor.main(new String[0]); }
                catch (IOException ex) { ex.printStackTrace(); }
            }, "Servidor");
            t.setDaemon(false);
            t.start();

            // Pequena pausa para o servidor arrancar
            Timer timer = new Timer(800, ev -> {
                SwingUtilities.invokeLater(() -> new ClienteGUI().setVisible(true));
                SwingUtilities.invokeLater(() -> new ClienteGUI().setVisible(true));
            });
            timer.setRepeats(false);
            timer.start();
        });
        painel.add(btnDois, c);

        // Nota
        c.gridy++;
        c.insets = new Insets(20, 0, 0, 0);
        JLabel nota = new JLabel(
            "<html><center><small>Para jogar em rede: um PC inicia o Servidor,<br>" +
            "os jogadores entram com o IP do servidor.</small></center></html>",
            SwingConstants.CENTER);
        nota.setForeground(new Color(100, 130, 170));
        nota.setFont(new Font("SansSerif", Font.PLAIN, 11));
        painel.add(nota, c);

        setContentPane(painel);
        pack();
        setLocationRelativeTo(null);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(cor);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 48));
        btn.addMouseListener(new MouseAdapter() {
            final Color original = cor;
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(original.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });
        return btn;
    }
}
