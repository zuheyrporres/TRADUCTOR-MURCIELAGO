/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author zuhey
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class traductor extends JFrame {
    private JTextArea textAreaOriginal;
    private JTextArea textAreaTranslated;
    private JTextArea textAreaResults;
    private JButton translateButton;
    private JButton clearButton;
    private JFileChooser fileChooser;
    private String currentFile = null;

    public traductor() {
        setTitle("Traductor Murcielago");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //áreas de texto
        textAreaOriginal = new JTextArea();
        textAreaOriginal.setLineWrap(true);
        JScrollPane scrollPaneOriginal = new JScrollPane(textAreaOriginal);
        textAreaOriginal.setBorder(BorderFactory.createTitledBorder("Texto Original"));

        textAreaTranslated = new JTextArea();
        textAreaTranslated.setLineWrap(true);
        JScrollPane scrollPaneTranslated = new JScrollPane(textAreaTranslated);
        textAreaTranslated.setBorder(BorderFactory.createTitledBorder("Texto Traducido"));

        textAreaResults = new JTextArea();
        textAreaResults.setLineWrap(true);
        textAreaResults.setEditable(false);
        JScrollPane scrollPaneResults = new JScrollPane(textAreaResults);
        textAreaResults.setBorder(BorderFactory.createTitledBorder("Resultados"));

        //  botones
        translateButton = new JButton("Procesar");
        translateButton.addActionListener(e -> translateAndProcessText());

        clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> clearTextAreas());

        //  panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(translateButton);
        buttonPanel.add(clearButton);

        // panel principal para las áreas de texto
        JPanel textPanel = new JPanel(new GridLayout(3, 1));
        textPanel.add(scrollPaneOriginal);
        textPanel.add(scrollPaneTranslated);
        textPanel.add(scrollPaneResults);

        // panel para la barra de menú
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Archivo");
        menuBar.add(fileMenu);

        JMenuItem openItem = new JMenuItem("Abrir");
        openItem.addActionListener(e -> openFile());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Guardar");
        saveItem.addActionListener(e -> saveFile(false));
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Guardar como");
        saveAsItem.addActionListener(e -> saveFile(true));
        fileMenu.add(saveAsItem);

        JMenu editMenu = new JMenu("Editar");
        menuBar.add(editMenu);

        JMenuItem copyItem = new JMenuItem("Copiar");
        copyItem.addActionListener(e -> textAreaOriginal.copy());
        editMenu.add(copyItem);

        JMenuItem cutItem = new JMenuItem("Cortar");
        cutItem.addActionListener(e -> textAreaOriginal.cut());
        editMenu.add(cutItem);

        JMenuItem pasteItem = new JMenuItem("Pegar");
        pasteItem.addActionListener(e -> textAreaOriginal.paste());
        editMenu.add(pasteItem);

        JMenuItem searchItem = new JMenuItem("Buscar");
        searchItem.addActionListener(e -> searchText());
        editMenu.add(searchItem);

        JMenuItem replaceItem = new JMenuItem("Reemplazar");
        replaceItem.addActionListener(e -> replaceText());
        editMenu.add(replaceItem);

        fileChooser = new JFileChooser();

        // Configurar el diseño de la ventana principal
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(textPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void clearTextAreas() {
        textAreaOriginal.setText("");
        textAreaTranslated.setText("");
        textAreaResults.setText("");
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textAreaOriginal.read(reader, null);
                currentFile = file.getAbsolutePath();
                textAreaTranslated.setText(""); // Limpiar el área de texto traducido al abrir un nuevo archivo
                textAreaResults.setText(""); // Limpiar los resultados al abrir un nuevo archivo
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error abriendo el archivo.");
            }
        }
    }

    private void saveFile(boolean saveAs) {
        if (saveAs || currentFile == null) {
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                currentFile = file.getAbsolutePath();
                writeFile(file);
            }
        } else {
            writeFile(new File(currentFile));
        }
    }

    private void writeFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
           writer.write("Texto Original:\n");
            textAreaOriginal.write(writer);
            writer.write("\n\nTexto Traducido:\n");
            textAreaTranslated.write(writer);
            writer.write("\n\nResultados:\n");
            textAreaResults.write(writer); 
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error guardando el archivo.");
        }
    }

    private void searchText() {
        String search = JOptionPane.showInputDialog(this, "Buscar:");
        if (search != null) {
            String text = textAreaOriginal.getText();
            int index = text.indexOf(search);
            if (index != -1) {
                textAreaOriginal.select(index, index + search.length());
                JOptionPane.showMessageDialog(this, "Texto encontrado.");
            } else {
                JOptionPane.showMessageDialog(this, "Texto no encontrado.");
            }
        }
    }

    private void replaceText() {
        String search = JOptionPane.showInputDialog(this, "Buscar:");
        String replace = JOptionPane.showInputDialog(this, "Reemplazar con:");
        if (search != null && replace != null) {
            String text = textAreaOriginal.getText();
            text = text.replace(search, replace);
            textAreaOriginal.setText(text);
        }
    }

    private void translateAndProcessText() {
        String text = textAreaOriginal.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El área de texto está vacía.");
            return;
        }

        // Traducción
        String translatedText = translateToMurcielago(text);
        textAreaTranslated.setText(translatedText);

        // Procesar el texto original
        processText();
    }

    private void processText() {
        String text = textAreaOriginal.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El área de texto está vacía.");
            return;
        }

        // estadísticas
        int length = text.length();
        int wordCount = text.split("\\s+").length;
        char firstLetter = text.charAt(0);
        char centralLetter = text.charAt(text.length() / 2);
        String[] words = text.split("\\s+");
        String firstWord = words[0];
        String centralWord = words[words.length / 2];
        String lastWord = words[words.length - 1];

        // Contar repeticiones de vocales agrupadas
        Map<String, Integer> vowelCount = countVowels(text);

        // Contar palabras con longitud par e impar
        int evenLengthWords = 0;
        int oddLengthWords = 0;
        for (String word : words) {
            if (word.length() % 2 == 0) {
                evenLengthWords++;
            } else {
                oddLengthWords++;
            }
        }

        // estadísticas en el área de resultados
        textAreaResults.setText(String.format("Longitud del texto: %d\n" +
                                              "Total de palabras: %d\n" +
                                              "Primera letra: %c\n" +
                                              "Letra central: %c\n" +
                                              "Primera palabra: %s\n" +
                                              "Palabra central: %s\n" +
                                              "Última palabra: %s\n" +
                                              "Repeticiones de vocales:\n" +
                                              "A (mayúsculas, minúsculas, á): %d\n" +
                                              "E (mayúsculas, minúsculas, é): %d\n" +
                                              "I (mayúsculas, minúsculas, í): %d\n" +
                                              "O (mayúsculas, minúsculas, ó): %d\n" +
                                              "U (mayúsculas, minúsculas, ú): %d\n" +
                                              "Palabras con longitud par: %d\n" +
                                              "Palabras con longitud impar: %d",
                                              length, wordCount, firstLetter, centralLetter, firstWord, centralWord, lastWord,
                                              vowelCount.get("A"), vowelCount.get("E"), vowelCount.get("I"),
                                              vowelCount.get("O"), vowelCount.get("U"), evenLengthWords, oddLengthWords));
    }

    private Map<String, Integer> countVowels(String text) {
        Map<String, Integer> vowelCount = new HashMap<>();
        vowelCount.put("A", countOccurrences(text, 'a') + countOccurrences(text, 'á') +
                                  countOccurrences(text, 'A') + countOccurrences(text, 'Á'));
        vowelCount.put("E", countOccurrences(text, 'e') + countOccurrences(text, 'é') +
                                  countOccurrences(text, 'E') + countOccurrences(text, 'É'));
        vowelCount.put("I", countOccurrences(text, 'i') + countOccurrences(text, 'í') +
                                  countOccurrences(text, 'I') + countOccurrences(text, 'Í'));
        vowelCount.put("O", countOccurrences(text, 'o') + countOccurrences(text, 'ó') +
                                  countOccurrences(text, 'O') + countOccurrences(text, 'Ó'));
        vowelCount.put("U", countOccurrences(text, 'u') + countOccurrences(text, 'ú') +
                                  countOccurrences(text, 'U') + countOccurrences(text, 'Ú'));
        return vowelCount;
    }

    private int countOccurrences(String text, char c) {
        int count = 0;
        for (char ch : text.toCharArray()) {
            if (ch == c) {
                count++;
            }
        }
        return count;
    }

    private String translateToMurcielago(String text) {
        return text.replace("m", "0")
                   .replace("u", "1")
                   .replace("ú", "1")
                   .replace("r", "2")
                   .replace("c", "3")
                   .replace("i", "4")
                   .replace("í", "4")
                   .replace("e", "5")
                   .replace("é", "5")
                   .replace("l", "6")
                   .replace("a", "7")
                   .replace("á", "7")
                   .replace("g", "8")
                   .replace("o", "9")
                   .replace("ó", "9")
                   .replace("M", "0")
                   .replace("U", "1")
                   .replace("Ú", "1")
                   .replace("R", "2")
                   .replace("C", "3")
                   .replace("I", "4")
                   .replace("Í", "4")
                   .replace("E", "5")
                   .replace("É", "5")
                   .replace("L", "6")
                   .replace("A", "7")
                   .replace("Á", "7")
                   .replace("G", "8")
                   .replace("O", "9")
                   .replace("Ó", "9");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(traductor::new);
    }
}