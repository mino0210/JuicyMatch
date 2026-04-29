package cardGame.game;

import cardGame.database.RecordDAO;
import cardGame.entity.Board;
import cardGame.entity.Card;
import cardGame.entity.Player;
import cardGame.entity.Record;
import cardGame.entity.User;
import cardGame.mgr.Manageable;
import cardGame.game.components.WoodButton;
import cardGame.game.config.GameWindowLayout;
import cardGame.game.ui.GameIconButtonFactory;
import cardGame.game.ui.GameImagePanel;
import cardGame.game.ui.InGameScoreBox;
import cardGame.game.ui.MatchedScorePanel;
import cardGame.mgr.Manager;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static cardGame.game.GameController.*;

/**
 * 인게임 화면과 카드 매칭 흐름을 담당하는 메인 패널입니다.
 * Main panel responsible for the in-game screen and card matching flow.
 */
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
        
        
        
    }

    /**
     * 인게임 UI를 생성하고 카드 보드, 점수 패널, 사운드를 초기화합니다.
     * Builds the in-game UI and initializes the board, score panels, and sound.
     */
    public JPanel setupGame() {
        final int SCREEN_W = GameWindowLayout.SCREEN_W;
        final int SCREEN_H = GameWindowLayout.SCREEN_H;
        final int SIDE_PANEL_W = GameWindowLayout.SIDE_PANEL_W;
        final int SIDE_PANEL_H = GameWindowLayout.SIDE_PANEL_H;
        final int SIDE_PANEL_Y = GameWindowLayout.SIDE_PANEL_Y;
        final int RIGHT_PANEL_X = GameWindowLayout.RIGHT_PANEL_X;
        final int TITLE_W = GameWindowLayout.TITLE_W;
        final int TITLE_H = GameWindowLayout.TITLE_H;
        final int TITLE_X = GameWindowLayout.TITLE_X;
        final int TITLE_Y = GameWindowLayout.TITLE_Y;
        final int BOARD_BG_W = GameWindowLayout.BOARD_BG_W;
        final int BOARD_BG_H = GameWindowLayout.BOARD_BG_H;
        final int BOARD_BG_X = GameWindowLayout.BOARD_BG_X;
        final int BOARD_BG_Y = GameWindowLayout.BOARD_BG_Y;

        JPanel mainPanel = new GameImagePanel("/background.png");
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));

        
        
        JButton homeButton = GameIconButtonFactory.create("/homeBtn.png", 60);
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

        JButton muteButton = GameIconButtonFactory.create("/soundBtn.png", 60);
        muteButton.setBounds(260, 140, 80, 80);
        muteButton.addActionListener(e -> {
            btnClickSound.play("BtnClick.wav", false, -10.0f);
            isMuted = !isMuted;
            if (backBGM != null) {
                backBGM.setMute(isMuted);
            }
            GameIconButtonFactory.setIcon(muteButton, isMuted ? "/soundBtn_mute.png" : "/soundBtn.png", 60);
        });
        mainPanel.add(muteButton);

        JButton resetButton = GameIconButtonFactory.create("/backBtn.png", 60);
        resetButton.setBounds(320, 140, 80, 80);
        resetButton.addActionListener(e -> {
            btnClickSound.play("BtnClick.wav", false, -10.0f);
            resetGame();
        });
        mainPanel.add(resetButton);

        
        
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

        
        
        InGameScoreBox userScoreBox = new InGameScoreBox();
        userScoreLabel = userScoreBox.getScoreLabel();
        userScoreBox.setBounds(RIGHT_PANEL_X - 25, 140, 250, 70);
        mainPanel.add(userScoreBox);

        
        
        MatchedScorePanel computerScorePanel = new MatchedScorePanel("CPU Lv." + currentLevel, false, SIDE_PANEL_W, SIDE_PANEL_H, () -> {});
        computerPanel = computerScorePanel.getMatchedCardsArea();
        computerScorePanel.setBounds(GameWindowLayout.LEFT_MARGIN, SIDE_PANEL_Y, SIDE_PANEL_W, SIDE_PANEL_H);
        mainPanel.add(computerScorePanel);

        String userName = (loginedUser != null && loginedUser.getName() != null && !loginedUser.getName().isBlank())
                ? loginedUser.getName() : "PLAYER";
        MatchedScorePanel userScorePanel = new MatchedScorePanel(userName, true, SIDE_PANEL_W, SIDE_PANEL_H, () -> {
            if (!isProcessing && userTurn) {
                bonusButton.setEnabled(false);
                bonusButton.setButtonText("使用済み");
                useBonusItem();
            }
        });
        userCardPanel = userScorePanel.getMatchedCardsArea();
        bonusButton = userScorePanel.getBonusButton();
        userScorePanel.setBounds(RIGHT_PANEL_X, SIDE_PANEL_Y, SIDE_PANEL_W, SIDE_PANEL_H);
        mainPanel.add(userScorePanel);

        
        
        JPanel cardBoardBg = new GameImagePanel("/ingame_cardboard_bg.png");
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

        
        
        cardCountLabel = new JLabel("残りカード:" + (board != null ? board.getCardCnt() : 0));
        cardCountLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        cardCountLabel.setForeground(new Color(255, 250, 240));
        cardCountLabel.setBounds(BOARD_BG_X + 20, BOARD_BG_Y - 30, 330, 40);
        mainPanel.add(cardCountLabel);

        
        
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

    /**
     * 선택된 두 카드의 일치 여부를 판정하고 턴, 점수, 콤보를 갱신합니다.
     * Checks whether two selected cards match and updates turn, score, and combo state.
     */
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

    /**
     * CPU 난이도에 맞춰 기억한 카드 또는 무작위 카드 두 장을 선택합니다.
     * Selects two cards for the CPU using memory by level or random choice.
     */
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

    /**
     * 현재 게임 상태를 초기화하고 같은 화면에서 새 게임을 시작합니다.
     * Resets the current game state and starts a new game on the same screen.
     */
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

    /**
     * 보너스 버튼 사용 시 일정 시간 동안 남은 카드를 모두 공개합니다.
     * Reveals all remaining cards for a short time when the bonus button is used.
     */
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
