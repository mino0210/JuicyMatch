package cardGame.game.panels;

import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static cardGame.game.GameController.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GameMenuPanel extends JPanel {
    private JButton startBtn;
    private JButton explanationBtn;
    private JButton rankingBtn;
    private JButton loginBtn;
    private JButton logoutBtn;
    private GameController gameController;
    private Sound sound = new Sound(); // 효과음용 객체

    private final int windowWidth = 1200;
    private final int windowHeight = 1000;

    public static User loginedUser;
    private Sound backBGM = new Sound(); // 배경음악용 객체

    public GameMenuPanel(GameController gameController, User loginedUser){
        this.gameController = gameController;
        this.loginedUser = loginedUser;
    }

    public static User getLoginedUser(){
        return loginedUser;
    }

    public JPanel getPanel(User loginedUser){
        gameController.setDefaultCloseOperation(EXIT_ON_CLOSE); // 창 닫기 동작 설정

        this.loginedUser = loginedUser;
        gameController.setTitle("Juicy Match");

        JPanel mainPanel = new JPanel(new BorderLayout());
        BackgroundPanel menuPanel = new BackgroundPanel(FrontImagePath + "/fruitbackground.jpg");
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // 1. 배경음악 재생 (Mainmusic.wav)
        if (!backBGM.isPlaying()) {
            backBGM.play("Mainmusic.wav", true, -25.0f);
        }

        CustomLabel gameNameLabel = new CustomLabel(
                "Juicy Match",
                Color.white,
                new Color(255, 115, 0),
                new Font("Serif", Font.BOLD, 110)
        );

        gameNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalStrut(150));
        menuPanel.add(gameNameLabel);
        menuPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel();
        startBtn = initBtn("게임 시작", 350, 100);
        explanationBtn = initBtn("설명", 350, 100);
        rankingBtn = initBtn("기록", 350, 100);
        loginBtn = initBtn("로그인", 350, 100);
        logoutBtn = initBtn("로그아웃", 350, 100);

        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPanel.setOpaque(false);

        if(loginedUser == null){
            startBtn.setVisible(false);
            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);
        }
        else{
            startBtn.setVisible(true);
            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);
        }

        btnPanel.add(startBtn);
        btnPanel.add(loginBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(logoutBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(rankingBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(explanationBtn);

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnPanel);
        menuPanel.add(Box.createVerticalStrut(170));

        // 2. 각 버튼 액션 리스너 설정 (효과음 play() 메서드 호출)
        startBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f);
            backBGM.stop(); // 게임 시작 시 배경음악 종료
            gameController.switchToPanel("selectLevel", loginedUser);
        });

        explanationBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("explanation", loginedUser);
        });

        rankingBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("ranking", loginedUser);
        });

        loginBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("login", loginedUser);
        });

        logoutBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f); // 로그아웃 버튼도 효과음 추가
            int confirm = JOptionPane.showConfirmDialog(null, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gameController.logout();
            }
        });

        String message = loginedUser != null ? "게임을 시작하려면 [게임 시작] 버튼을 클릭하세요!" : "게임을 시작하려면 로그인 후 [게임 시작] 버튼을 클릭하세요!";

        JLabel statusLabel = createDynamicStatusLabel(message);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JLabel createDynamicStatusLabel(String message) {
        JLabel statusLabel = new JLabel(message);
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        statusLabel.setForeground(new Color(255, 115, 0));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setOpaque(false);

        Timer textTimer = new Timer(100, new ActionListener() {
            int x = 0;
            final int resetPosition = -300;

            @Override
            public void actionPerformed(ActionEvent e) {
                x += 5;
                if (x > windowWidth) {
                    x = resetPosition;
                }
                statusLabel.setLocation(x, statusLabel.getY());
            }
        });
        textTimer.start();
        return statusLabel;
    }

    private JButton initBtn(String text, int width, int height) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(width, height));

        btn.setBackground(Color.white);
        btn.setForeground(Color.black);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 232, 204));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.white);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 115, 0));
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.white);
            }
        });

        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 115, 0), 2, true));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = toolkit.getImage(FrontImagePath + "/fruit/fruit09.png");
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor");
        btn.setCursor(customCursor);

        return btn;
    }
}