package scripts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("unchecked")
class GUI extends JFrame {

    static final String[] dhideBoxOptions = {"Choose Hide", "Black", "Red", "Blue", "Green"};

    private JButton reportButton = new JButton("<HTML><FONT color =\"#000099\"><U>Report a Bug</U></FONT><HTML>");
    private JButton layoutButton = new JButton("<HTML><FONT color =\"#000099\"><U>Inventory Layout</U></FONT><HTML>");
    private JComboBox dhideComboBox = new JComboBox(dhideBoxOptions);
    private JButton startButton = new JButton("Start Script");
    private JPanel mainPanel = new JPanel();
    private Font newButtonFont = new Font(startButton.getFont().getName(), Font.BOLD, startButton.getFont().getSize());

    GUI(String name) throws URISyntaxException {
        super(name);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addComponentsToPane(this.getContentPane());
        this.setLocationRelativeTo(null);
        this.pack();
    }

    private void addComponentsToPane(final Container pane) throws URISyntaxException {

        mainPanel.setLayout(new GridLayout(2, 2, 5, 5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        ((JLabel)dhideComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        reportButton.setFocusPainted(false);
        startButton.setFocusPainted(false);
        layoutButton.setFocusPainted(false);
        dhideComboBox.setFocusable(false);

        startButton.setFont(newButtonFont);

        mainPanel.add(dhideComboBox);
        mainPanel.add(startButton);
        mainPanel.add(reportButton);
        mainPanel.add(layoutButton);

        ActionListener aListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object event = e.getSource();
                if (event == startButton) {
                    LunarTanner.dhideGuiChoice = dhideComboBox.getSelectedIndex();
                    dispose();
                }
            }
        };

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dhideComboBox.setSelectedIndex(0);
                LunarTanner.dhideGuiChoice = dhideComboBox.getSelectedIndex();
                e.getWindow().dispose();
            }
        });

        startButton.addActionListener(aListener);
        reportButton.addActionListener(new OpenUrlAction());
        layoutButton.addActionListener(new OpenUrlAction());
        pane.add(mainPanel);

    }

    class OpenUrlAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            Object source = e.getSource();

            try {
                if (source == reportButton) {
                    open(new URI("https://www.powerbot.org"));
                } else if (source == layoutButton) {
                    open(new URI("http://i.imgur.com/866ixtz.png"));
                }
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Copy/paste this link to your browser:\n" +
                        uri.toString(), "" , JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
