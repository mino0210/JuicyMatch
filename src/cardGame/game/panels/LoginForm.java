package cardGame.game.panels;

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

public class LoginForm extends JPanel{
    private User loginedUser;
    private GameController gameController;

    Sound sound = new Sound();

    public LoginForm(GameController gameController){
        this.gameController = gameController;
    }


    public JPanel showLogin() {
        gameController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 이미지 삽입을 위한 패널 정의
        JPanel panel = new JPanel() {
            private Image backgroundImage;

            {
                // 배경 이미지 로드
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
                    // 패널 크기에 맞게 배경 이미지를 그림
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 간격 설정

        // 로그인 폼 영역
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setOpaque(false); // 배경 투명
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(20, 10, 20, 10); // 내부 간격 설정

        // 아이디 레이블 및 입력 필드
        JLabel idLabel = new JLabel("아이디");
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        idLabel.setForeground(Color.white);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(10, 30, 50, 10); // 상, 좌, 하, 우 (좌측 여백 추가)
        loginFormPanel.add(idLabel, formGbc);

        // 아이디 입력 필드
        JTextField userIdField = new JTextField();
        userIdField.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
        userIdField.setPreferredSize(new Dimension(250, 40));
        formGbc.gridx = 1;
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(10, 10, 50, 10); // 기본 여백
        loginFormPanel.add(userIdField, formGbc);

        // 비밀번호 레이블
        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        passwordLabel.setForeground(Color.white);
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        formGbc.insets = new Insets(10, 30, 10, 10); // 상, 좌, 하, 우 (좌측 여백 추가)
        loginFormPanel.add(passwordLabel, formGbc);

        // 비밀번호 입력 필드
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
        passwordField.setPreferredSize(new Dimension(250, 40));
        formGbc.gridx = 1;
        formGbc.gridy = 1;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(10, 10, 10, 10); // 기본 여백
        loginFormPanel.add(passwordField, formGbc);

        // 버튼 영역
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 버튼 사이 간격 설정
        buttonPanel.setOpaque(false);

        JButton backBtn = new JButton("돌아가기");
        backBtn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        buttonPanel.add(backBtn);

        JButton loginCheckBtn = new JButton("로그인");
        loginCheckBtn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        loginCheckBtn.setForeground(Color.WHITE);
        loginCheckBtn.setBackground(new Color(50, 150, 250));
        loginCheckBtn.setFocusPainted(false);
        loginCheckBtn.setBorderPainted(false);
        loginCheckBtn.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(loginCheckBtn);

        JButton joinBtn = new JButton("회원가입");
        joinBtn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setBackground(new Color(30, 200, 100));
        joinBtn.setFocusPainted(false);
        joinBtn.setBorderPainted(false);
        joinBtn.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(joinBtn);

        // 버튼 패널을 로그인 폼에 추가
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formGbc.gridwidth = 2; // 버튼 패널을 두 열에 걸쳐 배치
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(40, 10, 20, 10); // 버튼들 간의 간격을 조정
        loginFormPanel.add(buttonPanel, formGbc);


        // 로그인 폼을 메인 패널에 추가
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginFormPanel, gbc);

        // 버튼 이벤트 리스너 (수정 버전)
        loginCheckBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
                String username = userIdField.getText(); // 사용자가 입력한 아이디
                String password = new String(passwordField.getPassword()); // 사용자가 입력한 비번

                if (username.isBlank() || password.isBlank()) {
                    JOptionPane.showMessageDialog(gameController, "모든 정보를 입력해주세요.");
                    return;
                }

                // --- 여기부터 DB 연동 로직입니다 ---
                cardGame.database.UserDAO userDAO = new cardGame.database.UserDAO();
                User checkUser = userDAO.loginCheck(username, password);

                if (checkUser != null) {
                    // 로그인 성공
                    JOptionPane.showMessageDialog(gameController, "사용자: " + username + "이 로그인했습니다.");
                    loginedUser = checkUser;

                    // 기존 변수 구조 유지: 게임 컨트롤러에 유저 정보 전달
                    gameController.switchToPanel("gameMenu", loginedUser);
                } else {
                    // 로그인 실패 (DB에 일치하는 정보가 없음)
                    JOptionPane.showMessageDialog(gameController, "아이디 또는 비밀번호가 틀렸습니다.");
                }
            }
        });

        backBtn.addActionListener(e ->{
                    sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
                    gameController.switchToPanel("gameMenu",loginedUser);
                });

        joinBtn.addActionListener(e -> {
                    sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
                    gameController.switchToPanel("join",loginedUser);
                }
        );

        return panel;
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


}
