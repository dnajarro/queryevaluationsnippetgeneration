import shared.ResultSentence;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Console {
    private JFrame frame;    // where the CLI sits in the screen
    private JTextPane outputField;
    private JTextField inputField;    // the input field at the bottom of the CLI
    private JScrollPane scrollpane;  // gives scrolling functionality
    private Dimension screenSize;    // screen size information
    private JButton sendButton;
    private double screenWidth;
    private double screenHeight;
    private List<String> recentInputs;
    private int recentInputId = 0;
    private final int MAXRECENTCMD = 30;
    private final int FONTSIZE = 14;
    private final String FONT = "Arial";
    private final int CLICOLOR = 50;
    private final int PORT = 3001;
    private final String SERVERADDRESS = "127.0.0.1";
    private final String TITLE = "LHF MUD";
    private SearchEngine engine;

    public Console() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException e) {
            System.out.println("Class or initializer is not acceptable");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("Unable to create new instance of class");
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Can't support that look and feel");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Look and Feel class not found");
            e.printStackTrace();
        }
        init();
        addCommandInput();
    }

    private void addCommandInput() {
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitQuery();
                inputField.requestFocusInWindow();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitQuery();
                inputField.requestFocusInWindow();
            }
        });
    }

    private int getTwoThirds(double dimension) {
        return (int)(2.0d * dimension / 3.0d);
    }

    private void scrollBottom() {
        outputField.setCaretPosition(outputField.getDocument().getLength());
    }

    private void print(ResultSentence sentence) {
        try {
            printSentence(sentence.getSentence(), sentence.getQueryIndexes());
            scrollBottom();
        } catch (Exception e) {
            System.out.println("unable to print to CLI");
        }
    }

    private void printSentence(String[] sentence, int[] boldIndexes) {
        for (int i = 0; i < sentence.length; i++) {
            boolean isBold = false;
            for (Integer boldIndex : boldIndexes) {
                if (boldIndex == i) {
                    isBold = true;
                    break;
                }
            }
            String msg = sentence[i];
            appendToPane(msg, isBold);
            appendToPane(" ", false);
        }
        appendToPane("\n\r", false);
    }

    private void appendToPane(String msg, boolean isBold)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Color c = new Color(255, 255, 255);
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, FONT);
        if (isBold) {
            aset = sc.addAttribute(aset, StyleConstants.Bold, true);
        }
        else {
            aset = sc.addAttribute(aset, StyleConstants.Bold, false);
        }

        StyledDocument document = outputField.getStyledDocument();
        int len = document.getLength();
        outputField.setCaretPosition(len);
        outputField.setCharacterAttributes(aset, false);
        try {
            document.insertString(len, msg, aset);
        } catch (BadLocationException b) {
            System.out.println("tried to access bad location in document");
        }
    }

    private void submitQuery() {
        String text = inputField.getText();

        if (text.length() > 0) {
            if (recentInputs.size() >= MAXRECENTCMD) {
                recentInputs.remove(0);
            }
            recentInputs.add(text);
            recentInputId = 0;

            sendQuery(text);

            inputField.setText("");
        }
    }

    // allows you to see and use previous commands
