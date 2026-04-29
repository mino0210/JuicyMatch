package cardGame.game;

import cardGame.database.RecordDAO;
import cardGame.entity.Board;
import cardGame.entity.Card;
import cardGame.entity.Player;
import cardGame.entity.Record;
import cardGame.entity.User;
import cardGame.mgr.Manageable;
import cardGame.game.components.WoodButton;
import cardGame.mgr.Manager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static cardGame.game.GameController.*;

public class GameWindow extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private Player computer;
    private Manager recordMgr;
    private Board board;
    private final ArrayList<Card> selectedCards = new ArrayList<>();
    private boolean userTurn = true;
    private JLabel statusLabel = new JLabel();
    private int currentLevel;
    private Map<Integer, Integer> knownCards = new HashMap<>();
    private JPanel userPanel = new JPanel();
    private JPanel computerPanel = new JPanel();
    private JLabel userScoreLabel;
    private Sound backBGM = new Sound();
    private Sound success = new Sound();
    private JLabel cardCountLabel;
    private boolean isProcessing = false;
    private JLabel comboLabel;
    private JLayeredPane layeredPane;
    private int comboCount = 0;
    private Timer comboTimer;
    private JPanel userCardPanel;
    private Sound btnClickSound = new Sound();
    private Sound popupClickSound = new Sound();
    private WoodButton bonusButton;
    private boolean isMuted = false;

    public GameWindow(GameController gameController, User loginedUser, Board board,
                      Player computer, Manager recordMgr, int level) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        this.board = board;
        this.computer = computer;
        this.recordMgr = recordMgr;
        this.currentLevel = level;

        setLayout(new BorderLayout());
        // setupGame() is called once from GameController.
        // Calling it here as well created two game panels and restarted Casino.wav immediately,
        // which caused the in-game BGM to stutter at the beginning.
    }

    public JPanel setupGame() {
        final int SCREEN_W = 1920;
        final int SCREEN_H = 1080;

        final int LEFT_MARGIN = 200;
        final int SIDE_PANEL_W = 200;
        final int SIDE_PANEL_H = 800; //700
        final int SIDE_PANEL_Y = 215; //155
        final int RIGHT_PANEL_X = SCREEN_W - SIDE_PANEL_W - LEFT_MARGIN;

        final int TITLE_W = 600;
        final int TITLE_H = 270;//170
        final int TITLE_X = (SCREEN_W - TITLE_W) / 2;
        final int TITLE_Y = 0;

        final int BOARD_BG_W = 900;
        final int BOARD_BG_H = 800;
        final int BOARD_BG_X = (SCREEN_W - BOARD_BG_W) / 2;
        final int BOARD_BG_Y = 215;

        JPanel mainPanel = new JPanel() {
            private BufferedImage bgImage;
            {
                try {
                    java.net.URL url = getClass().getResource(FrontImagePath + "/background.png");
                    if (url != null) {
                        bgImage = ImageIO.read(url);
                    }
                } catch (Exception e) {
                    System.err.println("배경 이미지 로드 실패: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                    g2.dispose();
                }
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));

        // 상단 좌측 버튼들: 홈 / 사운드 / 리셋
        // Top-left buttons: home / sound / reset
        JButton homeButton = createNavButton("/homeBtn.png", 60);
        homeButton.setBounds(200, 140, 80, 80);
        homeButton.addActionListener(e -> {
            btnClickSound.play("BtnClick.wav", false, -10.0f);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "メインメニューに戻りますか？",
                    "終了確認", JOptionPane.YES_NO_OPTION);
            popupClickSound.play("click.wav", false, -5.0f);
            if (confirm == JOptionPane.YES_OPTION) {
                backBGM.stop();
                btnClickSound.stop();
                btnClickSound.stopItemSound();
                gameController.switchToPanel("gameMenu", loginedUser);
            }
        });
        mainPanel.add(homeButton);

        JButton muteButton = createNavButton("/soundBtn.png", 60);
        muteButton.setBounds(260, 140, 80, 80);
        muteButton.addActionListener(e -> {
            btnClickSound.play("BtnClick.wav", false, -10.0f);
            isMuted = !isMuted;
            if (backBGM != null) {
                backBGM.setMute(isMuted);
            }
            setNavButtonIcon(muteButton, isMuted ? "/soundBtn_mute.png" : "/soundBtn.png", 60);
        });
        mainPanel.add(muteButton);

        JButton resetButton = createNavButton("/backBtn.png", 60);
        resetButton.setBounds(320, 140, 80, 80);
        resetButton.addActionListener(e -> {
            btnClickSound.play("BtnClick.wav", false, -10.0f);
            resetGame();
        });
        mainPanel.add(resetButton);

        // 상단 중앙 JuicyMatch 간판
        // Top-center JuicyMatch signboard
        JLabel titleLabel = new JLabel();
        try {
            java.net.URL titleUrl = getClass().getResource(FrontImagePath + "/ingame_title.png");
            if (titleUrl != null) {
                titleLabel.setIcon(new ImageIcon(new ImageIcon(titleUrl)
                        .getImage().getScaledInstance(TITLE_W, TITLE_H, Image.SCALE_SMOOTH)));
            } else {
                titleLabel.setText("Juicy Match");
                titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 48));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            titleLabel.setText("Juicy Match");
        }
        titleLabel.setBounds(TITLE_X, TITLE_Y, TITLE_W, TITLE_H);
        mainPanel.add(titleLabel);

        // 오른쪽 상단 플레이어 스코어 박스 (ingame_score.png)
        // Top-right player score box (ingame_score.png)
        JPanel userScoreBox = createPlayerScoreBox();
        userScoreBox.setBounds(RIGHT_PANEL_X - 25, 140, 250, 70);
        mainPanel.add(userScoreBox);

        // 좌우 세로 패널
        // Left/right vertical panels
        JPanel computerScorePanel = createScorePanel("CPU Lv." + currentLevel, false, SIDE_PANEL_W, SIDE_PANEL_H);
        computerScorePanel.setBounds(LEFT_MARGIN, SIDE_PANEL_Y, SIDE_PANEL_W, SIDE_PANEL_H);
        mainPanel.add(computerScorePanel);

        String userName = (loginedUser != null && loginedUser.getName() != null && !loginedUser.getName().isBlank())
                ? loginedUser.getName() : "PLAYER";
        JPanel userScorePanel = createScorePanel(userName, true, SIDE_PANEL_W, SIDE_PANEL_H);
        userScorePanel.setBounds(RIGHT_PANEL_X, SIDE_PANEL_Y, SIDE_PANEL_W, SIDE_PANEL_H);
        mainPanel.add(userScorePanel);

        // 카드 보드 배경
        // Card board background
        JPanel cardBoardBg = new JPanel() {
            private BufferedImage bgImage;
            {
                try {
                    java.net.URL url = getClass().getResource(FrontImagePath + "/ingame_cardboard_bg.png");
                    if (url != null) {
                        bgImage = ImageIO.read(url);
                    }
                } catch (Exception e) {
                    System.err.println("카드보드 배경 로드 실패: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                    g2.dispose();
                }
            }
        };
        cardBoardBg.setOpaque(false);
        cardBoardBg.setLayout(null);
        cardBoardBg.setBounds(BOARD_BG_X, BOARD_BG_Y, BOARD_BG_W, BOARD_BG_H);
        mainPanel.add(cardBoardBg);

        JPanel boardPanel = board.getBoardContainer();
        boardPanel.setOpaque(false);
        Dimension boardSize = board.getBoardSize();
        int boardInnerX = (BOARD_BG_W - boardSize.width) / 2;
        int boardInnerY = (BOARD_BG_H - boardSize.height) / 2 + 3;
        boardPanel.setBounds(boardInnerX, boardInnerY, boardSize.width, boardSize.height);
        cardBoardBg.add(boardPanel);

        if (comboLabel == null) comboLabel = new JLabel("", SwingConstants.CENTER);
        comboLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 60));
        comboLabel.setForeground(new Color(255, 69, 0));
        comboLabel.setVisible(false);
        comboLabel.setBounds(0, 0, BOARD_BG_W, BOARD_BG_H);
        cardBoardBg.add(comboLabel);
        cardBoardBg.setComponentZOrder(comboLabel, 0);

        this.layeredPane = new JLayeredPane();
        this.layeredPane.setBounds(BOARD_BG_X, BOARD_BG_Y, BOARD_BG_W, BOARD_BG_H);

        // 남은 카드 수 - 텍스트만 사용
        // Remaining card count - text only
        cardCountLabel = new JLabel("残りカード:" + (board != null ? board.getCardCnt() : 0));
        cardCountLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        cardCountLabel.setForeground(new Color(255, 250, 240));
        cardCountLabel.setBounds(BOARD_BG_X + 20, BOARD_BG_Y - 30, 330, 40);
        mainPanel.add(cardCountLabel);

        // 기존 로직 호환용 참조
        // References for legacy logic compatibility
        this.userPanel = userScorePanel;

        updateStatus();
        updateUserPanel();
        updateCardCnt();
        if (!backBGM.isPlaying()) {
            backBGM.play("Casino.wav", true, -20.0f);
        }
        backBGM.setMute(isMuted);

        return mainPanel;
    }

    /**
     * 좌/우 세로 점수 패널 생성
     * Create left/right vertical score panel
     */
    private JPanel createScorePanel(String playerName, boolean isUser, int panelW, int panelH) {
        JPanel panel = new JPanel() {
            private BufferedImage bgImage;
            {
                try {
                    java.net.URL url = getClass().getResource(FrontImagePath + "/ingame_sorce_panel.png");
                    if (url != null) {
                        bgImage = ImageIO.read(url);
                    }
                } catch (Exception e) {
                    System.err.println("점수 패널 배경 로드 실패: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (bgImage != null) {
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                }

                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Yu Gothic UI", Font.BOLD, 26));
                drawCenteredOutlinedText(g2, playerName, getWidth() / 2, 62,
                        new Color(255, 219, 64), new Color(70, 35, 8));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);

        JPanel matchedCardsArea = new JPanel(new GridLayout(0, 2, 5, 5));
        matchedCardsArea.setOpaque(false);

        if (isUser) {
            matchedCardsArea.setBounds(22, 90, 156, 495);
            this.userCardPanel = matchedCardsArea;
            this.userPanel = panel;
        } else {
            matchedCardsArea.setBounds(22, 90, 156, 575);
            this.computerPanel = matchedCardsArea;
        }
        panel.add(matchedCardsArea);

        if (isUser) {
            bonusButton = new WoodButton("BONUS", 156, 50);
            bonusButton.setBounds(22, panelH - 78, 156, 50);
            bonusButton.addActionListener(e -> {
                if (!isProcessing && userTurn) {
                    bonusButton.setEnabled(false);
                    bonusButton.setButtonText("使用済み");
                    useBonusItem();
                }
            });
            panel.add(bonusButton);
        }

        return panel;
    }

    /**
     * 플레이어용 상단 점수 박스 (ingame_score.png)
     */
    private JPanel createPlayerScoreBox() {
        JPanel panel = new JPanel() {
            private BufferedImage bgImage;
            {
                try {
                    java.net.URL url = getClass().getResource(FrontImagePath + "/ingame_score.png");
                    if (url != null) {
                        bgImage = ImageIO.read(url);
                    }
                } catch (Exception e) {
                    System.err.println("점수 박스 배경 로드 실패: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);

        userScoreLabel = new JLabel("スコア:0", SwingConstants.CENTER);
        userScoreLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 23));
        userScoreLabel.setForeground(new Color(255, 250, 240));
        userScoreLabel.setBounds(0, 0, 250, 70);
        panel.add(userScoreLabel);
        return panel;
    }

    private void drawCenteredOutlinedText(Graphics2D g2, String text, int centerX, int y,
                                          Color fill, Color outline) {
        FontMetrics fm = g2.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        g2.setColor(outline);
        g2.drawString(text, x - 2, y);
        g2.drawString(text, x + 2, y);
        g2.drawString(text, x, y - 2);
        g2.drawString(text, x, y + 2);
        g2.setColor(fill);
        g2.drawString(text, x, y);
    }

    private JButton createNavButton(String path, int size) {
        JButton btn = new JButton();
        setNavButtonIcon(btn, path, size);
        btn.setPreferredSize(new Dimension(50, 50));
        // 버튼 자체는 불투명 (배경 투명) - 이미지가 선명하게 보이도록
        // Button itself is opaque (transparent background) - icon shows clearly
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        // 호버 시 반투명 효과 비활성화
        // Disable hover translucent effect
        btn.setRolloverEnabled(false);
        return btn;
    }

    private void setNavButtonIcon(JButton btn, String path, int size) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(FrontImagePath + path));
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setText(null);
        } catch (Exception e) {
            btn.setIcon(null);
            btn.setText(path.replace("/", "").replace(".png", ""));
            System.err.println("이미지 로드 실패: " + path);
        }
    }

    public void gameFinished(int finalScore) {
        if (this.loginedUser == null) {
            System.out.println("ログイン情報がないため、記録を保存できません.");
            return;
        }

        Record newRecord = new Record(this.loginedUser, finalScore, currentLevel);
        RecordDAO recordDAO = new RecordDAO();
        boolean success = recordDAO.insertRecord(newRecord);

        if (success) {
            recordMgr.addMList(newRecord);
            System.out.println("記録がDBとリストに正常に保存されました.");
        }
    }

    private JScrollPane getjScrollPane(Map<String, List<Integer>> gameRecords) {
        for (Manageable m : recordMgr.mList) {
            Record record = (Record) m;
            int score = record.getScore();
            String id = record.getUser().getUsername();
            gameRecords.computeIfAbsent(id, k -> new ArrayList<>()).add(score);
        }

        List<Map.Entry<String, Integer>> totalScores = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : gameRecords.entrySet()) {
            String id = entry.getKey();
            List<Integer> scores = entry.getValue();
            int totalScore = scores.stream().mapToInt(Integer::intValue).sum();
            totalScores.add(new AbstractMap.SimpleEntry<>(id, totalScore));
        }

        totalScores.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        String[] columnNames = {"등수", "사용자", "총점"};
        Object[][] rowData = new Object[totalScores.size()][3];

        int idx = 1;
        for (int i = 0; i < totalScores.size(); i++) {
            Map.Entry<String, Integer> entry = totalScores.get(i);
            rowData[i][0] = idx++;
            rowData[i][1] = entry.getKey();
            rowData[i][2] = entry.getValue();
        }

        JTable recordTable = new JTable(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recordTable.setFillsViewportHeight(true);
        recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        recordTable.getTableHeader().setReorderingAllowed(false);

        recordTable.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        recordTable.getTableHeader().setFont(new Font("Yu Gothic UI", Font.BOLD, 18));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < recordTable.getColumnCount(); i++) {
            recordTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        return scrollPane;
    }

    private void addCardToUserArea(Card card) {
        ImageIcon icon = card.getMatchedImageIcon();
        JLabel label = new JLabel(icon);
        label.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        userCardPanel.add(label);
        userCardPanel.revalidate();
        userCardPanel.repaint();
    }

    private void addCardToComputerArea(Card card) {
        ImageIcon icon = card.getMatchedImageIcon();
        JLabel label = new JLabel(icon);
        label.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        computerPanel.add(label);
        computerPanel.revalidate();
        computerPanel.repaint();
    }

    private JPanel createItemPanel() {
        JButton itemButton = new JButton("BONUS");
        itemButton.setFont(new Font("Yu Gothic UI", Font.BOLD, 20));
        itemButton.setBackground(new Color(212, 232, 228));

        itemButton.addActionListener(e -> {
            if (!isProcessing && userTurn) {
                itemButton.setEnabled(false);
                itemButton.setText("사용 완료");
                itemButton.setBackground(Color.GRAY);

                useBonusItem();
            }
        });

        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setPreferredSize(new Dimension(220, 60));
        itemPanel.setBackground(new Color(153, 102, 51));
        itemPanel.add(itemButton, BorderLayout.CENTER);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        return itemPanel;
    }

    private JPanel createBlackPanel(Dimension size) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 235));
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);
        panel.setMaximumSize(size);
        return panel;
    }

    public void reSetupCardListeners(){
        for (Card card : board.getCards()) {
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleCardClick(card);
                }
            });
        }
    }

    public void setupCardListeners() {
        for (Card card : board.getCards()) {
            card.reveal(false);
        }

        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Card card : board.getCards()) {
                    if (!card.isMatched()) {
                        card.flip();
                    }
                }
                board.getBoardContainer().repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();

        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                card.reveal(false);
            }
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleCardClick(card);
                }
            });
        }
    }

    private void handleCardClick(Card card) {
        if (isProcessing || !userTurn || card.isMatched() || selectedCards.contains(card) || selectedCards.size() >= 2) {
            return;
        }

        card.reveal();
        rememberCard(card);
        selectedCards.add(card);

        if (selectedCards.size() == 2) {
            isProcessing = true;
            Timer timer = new Timer(500, e -> checkMatch());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void updateUserPanel(){
        if (userScoreLabel != null) {
            userScoreLabel.setText("スコア:" + loginedUser.getScore());
        }
    }

    private void updateCardCnt(){
        if (cardCountLabel != null) {
            cardCountLabel.setText("残りカード:" + board.getCardCnt());
        }
    }

    private void continueSuccess(Player player1, Player player2, Card card) {
        player2.resetCount();
        player1.incrementScore(card.getNumber() * player1.getCount());

        if (userTurn){
            addCardToUserArea(card);
            updateUserPanel();
        }
        else
            addCardToComputerArea(card);

        statusLabel.setText(player1.getName() + "가 성공 이어서 선택!");
        updateCardCnt();
        player1.incrementCount();
    }

    private void checkMatch() {
        if (selectedCards.size() < 2) return;

        Card c1 = selectedCards.get(0);
        Card c2 = selectedCards.get(1);

        if (c1.getId() == c2.getId()) {
            c1.setMatched(true);
            c2.setMatched(true);
            isProcessing = false;

            updateCombo(true);

            if (userTurn) {
                int baseScore = 100;
                int bonus = (comboCount - 1) * 20;
                int totalIncrease = baseScore + Math.max(0, bonus);

                loginedUser.addScore(totalIncrease);
                addCardToUserArea(c1);
                addCardToUserArea(c2);
                updateUserPanel();
            } else {
                int compBaseScore = 100;
                int compBonus = (comboCount - 1) * 20;
                computer.addScore(compBaseScore + Math.max(0, compBonus));

                addCardToComputerArea(c1);
                addCardToComputerArea(c2);
            }

            board.removeCard(c1);
            board.removeCard(c2);
            updateCardCnt();
            selectedCards.clear();

            board.getBoardContainer().revalidate();
            board.getBoardContainer().repaint();
            checkGameEnd();

            if (!userTurn && !board.isAllMatched()) {
                Timer t = new Timer(400, e -> computerTurn());
                t.setRepeats(false);
                t.start();
            }

        } else {
            isProcessing = true;

            Timer flipBackTimer = new Timer(300, e -> {
                c1.flip();
                c2.flip();
                selectedCards.clear();

                updateCombo(false);

                userTurn = !userTurn;
                updateStatus();
                board.getBoardContainer().repaint();

                if (!userTurn && !board.isAllMatched()) {
                    Timer computerThinkingTimer = new Timer(300, ev -> {
                        isProcessing = false;
                        computerTurn();
                    });
                    computerThinkingTimer.setRepeats(false);
                    computerThinkingTimer.start();
                } else {
                    isProcessing = false;
                }
            });
            flipBackTimer.setRepeats(false);
            flipBackTimer.start();
        }
    }

    private void processMatch(Card firstCard, Card secondCard) {
        firstCard.match(userTurn);
        secondCard.match(userTurn);

        if (userTurn) {
            continueSuccess(loginedUser, computer, firstCard);
            success.play("success_match.wav", false, -10.0f);
        } else {
            continueSuccess(computer, loginedUser, firstCard);
            success.play("success_match.wav", false, -10.0f);
        }

        board.removeCard(firstCard);
        board.removeCard(secondCard);

        updateCombo(true);
        selectedCards.clear();
        checkGameEnd();

        if (!userTurn) {
            computerTurn();
        }
    }

    private void processMismatch(Card firstCard, Card secondCard) {
        loginedUser.resetCount();
        computer.resetCount();

        Timer timer = new Timer(1000, e -> {
            if (!firstCard.isMatched())
                firstCard.hide();
            if (!secondCard.isMatched())
                secondCard.hide();

            selectedCards.clear();
            userTurn = !userTurn;
            updateCombo(false);
            updateStatus();

            if (!userTurn) {
                Timer computerTurnTimer = new Timer(1250, event -> computerTurn());
                computerTurnTimer.setRepeats(false);
                computerTurnTimer.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateStatus() {
        try {
            if (userTurn) {
                statusLabel.setText(loginedUser.getName() + " のターン");
            } else {
                statusLabel.setText(computer.getName() + " のターン");
            }
        } catch (Exception ignored) {}

        try {
            if (userPanel != null) {
                userPanel.revalidate();
                userPanel.repaint();
            }
            if (computerPanel != null) {
                computerPanel.revalidate();
                computerPanel.repaint();
            }
        } catch (Exception ignored) {}
    }

    private void checkGameEnd() {
        if (board.isAllMatched()) {
            if (backBGM != null) backBGM.stop();

            int finalScore = loginedUser.getScore();

            Record newRecord = new Record(loginedUser, finalScore, GameController.level);
            new RecordDAO().insertRecord(newRecord);

            JOptionPane.showMessageDialog(gameController,
                    "おめでとうございます！すべてのカードを揃えました。\n최종 スコア: " + finalScore,
                    "ゲーム終了", JOptionPane.INFORMATION_MESSAGE);
            popupClickSound.play("click.wav", false, -5.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        }
    }

    private boolean isAllMatched() {
        boolean allMatched = true;
        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                allMatched = false;
                break;
            }
        }
        return allMatched;
    }

    private void computerTurn() {
        if (userTurn || board.isAllMatched() || isProcessing) return;

        int[] smartChoices = getSmartChoices();
        int[] finalChoices;

        if (smartChoices != null) {
            finalChoices = smartChoices;
        } else {
            finalChoices = getComputerChoices();
        }

        if (finalChoices == null) return;

        Card card1 = board.getCard(finalChoices[0]);
        if (card1 != null) {
            card1.reveal();
            rememberCard(card1);
            selectedCards.add(card1);
        }

        Timer t2 = new Timer(800, e -> {
            Card card2 = board.getCard(finalChoices[1]);
            if (card2 != null) {
                card2.reveal();
                rememberCard(card2);
                selectedCards.add(card2);
                checkMatch();
            }
        });
        t2.setRepeats(false);
        t2.start();
    }

    private int[] getSmartChoices() {
        List<Integer> keys = new ArrayList<>(knownCards.keySet());

        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                int idx1 = keys.get(i);
                int idx2 = keys.get(j);

                if (knownCards.get(idx1).equals(knownCards.get(idx2))) {
                    if (!board.getCard(idx1).isMatched() && !board.getCard(idx2).isMatched()) {
                        return new int[]{idx1, idx2};
                    }
                }
            }
        }
        return null;
    }

    private int[] getComputerChoices() {
        List<Card> allCards = board.getCards();
        List<Integer> availableIndices = new ArrayList<>();

        for (int i = 0; i < allCards.size(); i++) {
            if (!allCards.get(i).isMatched()) {
                availableIndices.add(i);
            }
        }

        if (availableIndices.size() < 2) return null;

        Collections.shuffle(availableIndices);
        return new int[]{availableIndices.get(0), availableIndices.get(1)};
    }

    private void rememberCard(Card card) {
        if (currentLevel == 1) return;

        int index = board.getCards().indexOf(card);
        int value = card.getId();

        if (currentLevel == 2) {
            if (Math.random() > 0.5) knownCards.put(index, value);
        } else if (currentLevel >= 3) {
            knownCards.put(index, value);
        }
    }

    private String createCombinationKey(Card card1, Card card2) {
        int id1 = card1.getNumber();
        int id2 = card2.getNumber();
        return id1 < id2 ? id1 + "," + id2 : id2 + "," + id1;
    }

    private void initComboLabel() {
        comboLabel.setFont(new Font("Serif", Font.BOLD, 60));
        comboLabel.setForeground(new Color(255, 127, 0));
        comboLabel.setVisible(false);
        add(comboLabel, BorderLayout.NORTH);
    }

    private void updateCombo(boolean success) {
        if (success) {
            comboCount++;
            showComboEffect(comboCount + " COMBO!");
        } else {
            comboCount = 0;
            if (comboLabel != null) comboLabel.setVisible(false);
        }
    }

    private void showComboEffect(String text) {
        if (comboLabel == null) return;
        if (comboTimer != null && comboTimer.isRunning()) comboTimer.stop();

        comboLabel.setText("<html><div style='text-align: center; color: #FF4500; font-size:20px;'>" + text + "</div></html>");
        comboLabel.setVisible(true);

        comboTimer = new Timer(400, e -> comboLabel.setVisible(false));
        comboTimer.setRepeats(false);
        comboTimer.start();
    }

    private void resetGame() {
        btnClickSound.stopItemSound();
        Object[] options = {"はい", "いいえ"};
        int confirm = JOptionPane.showOptionDialog(
                gameController,
                "ゲームをリセットしますか？",
                "リセット確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        popupClickSound.play("click.wav", false, -5.0f);

        if (confirm == JOptionPane.YES_OPTION) {
            board.resetBoard();
            selectedCards.clear();
            comboCount = 0;
            updateCombo(false);
            loginedUser.resetScore();
            computer.resetScore();
            userTurn = true;

            resetPlayerPanels();
            resetBonusButtonState();

            updateUserPanel();
            updateCardCnt();
            updateStatus();
            setupCardListeners();

            backBGM.stop();
            if (!backBGM.isPlaying()) {
                backBGM.play("Casino.wav", true, -20.0f);
            }
            backBGM.setMute(isMuted);

            JOptionPane.showMessageDialog(
                    gameController,
                    "ゲームをリセットしました。",
                    "リセット完了",
                    JOptionPane.INFORMATION_MESSAGE
            );

            popupClickSound.play("click.wav", false, -5.0f);
        }
        knownCards.clear();
    }

    private void resetPlayerPanels() {
        if (userCardPanel != null) {
            userCardPanel.removeAll();
            userCardPanel.revalidate();
            userCardPanel.repaint();
        }

        if (computerPanel != null) {
            computerPanel.removeAll();
            computerPanel.revalidate();
            computerPanel.repaint();
        }
    }

    private void resetBonusButtonState() {
        try {
            if (bonusButton != null) {
                bonusButton.setEnabled(true);
                bonusButton.setButtonText("BONUS");
                bonusButton.revalidate();
                bonusButton.repaint();
            }
        } catch (Exception e) {
            System.err.println("보너스 버튼 리셋 실패: " + e.getMessage());
        }
    }

    private void useBonusItem() {
        btnClickSound.play("BtnClick.wav", false, -10.0f);

        if (!userTurn || isProcessing) return;

        try {
            if (bonusButton != null) {
                bonusButton.setEnabled(false);
                bonusButton.setButtonText("使用済み");
            }
        } catch (Exception ignored) {}

        isProcessing = true;
        btnClickSound.playItemSound("clock_sound.wav", -10.0f);

        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                card.reveal(false);
            }
        }
        board.getBoardContainer().repaint();

        Timer itemTimer = new Timer(3000, e -> {
            for (Card card : board.getCards()) {
                if (!card.isMatched()) {
                    card.flip();
                }
            }
            board.getBoardContainer().repaint();
            btnClickSound.stopItemSound();
            isProcessing = false;
        });
        itemTimer.setRepeats(false);
        itemTimer.start();
    }
}
