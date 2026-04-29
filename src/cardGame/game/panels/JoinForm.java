package cardGame.game.panels;

import cardGame.database.UserDAO;
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





public class JoinForm extends JPanel {
    private User loginedUser;
    private GameController gameController;
    private BufferedImage panelImage;
    private BufferedImage inputFieldImage;

    private Sound sound = new Sound();

    
    
    
    private static final int SCREEN_W = 1920;
    private static final int SCREEN_H = 1080;

    
    
    
    private static final int JOIN_PANEL_W = 800;
    private static final int JOIN_PANEL_H = 600;

    private static final int JOIN_PANEL_Y = 240;
    private static final int JOIN_PANEL_X = (SCREEN_W - JOIN_PANEL_W) / 2;

    
    
    
    private static final int INPUT_GROUP_SHIFT_Y = 15;
    private static final int INPUT_GROUP_SHIFT_X = 0;

    
    
    
    private static final int BUTTON_GROUP_SHIFT_Y = 0;
    private static final int BUTTON_GROUP_SHIFT_X = 0;

    
    
    
    










    private static final int FIELD_TEXT_PADDING_TOP = 5;
    private static final int FIELD_TEXT_PADDING_LEFT = 30;
    private static final int FIELD_TEXT_PADDING_BOTTOM = 5;
    private static final int FIELD_TEXT_PADDING_RIGHT = 15;

    public JoinForm(GameController gameController) {
        this.gameController = gameController;
        loadImages();
    }

    private void loadImages() {
        try {
            panelImage = ImageIO.read(new File("src/cardGame/img/join_panel.png"));
            inputFieldImage = ImageIO.read(new File("src/cardGame/img/input_field.png"));
        } catch (IOException e) {
            System.err.println("회원가입 패널 이미지 로드 실패: " + e.getMessage());
        }
    }

    public JPanel showJoin() {
        gameController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        panel.setLayout(null);

        JPanel joinPanel = new JPanel() {
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

        joinPanel.setOpaque(false);
        joinPanel.setLayout(null);
        joinPanel.setBounds(JOIN_PANEL_X, JOIN_PANEL_Y, JOIN_PANEL_W, JOIN_PANEL_H);
        panel.add(joinPanel);

        Font labelFont = createFont(20, Font.BOLD);
        Font fieldFont = createFont(18, Font.BOLD);
        Color labelColor = new Color(255, 250, 240);

        
        
        
        int labelX = 160 + INPUT_GROUP_SHIFT_X;
        int fieldX = 300 + INPUT_GROUP_SHIFT_X;

        int labelW = 180;
        int labelH = 30;

        int fieldW = 380;
        int fieldH = 40;

        int userIdY = 130 + INPUT_GROUP_SHIFT_Y;
        int passwordY = 200 + INPUT_GROUP_SHIFT_Y;
        int nicknameY = 270 + INPUT_GROUP_SHIFT_Y;
        int genderY = 340 + INPUT_GROUP_SHIFT_Y;

        JLabel idLabel = new JLabel("ユーザーID");
        idLabel.setFont(labelFont);
        idLabel.setForeground(labelColor);
        idLabel.setBounds(labelX, userIdY, labelW, labelH);
        joinPanel.add(idLabel);

        JTextField userIdField = createStyledTextField();
        userIdField.setFont(fieldFont);
        userIdField.setBounds(fieldX, userIdY, fieldW, fieldH);
        joinPanel.add(userIdField);

        JLabel passwordLabel = new JLabel("パスワード");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(labelColor);
        passwordLabel.setBounds(labelX, passwordY, labelW, labelH);
        joinPanel.add(passwordLabel);

        JPasswordField passwordField = createStyledPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(fieldX, passwordY, fieldW, fieldH);
        joinPanel.add(passwordField);

        JLabel nicknameLabel = new JLabel("ニックネーム");
        nicknameLabel.setFont(labelFont);
        nicknameLabel.setForeground(labelColor);
        nicknameLabel.setBounds(labelX, nicknameY, labelW, labelH);
        joinPanel.add(nicknameLabel);

        JTextField nicknameField = createStyledTextField();
        nicknameField.setFont(fieldFont);
        nicknameField.setBounds(fieldX, nicknameY, fieldW, fieldH);
        joinPanel.add(nicknameField);

        JLabel genderLabel = new JLabel("性別");
        genderLabel.setFont(labelFont);
        genderLabel.setForeground(labelColor);
        genderLabel.setBounds(labelX, genderY, labelW, labelH);
        joinPanel.add(genderLabel);

        JRadioButton maleRadio = new JRadioButton("男性");
        maleRadio.setFont(fieldFont);
        maleRadio.setForeground(labelColor);
        maleRadio.setOpaque(false);
        maleRadio.setBounds(fieldX + 10, genderY, 100, 30);
        maleRadio.setSelected(true);
        joinPanel.add(maleRadio);

        JRadioButton femaleRadio = new JRadioButton("女性");
        femaleRadio.setFont(fieldFont);
        femaleRadio.setForeground(labelColor);
        femaleRadio.setOpaque(false);
        femaleRadio.setBounds(fieldX + 130, genderY, 100, 30);
        joinPanel.add(femaleRadio);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        int backBtnX = 180 + BUTTON_GROUP_SHIFT_X;
        int registerBtnX = 420 + BUTTON_GROUP_SHIFT_X;
        int buttonY = 460 + BUTTON_GROUP_SHIFT_Y;

        WoodButton backBtn = new WoodButton("戻る");
        backBtn.setBounds(backBtnX, buttonY, 200, 70);
        joinPanel.add(backBtn);

        WoodButton registerBtn = new WoodButton("登録");
        registerBtn.setBounds(registerBtnX, buttonY, 200, 70);
        joinPanel.add(registerBtn);

        registerBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);

            String userId = userIdField.getText().trim();
            String password = new String(passwordField.getPassword());
            String nickname = nicknameField.getText().trim();
            String gender = maleRadio.isSelected() ? "M" : "F";

            if (userId.isBlank() || password.isBlank() || nickname.isBlank()) {
                JOptionPane.showMessageDialog(gameController, "すべての情報を入力してください。");
                sound.play("click.wav", false, -5.0f);
                return;
            }

            UserDAO userDAO = new UserDAO();

            if (userDAO.isIdDuplicate(userId)) {
                JOptionPane.showMessageDialog(gameController, "既に存在するIDです。");
                sound.play("click.wav", false, -5.0f);
                return;
            }

            User newUser = new User(userId, password, nickname, gender);
            boolean success = userDAO.insertUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(gameController, "登録が完了しました。");
                sound.play("click.wav", false, -5.0f);
                gameController.switchToPanel("login", null);
            } else {
                JOptionPane.showMessageDialog(gameController, "登録に失敗しました。");
                sound.play("click.wav", false, -5.0f);
            }
        });

        backBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("login", loginedUser);
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