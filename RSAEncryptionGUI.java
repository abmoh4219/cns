import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.*;

public class RSAEncryptionGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher cipher;
    
    public RSAEncryptionGUI() {
        setTitle("RSA Encryption/Decryption Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Input Text:"), BorderLayout.NORTH);
        inputTextArea = new JTextArea(5, 40);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
        
        // Output panel
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Output Text:"), BorderLayout.NORTH);
        outputTextArea = new JTextArea(5, 40);
        outputTextArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);
        
        // Add panels to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(outputPanel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loadKeysButton = new JButton("Load RSA Keys");
        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");
        JButton saveButton = new JButton("Save to File");
        JButton loadButton = new JButton("Load from File");
        
        buttonPanel.add(loadKeysButton);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
        // Add components to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        loadKeysButton.addActionListener(e -> loadRSAKeys());
        encryptButton.addActionListener(e -> encrypt());
        decryptButton.addActionListener(e -> decrypt());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());
        
        // Initialize cipher
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            JOptionPane.showMessageDialog(this, "Error initializing cipher: " + e.getMessage());
        }
    }
    
    private void loadRSAKeys() {
        try {
            // Load private key
            String privateKeyContent = new String(Files.readAllBytes(Paths.get("private_key.pem")));
            privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                                               .replace("-----END PRIVATE KEY-----", "")
                                               .replaceAll("\\s", "");
            
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(privateKeySpec);
            
            // Load public key
            String publicKeyContent = new String(Files.readAllBytes(Paths.get("public_key.pem")));
            publicKeyContent = publicKeyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                                             .replace("-----END PUBLIC KEY-----", "")
                                             .replaceAll("\\s", "");
            
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
            
            JOptionPane.showMessageDialog(this, "RSA keys loaded successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading RSA keys: " + e.getMessage());
        }
    }
    
    private void encrypt() {
        if (publicKey == null) {
            JOptionPane.showMessageDialog(this, "Please load RSA keys first!");
            return;
        }
        
        String inputText = inputTextArea.getText();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to encrypt!");
            return;
        }
        
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(inputText.getBytes());
            outputTextArea.setText(Base64.getEncoder().encodeToString(encryptedBytes));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during encryption: " + e.getMessage());
        }
    }
    
    private void decrypt() {
        if (privateKey == null) {
            JOptionPane.showMessageDialog(this, "Please load RSA keys first!");
            return;
        }
        
        String inputText = inputTextArea.getText();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter encrypted text to decrypt!");
            return;
        }
        
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(inputText);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            outputTextArea.setText(new String(decryptedBytes));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during decryption: " + e.getMessage());
        }
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
            new RSAEncryptionGUI().setVisible(true);
        });
    }
} 