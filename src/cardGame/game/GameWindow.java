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
    private boolean isProcessing = false; // 카드가 뒤집히는 중인지 확인하는 플래그
    private JLabel comboLabel; // 이 한 줄이 반드시 필요합니다!
    private JLayeredPane layeredPane; // 화면에 뜰 콤보 라벨
    private int comboCount = 0; // 연속 맞추기 카운트
    private Timer comboTimer;
    private JPanel userCardPanel;
    private Sound btnClickSound = new Sound(); // 효과음용 객체 생성
    private Sound popupClickSound = new Sound();

    public GameWindow(GameController gameController, User loginedUser, Board board,
                      Player computer, Manager recordMgr, int level) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        this.board = board;
        this.computer = computer;
        this.recordMgr = recordMgr;
        this.currentLevel = level;

        // [수정 포인트]
        setLayout(new BorderLayout()); // GameWindow 자체의 레이아웃 설정
        add(setupGame());              // setupGame이 만든 패널을 현재 클래스에 부착
    }

    public JPanel setupGame() {
        // 1. 메인 패널 및 배경 설정
        JPanel mainPanel = new JPanel(new BorderLayout());
        Color themeBgColor = new Color(245, 245, 235);
        mainPanel.setBackground(themeBgColor);

        // 여백 설정 (테두리 효과)
        mainPanel.add(createBlackPanel(new Dimension(getWidth(), 15)), BorderLayout.SOUTH);
        mainPanel.add(createBlackPanel(new Dimension(15, getHeight())), BorderLayout.WEST);
        mainPanel.add(createBlackPanel(new Dimension(15, getHeight())), BorderLayout.EAST);

        // --- [컴퓨터 영역: 왼쪽] ---
        JPanel computerContainer = new JPanel();
        computerContainer.setLayout(new BoxLayout(computerContainer, BoxLayout.Y_AXIS));
        computerContainer.setBackground(themeBgColor);
        computerContainer.setPreferredSize(new Dimension(220, 1040));
        computerContainer.setMaximumSize(new Dimension(220, 2000));

        JPanel computerNamePanel = new JPanel(new BorderLayout());
        computerNamePanel.setMaximumSize(new Dimension(220, 60));
        computerNamePanel.setBackground(themeBgColor);

        // 홈 버튼 (확인 창 추가 및 효과음 적용)
        JButton homeButton = createNavButton("/home.png", 35);
        homeButton.addActionListener(e -> {
            btnClickSound.play("BtnClick.wav", false, -10.0f);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "메인 화면으로 나가시겠습니까?",
                    "나가기 확인", JOptionPane.YES_NO_OPTION);

            popupClickSound.play("click.wav", false, -5.0f);

            if (confirm == JOptionPane.YES_OPTION) {
                // [중요] 모든 사운드 객체를 정지시킵니다.
                backBGM.stop();      // 배경음 정지
                btnClickSound.stop(); // 버튼음 정지
                btnClickSound.stopItemSound(); // 시계 소리 정지

                gameController.switchToPanel("gameMenu", loginedUser);
            }
        });

        // 리셋 버튼 (확인 창 추가)
        // [setupGame 메서드 내부의 resetButton 리스너 수정]
        JButton resetButton = createNavButton("/reset.png", 40);
        resetButton.addActionListener(e -> {
            // 버튼 클릭 소리만 여기서 재생
            btnClickSound.play("BtnClick.wav", false, -10.0f);

            // 리셋 메서드 호출 (확인 창은 메서드 안에서 띄움)
            resetGame();
        });

        // 음소거 버튼 (반투명 대신 아이콘 교체로 안정화)
        final boolean[] isMuted = {false};
        JButton muteButton = createNavButton("/volume_on.png", 40);
        muteButton.addActionListener(e -> {
            isMuted[0] = !isMuted[0];
            if (backBGM != null) {
                backBGM.setMute(isMuted[0]);
                try {
                    String iconPath = isMuted[0] ? "/volume_off.png" : "/volume_on.png";
                    muteButton.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(FrontImagePath + iconPath))
                            .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
                } catch (Exception ex) {
                    muteButton.setText(isMuted[0] ? "OFF" : "ON");
                }
            }
        });

        // 버튼 배치 (기록 버튼 제거)
        computerNamePanel.add(homeButton, BorderLayout.WEST);
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightButtonPanel.setOpaque(false);
        rightButtonPanel.add(muteButton);
        rightButtonPanel.add(resetButton);
        computerNamePanel.add(rightButtonPanel, BorderLayout.EAST);

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

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(themeBgColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel();
        titleLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(FrontImagePath + "/title.png"))
                .getImage().getScaledInstance(280, 90, Image.SCALE_SMOOTH)));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(themeBgColor);

        cardCountLabel = new JLabel("남은 카드: " + board.getCardCnt());
        cardCountLabel.setFont(new Font("맑은 고딕", Font.BOLD, 17));

        if (comboLabel == null) comboLabel = new JLabel("", SwingConstants.CENTER);
        comboLabel.setFont(new Font("맑은 고딕", Font.BOLD, 45));
        comboLabel.setForeground(new Color(255, 69, 0));
        comboLabel.setVisible(false);

        JPanel boardPanel = board.getBoardContainer();
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

        // [중요] 멤버 변수 layeredPane에 할당 (지역 변수 선언 제거)
        this.layeredPane = new JLayeredPane();
        this.layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        this.layeredPane.add(comboLabel, JLayeredPane.PALETTE_LAYER);

        middlePanel.add(cardCountLabel, BorderLayout.NORTH);
        middlePanel.add(this.layeredPane, BorderLayout.CENTER);

        boardPanelContainer.add(titlePanel, BorderLayout.NORTH);
        boardPanelContainer.add(middlePanel, BorderLayout.CENTER);

        // --- [유저 영역: 오른쪽] ---
        JPanel userContainer = new JPanel();
        userContainer.setLayout(new BoxLayout(userContainer, BoxLayout.Y_AXIS));
        userContainer.setBackground(themeBgColor);
        userContainer.setPreferredSize(new Dimension(220, 1020));
        userContainer.setMaximumSize(new Dimension(220, 2000));

        JPanel userNamePanel = new JPanel(new BorderLayout());
        userNamePanel.setMaximumSize(new Dimension(220, 45));
        userNamePanel.setBackground(new Color(147, 191, 133));
        userScoreLabel = new JLabel("점수: " + loginedUser.getScore(), SwingConstants.CENTER);
        userScoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        userNamePanel.add(userScoreLabel, BorderLayout.CENTER);

        userPanel = new JPanel(new BorderLayout());
        userCardPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        userCardPanel.setBackground(new Color(147, 191, 133));
        JScrollPane userScroll = new JScrollPane(userCardPanel);
        userScroll.setBorder(null);

        userPanel.add(userScroll, BorderLayout.CENTER);
        userPanel.add(createItemPanel(), BorderLayout.SOUTH);

        userContainer.add(userNamePanel);
        userContainer.add(Box.createVerticalStrut(10));
        userContainer.add(userPanel);

        // --- 최종 배치 ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBackground(themeBgColor);
        centerPanel.add(computerContainer);
        centerPanel.add(Box.createHorizontalStrut(20)); // 보드 간격 확보
        centerPanel.add(boardPanelContainer);
        centerPanel.add(Box.createHorizontalStrut(20));
        centerPanel.add(userContainer);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 리사이즈 리스너 (Null 방지 및 정확한 크기 계산)
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int bw = Math.max(750, mainPanel.getWidth() - 500);
                int bh = mainPanel.getHeight() - 250;
                boardPanel.setBounds(0, 0, bw, bh);
                comboLabel.setBounds(0, 0, bw, bh);
                if (layeredPane != null) {
                    layeredPane.setPreferredSize(new Dimension(bw, bh));
                }
                mainPanel.revalidate();
            }
        });

        updateStatus();
        backBGM.play("Casino.wav", true, -20.0f);

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
        // 이미지 아이콘 가져오기
        ImageIcon icon = card.getMatchedImageIcon();
        JLabel label = new JLabel(icon);

        // 핵심: 라벨의 최대 크기를 이미지 크기에 맞게 고정하여 GridLayout이 깨지는 것을 방지
        label.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        userCardPanel.add(label);
        userCardPanel.revalidate();
        userCardPanel.repaint();
    }

    private void addCardToComputerArea(Card card) {
        // 1. 매칭된 작은 아이콘 가져오기
        ImageIcon icon = card.getMatchedImageIcon();
        JLabel label = new JLabel(icon);

        // 2. 핵심: 라벨의 크기를 이미지 크기만큼만 가지도록 제한 (패널 확장 방지)
        // 아이콘 크기가 너무 크다면 여기서 직접 Dimension(50, 70) 식으로 고정해도 됩니다.
        label.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        // 3. 패널에 추가
        computerPanel.add(label);

        // 4. 레이아웃 갱신
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
        // 1. 게임 시작 시 모든 카드를 조용히 공개 (false 추가)
        for (Card card : board.getCards()) {
            card.reveal(false); // <--- 이 부분에 false를 넣으세요!
        }

        // 2. 일정 시간(예: 2초) 동안 보여준 후 다시 조용히 숨기기
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Card card : board.getCards()) {
                    if (!card.isMatched()) {
                        card.flip(); // 뒷면으로 돌리는 건 원래 소리가 안 나거나 짧으므로 그대로 둡니다.
                    }
                }
                board.getBoardContainer().repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();

        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                card.reveal(false); // 소리 없이 뒤집기 (false 추가)
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
        userScoreLabel.setText("점수: " + loginedUser.getScore());
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

        if (c1.getId() == c2.getId()) { // [매칭 성공]
            c1.setMatched(true);
            c2.setMatched(true);
            isProcessing = false;

            // --- 콤보 로직 통합 (유저/컴퓨터 공통) ---
            // 매칭 성공 시 updateCombo(true)를 호출하여 comboCount를 올리고 화면에 표시합니다.
            updateCombo(true);
            // ---------------------------------------

            if (userTurn) {
                // 점수 가중치 계산 (통합된 comboCount 사용)
                int baseScore = 100;
                int bonus = (comboCount - 1) * 20; // 첫 번째 성공은 100점, 이후부터 가중치
                int totalIncrease = baseScore + Math.max(0, bonus);

                loginedUser.addScore(totalIncrease);
                addCardToUserArea(c1);
                addCardToUserArea(c2);
                updateUserPanel();
            } else {
                // 컴퓨터 점수 가중치 (필요 없다면 baseScore만 주셔도 됩니다)
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

        } else { // [매칭 실패]
            isProcessing = true;

            Timer flipBackTimer = new Timer(300, e -> {
                c1.flip();
                c2.flip();
                selectedCards.clear();

                // --- 콤보 초기화 (통합 변수) ---
                updateCombo(false); // 내부에서 comboCount = 0 처리
                // ----------------------------

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
            if (backBGM != null) backBGM.stop();

            int finalScore = loginedUser.getScore();

            // DB에 결과 저장 (level 데이터 포함)
            Record newRecord = new Record(loginedUser, finalScore, GameController.level);
            new RecordDAO().insertRecord(newRecord);

            // 알림창 표시
            JOptionPane.showMessageDialog(gameController,
                    "축하합니다! 모든 카드를 맞췄습니다.\n최종 점수: " + finalScore,
                    "게임 종료", JOptionPane.INFORMATION_MESSAGE);
            popupClickSound.play("click.wav", false, -5.0f);
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

        // 1. 기억(knownCards) 속에서 이미 알고 있는 짝이 있는지 먼저 찾기
        int[] smartChoices = getSmartChoices();
        int[] finalChoices;

        if (smartChoices != null) {
            // 아는 짝이 있다면 확정 선택
            finalChoices = smartChoices;
        } else {
            // 아는 짝이 없다면 랜덤 선택
            finalChoices = getComputerChoices();
        }

        if (finalChoices == null) return;

        // 첫 번째 카드 뒤집기
        Card card1 = board.getCard(finalChoices[0]);
        if (card1 != null) {
            card1.reveal();
            rememberCard(card1); // 뒤집은 카드 기억
            selectedCards.add(card1);
        }

        // 두 번째 카드 뒤집기 (0.8초 후)
        Timer t2 = new Timer(800, e -> {
            Card card2 = board.getCard(finalChoices[1]);
            if (card2 != null) {
                card2.reveal();
                rememberCard(card2); // 뒤집은 카드 기억
                selectedCards.add(card2);
                checkMatch();
            }
        });
        t2.setRepeats(false);
        t2.start();
    }

    private int[] getSmartChoices() {
        // 저장된 모든 기억 정보를 리스트로 변환
        List<Integer> keys = new ArrayList<>(knownCards.keySet());

        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                int idx1 = keys.get(i);
                int idx2 = keys.get(j);

                // 두 카드의 숫자가 같고, 아직 매칭되지 않은 상태라면
                if (knownCards.get(idx1).equals(knownCards.get(idx2))) {
                    if (!board.getCard(idx1).isMatched() && !board.getCard(idx2).isMatched()) {
                        return new int[]{idx1, idx2};
                    }
                }
            }
        }
        return null; // 아는 짝이 없음
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
        if (currentLevel == 1) return;

        int index = board.getCards().indexOf(card);
        int value = card.getId(); // getNumber() 대신 getId()로 통일하여 정확도 향상

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
            comboCount++; // 통합 카운트 증가
            showComboEffect(comboCount + " COMBO!"); // 심플하게 표시
        } else {
            comboCount = 0; // 매칭 실패 시 초기화
            if (comboLabel != null) comboLabel.setVisible(false);
        }
    }
    private void showComboEffect(String text) {
        if (comboLabel == null) return;
        if (comboTimer != null && comboTimer.isRunning()) comboTimer.stop();

        // 누가 맞추든 눈에 잘 띄는 색상으로 고정
        comboLabel.setText("<html><div style='text-align: center; color: #FF4500; font-size:20px;'>" + text + "</div></html>");
        comboLabel.setVisible(true);

        comboTimer = new Timer(400, e -> comboLabel.setVisible(false));
        comboTimer.setRepeats(false);
        comboTimer.start();
    }

    private void resetGame() {
        btnClickSound.stopItemSound();
        int confirm = JOptionPane.showConfirmDialog(
                gameController,
                "게임을 초기화하시겠습니까?",
                "초기화 확인",
                JOptionPane.YES_NO_OPTION
        );

        // (예/아니오)에서 어떤 버튼이든 눌렀을 때 소리 재생
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
            backBGM.play("Casino.wav", true, -20.0f);

            JOptionPane.showMessageDialog(
                    gameController,
                    "게임이 초기화되었습니다!",
                    "초기화 완료",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // 2. 마지막 "초기화 완료" 창의 [확인] 버튼을 눌러서 창이 닫힐 때 소리 재생
            popupClickSound.play("click.wav", false, -5.0f);
        }
        knownCards.clear();
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
    // [GameWindow.java 내의 useBonusItem 메서드를 찾아서 아래 내용으로 교체하세요]

    // [GameWindow.java - useBonusItem 메서드 수정]
    // [GameWindow.java] - 셔플 소리 제거 및 3초 고정 버전
    private void useBonusItem() {
        // 1. 버튼 클릭 소리 (아이템 사용 시작)
        btnClickSound.play("BtnClick.wav", false, -10.0f);

        if (!userTurn || isProcessing) return;

        // 버튼 비활성화 및 UI 처리
        JPanel itemPanel = (JPanel) userPanel.getComponent(1);
        JButton itemButton = (JButton) itemPanel.getComponent(0);
        itemButton.setEnabled(false);
        itemButton.setText("사용 완료");
        itemButton.setBackground(Color.GRAY);

        isProcessing = true; // 효과 도중 다른 클릭 방지

        // 2. 시계 소리 재생 시작 (3초 타이머와 동기화)
        btnClickSound.playItemSound("clock_sound.wav", -10.0f);

        // 3. 모든 카드 직접 뒤집기
        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                card.reveal(false);
            }
        }
        board.getBoardContainer().repaint();

        // 4. 정확히 3초(3000ms) 유지하는 타이머
        Timer itemTimer = new Timer(3000, e -> {
            // 모든 카드 다시 숨기기
            for (Card card : board.getCards()) {
                if (!card.isMatched()) {
                    card.flip(); // 카드 뒷면으로 돌리기
                }
            }
            board.getBoardContainer().repaint();

            // 5. 3초 종료 시 시계 소리 즉시 정지
            btnClickSound.stopItemSound();

            isProcessing = false; // 잠금 해제
        });
        itemTimer.setRepeats(false);
        itemTimer.start();
    }

}
