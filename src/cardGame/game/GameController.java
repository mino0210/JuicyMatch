package cardGame.game;

import cardGame.database.RecordDAO;
import cardGame.entity.Board;
import cardGame.entity.Player;
import cardGame.entity.User;
import cardGame.entity.Record;
import cardGame.game.panels.*;
import cardGame.mgr.Manager;

import javax.swing.*;
import java.awt.*;

public class GameController extends JFrame {
    private GameManager gameManager;
    public static Manager userMgr = new Manager();
    public static Manager recordMgr = new Manager();
    private GameMenuPanel gameMenu;
    private Board board = new Board(4, 3);

    private User loginedUser;

    public static final String FrontImagePath = "/cardGame/img";
    public static final String ResourcePath = "cardGame/resource";
    public static final String SoundPath = "cardGame/sound";

    public static final String BackImagePath = FrontImagePath + "/card_back.png";
    public static final String CardSoundPath = SoundPath + "/Card_Flip.wav";
    public static final String MenuScreenPath = FrontImagePath + "/fruitbackground.jpg";

    public static int rows;
    public static int cols;
    public static int level = 1;

    public void switchToPanel(String panelName, User user) {
        if (user != null) {
            this.loginedUser = user;
        }

        JPanel panel;
        switch (panelName) {
            case "gameMenu" -> panel = gameMenu.getPanel(this.loginedUser);
            case "login" -> panel = new LoginForm(this).showLogin();
            case "join" -> panel = new JoinForm(this).showJoin();
            case "ranking" -> {
                try {
                    TablePanel rankingTable = TablePanel.GetInstance();
                    rankingTable.initUI();
                    rankingTable.resetTable();
                    panel = new RankingPanel(this, this.loginedUser).showRanking();
                } catch (Exception e) {
                    e.printStackTrace();
                    panel = gameMenu.getPanel(this.loginedUser);
                }
            }
            case "explanation" -> panel = new ExplanationPanel(this, this.loginedUser).showExplanation();
            case "selectLevel" -> {
                SelectLevelPanel selectLevelPanel = new SelectLevelPanel(this, this.loginedUser);
                panel = selectLevelPanel.selectLevel();
                board = selectLevelPanel.getBoard();
            }
            default -> panel = gameMenu.getPanel(this.loginedUser);
        }
        updateContentPane(panel);
    }

    public void switchToPanel(String panelName, User user, Board board) {
        if (user != null) {
            this.loginedUser = user;
        }

        JPanel panel;
        if (panelName.equals("startGame")) {
            int levelNum = 1;
            if (rows == 3 && cols == 4) levelNum = 1;
            else if (rows == 4 && cols == 4) levelNum = 2;
            else if (rows == 4 && cols == 5) levelNum = 3;

            // level 변수를 명확히 전달
            GameWindow gameWindow = new GameWindow(this, this.loginedUser, board, new Player("컴퓨터"), recordMgr, levelNum);
            panel = gameWindow.setupGame();
            gameWindow.setupCardListeners();
        } else {
            panel = gameMenu.getPanel(this.loginedUser);
        }
        updateContentPane(panel);
    }

    private void updateContentPane(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    public void loginSuccess(User user) {
        this.loginedUser = user;
        System.out.println("[시스템] " + user.getName() + "님 로그인 성공");
        switchToPanel("gameMenu", user);
    }

    public void logout() {
        this.loginedUser = null;
        // recordMgr를 초기화할 필요가 있다면 (중복 로드 방지)
        // recordMgr.getMList().clear();

        getContentPane().removeAll();
        switchToPanel("login", null);

        revalidate();
        repaint();
        System.out.println("[시스템] 로그아웃 완료 및 데이터 메모리 정리");
    }

    /**
     * [수정] 현재 존재하는 필드와 클래스만 사용하여 기록 저장
     */
    public void gameFinished(int finalScore) {
        if (this.loginedUser == null) return;

        // Record 클래스 생성자에 맞춰 객체 생성 (기존 소스 기반)
        Record newRecord = new Record(this.loginedUser, finalScore, GameController.level);

        RecordDAO recordDAO = new RecordDAO();
        boolean success = recordDAO.insertRecord(newRecord);

        if (success) {
            // 메모리 리스트에 추가하여 랭킹 반영
            recordMgr.addMList(newRecord);
            // 테이블 UI 갱신
            TablePanel.GetInstance().loadData();
            System.out.println("[시스템] DB 및 메모리 기록 저장 완료");
        }
    }

    public void run() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameManager = new GameManager(recordMgr);
        gameManager.callInfo();

        setTitle("Juicy Match Card Game");
        gameMenu = new GameMenuPanel(this, null);
        JPanel gameMenuPanel = gameMenu.getPanel(null);

        add(gameMenuPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController gameController = new GameController();
            gameController.run();
        });
    }
}