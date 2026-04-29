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





public class SelectLevelPanel extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private Board board;
    private Sound sound = new Sound();
    

    private int selectedLevel = 1;
    private String selectedTheme = "fruit";

    private BufferedImage levelPanelImage;
    private BufferedImage themeFruitIcon;
    private BufferedImage themeVegIcon;
    private BufferedImage lv1Image;
    private BufferedImage lv2Image;
    private BufferedImage lv3Image;
    
    
    private BufferedImage lv1CharacterImage;
    private BufferedImage lv2CharacterImage;
    private BufferedImage lv3CharacterImage;

    
    
    private JPanel level1Box;
    private JPanel level2Box;
    private JPanel level3Box;
    private JPanel fruitBox;
    private JPanel vegBox;

    
    
    
    
    private static final int THEME_ICON_SIZE = 120;
    
    
    private static final int THEME_ICON_Y = 20;
    
    
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
            
            
            lv1CharacterImage = ImageIO.read(new File("src/cardGame/img/Lv.1_character.png"));
            lv2CharacterImage = ImageIO.read(new File("src/cardGame/img/Lv.2_character.png"));
            lv3CharacterImage = ImageIO.read(new File("src/cardGame/img/Lv.3_character.png"));
        } catch (IOException e) {
            System.err.println("レベル選択画像のロード失敗: " + e.getMessage());
            
        }
    }

    public JPanel selectLevel() {
        
        
        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(1920, 1080));
        panel.setLayout(null);

        gameController.playMenuBGM();

        
        
        TitleBoard titleBoard = new TitleBoard("テーマ / レベル");
        titleBoard.setBounds(710, 0, 500, 150);
        panel.add(titleBoard);

        
        
        
        
        fruitBox = createThemeBox(themeFruitIcon, "fruit");
        fruitBox.setBounds(700, 250, 200, 200);
        panel.add(fruitBox);

        vegBox = createThemeBox(themeVegIcon, "vegetable");
        vegBox.setBounds(1020, 250, 200, 200);
        panel.add(vegBox);

        
        
        level1Box = createLevelBox(lv1Image, lv1CharacterImage, 1);
        level1Box.setBounds(450, 550, 280, 260);
        panel.add(level1Box);

        level2Box = createLevelBox(lv2Image, lv2CharacterImage, 2);
        level2Box.setBounds(820, 550, 280, 260);
        panel.add(level2Box);

        level3Box = createLevelBox(lv3Image, lv3CharacterImage, 3);
        level3Box.setBounds(1190, 550, 280, 260);
        panel.add(level3Box);

        
        
        WoodButton backBtn = new WoodButton("メニューへ");
        backBtn.setBounds(660, 880, 300, 70);
        panel.add(backBtn);

        
        
        WoodButton startBtn = new WoodButton("スタート");
        startBtn.setBounds(960, 880, 300, 70);
        panel.add(startBtn);

        
        
        updateLevelSelection(1);
        updateThemeSelection("fruit");

        
        
        startBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);

            
            
            
            
            GameController.rows = 4;
            GameController.cols = 5;
            GameController.level = selectedLevel;

            
            
            Card.cardTheme = selectedTheme;

            
            
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

                
                
                if (levelPanelImage != null) {
                    g2.drawImage(levelPanelImage, 0, 0, getWidth(), getHeight(), null);
                }

                
                
                if (lvImage != null) {
                    int lvW = 120;
                    int lvH = 50;
                    int lvX = (getWidth() - lvW) / 2;
                    int lvY = 20;
                    g2.drawImage(lvImage, lvX, lvY, lvW, lvH, null);
                }

                
                
                if (characterImage != null) {
                    int charW = 130;
                    int charH = 130;
                    int charX = (getWidth() - charW) / 2;
                    int charY = (getHeight() - charH) / 2 + 25;
                    g2.drawImage(characterImage, charX, charY, charW, charH, null);
                }

                
                
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

                
                
                int iconX = (getWidth() - THEME_ICON_SIZE) / 2;
                int iconY = THEME_ICON_Y;

                
                
                if (iconImage != null) {
                    g2.drawImage(iconImage, iconX, iconY, THEME_ICON_SIZE, THEME_ICON_SIZE, null);
                }

                
                
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
