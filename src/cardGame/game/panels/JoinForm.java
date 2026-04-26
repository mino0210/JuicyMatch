package cardGame.game.panels;

import cardGame.database.UserDAO;
import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.mgr.Manageable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import static cardGame.game.GameController.*;

public class JoinForm extends JPanel {
    private UserDAO userDAO = new UserDAO();
    private GameController gameController;
    private JRadioButton rbtnMale;
    private JRadioButton rbtnFemale;
    private JTextField tfId;
    private JPasswordField tfPw;
    private JPasswordField tfRe;
    private JTextField tfUserName;
    private User loginedUser;
    private Sound sound = new Sound();

    public JoinForm(GameController gameController) {
        this.gameController = gameController;
    }

    private boolean isBlank(JTextField idTextField, JPasswordField passwordField, JTextField nicknameField) {
        if (idTextField.getText().isEmpty())
            return true;
        if (String.valueOf(passwordField.getPassword()).isEmpty())
            return true;
        return nicknameField.getText().isEmpty();
    }

    private boolean checkKwd(String username) {
        User user;
        for (Manageable m : userMgr.mList) {
            user = (User) m;
            if (user.matches(username))
                return true;
        }
        return false;
    }

    public JPanel showJoin() {
        // 이미지 삽입을 위한 패널 정의
        JPanel panel = new JPanel() {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = new ImageIcon(Objects.requireNonNull
                            (getClass().getResource(FrontImagePath + "/login_img/loginBackground.png"))).getImage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        panel.setPreferredSize(new Dimension(600, 400));

        JLabel joinLabel = new JLabel("회원가입");
        joinLabel.setFont(new Font("맑은 고딕", Font.BOLD, 45));
        joinLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(joinLabel, gbc);

        JLabel lblId = new JLabel("아이디");
        lblId.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblId.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);
        panel.add(lblId, gbc);

        JTextField idTextField = new JTextField();
        idTextField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        idTextField.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(idTextField, gbc);

        JLabel lblPw = new JLabel("비밀번호");
        lblPw.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblPw.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);
        panel.add(lblPw, gbc);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        JLabel lblUserName = new JLabel("닉네임");
        lblUserName.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblUserName.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);
        panel.add(lblUserName, gbc);

        JTextField nicknameField = new JTextField();
        nicknameField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        nicknameField.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nicknameField, gbc);

        JLabel lblGender = new JLabel("              ");
        lblGender.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblGender.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblGender, gbc);

        rbtnMale = new JRadioButton("남성");
        rbtnMale.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        rbtnMale.setForeground(Color.WHITE);
        rbtnMale.setOpaque(false);

        rbtnFemale = new JRadioButton("여성");
        rbtnFemale.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        rbtnFemale.setForeground(Color.WHITE);
        rbtnFemale.setOpaque(false);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbtnMale);
        genderGroup.add(rbtnFemale);

        JPanel genderPanel = new JPanel();
        genderPanel.setOpaque(false);
        genderPanel.add(rbtnMale);
        genderPanel.add(rbtnFemale);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(genderPanel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        JButton cancelBtn = new JButton("돌아가기");
        cancelBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);

        GridBagConstraints gbcCancel = new GridBagConstraints();
        gbcCancel.gridx = 0;
        gbcCancel.gridy = 0;
        gbcCancel.insets = new Insets(0, 0, 0, 160);
        buttonPanel.add(cancelBtn, gbcCancel);

        JButton joinBtn = new JButton("가입하기");
        joinBtn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setBackground(new Color(30, 200, 100));
        GridBagConstraints gbcJoin = new GridBagConstraints();
        gbcJoin.gridx = 1;
        gbcJoin.gridy = 0;
        gbcJoin.insets = new Insets(0, 10, 0, 0);
        buttonPanel.add(joinBtn, gbcJoin);

        GridBagConstraints gbcButtonPanel = new GridBagConstraints();
        gbcButtonPanel.gridx = 0;
        gbcButtonPanel.gridy = 5;
        gbcButtonPanel.gridwidth = 2;
        panel.add(buttonPanel, gbcButtonPanel);

        joinBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                sound.play("BtnClick.wav", false, -10.0f);
                String username = idTextField.getText();
                char[] password = passwordField.getPassword();

                if (isBlank(idTextField, passwordField, nicknameField)) {
                    JOptionPane.showMessageDialog(gameController, "모든 정보를 입력해주세요.");
                } else {
                    String nickname = nicknameField.getText().trim();
                    String gender = rbtnMale.isSelected() ? "남성" : (rbtnFemale.isSelected() ? "여성" : "선택안함");

                    // DB 도구 선언
                    cardGame.database.UserDAO userDAO = new cardGame.database.UserDAO();

                    // 1. ID 중복 확인 (DB 연동)
                    if (userDAO.isIdDuplicate(username)) {
                        JOptionPane.showMessageDialog(gameController, "이미 존재하는 Id입니다.");
                        idTextField.requestFocus();
                    } else {
                        // 2. 신규 사용자 생성 및 DB 저장
                        User newUser = new User(username, String.valueOf(password), nickname, gender);
                        boolean isSuccess = userDAO.insertUser(newUser);

                        if (isSuccess) {
                            JOptionPane.showMessageDialog(gameController, "회원가입에 성공했습니다.");
                            gameController.switchToPanel("login", loginedUser);
                        } else {
                            JOptionPane.showMessageDialog(gameController, "회원가입 실패: 오류가 발생했습니다.");
                        }
                    }
                }
            }
        });

        cancelBtn.addActionListener(e ->{
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("login",loginedUser);
        } );

        return panel;
    }

    public String getGender() {
        return rbtnMale.isSelected() ? rbtnMale.getText() : rbtnFemale.getText();
    }
}