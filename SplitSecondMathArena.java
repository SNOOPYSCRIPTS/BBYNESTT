import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * BYNEST
 * Java Swing conversion of the original HTML/CSS/JavaScript game.
 *
 * Run:
 *   javac SplitSecondMathArena.java
 *   java SplitSecondMathArena
 */
public class SplitSecondMathArena extends JFrame {
    // ---------- Theme ----------
    private static final Color BG = new Color(10, 14, 23);
    private static final Color PANEL = new Color(18, 24, 38);
    private static final Color PANEL2 = new Color(26, 34, 51);
    private static final Color LINE = new Color(38, 48, 74);
    private static final Color CYAN = new Color(76, 224, 210);
    private static final Color MAGENTA = new Color(255, 95, 162);
    private static final Color AMBER = new Color(255, 200, 87);
    private static final Color DANGER = new Color(255, 107, 107);
    private static final Color TEXT = new Color(234, 240, 246);
    private static final Color DIM = new Color(139, 152, 172);

    private final CardLayout cards = new CardLayout();
    private final JPanel screen = new JPanel(cards);
    private final Random random = new Random();

    private String playerName = "";
    private final List<ScoreEntry> leaderboard = new ArrayList<>();

    // 5 families x 3 tiers
    private static final String[] FAMILIES = {
            "Easy", "Medium", "Hard", "Difficult", "Insane"
    };

    private static final String[][] DESCRIPTIONS = {
            {"+ / − only", "+ / − only", "+ / − only"},
            {"+ − ×", "+ − ×", "+ − ×"},
            {"+ − × ÷", "+ − × ÷", "+ − × ÷"},
            {"bigger numbers", "bigger numbers", "bigger numbers"},
            {"chained & huge", "chained & huge", "chained & huge"}
    };

    private JLabel playerLabel;
    private JPanel practicePanel;
    private JLabel questionLabel;
    private JLabel questionNumberLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel feedbackLabel;
    private JProgressBar timerBar;
    private JButton[] answerButtons;
    private JComboBox<String> levelBox;

    private List<Question> questions = new ArrayList<>();
    private int questionIndex;
    private int score;
    private long questionStart;
    private javax.swing.Timer gameTimer;

    public SplitSecondMathArena() {
        setTitle("BYNEST");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);

