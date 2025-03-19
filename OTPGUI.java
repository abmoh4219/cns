import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.Random;

public class OTPGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextArea keyTextArea;
    private JTextArea decryptedTextArea;
    private String key;

    public OTPGUI() {
        setTitle("OTP Encryption/Decryption Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save to File");
        JMenuItem loadMenuItem = new JMenuItem("Load from File");
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Input Text:"), BorderLayout.NORTH);
        inputTextArea = new JTextArea(5, 40);
        inputTextArea.setBackground(Color.RED);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        // Key panel
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.add(new JLabel("Key:"), BorderLayout.NORTH);
        keyTextArea = new JTextArea(3, 40);
        keyTextArea.setEditable(false);
        keyTextArea.setBackground(Color.RED);
        keyPanel.add(new JScrollPane(keyTextArea), BorderLayout.CENTER);

        // Output panel
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Encrypted Text:"), BorderLayout.NORTH);
        outputTextArea = new JTextArea(5, 40);
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(Color.YELLOW);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        // Decrypted panel
        JPanel decryptedPanel = new JPanel(new BorderLayout());
        decryptedPanel.add(new JLabel("Decrypted Text:"), BorderLayout.NORTH);
        decryptedTextArea = new JTextArea(5, 40);
        decryptedTextArea.setEditable(false);
        decryptedTextArea.setBackground(Color.GREEN);
        decryptedPanel.add(new JScrollPane(decryptedTextArea), BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(keyPanel);
        mainPanel.add(outputPanel);
        mainPanel.add(decryptedPanel);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton generateKeyButton = new JButton("Generate Key");
        JButton encryptButton = new JButton("Encrypt Text");
        JButton decryptButton = new JButton("Decrypt Text");

        generateKeyButton.setToolTipText("Generate a random key for OTP encryption");
        encryptButton.setToolTipText("Encrypt the input text using the generated key");
        decryptButton.setToolTipText("Decrypt the encrypted text using the generated key");

        buttonPanel.add(generateKeyButton);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        // Add components to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add button listeners
        generateKeyButton.addActionListener(e -> generateKey());
        encryptButton.addActionListener(e -> encrypt());
        decryptButton.addActionListener(e -> decrypt());
        saveMenuItem.addActionListener(e -> saveToFile());
        loadMenuItem.addActionListener(e -> loadFromFile());
    }

    private void generateKey() {
        String inputText = inputTextArea.getText();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to generate a key!");
            return;
        }

        Random random = new Random();
        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < inputText.length(); i++) {
            keyBuilder.append((char) (random.nextInt(256)));
        }
        key = keyBuilder.toString();
        keyTextArea.setText(key);
    }

    private void encrypt() {
        if (key == null || key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a key first!");
            return;
        }

        String inputText = inputTextArea.getText();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to encrypt!");
            return;
        }

        StringBuilder encryptedText = new StringBuilder();
        for (int i = 0; i < inputText.length(); i++) {
            encryptedText.append((char) (inputText.charAt(i) ^ key.charAt(i)));
        }
        outputTextArea.setText(encryptedText.toString());
    }

    private void decrypt() {
        if (key == null || key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a key first!");
            return;
        }

        String encryptedText = outputTextArea.getText();
        if (encryptedText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter encrypted text to decrypt!");
            return;
        }

        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < encryptedText.length(); i++) {
            decryptedText.append((char) (encryptedText.charAt(i) ^ key.charAt(i)));
        }
        decryptedTextArea.setText(decryptedText.toString());
    }

    private void saveToFile() {
        try {
            String outputText = outputTextArea.getText();
            if (outputText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No text to save!");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                Path filePath = fileChooser.getSelectedFile().toPath();
                Files.write(filePath, outputText.getBytes());
                JOptionPane.showMessageDialog(this, "File saved successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                Path filePath = fileChooser.getSelectedFile().toPath();
                String content = new String(Files.readAllBytes(filePath));
                inputTextArea.setText(content);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OTPGUI().setVisible(true);
        });
    }
}