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
    private final Sound menuBGM = new Sound();

    private User loginedUser;

    public static final String FrontImagePath = "/cardGame/img";

    public static final String BackImagePath = FrontImagePath + "/card_back.png";
    public static final String MenuScreenPath = FrontImagePath + "/fruitbackground.jpg";

    public static int rows;
    public static int cols;
    public static int level = 1;

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    public void playMenuBGM() {
        if (!menuBGM.isPlaying()) {
            menuBGM.play("Mainmusic.wav", true, -25.0f);
        }
    }

    public void stopMenuBGM() {
        menuBGM.stop();
    }

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
            stopMenuBGM();
            // [변경] 모든 레벨이 4x5 보드 사용, level 변수로 직접 판단
            // [Changed] All levels use 4x5, use level variable directly
            int levelNum = GameController.level;
            if (levelNum < 1 || levelNum > 3) levelNum = 1;

            String cpuName = "CPU Lv." + levelNum;

            GameWindow gameWindow = new GameWindow(
                    this,
                    this.loginedUser,
                    board,
                    new Player(cpuName),
                    recordMgr,
                    levelNum
            );

            panel = gameWindow.setupGame();
            gameWindow.setupCardListeners();
        } else {
            panel = gameMenu.getPanel(this.loginedUser);
        }

        updateContentPane(panel);
    }

    private void updateContentPane(JPanel panel) {
        panel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        panel.setMinimumSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        panel.setMaximumSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

        setContentPane(panel);
        pack();

        setLocationRelativeTo(null);

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
        switchToPanel("login", null);

        System.out.println("[시스템] 로그아웃 완료 및 데이터 메모리 정리");
    }

    public void gameFinished(int finalScore) {
        if (this.loginedUser == null) {
            return;
        }

        Record newRecord = new Record(this.loginedUser, finalScore, GameController.level);

        RecordDAO recordDAO = new RecordDAO();
        boolean success = recordDAO.insertRecord(newRecord);

        if (success) {
            recordMgr.addMList(newRecord);
            System.out.println("[시스템] DB 및 메모리 기록 저장 완료");
        }
    }

    public void run() {
        System.setProperty("sun.java2d.uiScale", "1.0");

        setTitle("Juicy Match Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        /*
         * 1920x1080 좌표계를 그대로 쓰기 위해 타이틀바 제거.
         * 반드시 setVisible(true)보다 먼저 호출해야 함.
         */
        setUndecorated(true);

        gameManager = new GameManager(recordMgr);
        gameManager.callInfo();

        gameMenu = new GameMenuPanel(this, null);
        JPanel gameMenuPanel = gameMenu.getPanel(null);

        updateContentPane(gameMenuPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController gameController = new GameController();
            gameController.run();
        });
    }
}