        applyCrossPlatformLookAndFeel();
        buildUI();
        showNameDialog();
    }

    private void applyCrossPlatformLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep the platform look and feel if the cross-platform one is unavailable.
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);
        top.setBorder(new EmptyBorder(14, 18, 10, 18));

        JLabel title = new JLabel("BYNEST");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        playerLabel = new JLabel();
        playerLabel.setForeground(DIM);
        playerLabel.setFont(new Font("Monospaced", Font.BOLD, 14));

        top.add(title, BorderLayout.WEST);
        top.add(playerLabel, BorderLayout.EAST);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nav.setBackground(BG);
        nav.setBorder(new EmptyBorder(0, 18, 12, 18));

        nav.add(navButton("Home", e -> showHome()));
        nav.add(navButton("Practice", e -> showPractice()));
        nav.add(navButton("Leaderboard", e -> showLeaderboard()));

        root.add(top, BorderLayout.NORTH);
        root.add(nav, BorderLayout.CENTER);

        screen.setBackground(PANEL);
        root.add(screen, BorderLayout.SOUTH);

        // Replace SOUTH with a container so the layout expands correctly.
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(nav, BorderLayout.NORTH);
        center.add(screen, BorderLayout.CENTER);

        root.remove(nav);
        root.remove(screen);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);

        buildHome();
        buildPractice();
        buildLeaderboard();
    }

    private JButton navButton(String text, ActionListener action) {
        JButton b = new JButton(text);
        styleButton(b, false);
        b.addActionListener(action);
        return b;
    }

    private void buildHome() {
        JPanel p = basePanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel title = heading("Welcome to BYNEST");
        p.add(title);
        p.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Sharpen your mental math with 10 timed multiple-choice problems.");
        sub.setForeground(DIM);
        p.add(sub);
        p.add(Box.createVerticalStrut(25));

        JPanel cards = new JPanel(new GridLayout(1, 2, 14, 0));
        cards.setOpaque(false);

        JPanel practice = gameCard(
                "Quick Practice",
                "10 shuffled problems across 15 difficulty tiers.",
                "Practice Now",
                e -> showPractice()
        );

        JPanel info = gameCard(
                "Java Edition",
                "This version runs locally as a Java Swing application.",
                "Choose Level",
                e -> showPractice()
        );

        cards.add(practice);
        cards.add(info);
        p.add(cards);

        screen.add(p, "home");
    }

    private JPanel gameCard(String title, String desc, String buttonText, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL2);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setForeground(DIM);
        d.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton b = new JButton(buttonText);
        styleButton(b, true);
        b.addActionListener(action);

        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(d);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(12));
        card.add(b);

        return card;
    }

    private void buildPractice() {
        practicePanel = basePanel();
        practicePanel.setLayout(new BorderLayout(14, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = heading("Practice");
        top.add(title, BorderLayout.WEST);

        levelBox = new JComboBox<>();
        for (int family = 0; family < 5; family++) {
            for (int tier = 1; tier <= 3; tier++) {
                levelBox.addItem(FAMILIES[family] + " · " + tier);
            }
        }
        levelBox.setBackground(PANEL2);
        levelBox.setForeground(TEXT);
        levelBox.setOpaque(true);
        top.add(levelBox, BorderLayout.EAST);

        practicePanel.add(top, BorderLayout.NORTH);

        JPanel game = new JPanel();
        game.setLayout(new BoxLayout(game, BoxLayout.Y_AXIS));
        game.setBackground(PANEL2);
        game.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel stats = new JPanel(new BorderLayout());
        stats.setOpaque(false);

        questionNumberLabel = smallLabel("Question 1 / 10");
        scoreLabel = smallLabel("Score: 0");
        timerLabel = smallLabel("Time: 10.0s");

        stats.add(questionNumberLabel, BorderLayout.WEST);
        stats.add(scoreLabel, BorderLayout.CENTER);
        stats.add(timerLabel, BorderLayout.EAST);

        game.add(stats);
        game.add(Box.createVerticalStrut(12));

        timerBar = new JProgressBar(0, 1000);
        timerBar.setValue(1000);
        timerBar.setForeground(CYAN);
        timerBar.setBackground(BG);
        timerBar.setBorderPainted(false);
        game.add(timerBar);

        game.add(Box.createVerticalStrut(28));

        questionLabel = new JLabel("Choose a level and start.");
        questionLabel.setForeground(TEXT);
        questionLabel.setFont(new Font("Monospaced", Font.BOLD, 38));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        game.add(questionLabel);

        game.add(Box.createVerticalStrut(22));

        answerButtons = new JButton[4];
        JPanel choices = new JPanel(new GridLayout(2, 2, 12, 12));
        choices.setOpaque(false);
        choices.setMaximumSize(new Dimension(480, 150));

        for (int i = 0; i < 4; i++) {
            final int index = i;
            answerButtons[i] = new JButton("—");
            answerButtons[i].setFont(new Font("Monospaced", Font.BOLD, 22));
            answerButtons[i].setBackground(BG);
            answerButtons[i].setForeground(TEXT);
            answerButtons[i].setOpaque(true);
            answerButtons[i].setContentAreaFilled(true);
            answerButtons[i].setBorderPainted(true);
            answerButtons[i].setFocusPainted(false);
            answerButtons[i].setBorder(BorderFactory.createLineBorder(LINE, 2));
            answerButtons[i].addActionListener(e -> answerSelected(index));
            choices.add(answerButtons[i]);
        }

        game.add(choices);
        game.add(Box.createVerticalStrut(12));

        feedbackLabel = smallLabel("");
        game.add(feedbackLabel);

        game.add(Box.createVerticalGlue());

        JButton start = new JButton("Start / Restart");
        styleButton(start, true);
        start.addActionListener(e -> startGame());
        game.add(start);

        practicePanel.add(game, BorderLayout.CENTER);
        screen.add(practicePanel, "practice");
    }

    private void buildLeaderboard() {
        JPanel p = basePanel();
        p.setLayout(new BorderLayout(10, 10));

        p.add(heading("Leaderboard"), BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(PANEL2);
        area.setForeground(TEXT);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(new JScrollPane(area), BorderLayout.CENTER);

        p.putClientProperty("leaderboardArea", area);
        screen.add(p, "leaderboard");
    }

    private JPanel basePanel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        return p;
    }

    private JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        return l;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(DIM);
        l.setFont(new Font("Monospaced", Font.BOLD, 13));
        return l;
    }

    private void styleButton(JButton b, boolean primary) {
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(primary ? CYAN : PANEL2);
        b.setForeground(primary ? new Color(4, 17, 15) : TEXT);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? CYAN : LINE),
                new EmptyBorder(9, 16, 9, 16)
        ));
    }

    private void showNameDialog() {
        SwingUtilities.invokeLater(() -> {
            String name = JOptionPane.showInputDialog(
                    this,
                    "Pick a player name:",
                    "BYNEST",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (name == null || name.trim().length() < 2) {
                playerName = "Player";
            } else {
                playerName = name.trim();
            }

            playerLabel.setText("Player: " + playerName);
            showHome();
        });
    }

    private void showHome() {
        stopTimer();
        cards.show(screen, "home");
    }

    private void showPractice() {
        cards.show(screen, "practice");
    }

    private void showLeaderboard() {
        updateLeaderboard();
        cards.show(screen, "leaderboard");
    }

    // ---------- Game engine ----------

    private void startGame() {
        stopTimer();

        int level = levelBox.getSelectedIndex();
        questions = new ArrayList<>();

        Set<String> seen = new HashSet<>();
        int guard = 0;

        while (questions.size() < 10 && guard++ < 500) {
            Question q = makeQuestion(level);
            if (seen.add(q.text)) {
                questions.add(q);
            }
        }

        while (questions.size() < 10) {
            questions.add(makeQuestion(level));
        }

        Collections.shuffle(questions);

        questionIndex = 0;
        score = 0;
        feedbackLabel.setText("");
        showQuestion();
    }

    private void showQuestion() {
        if (questionIndex >= questions.size()) {
            finishGame();
            return;
        }

        Question q = questions.get(questionIndex);

        questionNumberLabel.setText("Question " + (questionIndex + 1) + " / " + questions.size());
        scoreLabel.setText("Score: " + score);
        questionLabel.setText(q.text);

        List<Integer> choices = makeChoices(q.answer);
        for (int i = 0; i < 4; i++) {
            answerButtons[i].setText(String.valueOf(choices.get(i)));
            answerButtons[i].setEnabled(true);
            answerButtons[i].setForeground(TEXT);
            answerButtons[i].setBackground(BG);
        }

        feedbackLabel.setText("");
        questionStart = System.currentTimeMillis();

        gameTimer = new javax.swing.Timer(50, e -> updateTimer());
        gameTimer.start();
    }

    private void updateTimer() {
        long elapsed = System.currentTimeMillis() - questionStart;
        long limit = 10_000;
        long remaining = Math.max(0, limit - elapsed);

        timerLabel.setText(String.format("Time: %.1fs", remaining / 1000.0));
        timerBar.setValue((int) (remaining * 1000 / limit));

        if (remaining <= 0) {
            stopTimer();
            for (JButton b : answerButtons) b.setEnabled(false);
            feedbackLabel.setForeground(DANGER);
            feedbackLabel.setText("Time's up!");
            javax.swing.Timer next = new javax.swing.Timer(700, e -> {
                ((javax.swing.Timer) e.getSource()).stop();
                questionIndex++;
                showQuestion();
            });
            next.setRepeats(false);
            next.start();
        }
    }

    private void answerSelected(int index) {
        if (questionIndex >= questions.size()) return;

        stopTimer();

        Question q = questions.get(questionIndex);
        int chosen = Integer.parseInt(answerButtons[index].getText());

        for (JButton b : answerButtons) b.setEnabled(false);

        if (chosen == q.answer) {
            score++;
            answerButtons[index].setForeground(CYAN);
            feedbackLabel.setForeground(CYAN);
            feedbackLabel.setText("Correct!");
        } else {
            answerButtons[index].setForeground(DANGER);
            feedbackLabel.setForeground(DANGER);
            feedbackLabel.setText("Wrong — answer: " + q.answer);
        }

        scoreLabel.setText("Score: " + score);

        javax.swing.Timer next = new javax.swing.Timer(650, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            questionIndex++;
            showQuestion();
        });
        next.setRepeats(false);
        next.start();
    }

    private void finishGame() {
        stopTimer();

        int level = levelBox.getSelectedIndex();
        String levelName = FAMILIES[level / 3] + " · " + ((level % 3) + 1);

        leaderboard.add(new ScoreEntry(
                playerName,
                score,
                questions.size(),
                levelName
        ));

        feedbackLabel.setForeground(AMBER);
        feedbackLabel.setText("Round finished: " + score + "/" + questions.size());

        JOptionPane.showMessageDialog(
                this,
                "Round complete!\n\n" +
                        "Player: " + playerName + "\n" +
                        "Score: " + score + "/" + questions.size() + "\n" +
                        "Level: " + levelName,
                "Results",
                JOptionPane.INFORMATION_MESSAGE
        );

        updateLeaderboard();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }

    // ---------- Question generation ----------
    // Mirrors the original JS level families and tier ranges.

    private Question makeQuestion(int levelId) {
        int family = levelId / 3;
        int tier = levelId % 3;

        int[] range = getRange(family, tier);
        int a, b, answer;
        String op;

        switch (family) {
            case 0: // Easy
                op = random.nextBoolean() ? "+" : "-";
                a = pick(range[0], range[1]);
                b = pick(range[0], range[1]);
                if (op.equals("-") && b > a) {
                    int t = a; a = b; b = t;
                }
                answer = op.equals("+") ? a + b : a - b;
                return new Question(a + " " + op + " " + b, answer);

            case 1: // Medium
                op = pickOp("+", "-", "×");
                if (op.equals("×")) {
                    a = pick(range[0], range[1]);
                    b = pick(range[0], range[0] + (tier + 1) * 2);
                } else {
                    a = pick(range[0], range[1]);
                    b = pick(1, range[1]);
                    if (op.equals("-") && b > a) {
                        int t = a; a = b; b = t;
                    }
                }
                answer = calculate(a, b, op);
                return new Question(a + " " + op + " " + b, answer);

            case 2: // Hard
            case 3: // Difficult
                op = pickOp("+", "-", "×", "÷");
                if (op.equals("÷")) {
                    int divisor = pick(range[0], range[1]);
                    int quotient = pick(Math.max(2, range[2]), range[3]);
                    a = divisor * quotient;
                    return new Question(a + " ÷ " + divisor, quotient);
                }
                if (op.equals("×")) {
                    a = pick(range[0], range[1]);
                    b = pick(range[0], range[1]);
                } else {
                    a = pick(range[2], range[3]);
                    b = pick(Math.max(2, range[2] / 2), range[3]);
                    if (op.equals("-") && b > a) {
                        int t = a; a = b; b = t;
                    }
                }
                return new Question(a + " " + op + " " + b, calculate(a, b, op));

            default: // Insane
                if (random.nextDouble() < 0.5) {
                    op = pickOp("+", "-", "×", "÷");
                    if (op.equals("÷")) {
                        int divisor = pick(range[0], range[1]);
                        int quotient = pick(range[2], range[3]);
                        a = divisor * quotient;
                        return new Question(a + " ÷ " + divisor, quotient);
                    }
                    if (op.equals("×")) {
                        a = pick(range[0], range[1]);
                        b = pick(range[0], range[1]);
                    } else {
                        a = pick(range[2], range[3]);
                        b = pick(Math.max(1, range[2] / 2), range[3]);
                        if (op.equals("-") && b > a) {
                            int t = a; a = b; b = t;
                        }
                    }
                    return new Question(a + " " + op + " " + b, calculate(a, b, op));
                } else {
                    int x = pick(range[0], range[1]);
                    int y = pick(range[0], range[1]);
                    int z = pick(5, Math.max(5, range[1] / 2));
                    String op1 = random.nextBoolean() ? "+" : "-";
                    String op2 = random.nextBoolean() ? "+" : "-";

                    int value = op1.equals("+") ? x + y : x - y;
                    value = op2.equals("+") ? value + z : value - z;

                    return new Question(
                            x + " " + op1 + " " + y + " " + op2 + " " + z,
                            value
                    );
                }
        }
    }

    private int[] getRange(int family, int tier) {
        // Values correspond closely to the original JS RANGES.
        switch (family) {
            case 0: // easy
                int[][] easy = {{1,6}, {1,10}, {5,15}};
                return new int[]{easy[tier][0], easy[tier][1], easy[tier][0], easy[tier][1]};

            case 1: // medium
                int[][] medium = {{2,8,5,15}, {2,10,10,25}, {2,12,10,35}};
                return medium[tier];

            case 2: // hard
                int[][] hard = {{3,12,10,40}, {5,15,20,60}, {8,20,30,80}};
                return hard[tier];

            case 3: // difficult
                int[][] difficult = {{10,20,40,100}, {15,28,60,150}, {20,35,100,200}};
                return difficult[tier];

            default: // insane
                int[][] insane = {{15,30,100,250}, {20,35,150,350}, {25,45,200,500}};
                return insane[tier];
        }
    }

    private int calculate(int a, int b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "×": return a * b;
            case "÷": return a / b;
            default: throw new IllegalArgumentException("Unknown operator");
        }
    }

    private String pickOp(String... ops) {
        return ops[random.nextInt(ops.length)];
    }

    private int pick(int min, int max) {
        if (max < min) max = min;
        return min + random.nextInt(max - min + 1);
    }

    private List<Integer> makeChoices(int correct) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        set.add(correct);

        int[] offsets = {1, 2, 3, -1, -2, -3, 5, -5, 10, -10, 7, -7, 4, -4};
        List<Integer> shuffled = new ArrayList<>();
        for (int x : offsets) shuffled.add(x);
        Collections.shuffle(shuffled);

        for (int offset : shuffled) {
            if (set.size() >= 4) break;
            set.add(correct + offset);
        }

        while (set.size() < 4) {
            set.add(correct + pick(-15, 15));
        }

        List<Integer> result = new ArrayList<>(set);
        Collections.shuffle(result);
        return result;
    }

    // ---------- Leaderboard ----------

    private void updateLeaderboard() {
        JPanel p = null;
        for (Component c : screen.getComponents()) {
            if (c instanceof JPanel && ((JPanel) c).getClientProperty("leaderboardArea") != null) {
                p = (JPanel) c;
                break;
            }
        }

        if (p == null) return;

        JTextArea area = (JTextArea) p.getClientProperty("leaderboardArea");

        leaderboard.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.score, a.score);
            if (scoreCompare != 0) return scoreCompare;
            return Integer.compare(b.total, a.total);
        });

        StringBuilder sb = new StringBuilder();
        sb.append("RANK   PLAYER              SCORE     LEVEL\n");
        sb.append("------------------------------------------------\n");

        if (leaderboard.isEmpty()) {
            sb.append("No scores yet. Play a round!\n");
        } else {
            int limit = Math.min(20, leaderboard.size());
            for (int i = 0; i < limit; i++) {
                ScoreEntry s = leaderboard.get(i);
                sb.append(String.format(
                        "#%-5d %-19s %2d/%-7d %s%n",
                        i + 1,
                        truncate(s.player, 18),
                        s.score,
                        s.total,
                        s.level
                ));
            }
        }

        area.setText(sb.toString());
    }

    private String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

    // ---------- Data classes ----------

    private static class Question {
        final String text;
        final int answer;

        Question(String text, int answer) {
            this.text = text;
            this.answer = answer;
        }
    }

    private static class ScoreEntry {
        final String player;
        final int score;
        final int total;
        final String level;

        ScoreEntry(String player, int score, int total, String level) {
            this.player = player;
            this.score = score;
            this.total = total;
            this.level = level;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new SplitSecondMathArena().setVisible(true);
        });
    }
}
