package cardGame.game.panels;

import cardGame.entity.Board;
import cardGame.entity.Card;
import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.game.components.WoodButton;
import cardGame.game.components.ImagePanel;
import cardGame.game.components.TitleBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 레벨 선택 화면 - 이미지 기반 UI / Level selection screen
 * 레벨과 테마를 선택하여 게임 시작
 */
public class SelectLevelPanel extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private Board board;
    private Sound sound = new Sound();
    // Mainmusic.wav is managed by GameController to avoid restarting between menu and select screens.

    private int selectedLevel = 1;
    private String selectedTheme = "fruit";

    private BufferedImage levelPanelImage;
    private BufferedImage themeFruitIcon;
    private BufferedImage themeVegIcon;
    private BufferedImage lv1Image;
    private BufferedImage lv2Image;
    private BufferedImage lv3Image;
    // 레벨별 캐릭터 이미지
    // Level character images
    private BufferedImage lv1CharacterImage;
    private BufferedImage lv2CharacterImage;
    private BufferedImage lv3CharacterImage;

    // 선택 표시용
    // Selection indicators
    private JPanel level1Box;
    private JPanel level2Box;
    private JPanel level3Box;
    private JPanel fruitBox;
    private JPanel vegBox;

    // ===== 테마 아이콘 표시 관련 상수 =====
    // ===== Theme icon display constants =====
    // 아이콘 크기
    // Icon size
    private static final int THEME_ICON_SIZE = 120;
    // 아이콘의 Y 위치
    // Icon Y position
    private static final int THEME_ICON_Y = 20;
    // 선택 테두리 여백 (값 작을수록 아이콘에 더 딱 맞음)
    // Selection border padding (smaller value = tighter fit around icon)
    private static final int THEME_BORDER_PADDING = 8;

    public SelectLevelPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        loadImages();
    }

    private void loadImages() {
        try {
            levelPanelImage = ImageIO.read(new File("src/cardGame/img/level_panel.png"));
            themeFruitIcon = ImageIO.read(new File("src/cardGame/img/theme_fruit_icon.png"));
            themeVegIcon = ImageIO.read(new File("src/cardGame/img/theme_veg_icon.png"));
            lv1Image = ImageIO.read(new File("src/cardGame/img/Lv.1.png"));
            lv2Image = ImageIO.read(new File("src/cardGame/img/Lv.2.png"));
            lv3Image = ImageIO.read(new File("src/cardGame/img/Lv.3.png"));
            // 레벨별 캐릭터 이미지 로드
            // Load level character images
            lv1CharacterImage = ImageIO.read(new File("src/cardGame/img/Lv.1_character.png"));
            lv2CharacterImage = ImageIO.read(new File("src/cardGame/img/Lv.2_character.png"));
            lv3CharacterImage = ImageIO.read(new File("src/cardGame/img/Lv.3_character.png"));
        } catch (IOException e) {
            System.err.println("レベル選択画像のロード失敗: " + e.getMessage());
            // Failed to load level select images
        }
    }

    public JPanel selectLevel() {
        // 배경 패널
        // Background panel
        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(1920, 1080));
        panel.setLayout(null);

        gameController.playMenuBGM();

        // 타이틀
        // Title
        TitleBoard titleBoard = new TitleBoard("テーマ / レベル");
        titleBoard.setBounds(710, 0, 500, 150);
        panel.add(titleBoard);

        // 테마 박스 2개
        // 2 Theme boxes
        // 텍스트는 제거했으므로 label 인자는 받지 않음
        // Text is removed, so no label argument
        fruitBox = createThemeBox(themeFruitIcon, "fruit");
        fruitBox.setBounds(700, 250, 200, 200);
        panel.add(fruitBox);

        vegBox = createThemeBox(themeVegIcon, "vegetable");
        vegBox.setBounds(1020, 250, 200, 200);
        panel.add(vegBox);

        // 레벨 박스 3개
        // 3 Level boxes
        level1Box = createLevelBox(lv1Image, lv1CharacterImage, 1);
        level1Box.setBounds(450, 550, 280, 260);
        panel.add(level1Box);

        level2Box = createLevelBox(lv2Image, lv2CharacterImage, 2);
        level2Box.setBounds(820, 550, 280, 260);
        panel.add(level2Box);

        level3Box = createLevelBox(lv3Image, lv3CharacterImage, 3);
        level3Box.setBounds(1190, 550, 280, 260);
        panel.add(level3Box);

        // 메뉴 이동 버튼 (좌측)
        // Go to menu button (left)
        WoodButton backBtn = new WoodButton("メニューへ");
        backBtn.setBounds(660, 880, 300, 70);
        panel.add(backBtn);

        // 스타트 버튼 (우측)
        // Start button (right)
        WoodButton startBtn = new WoodButton("スタート");
        startBtn.setBounds(960, 880, 300, 70);
        panel.add(startBtn);

        // 초기 선택 표시
        // Initial selection
        updateLevelSelection(1);
        updateThemeSelection("fruit");

        // 이벤트
        // Events
        startBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);

            // [변경] 모든 레벨이 4x5 보드 사용 (10쌍, 20장)
            // [Changed] All levels use 4x5 board (10 pairs, 20 cards)
            // 레벨 차이는 컴퓨터 AI(smartChoices) 로직 차이로만 구분
            // Level difference is only determined by computer AI (smartChoices) logic
            GameController.rows = 4;
            GameController.cols = 5;
            GameController.level = selectedLevel;

            // 테마 설정 (Card 클래스의 static 변수)
            // Set theme
            Card.cardTheme = selectedTheme;

            // 4x5 = 20장 카드 = 10쌍
            // 4x5 = 20 cards = 10 pairs
            board = new Board(GameController.rows, GameController.cols);
            gameController.stopMenuBGM();
            gameController.switchToPanel("startGame", loginedUser, board);
        });

        backBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });

        return panel;
    }

    /**
     * 레벨 박스 생성 / Create level box
     */
    private JPanel createLevelBox(BufferedImage lvImage, BufferedImage characterImage, int level) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 패널 배경
                // Panel background
                if (levelPanelImage != null) {
                    g2.drawImage(levelPanelImage, 0, 0, getWidth(), getHeight(), null);
                }

                // 레벨 이미지 (상단 둥근 부분에 위치)
                // Level image (top circle area)
                if (lvImage != null) {
                    int lvW = 120;
                    int lvH = 50;
                    int lvX = (getWidth() - lvW) / 2;
                    int lvY = 20;
                    g2.drawImage(lvImage, lvX, lvY, lvW, lvH, null);
                }

                // 캐릭터 이미지 (level_panel.png 중앙 갈색 공간 가운데에 표시)
                // Character image (centered in the brown area of level_panel.png)
                if (characterImage != null) {
                    int charW = 130;
                    int charH = 130;
                    int charX = (getWidth() - charW) / 2;
                    int charY = (getHeight() - charH) / 2 + 25;
                    g2.drawImage(characterImage, charX, charY, charW, charH, null);
                }

                // 선택 표시 (기존 유지)
                // Selection border
                if (selectedLevel == level) {
                    g2.setColor(new Color(255, 215, 0));
                    g2.setStroke(new BasicStroke(5));
                    g2.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                }
            }
        };

        box.setOpaque(false);
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));

        box.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                sound.play("BtnClick.wav", false, -10.0f);
                updateLevelSelection(level);
            }
        });

        return box;
    }

    /**
     * 테마 박스 생성 / Create theme box
     * - 텍스트는 그리지 않음
     * - 선택 테두리는 아이콘 주위에만 그림
     */
    private JPanel createThemeBox(BufferedImage iconImage, String theme) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 아이콘 위치 계산
                // Calculate icon position
                int iconX = (getWidth() - THEME_ICON_SIZE) / 2;
                int iconY = THEME_ICON_Y;

                // 아이콘 그리기
                // Draw icon
                if (iconImage != null) {
                    g2.drawImage(iconImage, iconX, iconY, THEME_ICON_SIZE, THEME_ICON_SIZE, null);
                }

                // 선택 표시 (아이콘 크기에 맞춤)
                // Selection indicator (fit to icon size)
                if (selectedTheme.equals(theme)) {
                    g2.setColor(new Color(255, 215, 0));
                    g2.setStroke(new BasicStroke(5));

                    int borderX = iconX - THEME_BORDER_PADDING;
                    int borderY = iconY - THEME_BORDER_PADDING;
                    int borderW = THEME_ICON_SIZE + THEME_BORDER_PADDING * 2;
                    int borderH = THEME_ICON_SIZE + THEME_BORDER_PADDING * 2;

                    g2.drawRoundRect(borderX, borderY, borderW, borderH, 18, 18);
                }
            }
        };

        box.setOpaque(false);
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));

        box.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                sound.play("BtnClick.wav", false, -10.0f);
                updateThemeSelection(theme);
            }
        });

        return box;
    }

    private void updateLevelSelection(int level) {
        selectedLevel = level;
        if (level1Box != null) level1Box.repaint();
        if (level2Box != null) level2Box.repaint();
        if (level3Box != null) level3Box.repaint();
    }

    private void updateThemeSelection(String theme) {
        selectedTheme = theme;
        if (fruitBox != null) fruitBox.repaint();
        if (vegBox != null) vegBox.repaint();
    }

    public Board getBoard() {
        return board;
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
