package cardGame.game;

import cardGame.database.RecordDAO;
import cardGame.entity.Record;
import cardGame.entity.*;
import cardGame.mgr.Manageable;
import cardGame.mgr.Manager;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;

import static cardGame.game.GameController.*;

public class GameWindow extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private Player computer;
    private Manager recordMgr;
    private Board board;
    private final ArrayList<Card> selectedCards = new ArrayList<>(); // 카드 2개 1번째 2번째 저장 get(0), get(1)로 확인
    private boolean userTurn = true; // user 먼저 시작
    private JLabel statusLabel = new JLabel();
    private int currentLevel;
    private Map<Integer, Integer> knownCards = new HashMap<>();
    private JPanel userPanel = new JPanel();
    private JPanel computerPanel = new JPanel();
    private JLabel userScoreLabel;
    private Sound backBGM = new Sound(); // 배경음악
    private Sound success = new Sound(); // 배경음악
    private JLabel cardCountLabel;
    private int bonusUsesRemaining; // 남은 보너스 사용 횟수
    private HashSet<String> computerCombi = new HashSet<>();
    private boolean isProcessing = false; // 카드가 뒤집히는 중인지 확인하는 플래그
    private JLabel comboLabel; // 이 한 줄이 반드시 필요합니다!
    private JLayeredPane layeredPane; // 화면에 뜰 콤보 라벨
    private int comboCount = 0; // 연속 맞추기 카운트
    private JPanel userCardPanel;

    public GameWindow(GameController gameController, User loginedUser, Board board,
                      Player computer, Manager recordMgr, int level) { // level 추가
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        this.board = board;
        this.computer = computer;
        this.recordMgr = recordMgr;
        this.currentLevel = level; // 받아온 레벨을 클래스 변수에 저장

        // 초기화 로직들...
        setupGame();
    }

    public JPanel setupGame() {
        // 1. 메인 패널 레이아웃 및 배경 설정
        JPanel mainPanel = new JPanel(new BorderLayout());
        Color themeBgColor = new Color(245, 245, 235);
        mainPanel.setBackground(themeBgColor);

        // 여백 패널 (테두리 효과)
        mainPanel.add(createBlackPanel(new Dimension(getWidth(), 20)), BorderLayout.SOUTH);
        mainPanel.add(createBlackPanel(new Dimension(20, getHeight())), BorderLayout.WEST);
        mainPanel.add(createBlackPanel(new Dimension(20, getHeight())), BorderLayout.EAST);

        // --- [컴퓨터 영역: 왼쪽] ---
        JPanel computerContainer = new JPanel();
        computerContainer.setLayout(new BoxLayout(computerContainer, BoxLayout.Y_AXIS));
        computerContainer.setBackground(themeBgColor);
        computerContainer.setPreferredSize(new Dimension(220, 1040));

        JPanel computerNamePanel = new JPanel(new BorderLayout());
        computerNamePanel.setMaximumSize(new Dimension(220, 60));
        computerNamePanel.setBackground(themeBgColor);

        JButton homeButton = createNavButton("/home.png", 40);
        homeButton.addActionListener(e -> {
            backBGM.Stop_Sound();
            gameController.switchToPanel("gameMenu", loginedUser);
            loginedUser.resetScore();
        });

        JButton rankingButton = createNavButton("/note.png", 45);
        rankingButton.addActionListener(e -> {
            Map<String, List<Integer>> gameRecords = new HashMap<>();
            JScrollPane scrollPane = getjScrollPane(gameRecords);
            JOptionPane.showMessageDialog(gameController, scrollPane, "기록 보기", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton resetButton = createNavButton("/reset.png", 60);
        resetButton.addActionListener(e -> resetGame());

        computerNamePanel.add(homeButton, BorderLayout.WEST);
        computerNamePanel.add(rankingButton, BorderLayout.CENTER);
        computerNamePanel.add(resetButton, BorderLayout.EAST);

        computerPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        computerPanel.setBackground(new Color(147, 191, 133));
        JScrollPane computerScroll = new JScrollPane(computerPanel);
        computerScroll.setBorder(null);

        computerContainer.add(computerNamePanel);
        computerContainer.add(Box.createVerticalStrut(10));
        computerContainer.add(computerScroll);

        // --- [게임 보드 영역: 중앙] ---
        JPanel boardPanelContainer = new JPanel(new BorderLayout());
        boardPanelContainer.setBackground(themeBgColor);

        // 타이틀 영역
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(themeBgColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel();
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(FrontImagePath + "/title.png")));
        titleLabel.setIcon(new ImageIcon(originalIcon.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH)));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 보드 본체 (JLayeredPane을 사용하여 콤보 라벨을 보드 위에 겹침)
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(themeBgColor);

        cardCountLabel = new JLabel("남은 카드: " + board.getCardCnt(), SwingConstants.LEFT);
        cardCountLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        cardCountLabel.setForeground(new Color(11, 102, 74));

        // 중앙 팝업용 콤보 라벨 초기화
        comboLabel = new JLabel("", SwingConstants.CENTER);
        comboLabel.setFont(new Font("맑은 고딕", Font.BOLD, 45));
        comboLabel.setForeground(new Color(255, 69, 0)); // 주황색
        comboLabel.setVisible(false);

        JPanel boardPanel = board.getBoardContainer();
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // 레이어 설정: 보드는 바닥(DEFAULT), 콤보는 위(PALETTE)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(comboLabel, JLayeredPane.PALETTE_LAYER);

        middlePanel.add(cardCountLabel, BorderLayout.NORTH);
        middlePanel.add(layeredPane, BorderLayout.CENTER);

        boardPanelContainer.add(titlePanel, BorderLayout.NORTH);
        boardPanelContainer.add(middlePanel, BorderLayout.CENTER);

        // --- [유저 영역: 오른쪽] ---
        JPanel userContainer = new JPanel();
        userContainer.setLayout(new BoxLayout(userContainer, BoxLayout.Y_AXIS));
        userContainer.setBackground(themeBgColor);
        userContainer.setPreferredSize(new Dimension(220, 1020));

        JPanel userNamePanel = new JPanel(new BorderLayout());
        userNamePanel.setMaximumSize(new Dimension(220, 40));
        userNamePanel.setBackground(new Color(147, 191, 133));

        userScoreLabel = new JLabel("점수: " + loginedUser.getScore(), SwingConstants.CENTER);
        userScoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        userNamePanel.add(userScoreLabel, BorderLayout.CENTER);

        userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(new Color(147, 191, 133));

        userCardPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        userCardPanel.setBackground(new Color(147, 191, 133));
        JScrollPane userScroll = new JScrollPane(userCardPanel);
        userScroll.setBorder(null);

        JPanel itemPanel = createItemPanel(); // 하단 보너스 버튼 (1회 제한 로직 포함)

        userPanel.add(userScroll, BorderLayout.CENTER);
        userPanel.add(itemPanel, BorderLayout.SOUTH);

        userContainer.add(userNamePanel);
        userContainer.add(Box.createVerticalStrut(10));
        userContainer.add(userPanel);

        // --- 전체 배치 및 리사이즈 설정 ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBackground(themeBgColor);
        centerPanel.add(computerContainer);
        centerPanel.add(Box.createHorizontalStrut(20));
        centerPanel.add(boardPanelContainer);
        centerPanel.add(Box.createHorizontalStrut(20));
        centerPanel.add(userContainer);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int bw = Math.max(800, mainPanel.getWidth() - 480);
                int bh = mainPanel.getHeight() - 250;

                // 보드판과 콤보 라벨의 크기를 레이어 크기에 맞춤
                boardPanel.setBounds(0, 0, bw, bh);
                comboLabel.setBounds(0, 0, bw, bh);

                layeredPane.setPreferredSize(new Dimension(bw, bh));
                mainPanel.revalidate();
            }
        });

        updateStatus();
        backBGM.Sound(SoundPath + "/Casino.wav", true, -20.0f);

        return mainPanel;
    }

    // 헬퍼 메서드: 아이콘 버튼 생성 단순화
    private JButton createNavButton(String path, int size) {
        ImageIcon icon = new ImageIcon(getClass().getResource(FrontImagePath + path));
        Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(img));
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    public void gameFinished(int finalScore) {
        if (this.loginedUser == null) {
            System.out.println("로그인 정보가 없어 기록을 저장할 수 없습니다.");
            return;
        }

        // 1. DB 저장을 위한 객체 생성
        Record newRecord = new Record(this.loginedUser, finalScore, currentLevel);
        // 2. DB에 실제로 저장
        RecordDAO recordDAO = new RecordDAO();
        boolean success = recordDAO.insertRecord(newRecord);

        if (success) {
            // 3. 메모리에도 추가 (랭킹 화면 즉시 반영용)
            recordMgr.addMList(newRecord);
            System.out.println("기록이 DB와 리스트에 성공적으로 저장되었습니다.");
        }
    }

    private JScrollPane getjScrollPane(Map<String, List<Integer>> gameRecords) {
        // recordMgr.mList에서 각 플레이어의 점수 정보를 gameRecords에 저장
        for (Manageable m : recordMgr.mList) {
            Record record = (Record) m;
            int score = record.getScore();
            String id = record.getUser().getUsername();
            gameRecords.computeIfAbsent(id, k -> new ArrayList<>()).add(score);
        }

        // 총점 계산
        List<Map.Entry<String, Integer>> totalScores = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : gameRecords.entrySet()) {
            String id = entry.getKey();
            List<Integer> scores = entry.getValue();
            int totalScore = scores.stream().mapToInt(Integer::intValue).sum();
            totalScores.add(new AbstractMap.SimpleEntry<>(id, totalScore));
        }

        // 내림차순 정렬: 총점에 따라 정렬
        totalScores.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        // JTable에 사용할 데이터 준비
        String[] columnNames = {"등수", "사용자", "총점"};
        Object[][] rowData = new Object[totalScores.size()][3];

        int idx = 1;
        for (int i = 0; i < totalScores.size(); i++) {
            Map.Entry<String, Integer> entry = totalScores.get(i);
            rowData[i][0] = idx++; // 등수
            rowData[i][1] = entry.getKey(); // 사용자 이름
            rowData[i][2] = entry.getValue(); // 총점
        }

        // 테이블 생성
        JTable recordTable = new JTable(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 내용 수정 불가
            }
        };

        // 테이블 설정 (보기 좋게 설정)
        recordTable.setFillsViewportHeight(true);
        recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        recordTable.getTableHeader().setReorderingAllowed(false); // 헤더 순서 변경 방지

        // 글씨 크기 조정
        recordTable.setFont(new Font("맑은 고딕", Font.BOLD, 16));  // 글씨 크기와 폰트 설정
        recordTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 18)); // 헤더 폰트 크기

        // 셀 내용 중앙 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < recordTable.getColumnCount(); i++) {
            recordTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // JScrollPane로 테이블 감싸기
        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setPreferredSize(new Dimension(600, 400)); // 스크롤 영역 크기 설정
        return scrollPane;
    }

    private void addCardToUserArea(Card card) {
        JLabel label = new JLabel(card.getMatchedImageIcon());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        // [수정됨] 형변환 삭제하고 바로 추가
        userCardPanel.add(label);

        userCardPanel.revalidate();
        userCardPanel.repaint();
    }

    private void addCardToComputerArea(Card card) {
        JLabel label = new JLabel(card.getMatchedImageIcon());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        computerPanel.add(label);

        computerPanel.revalidate();
        computerPanel.repaint();
    }

    private JPanel createItemPanel() {
        JButton itemButton = new JButton("BONUS (1회)");
        itemButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        itemButton.setBackground(new Color(212, 232, 228));

        itemButton.addActionListener(e -> {
            if (!isProcessing && userTurn) {
                itemButton.setEnabled(false); // 즉시 비활성화
                itemButton.setText("사용 완료");
                itemButton.setBackground(Color.GRAY);

                useBonusItem(); // 기존 로직 실행
            }
        });

        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setPreferredSize(new Dimension(220, 60));
        itemPanel.setBackground(new Color(153, 102, 51));
        itemPanel.add(itemButton, BorderLayout.CENTER);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        return itemPanel;
    }

    // 레벨에 따라 보너스 사용 횟수 설정
    private void setBonusUsesByLevel() {
        if (rows == 2 && cols == 4) { // 1 레벨
            bonusUsesRemaining = 1;
        } else if (rows == 3 && cols == 4) { // 2 레벨
            bonusUsesRemaining = 2;
        } else if (rows == 4 && cols == 4) { // 3 레벨
            bonusUsesRemaining = 3;
        } else if (rows == 4 && cols == 5) { // 4 레벨
            bonusUsesRemaining = 4;
        } else if (rows == 4 && cols == 6) { // 5 레벨
            bonusUsesRemaining = 5;
        } else {
            bonusUsesRemaining = 0; // 기본값
        }
    }

    // 검은 패널 생성 메서드
    private JPanel createBlackPanel(Dimension size) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 235));
        panel.setPreferredSize(size); // 고정된 크기 설정
        panel.setMinimumSize(size); // 최소 크기 설정
        panel.setMaximumSize(size); // 최대 크기 설정
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
        board.showAllCard();

        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.hideAllCard();
            }
        });
        timer.setRepeats(false);
        timer.start();

        for (Card card : board.getCards()) {
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleCardClick(card);
                }
            });
        }
    }

    private void handleCardClick(Card card) {
        // isProcessing 확인을 추가하여 광클로 인한 버그 원천 차단
        if (isProcessing || !userTurn || card.isMatched() || selectedCards.contains(card) || selectedCards.size() >= 2) {
            return;
        }

        card.reveal(); // 카드를 뒤집음
        rememberCard(card);
        selectedCards.add(card);

        if (selectedCards.size() == 2) {
            isProcessing = true; // 두 장을 고르는 즉시 게임판 클릭 잠금

            // 유저가 두 번째 카드를 눈으로 확인할 수 있도록 0.5초 대기 후 검사
            Timer timer = new Timer(500, e -> checkMatch());
            timer.setRepeats(false);
            timer.start();
        }
    }



    private void updateUserPanel(){
        userScoreLabel.setText(" 점수: " + loginedUser.getScore());
    }
    private void updateCardCnt(){
        cardCountLabel.setText("남은 카드: " + board.getCardCnt());
    }

    private void continueSuccess(Player player1, Player player2, Card card) {
        player2.resetCount();
        player1.incrementScore(card.getNumber() * player1.getCount()); // 난이도 별로 // 점수 획득

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
            // [성공 로직]
            c1.setMatched(true);
            c2.setMatched(true);

            if (userTurn) {
                loginedUser.addScore(100);
                addCardToUserArea(c1);
                updateUserPanel();
                updateCombo(true); // 콤보 증가 및 효과 호출
            } else {
                computer.addScore(100);
                addCardToComputerArea(c1);
            }

            board.removeCard(c1);
            board.removeCard(c2);
            updateCardCnt();
            selectedCards.clear();

            // ★ 프리징 해결: 성공 시에도 즉시 플래그 해제
            isProcessing = false;

            board.getBoardContainer().revalidate();
            board.getBoardContainer().repaint();

            checkGameEnd();

            if (!userTurn && !board.isAllMatched()) {
                Timer t = new Timer(200, e -> computerTurn());
                t.setRepeats(false);
                t.start();
            }
        } else {
            // [실패 로직]
            isProcessing = true;
            Timer flipBackTimer = new Timer(200, e -> {
                c1.flip();
                c2.flip();
                selectedCards.clear();
                userTurn = !userTurn;
                updateCombo(false);
                updateStatus();

                board.getBoardContainer().revalidate();
                board.getBoardContainer().repaint();

                // ★ 실패 시 타이머 종료 후 플래그 해제
                isProcessing = false;

                if (!userTurn && !board.isAllMatched()) {
                    computerTurn();
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
            success.Sound(SoundPath + "/success_match.wav", false, -10.0f);
        } else {
            continueSuccess(computer, loginedUser, firstCard);
            success.Sound(SoundPath+ "/success_match.wav", false, -10.0f);
        }

        // 보드에서 카드 제거
        board.removeCard(firstCard);
        board.removeCard(secondCard);

        updateCombo(true); // 콤보 증가
        //updateScore();
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
            userTurn = !userTurn; // 턴 전환
            updateCombo(false); // 콤보 초기화
            updateStatus();

            if (!userTurn) {
                Timer computerTurnTimer = new Timer(1250, event -> computerTurn());
                computerTurnTimer.setRepeats(false);
                computerTurnTimer.start();
            }
        });
        timer.setRepeats(false); // 한 번만 실행
        timer.start();
    }


    private void updateStatus() {
        if (userTurn) {
            // 유저 턴: 유저 패널 테두리를 노란색으로, 컴퓨터 패널 테두리를 기본색으로
            userPanel.setBorder(
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 150, 0), 4), // 외부
                            // 테두리
                            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), // 내부 테두리 제거
                                    loginedUser.getName(), TitledBorder.CENTER, TitledBorder.TOP, // 제목을 상단에 정렬
                                    new Font("맑은 고딕", Font.BOLD, 30), new Color(11, 102, 74))));

            computerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), // 외부
                    // 테두리
                    BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), // 내부 테두리 제거
                            computer.getName(), TitledBorder.CENTER, TitledBorder.TOP, // 제목을 상단에 정렬
                            new Font("맑은 고딕", Font.BOLD, 20), new Color(11, 102, 74))));
        } else {
            // 컴퓨터 턴: 컴퓨터 패널 테두리를 노란색으로, 유저 패널 테두리를 기본색으로
            computerPanel.setBorder(
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 150, 0), 4), // 외부
                            // 테두리
                            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), // 내부 테두리 제거
                                    computer.getName(), TitledBorder.CENTER, TitledBorder.TOP, // 제목을 상단에 정렬
                                    new Font("맑은 고딕", Font.BOLD, 40), new Color(11, 102, 74))));

            userPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), // 외부
                    // 테두리
                    BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), // 내부 테두리 제거
                            loginedUser.getName(), TitledBorder.CENTER, TitledBorder.TOP, // 제목을 상단에 정렬
                            new Font("맑은 고딕", Font.BOLD, 20), new Color(11, 102, 74))));
        }

        // UI 갱신
        userPanel.revalidate();
        userPanel.repaint();
        computerPanel.revalidate();
        computerPanel.repaint();
    }


    private void checkGameEnd() {
        if (board.isAllMatched()) {
            if (backBGM != null) backBGM.Stop_Sound();

            int finalScore = loginedUser.getScore();

            // DB에 결과 저장 (level 데이터 포함)
            Record newRecord = new Record(loginedUser, finalScore, GameController.level);
            new RecordDAO().insertRecord(newRecord);

            // 알림창 표시
            JOptionPane.showMessageDialog(gameController,
                    "축하합니다! 모든 카드를 맞췄습니다.\n최종 점수: " + finalScore,
                    "게임 종료", JOptionPane.INFORMATION_MESSAGE);

            // 메인 메뉴 패널로 화면 전환 (가장 중요)
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

        int[] choices = getComputerChoices();
        if (choices == null) return;

        // 첫 번째 카드 즉시 뒤집기
        Card card1 = board.getCard(choices[0]);
        if (card1 != null) {
            card1.reveal();
            selectedCards.add(card1);
        }

        // 두 번째 카드는 0.8초 후 뒤집고 바로 검사
        Timer t2 = new Timer(800, e -> {
            Card card2 = board.getCard(choices[1]);
            if (card2 != null) {
                card2.reveal();
                selectedCards.add(card2);
                checkMatch(); // 컴퓨터도 여기서 매칭 검사를 호출함
            }
        });
        t2.setRepeats(false);
        t2.start();
    }

    private int[] getComputerChoices() {
        List<Card> allCards = board.getCards();
        List<Integer> availableIndices = new ArrayList<>();

        // 아직 매칭되지 않은 카드의 인덱스만 수집
        for (int i = 0; i < allCards.size(); i++) {
            if (!allCards.get(i).isMatched()) {
                availableIndices.add(i);
            }
        }

        // 선택할 카드가 없으면 null 반환
        if (availableIndices.size() < 2) return null;

        // 인덱스를 무작위로 섞음
        Collections.shuffle(availableIndices);

        // 섞인 인덱스 중 앞의 두 개를 선택하여 반환
        return new int[]{availableIndices.get(0), availableIndices.get(1)};
    }

    // 레벨별 기억 로직 도움 메서드
    // GameWindow.java 하단에 추가
    // [추가] 카드 정보를 저장하는 메서드
    private void rememberCard(Card card) {
        if (currentLevel == 1) return; // Lv.1은 기억하지 않음

        int index = board.getCards().indexOf(card);
        int value = card.getNumber();

        if (currentLevel == 2) {
            // Lv.2: 50% 확률로 기억 (난이도 조절)
            if (Math.random() > 0.5) knownCards.put(index, value);
        } else if (currentLevel >= 3) {
            // Lv.3 이상: 무조건 기억
            knownCards.put(index, value);
        }
    }

    private String createCombinationKey(Card card1, Card card2) {
        int id1 = card1.getNumber();
        int id2 = card2.getNumber();
        return id1 < id2 ? id1 + "," + id2 : id2 + "," + id1;
    }

    private void initComboLabel() {
        comboLabel.setFont(new Font("Serif", Font.BOLD, 60)); // 크기를 더 키워 시인성 확보
        comboLabel.setForeground(new Color(255, 127, 0)); // 선명한 주황색
        comboLabel.setVisible(false);

        // BorderLayout.NORTH에 배치하여 상단에 뜨게 합니다.
        // 만약 이미 상단에 다른게 있다면 패널 배치를 조정해야 할 수 있습니다.
        add(comboLabel, BorderLayout.NORTH);
    }
    // 콤보 업데이트 메서드
    private void updateCombo(boolean success) {
        if (success) {
            comboCount++;
            if (comboCount >= 1) { // 1콤보부터 바로 표시
                showComboEffect(comboCount + " COMBO!");
            }
        } else {
            comboCount = 0;
            if (comboLabel != null) comboLabel.setVisible(false);
        }
    }
    private void showComboEffect(String text) {
        if (comboLabel == null) return;

        // 텍스트 설정 (HTML로 중앙 정렬)
        comboLabel.setText("<html><div style='text-align: center;'>" + text + "</div></html>");
        comboLabel.setVisible(true);

        // 0.8초 후 사라짐
        Timer comboTimer = new Timer(800, e -> comboLabel.setVisible(false));
        comboTimer.setRepeats(false);
        comboTimer.start();
    }

    private void resetGame() {
        int confirm = JOptionPane.showConfirmDialog(
                gameController,
                "게임을 초기화하시겠습니까?",
                "초기화 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            board.resetBoard(); // 보드 초기화
            selectedCards.clear(); // 선택된 카드 초기화
            comboCount = 0; // 콤보 초기화
            updateCombo(false); // 콤보 라벨 업데이트
            loginedUser.resetScore(); // 유저 점수 초기화
            computer.resetScore(); // 컴퓨터 점수 초기화
            userTurn = true; // 턴 초기화

            resetPlayerPanels();

            setBonusUsesByLevel();
            resetBonusButtonState();

            // UI 갱신
            updateUserPanel();
            updateCardCnt();
            updateStatus();
            setupCardListeners();

            backBGM.Stop_Sound(); // 배경음악 중지
            backBGM.Sound(SoundPath+"/Casino.wav", true, -20.0f); // 배경음 재생

            JOptionPane.showMessageDialog(
                    gameController,
                    "게임이 초기화되었습니다!",
                    "초기화 완료",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    //플레이어 패널 초기화 메서드
    private void resetPlayerPanels() {
        // [수정됨] 형변환 삭제
        userCardPanel.removeAll();
        userCardPanel.revalidate();
        userCardPanel.repaint();

        // 컴퓨터 패널 초기화
        computerPanel.removeAll();
        computerPanel.revalidate();
        computerPanel.repaint();
    }

    //// 보너스 버튼 상태 초기화 메서드
    // 게임 시작/초기화 시점에 호출하세요
    private void resetBonusButtonState() {
        JPanel itemPanel = (JPanel) userPanel.getComponent(1);
        JButton itemButton = (JButton) itemPanel.getComponent(0);

        itemButton.setEnabled(true); // 버튼 다시 활성화
        itemButton.setBackground(new Color(212, 232, 228)); // 원래 색상으로 복원
        itemButton.revalidate();
        itemButton.repaint();
    }
    // [추가] 보너스 버튼 클릭 시 실행될 아이템 함수
    private void useBonusItem() {
        if (!userTurn || isProcessing) return;

        // 1회 사용 제한: 버튼을 찾아서 비활성화
        JPanel itemPanel = (JPanel) userPanel.getComponent(1);
        JButton itemButton = (JButton) itemPanel.getComponent(0);
        itemButton.setEnabled(false);
        itemButton.setText("사용 완료");
        itemButton.setBackground(Color.GRAY);

        isProcessing = true; // 효과 도중 클릭 방지

        // 모든 카드 공개
        board.showAllCard();
        board.getBoardContainer().repaint();

        // 1.5초 후 다시 숨기기
        Timer itemTimer = new Timer(1500, e -> {
            board.hideAllCard();
            board.getBoardContainer().repaint();
            isProcessing = false;
        });
        itemTimer.setRepeats(false);
        itemTimer.start();
    }

    // GameWindow.java 내부에 추가할 메서드
    private void flipCard(Card card, int index) {
        // 모든 예외 상황을 입구에서 컷트합니다.
        if (isProcessing || !userTurn || card.isMatched() || selectedCards.contains(card)) {
            return;
        }

        card.reveal();
        selectedCards.add(card);

        if (selectedCards.size() == 2) {
            isProcessing = true; // 2장이 뒤집히면 매칭 확인 전까지 클릭 잠금
            Timer timer = new Timer(700, e -> {
                checkMatch();
                // checkMatch 내부 로직(실패 시 지연 시간 등)이 끝날 때까지
                // isProcessing 해제는 checkMatch의 각 분기점에서 관리합니다.
            });
            timer.setRepeats(false);
            timer.start();
        }
    }



}
