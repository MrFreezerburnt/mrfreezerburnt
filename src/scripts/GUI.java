package scripts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("unchecked")
class GUI extends JFrame {

    static final String[] dhideBoxOptions = {"----", "Black", "Red", "Blue", "Green"};

    private JLabel dhideOptionsLabel = new JLabel("Choose dhide:");
    private JComboBox dhideComboBox = new JComboBox(dhideBoxOptions);
    private JButton startButton = new JButton("Start");
    private JPanel mainPanel = new JPanel();

    GUI(String name) {
        super(name);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addComponentsToPane(this.getContentPane());
        this.setLocationRelativeTo(null);
        this.pack();
    }

    private void addComponentsToPane(final Container pane) {

        mainPanel.setLayout(new GridLayout(0, 3, 5, 5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        mainPanel.add(dhideOptionsLabel);
        mainPanel.add(dhideComboBox);
        mainPanel.add(startButton);

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
        pane.add(mainPanel);

    }
}
