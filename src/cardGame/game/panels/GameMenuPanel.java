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
    private Sound sound = new Sound();

    private final int windowWidth = 1200;
    private final int windowHeight = 1000;

    public static User loginedUser;
    private Sound backBGM = new Sound();

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
        gameController.setTitle("Menu");

        gameController.setTitle("Juicy Match");
        JPanel mainPanel = new JPanel(new BorderLayout());
        BackgroundPanel menuPanel = new BackgroundPanel(FrontImagePath+"/fruitbackground.jpg");
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // BGM 재생 중인지 확인 후 재생
        if (!backBGM.isPlaying()) {
//            backBGM.Sound(SoundPath + "/Mainmusic.wav", true, -25.0f);

        }

        CustomLabel gameNameLabel = new CustomLabel(
                "Juicy Match",
                Color.white,
                new Color(255, 115, 0),
                new Font("Serif", Font.BOLD, 110)
        );

        gameNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // 화면 중앙 정렬

        // 게임 이름을 메뉴 패널에 추가
        menuPanel.add(Box.createVerticalStrut(150)); // 제목을 아래로 내리는 여백 추가
        menuPanel.add(gameNameLabel);
        menuPanel.add(Box.createVerticalStrut(20));  // 게임 이름과 버튼들 사이에 여백

        JPanel btnPanel = new JPanel();
        startBtn = initBtn("게임 시작",350,100);
        explanationBtn = initBtn("설명",350,100);
        rankingBtn = initBtn("기록",350,100);
        loginBtn = initBtn("로그인",350,100);
        logoutBtn = initBtn("로그아웃",350,100);

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
        //btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(loginBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(logoutBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(rankingBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(explanationBtn);

        menuPanel.add(Box.createVerticalGlue()); // 버튼 아래에 위치하도록
        menuPanel.add(btnPanel);
        menuPanel.add(Box.createVerticalStrut(170)); //하단 간격

        startBtn.addActionListener(e -> {
            sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
            backBGM.Stop_Sound(); // 게임 시작 시 배경음악 종료
            gameController.switchToPanel("selectLevel", loginedUser);
        });
        //startBtn.addActionListener(e-> gameController.switchToPanel("selectLevel",loginedUser));
        explanationBtn.addActionListener(e -> {
            sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("explanation", loginedUser);
        });
        rankingBtn.addActionListener(e-> {
            sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("ranking",loginedUser);

        });
        loginBtn.addActionListener(e -> {
            sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("login", loginedUser);
        });
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 컨트롤러의 performLogout 하나만 호출
                gameController.logout();
            }
        });

        // 동적 상태 표시줄 추가
        String message = loginedUser!=null ? "게임을 시작하려면 [게임 시작] 버튼을 클릭하세요!":"게임을 시작하려면 로그인 후 [게임 시작] 버튼을 클릭하세요!";

        JLabel statusLabel = createDynamicStatusLabel(message);
        mainPanel.add(menuPanel, BorderLayout.CENTER); // 메뉴 패널을 중앙에 배치
        mainPanel.add(statusLabel, BorderLayout.SOUTH); // 상태 표시줄을 하단에 배치


        return mainPanel;
    }

    private JLabel createDynamicStatusLabel(String message) {

        JLabel statusLabel = new JLabel(message);
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        statusLabel.setForeground(new Color(255, 115, 0));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setOpaque(false);
        statusLabel.setBackground(new Color(0, 0, 0, 150));

        Timer textTimer = new Timer(100, new ActionListener() {
            int x = 0; //X 위치를 조정할 변수
            final int resetPosition = -300; //사라지는 위치
            final int startPosition = windowWidth; //나타나는 위치

            @Override
            public void actionPerformed(ActionEvent e) {
                x += 5; //속도
                if (x > windowWidth) {
                    x = resetPosition; //다시 왼쪽에서 시작
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
        btn.setMaximumSize(new Dimension(width, height)); //

        btn.setBackground(Color.white); // 배경 색
        btn.setForeground(Color.black); // 글씨 색
        //btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));


        //마우스 올렸을 때
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

        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 115, 0), 2, true)); // 둥근 테두리
        btn.setFocusPainted(false); // 포커스 표시 제거
        btn.setContentAreaFilled(false); // 기본 배경 제거
        btn.setOpaque(true); // 사용자 정의 배경 활성화

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = toolkit.getImage(FrontImagePath+"/fruit/fruit09.png"); // 커서 이미지 경로
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor");
        btn.setCursor(customCursor);

        return btn;
    }

}
