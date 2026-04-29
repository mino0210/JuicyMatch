package cardGame.game.panels;

import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.game.components.WoodButton;
import cardGame.game.components.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;




public class LoginForm extends JPanel {
    private User loginedUser;
    private GameController gameController;
    private BufferedImage panelImage;
    private BufferedImage inputFieldImage;

    private Sound sound = new Sound();

    private static final int SCREEN_W = 1920;
    private static final int SCREEN_H = 1080;

    
    
    
    










    private static final int FIELD_TEXT_PADDING_TOP = 5;
    private static final int FIELD_TEXT_PADDING_LEFT = 25;
    private static final int FIELD_TEXT_PADDING_BOTTOM = 5;
    private static final int FIELD_TEXT_PADDING_RIGHT = 15;

    public LoginForm(GameController gameController) {
        this.gameController = gameController;
        loadImages();
    }

    private void loadImages() {
        try {
            panelImage = ImageIO.read(new File("src/cardGame/img/login_panel.png"));
            inputFieldImage = ImageIO.read(new File("src/cardGame/img/input_field.png"));
        } catch (IOException e) {
            System.err.println("로그인 패널 이미지 로드 실패: " + e.getMessage());
        }
    }

    public JPanel showLogin() {
        gameController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        panel.setLayout(null);

        int loginPanelW = 600;
        int loginPanelH = 400;
        int loginPanelX = (SCREEN_W - loginPanelW) / 2;
        int loginPanelY = 340;

        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (panelImage != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );
                    g2.drawImage(panelImage, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };

        loginPanel.setOpaque(false);
        loginPanel.setLayout(null);
        loginPanel.setBounds(loginPanelX, loginPanelY, loginPanelW, loginPanelH);
        panel.add(loginPanel);

        Font labelFont = createFont(20, Font.BOLD);
        Font fieldFont = createFont(18, Font.BOLD);

        JLabel idLabel = new JLabel("ユーザーID");
        idLabel.setFont(labelFont);
        idLabel.setForeground(new Color(255, 250, 240));
        idLabel.setBounds(80, 100, 150, 30);
        loginPanel.add(idLabel);

        JTextField userIdField = createStyledTextField();
        userIdField.setFont(fieldFont);
        userIdField.setBounds(240, 100, 280, 40);
        loginPanel.add(userIdField);

        JLabel passwordLabel = new JLabel("パスワード");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(new Color(255, 250, 240));
        passwordLabel.setBounds(80, 170, 150, 30);
        loginPanel.add(passwordLabel);

        JPasswordField passwordField = createStyledPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(240, 170, 280, 40);
        loginPanel.add(passwordField);

        WoodButton backBtn = new WoodButton("戻る");
        backBtn.setBounds(80, 280, 140, 60);
        loginPanel.add(backBtn);

        WoodButton loginBtn = new WoodButton("ログイン");
        loginBtn.setBounds(240, 280, 140, 60);
        loginPanel.add(loginBtn);

        WoodButton joinBtn = new WoodButton("新規登録");
        joinBtn.setBounds(400, 280, 140, 60);
        loginPanel.add(joinBtn);

        loginBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);

            String username = userIdField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(gameController, "すべての情報を入力してください。");
                sound.play("click.wav", false, -5.0f);
                return;
            }

            cardGame.database.UserDAO userDAO = new cardGame.database.UserDAO();
            User checkUser = userDAO.loginCheck(username, password);

            if (checkUser != null) {
                JOptionPane.showMessageDialog(gameController, "ユーザー: " + username + " がログインしました。");
                sound.play("click.wav", false, -5.0f);

                loginedUser = checkUser;
                gameController.switchToPanel("gameMenu", loginedUser);
            } else {
                JOptionPane.showMessageDialog(gameController, "IDまたはパスワードが間違っています。");
                sound.play("click.wav", false, -5.0f);
            }
        });

        backBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });

        joinBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("join", loginedUser);
        });

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (inputFieldImage != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );
                    g2.drawImage(inputFieldImage, 0, 0, getWidth(), getHeight(), null);
                }

                super.paintComponent(g);
            }
        };

        applyInputFieldStyle(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (inputFieldImage != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );
                    g2.drawImage(inputFieldImage, 0, 0, getWidth(), getHeight(), null);
                }

                super.paintComponent(g);
            }
        };

        applyInputFieldStyle(field);
        return field;
    }

    private void applyInputFieldStyle(JTextField field) {
        field.setOpaque(false);

        field.setBorder(BorderFactory.createEmptyBorder(
                FIELD_TEXT_PADDING_TOP,
                FIELD_TEXT_PADDING_LEFT,
                FIELD_TEXT_PADDING_BOTTOM,
                FIELD_TEXT_PADDING_RIGHT
        ));

        field.setMargin(new Insets(
                FIELD_TEXT_PADDING_TOP,
                FIELD_TEXT_PADDING_LEFT,
                FIELD_TEXT_PADDING_BOTTOM,
                FIELD_TEXT_PADDING_RIGHT
        ));

        field.setHorizontalAlignment(JTextField.LEFT);
        field.setForeground(new Color(255, 255, 255));
        field.setCaretColor(new Color(255, 255, 255));
        field.setSelectionColor(new Color(255, 215, 120, 160));
        field.setSelectedTextColor(new Color(70, 40, 20));
    }

    private Font createFont(int size, int style) {
        int resolvedStyle = style | Font.BOLD;
        String[] fontNames = {"Yu Gothic UI", "Meiryo", "MS Gothic", "Hiragino Sans"};

        for (String fontName : fontNames) {
            Font font = new Font(fontName, resolvedStyle, size);

            if (font.getFamily().equals(fontName)) {
                return font;
            }
        }

        return new Font("Dialog", resolvedStyle, size);
    }
}