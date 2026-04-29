package cardGame.game.panels;

import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.game.components.WoodButton;
import cardGame.game.components.ImagePanel;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;




public class GameMenuPanel extends JPanel {
    private WoodButton startBtn;
    private WoodButton explanationBtn;
    private WoodButton rankingBtn;
    private WoodButton loginBtn;
    private WoodButton logoutBtn;
    private WoodButton exitBtn;
    private GameController gameController;
    private Sound sound = new Sound();

    public static User loginedUser;
    

    private BufferedImage logoImage;

    public GameMenuPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        loadImages();
    }

    private void loadImages() {
        try {
            logoImage = ImageIO.read(new File("src/cardGame/img/juicymatch_logo.png"));
        } catch (IOException e) {
            System.err.println("로고 이미지 로드 실패: " + e.getMessage());
        }
    }

    public static User getLoginedUser() {
        return loginedUser;
    }

    public JPanel getPanel(User loginedUser) {
        gameController.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.loginedUser = loginedUser;
        gameController.setTitle("Juicy Match");

        final int SCREEN_W = 1920;
        final int SCREEN_H = 1080;

        ImagePanel menuPanel = new ImagePanel("src/cardGame/img/background.png");
        menuPanel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        menuPanel.setLayout(null);

        gameController.playMenuBGM();

        
        
        
        int logoW = 700;
        int logoH = 300;
        int logoX = (SCREEN_W - logoW) / 2;

        
        
        int logoY = 165;

        int btnW = 400;
        int btnH = 86;
        int btnX = (SCREEN_W - btnW) / 2;

        
        
        int firstBtnY = 460;

        
        
        int gapY = 100;

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (logoImage != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );
                    g2.drawImage(logoImage, 0, 0, logoW, logoH, null);
                }
            }
        };

        logoPanel.setOpaque(false);
        logoPanel.setBounds(logoX, logoY, logoW, logoH);
        menuPanel.add(logoPanel);

        startBtn = new WoodButton("ゲームスタート");
        explanationBtn = new WoodButton("遊び方");
        rankingBtn = new WoodButton("きろく");
        loginBtn = new WoodButton("ログイン");
        logoutBtn = new WoodButton("ログアウト");
        exitBtn = new WoodButton("終了");

        
        
        
        int slot1Y = firstBtnY;              
        
        int slot2Y = firstBtnY + gapY;       
        
        int slot3Y = firstBtnY + gapY * 2;   
        
        int slot4Y = firstBtnY + gapY * 3;   
        
        int slot5Y = firstBtnY + gapY * 4;   
        

        startBtn.setBounds(btnX, slot1Y, btnW, btnH);
        loginBtn.setBounds(btnX, slot1Y, btnW, btnH);

        logoutBtn.setBounds(btnX, slot2Y, btnW, btnH);
        rankingBtn.setBounds(btnX, slot3Y, btnW, btnH);
        explanationBtn.setBounds(btnX, slot4Y, btnW, btnH);
        exitBtn.setBounds(btnX, slot5Y, btnW, btnH);

        boolean isLoggedIn = (loginedUser != null);

        if (isLoggedIn) {
            
            
            
            startBtn.setVisible(true);
            loginBtn.setVisible(false);
            logoutBtn.setVisible(true);
        } else {
            
            
            
            
            
            startBtn.setVisible(false);
            loginBtn.setVisible(true);
            logoutBtn.setVisible(false);
        }

        rankingBtn.setVisible(true);
        explanationBtn.setVisible(true);
        exitBtn.setVisible(true);

        menuPanel.add(startBtn);
        menuPanel.add(loginBtn);
        menuPanel.add(logoutBtn);
        menuPanel.add(rankingBtn);
        menuPanel.add(explanationBtn);
        menuPanel.add(exitBtn);

        startBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f);
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
            new Sound().play("BtnClick.wav", false, -10.0f);

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "ログアウトしますか？",
                    "ログアウト",
                    JOptionPane.YES_NO_OPTION
            );

            sound.play("click.wav", false, -5.0f);

            if (confirm == JOptionPane.YES_OPTION) {
                gameController.logout();
            }
        });


        exitBtn.addActionListener(e -> {
            new Sound().play("BtnClick.wav", false, -10.0f);

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "ゲームを終了しますか？",
                    "終了確認",
                    JOptionPane.YES_NO_OPTION
            );

            sound.play("click.wav", false, -5.0f);

            if (confirm == JOptionPane.YES_OPTION) {
                gameController.stopMenuBGM();
                gameController.dispose();
                System.exit(0);
            }
        });

        return menuPanel;
    }
}
