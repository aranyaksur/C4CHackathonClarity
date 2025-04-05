import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class WordDefiner {

    private static final Map<Integer, String> wordPositions = new HashMap<>();
    private static double scaleFactor = 1.0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clarity");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // -------------------- INTRO PANEL --------------------
        JPanel introPanel = new JPanel();
        introPanel.setLayout(new BoxLayout(introPanel, BoxLayout.Y_AXIS));
        introPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        introPanel.setBackground(new Color(230, 240, 255));

        JLabel title = new JLabel("Welcome to Clarity");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        title.setForeground(new Color(0, 102, 204));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description1 = new JLabel("Clarity helps you find tricky words in your writing!");
        description1.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        description1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description2 = new JLabel("Pick your reading level below, then click Start.");
        description2.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        description2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider levelSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        levelSlider.setMajorTickSpacing(1);
        levelSlider.setPaintTicks(true);
        levelSlider.setPaintLabels(true);
        levelSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sliderLabel = new JLabel("Reading Level");
        sliderLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(0, 153, 76));
        startButton.setForeground(Color.WHITE);

        introPanel.add(title);
        introPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        introPanel.add(description1);
        introPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        introPanel.add(description2);
        introPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        introPanel.add(sliderLabel);
        introPanel.add(levelSlider);
        introPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        introPanel.add(startButton);

        // -------------------- MAIN APP PANEL --------------------
        JPanel appPanel = new JPanel(new BorderLayout());
        appPanel.setBackground(Color.WHITE);

        JTextField inputField = new JTextField(40);
        inputField.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        JButton processButton = new JButton("Analyze");
        JButton loadFileButton = new JButton("Load File");

        JTextPane outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        outputPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        outputPane.setBackground(new Color(255, 255, 240));

        JLabel loadingLabel = new JLabel("🔄 Analyzing...");
        loadingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setForeground(new Color(0, 102, 204));
        loadingLabel.setVisible(false);

        StyledDocument doc = outputPane.getStyledDocument();
        Style easyStyle = outputPane.addStyle("easy", null);
        StyleConstants.setForeground(easyStyle, Color.BLACK);
        Style mediumStyle = outputPane.addStyle("medium", null);
        StyleConstants.setForeground(mediumStyle, new Color(255, 140, 0));
        StyleConstants.setUnderline(mediumStyle, true);
        Style hardStyle = outputPane.addStyle("hard", null);
        StyleConstants.setForeground(hardStyle, new Color(0, 102, 255));
        StyleConstants.setUnderline(hardStyle, true);

        JLabel instructions = new JLabel("Enter a text below and click 'Analyze' to find tricky words!");
        instructions.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        instructions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(245, 250, 255));
        topPanel.add(new JLabel("Enter Text:"));
        topPanel.add(inputField);
        topPanel.add(processButton);
        topPanel.add(loadFileButton);

        JPanel legendPanel = new JPanel();
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setLayout(new FlowLayout());
        legendPanel.add(new JLabel("Word Difficulty: "));
        legendPanel.add(createColorLabel("Easy", Color.BLACK));
        legendPanel.add(createColorLabel("Medium", new Color(255, 140, 0)));
        legendPanel.add(createColorLabel("Hard", new Color(0, 102, 255)));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(outputPane), BorderLayout.CENTER);
        centerPanel.add(loadingLabel, BorderLayout.SOUTH); // add spinner

        appPanel.add(instructions, BorderLayout.NORTH);
        appPanel.add(centerPanel, BorderLayout.CENTER);
        appPanel.add(legendPanel, BorderLayout.SOUTH);

        // -------------------- FUNCTIONALITY --------------------
        startButton.addActionListener(e -> {
            scaleFactor = levelSlider.getValue();
            cardLayout.show(mainPanel, "app");
        });

        processButton.addActionListener(e -> {
            loadingLabel.setVisible(true); // Show spinner
            SwingUtilities.invokeLater(() -> {
                try {
                    doc.remove(0, doc.getLength());
                    wordPositions.clear();
                    String sentence = inputField.getText();
                    java.util.List<String> words = new ArrayList<>();

                    for (String token : sentence.split(" ")) {
                        if (token.contains("--")) {
                            String[] parts = token.split("--");
                            for (String part : parts) {
                                if (!part.isEmpty()) words.add(part);
                            }
                        } else {
                            words.add(token);
                        }
                    }

                    for (int i = 0; i < words.size(); i++) {
                        String word = words.get(i);
                        String clean = word.replaceAll("[^a-zA-Z\\-]", "").toLowerCase();
                        if (clean.isEmpty()) continue;

                        String difficulty = getWordDifficulty(clean);
                        Style style = switch (difficulty) {
                            case "hard" -> hardStyle;
                            case "medium" -> mediumStyle;
                            default -> easyStyle;
                        };

                        int start = doc.getLength();
                        doc.insertString(doc.getLength(), word, style);

                        if (!difficulty.equals("easy")) {
                            wordPositions.put(start, clean);
                        }

                        if (i < words.size() - 1) {
                            doc.insertString(doc.getLength(), " ", easyStyle);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                } finally {
                    loadingLabel.setVisible(false); // Hide spinner
                }
            });
        });

        loadFileButton.addActionListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append(" ");
                    }
                    inputField.setText(sb.toString());
                    processButton.doClick();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to load file: " + ex.getMessage());
                }
            }
        });

        outputPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int offset = outputPane.viewToModel2D(e.getPoint());
                for (Map.Entry<Integer, String> entry : wordPositions.entrySet()) {
                    int start = entry.getKey();
                    int end = start + entry.getValue().length();
                    if (offset >= start && offset <= end + 1) {
                        String word = entry.getValue();
                        try {
                            String def = fetchDefinition(word);
                            JOptionPane.showMessageDialog(frame, word + ":\n" + def);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Definition not found.");
                        }
                        break;
                    }
                }
            }
        });

        mainPanel.add(introPanel, "intro");
        mainPanel.add(appPanel, "app");
        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "intro");
    }

    private static JLabel createColorLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        return label;
    }

    private static String getWordDifficulty(String word) throws Exception {
        if (word.isEmpty()) return "easy";
        String apiUrl = "https://api.datamuse.com/words?sp=" + URLEncoder.encode(word, "UTF-8") + "&md=f";
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) return "easy";

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        Matcher matcher = Pattern.compile("\"tags\":\\[\"f:([0-9.]+)\"\\]").matcher(response.toString());
        if (matcher.find()) {
            double freq = Double.parseDouble(matcher.group(1));
            double scaledFreq = freq * scaleFactor;
            if (scaleFactor == 1) return scaledFreq > 15 ? "easy" : scaledFreq >= 10 ? "medium" : "hard";
            if (scaleFactor == 2) return scaledFreq > 13 ? "easy" : scaledFreq >= 7.5 ? "medium" : "hard";
            if (scaleFactor == 3) return scaledFreq > 11 ? "easy" : scaledFreq >= 5 ? "medium" : "hard";
            if (scaleFactor == 4) return scaledFreq > 8 ? "easy" : scaledFreq >= 4.5 ? "medium" : "hard";
            else return scaledFreq > 6 ? "easy" : scaledFreq >= 3 ? "medium" : "hard";
        }

        return "easy";
    }

    private static String fetchDefinition(String word) throws Exception {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + URLEncoder.encode(word, "UTF-8");
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) throw new IOException("Word not found.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }
        reader.close();

        Matcher matcher = Pattern.compile("\"definition\":\"(.*?)\"").matcher(json.toString());
        return matcher.find() ? matcher.group(1) : "Definition not found.";
    }
}