//    private void addDirectionalInput() {
//        inputField.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) { }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_UP) {
//                    if (recentInputId < (MAXRECENTCMD - 1) && recentInputId < (recentInputs.size() - 1)) {
//                        recentInputId++;
//                    }
//                    else if (recentInputId + 1 == recentInputs.size()) {
//                        recentInputId = 0;
//                    }
//
//                    inputField.setText(recentInputs.get(recentInputs.size() - 1 - recentInputId));
//                }
//                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
//                    if (recentInputId > 0) {
//                        recentInputId--;
//                    }
//                    else if (recentInputId == 0) {
//                        recentInputId = recentInputs.size() - 1;
//                    }
//
//                    inputField.setText(recentInputs.get(recentInputs.size() - 1 - recentInputId));
//                }
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) { }
//        });
//    }

    private void sendQuery(String query) {
        List<ResultSentence> results = engine.search(query);
        for (int i = 0; i < results.size(); i++) {
            print(results.get(i));
        }
    }

    private void getScreenDimensions() {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenHeight = screenSize.getHeight();
        screenWidth = screenSize.getWidth();
    }

    private void setUpOutputField(int inputFieldMinHeight) {
        double windowHeight = frame.getPreferredSize().getHeight();
        if (windowHeight * 0.1 < inputFieldMinHeight) {
            outputField.setSize(new Dimension((int)frame.getPreferredSize().getWidth(),
                    (int)(windowHeight - inputFieldMinHeight)));
        }
        else {
            outputField.setSize(new Dimension((int) frame.getPreferredSize().getWidth(),
                    (int) (frame.getPreferredSize().getHeight() * 0.9)));
        }
        setUpOutputFieldScrollable();
        outputField.setEditable(false);
        outputField.setFont(new Font(FONT, Font.PLAIN, FONTSIZE));
        outputField.setForeground(Color.WHITE);
        outputField.setBackground(new Color(CLICOLOR, CLICOLOR, CLICOLOR));
    }

    private void setUpInputField() {
        inputField.setBackground(new Color(CLICOLOR, CLICOLOR, CLICOLOR));
        inputField.setEditable(true);
        if (System.getProperty("os.name").contains("Windows")) {
            inputField.setForeground(Color.WHITE);
            inputField.setCaretColor(Color.WHITE);
        }
        else {
            if (System.getProperty("os.name").contains("Linux")) {
                inputField.setForeground(Color.BLACK);
                inputField.setCaretColor(Color.BLACK);
            }
        }
        inputField.setFont(new Font(FONT, Font.PLAIN, FONTSIZE));
    }

    private void setUpOutputFieldScrollable() {
        scrollpane.add(outputField);
        scrollpane.setViewportView(outputField);
    }

    private void setUpSplitPane(JSplitPane splitPane, JPanel inputPanel, int inputFieldMinHeight) {
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        double windowHeight = frame.getPreferredSize().getHeight();
        // set split pane divider to 90% down from the top of the window, right where input field ends
        if (windowHeight * 0.1 < inputFieldMinHeight) {
            splitPane.setDividerLocation((int)windowHeight - inputFieldMinHeight);
        }
        else {
            splitPane.setDividerLocation((int) (frame.getPreferredSize().getHeight() * 0.9));
        }
        splitPane.setTopComponent(scrollpane);
        splitPane.setBottomComponent(inputPanel);
        splitPane.setDividerSize(0);
    }

    private void setUpFrame(JSplitPane splitPane) {
        frame.setTitle(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setPreferredSize(new Dimension(getTwoThirds(screenWidth), getTwoThirds(screenHeight)));
        frame.setResizable(false);
        frame.getContentPane().setLayout(new GridLayout());
        frame.getContentPane().add(splitPane);
    }

    // initialize the CLI: appearance and connect to server
    private void init() {
        frame = new JFrame();
        JPanel inputPanel = new JPanel();
        JSplitPane splitPane = new JSplitPane();
        scrollpane = new JScrollPane();
        inputField = new JTextField();
        outputField = new JTextPane();
        sendButton = new JButton("SEND");

        int inputFieldMinHeight = 65; // in pixels

        getScreenDimensions();

        recentInputs = new ArrayList<>();

        setUpFrame(splitPane);
        setUpSplitPane(splitPane, inputPanel, inputFieldMinHeight);

        setUpOutputField(inputFieldMinHeight);
        setUpInputField();

        engine = new SearchEngine();

        inputPanel.setSize(new Dimension(Integer.MAX_VALUE, (int)(frame.getPreferredSize().getHeight() / 16)));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        frame.setVisible(true);
        frame.pack();

        inputField.requestFocusInWindow();
    }

//    public void disableInputField() {
//        inputField.setEditable(false);
//        inputField.setBackground(Color.LIGHT_GRAY);
//    }
}
