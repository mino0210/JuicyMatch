package cardGame.game.panels;

import cardGame.entity.Board;
import cardGame.entity.User;
import cardGame.game.GameController;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static cardGame.entity.Card.cardTheme;
import static cardGame.game.GameController.*;

public class SelectLevelPanel extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private Board board;

    public SelectLevelPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
    }

    public Board getBoard(){
        return board;
    }

    public JPanel selectLevel() {
        gameController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] themes = { "Fruit", "vegetable" };
        // UI 텍스트를 레벨 명칭으로 변경
        String[] levels = { "LEVEL 1", "LEVEL 2", "LEVEL 3" };
        String imgPath = FrontImagePath + "/level/";

        int themeWidth = 100;
        int themeHeight = 100;
        int levelWidth = 350;
        int levelHeight = 300;

        // 이미지 로드 (경로 에러 방지를 위해 예외 처리 포함된 메서드 사용)
        ImageIcon vegetableImg = createScaledIcon(FrontImagePath + "/fruit/fruit09.png", themeWidth, themeHeight);
        ImageIcon fruitImage = createScaledIcon(FrontImagePath + "/vegetable/vegetable08.png", themeWidth, themeHeight);

        // 모든 난이도에서 5X4 이미지(3.png)를 사용하도록 설정
        ImageIcon[] levelImages = {
                createScaledIcon(imgPath + "3.png", levelWidth, levelHeight),
                createScaledIcon(imgPath + "3.png", levelWidth, levelHeight),
                createScaledIcon(imgPath + "3.png", levelWidth, levelHeight)
        };

        Border normalBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        Border hoverBorder = BorderFactory.createLineBorder(Color.BLACK, 3);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topSection = createThemePanel(themes, vegetableImg, fruitImage, normalBorder, hoverBorder, themeWidth, themeHeight);
        JPanel bottomSection = createLevelPanel(levels, levelImages, normalBorder, hoverBorder, levelWidth, levelHeight);

        // 하단 버튼 구성
        JButton confirmBtn = initBtn("확인", 300, 40, Color.white);
        JButton backBtn = initBtn("메뉴로", 300, 40, Color.white);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPanel.setOpaque(false);

        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(confirmBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(backBtn);
        btnPanel.add(Box.createHorizontalGlue());

        confirmBtn.addActionListener(e -> {
            String selectedTheme = getSelectedRadioText(topSection);
            String selectedLevel = getSelectedRadioText(bottomSection);

            if (selectedTheme != null && selectedTheme.equals("vegetable") && loginedUser.getTotalScore() < 1000) {
                JOptionPane.showMessageDialog(gameController, "조건을 만족하지 않아 'vegetable' 테마를 선택할 수 없습니다.\n총점수 1000점 이상이어야 합니다.",
                        "선택 불가", JOptionPane.WARNING_MESSAGE);
            }
            else {
                if(selectedLevel != null && selectedTheme != null){
                    setupThemeAndLevel(selectedTheme, selectedLevel);
                    gameController.switchToPanel("startGame", loginedUser, board);
                }
                else {
                    JOptionPane.showMessageDialog(gameController, "테마 혹은 레벨을 모두 선택해주세요.");
                }
            }
        });

        backBtn.addActionListener(e -> gameController.switchToPanel("gameMenu", loginedUser));

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(Box.createVerticalGlue());
        wrapperPanel.add(btnPanel);
        wrapperPanel.add(Box.createVerticalStrut(40));

        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);
        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(bottomSection, BorderLayout.CENTER);

        return mainPanel;
    }

    private void setupThemeAndLevel(String theme, String levelText) {
        cardTheme = theme + "/" + theme;

        // 보드 크기는 어떤 레벨을 선택해도 4X5로 고정
        GameController.rows = 4;
        GameController.cols = 5;

        // 선택한 텍스트에 따라 난이도 숫자 설정
        if (levelText.equals("LEVEL 2")) {
            GameController.level = 2;
        } else if (levelText.equals("LEVEL 3")) {
            GameController.level = 3;
        } else {
            GameController.level = 1;
        }

        this.board = new Board(GameController.rows, GameController.cols);
    }

    private JPanel createThemePanel(String[] themes, ImageIcon vegetableImg, ImageIcon fruitImage, Border normalBorder,
                                    Border hoverBorder, int width, int height) {
        JPanel themePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("테마 선택", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel themeSelection = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 50));
        ButtonGroup themeGroup = new ButtonGroup();

        themeSelection.add(createSelectablePanel(themes[0], vegetableImg, themeGroup, normalBorder, hoverBorder, width, height));
        themeSelection.add(createSelectablePanel(themes[1], fruitImage, themeGroup, normalBorder, hoverBorder, width, height));

        themePanel.add(title, BorderLayout.NORTH);
        themePanel.add(themeSelection, BorderLayout.CENTER);

        return themePanel;
    }

    private JPanel createLevelPanel(String[] levels, ImageIcon[] images, Border normalBorder, Border hoverBorder,
                                    int width, int height) {
        JPanel levelPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("레벨 선택", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JPanel levelSelection = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 40));
        ButtonGroup levelGroup = new ButtonGroup();

        for (int i = 0; i < levels.length; i++) {
            levelSelection.add(createSelectablePanel(levels[i], images[i], levelGroup, normalBorder, hoverBorder, width, height));
        }

        levelPanel.add(title, BorderLayout.NORTH);
        levelPanel.add(levelSelection, BorderLayout.CENTER);
        return levelPanel;
    }

    private JPanel createSelectablePanel(String text, ImageIcon image, ButtonGroup group, Border normalBorder,
                                         Border hoverBorder, int windowWidth, int windowHeight) {
        JPanel panel = new JPanel(new BorderLayout());
        JRadioButton radioButton = new JRadioButton(text);
        group.add(radioButton);

        JPanel imagePanel = new JPanel(new GridLayout(1, 1));
        imagePanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        imagePanel.setBorder(normalBorder);
        imagePanel.add(new JLabel(image));

        imagePanel.addMouseListener(createMouseListener(imagePanel, normalBorder, hoverBorder, radioButton));

        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(radioButton, BorderLayout.SOUTH);
        return panel;
    }

    private MouseAdapter createMouseListener(JPanel panel, Border normalBorder, Border hoverBorder,
                                             JRadioButton radioButton) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(hoverBorder);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(normalBorder);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                radioButton.setSelected(true);
            }
        };
    }

    private ImageIcon createScaledIcon(String path, int width, int height) {
        try {
            return new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(path)))
                    .getImage()
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("이미지 로드 실패: " + path);
            return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        }
    }

    private String getSelectedRadioText(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JPanel) {
                String result = getSelectedRadioText((JPanel) component);
                if (result != null) return result;
            } else if (component instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) component;
                if (radioButton.isSelected()) return radioButton.getText();
            }
        }
        return null;
    }

    private JButton initBtn(String text, int width, int height, Color background) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setBackground(background);
        btn.setForeground(Color.black);
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        return btn;
    }
}