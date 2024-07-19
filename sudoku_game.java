import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class sudoku_game {
    public static void main(String[] args) {
        setup();
    }

    public static void setup() {
        int n = 9;
        int[][] pseudoboard = new int[n][n];
        int[][] boardsoln = new int[n][n];
        boolean built = false;
        while (!built)
            built = buildsudoku(n, pseudoboard, boardsoln);

        JFrame frame = new JFrame();
        frame.setSize(600, 700);
        frame.setTitle("Sudoku");
        frame.setResizable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        JPanel board = new JPanel();
        board.setPreferredSize(new Dimension(500, 500));
        board.setLayout(new GridLayout(n, n));
        board.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField[][] fieldref = new JTextField[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                JTextField field = new JTextField();
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setFont(new Font("SansSerif", Font.BOLD, 18));
                field.setBackground(Color.WHITE);
                field.setForeground(Color.DARK_GRAY);
                field.setOpaque(true);
                field.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!((c >= '1') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                            e.consume();  // Ignore non-numeric input
                        }
                    }
                });
                if (pseudoboard[i][j] != 0) {
                    field.setText(Integer.toString(pseudoboard[i][j]));
                    field.setEditable(false);
                    field.setBackground(new Color(220, 220, 220));
                }
                if (((i + 1) % (int) Math.sqrt(n)) == 0 && ((j + 1) % (int) Math.sqrt(n)) == 0)
                    field.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 3, Color.BLACK));
                else if (((i + 1) % (int) Math.sqrt(n)) == 0)
                    field.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, Color.BLACK));
                else if (((j + 1) % (int) Math.sqrt(n)) == 0)
                    field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 3, Color.BLACK));
                else
                    field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
                board.add(field);
                fieldref[i][j] = field;
            }

        JPanel control = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        control.setBackground(new Color(240, 240, 240));
        JButton reset = new JButton("Reset");
        JButton submit = new JButton("Submit");
        JButton solve = new JButton("Solve");
        JButton newgame = new JButton("New Game");

        customizeButton(reset);
        customizeButton(submit);
        customizeButton(solve);
        customizeButton(newgame);

        control.add(reset);
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        if (pseudoboard[i][j] == 0)
                            fieldref[i][j].setText("");
            }
        });

        control.add(submit);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[][] submission = new int[n][n];
                boolean allFilled = true;
                boolean validInput = true;

                // Check if all cells are filled and if inputs are valid numbers
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        String text = fieldref[i][j].getText();
                        if (text.isEmpty()) {
                            allFilled = false;
                        } else {
                            try {
                                int value = Integer.parseInt(text);
                                if (value < 1 || value > n) {
                                    validInput = false;
                                } else {
                                    submission[i][j] = value;
                                }
                            } catch (NumberFormatException ex) {
                                validInput = false;
                            }
                        }
                    }
                }

                // Provide feedback for empty cells
                if (!allFilled) {
                    JOptionPane.showMessageDialog(frame, "Fill all the boxes to submit.");
                    return;
                }

                // Provide feedback for invalid input
                if (!validInput) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers between 1 and 9.");
                    return;
                }

                // Check if the submission is correct according to Sudoku rules
                boolean correct = true;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (!issafe(n, submission, i, j)) {
                            correct = false;
                            break;
                        }
                    }
                    if (!correct) break;
                }

                if (!correct) {
                    JOptionPane.showMessageDialog(frame, "Wrong Answer. Try again.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Right Answer. You Won. :)");
                }
            }
        });

        control.add(solve);
        solve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        fieldref[i][j].setText(Integer.toString(boardsoln[i][j]));
            }
        });

        control.add(newgame);
        newgame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                setup();
            }
        });

        panel.add(board, BorderLayout.CENTER);
        panel.add(control, BorderLayout.SOUTH);

        // Add the credit label
        JPanel creditPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        creditPanel.setBackground(new Color(240, 240, 240));
        JLabel creditLabel = new JLabel("Developed by Dhruval Maniyar");
        creditLabel.setFont(new Font("Times New Roman", Font. BOLD, 24));
        creditLabel.setForeground(Color.GRAY);
        creditPanel.add(creditLabel);
        panel.add(creditPanel, BorderLayout.NORTH);

        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void customizeButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    public static boolean buildsudoku(int n, int[][] pseudoboard, int[][] boardsoln) {
        Random random = new Random();
        int clues = 17;
        int p = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                pseudoboard[i][j] = 0;
        while (p <= clues) {
            int ri = random.nextInt(1000000000) % n;
            int ci = random.nextInt(1000000000) % n;
            int val = random.nextInt(1000000000) % (n + 1);
            if (val == 0 || pseudoboard[ri][ci] != 0)
                continue;
            pseudoboard[ri][ci] = val;
            boolean safe = issafe(n, pseudoboard, ri, ci);
            if (!safe) {
                pseudoboard[ri][ci] = 0;
                continue;
            }
            p++;
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                boardsoln[i][j] = pseudoboard[i][j];
        boolean solnexist = fillsudoku(n, boardsoln, 0, 0);
        return solnexist;
    }

    public static boolean fillsudoku(int n, int[][] boardsoln, int ri, int ci) {
        if (ci == n) {
            ri = ri + 1;
            ci = 0;
        }
        if (ri == n && ci == 0)
            return true;
        if (boardsoln[ri][ci] != 0) {
            boolean retval = fillsudoku(n, boardsoln, ri, ci + 1);
            return retval;
        }
        for (int i = 1; i <= n; i++) {
            boardsoln[ri][ci] = i;
            boolean safe = issafe(n, boardsoln, ri, ci);
            if (!safe) {
                boardsoln[ri][ci] = 0;
                continue;
            }
            boolean retval = fillsudoku(n, boardsoln, ri, ci + 1);
            if (!retval) {
                boardsoln[ri][ci] = 0;
                continue;
            } else
                return true;
        }
        return false;
    }

    public static boolean issafe(int n, int[][] pseudoboard, int ri, int ci) {
        boolean[] check = new boolean[n];
        for (int i = 0; i < n; i++) check[i] = false;

        // Check row
        for (int i = 0; i < n; i++) {
            if (pseudoboard[ri][i] != 0) {
                if (check[pseudoboard[ri][i] - 1]) return false;
                check[pseudoboard[ri][i] - 1] = true;
            }
        }

        // Check column
        for (int i = 0; i < n; i++) check[i] = false;
        for (int i = 0; i < n; i++) {
            if (pseudoboard[i][ci] != 0) {
                if (check[pseudoboard[i][ci] - 1]) return false;
                check[pseudoboard[i][ci] - 1] = true;
            }
        }

        // Check 3x3 box
        int sn = (int) Math.sqrt(n);
        int boxri = (ri / sn) * sn;
        int boxci = (ci / sn) * sn;
        for (int i = 0; i < n; i++) check[i] = false;
        for (int i = 0; i < sn; i++) {
            for (int j = 0; j < sn; j++) {
                int val = pseudoboard[boxri + i][boxci + j];
                if (val != 0) {
                    if (check[val - 1]) return false;
                    check[val - 1] = true;
                }
            }
        }

        return true;
    }
}

