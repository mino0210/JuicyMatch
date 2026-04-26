package cardGame.game.panels;

import cardGame.entity.*;
import cardGame.game.GameController;
import cardGame.game.Sound;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static cardGame.game.GameController.FrontImagePath;

public class ExplanationPanel extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private String imgPath = FrontImagePath + "/explanation/";
    private Sound sound = new Sound();

    public ExplanationPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
    }

    private ImageIcon resizeImageIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));

        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    public JPanel showExplanation() {

        // 이미지 크기 조정
        ImageIcon[] images = {
                resizeImageIcon(imgPath + "/select_card.png", 1200, 700),
                resizeImageIcon(imgPath + "/select_card1.png", 1200, 700),
                resizeImageIcon(imgPath + "/select_card2.png", 1200, 700),
                resizeImageIcon(imgPath + "/select_card3.png", 1200, 700),
                resizeImageIcon(imgPath + "/select_card4.png", 1200, 700),
                resizeImageIcon(imgPath + "/select_card5.png", 1200, 700)
        };

        String[] destxt = {
                "테마를 선택하고 레벨을 선택한 뒤 확인 버튼을 눌러주세요.",
                "확인 버튼을 누르면 게임 화면이 띄워집니다. <br>게임화면 시작 시 약 2초간 카드의 앞면이 보이고 시간이 지나면 다시 카드가 뒤집힙니다. ",
                "유저 턴을 먼저 진행하고 카드를 클릭해 뒤집어 동일한 카드를 맞춥니다.",
                "유저가 카드를 맞추지 못하면, 컴퓨터의 턴으로 넘어가게 됩니다. <br>만약 컴퓨터가 카드를 맞추지 못한다면, 유저의 턴으로 되돌아 옵니다.",
                "유저나 컴퓨터가 카드를 맞추게 되면, 맞춘 카드가 유저 혹은 컴퓨터에게 옮겨집니다.",
                "카드를 모두 맞추게 되면, 얻은 점수에 따라 '유저 또는 컴퓨터가 이겼습니다'창이 뜨게 되고 확인버튼을 누르면 기본 창으로 되돌아 갑니다.<br>이제 왼쪽 나가기 버튼을 눌러 게임을 즐겨주세요~"
        };

        final int[] currentIdx = {0};

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#FFF8E8")); // 패널 배경색 변경

        JPanel describePanel = new JPanel();
        describePanel.setLayout(new BorderLayout());
        describePanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경

        // 이미지를 위한 패널
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경
        imagePanel.setPreferredSize(new Dimension(1500, 900));
        JLabel imageLabel = new JLabel(images[0]);
        imageLabel.setHorizontalAlignment(JLabel.CENTER); // 이미지 중앙 정렬
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // 텍스트를 위한 패널
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경
        textPanel.setPreferredSize(new Dimension(1500, 100)); // 텍스트 패널 높이 설정

        JLabel destxtLabel = new JLabel();
        destxtLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        destxtLabel.setHorizontalAlignment(JLabel.CENTER); // 텍스트 중앙 정렬
        destxtLabel.setVerticalAlignment(JLabel.CENTER);
        destxtLabel.setText("<html><div style='width:800px;'>" + destxt[currentIdx[0]] + "</div></html>");
        textPanel.add(destxtLabel, BorderLayout.CENTER);

        // 메인 패널에 추가
        describePanel.add(imagePanel, BorderLayout.CENTER); // imagePanel을 중앙에 배치
        describePanel.add(textPanel, BorderLayout.SOUTH);   // textPanel을 아래쪽에 배치
        describePanel.setPreferredSize(new Dimension(1500, 500));

        JButton exitButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource(imgPath + "exit.png"))));

        exitButton.setPreferredSize(new Dimension(90, 90));
        exitButton.setContentAreaFilled(false); // 배경색 제거
        exitButton.setBorderPainted(false);     // 테두리 제거
        exitButton.addActionListener(e ->{
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });

        JLabel pageNumberLabel = new JLabel("1 / " + images.length);

        JButton prevButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource(imgPath + "prev.png"))));

        prevButton.setPreferredSize(new Dimension(90, 90));
        prevButton.setContentAreaFilled(false); // 배경색 제거
        prevButton.setBorderPainted(false);     // 테두리 제거
        prevButton.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            currentIdx[0] = (currentIdx[0] - 1 + images.length) % images.length;
            destxtLabel.setText("<html>" + destxt[currentIdx[0]] + "</html>");
            imageLabel.setIcon(images[currentIdx[0]]);
            pageNumberLabel.setText((currentIdx[0] + 1) + " / " + images.length);
        });

        JButton nextButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource(imgPath + "next.png"))));

        nextButton.setPreferredSize(new Dimension(90, 90));
        nextButton.setContentAreaFilled(false); // 배경색 제거
        nextButton.setBorderPainted(false);     // 테두리 제거
        nextButton.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            currentIdx[0] = (currentIdx[0] + 1) % images.length;
            destxtLabel.setText("<html>" + destxt[currentIdx[0]] + "</html>");
            imageLabel.setIcon(images[currentIdx[0]]);
            pageNumberLabel.setText((currentIdx[0] + 1) + " / " + images.length);
        });

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경
        backButtonPanel.add(exitButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경

        pageNumberLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        JPanel pageNumberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pageNumberPanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경
        pageNumberPanel.add(pageNumberLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.decode("#FFF8E8")); // 배경색 변경
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        bottomPanel.add(pageNumberPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(backButtonPanel, BorderLayout.NORTH);
        panel.add(describePanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

}
