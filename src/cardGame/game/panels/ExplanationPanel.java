package cardGame.game.panels;

import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.game.components.ImagePanel;
import cardGame.game.components.TitleBoard;
import cardGame.game.components.WoodButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;





/**
 * 튜토리얼 이미지를 페이지별로 보여주는 설명 화면입니다.
 * Help screen that displays tutorial images page by page.
 */
public class ExplanationPanel extends JPanel {
    private final GameController gameController;
    private final User loginedUser;
    private final Sound sound = new Sound();

    private final BufferedImage[] explainPageImages = new BufferedImage[4];
    private BufferedImage arrowLeftImage;
    private BufferedImage arrowRightImage;

    private int currentPage = 0;
    private final int totalPages = 4;

    private JPanel explainPanel;
    private JPanel descriptionPanel;

    private static final int SCREEN_W = 1920;
    private static final int SCREEN_H = 1080;

    


    private static final String[][] PAGE_TEXTS = {
            {"テーマとレベルを選んでゲームスタート！"},
            {"カードの位置を覚えて、", "同じ絵柄のペアをそろえましょう！"},
            {"連続成功でCOMBO！", "ボーナススコアをゲット！"},
            {"たくさんペアをそろえて、", "高得点を目指そう！"}
    };

    public ExplanationPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        loadImages();
    }

    private void loadImages() {
        try {
            explainPageImages[0] = ImageIO.read(new File("src/cardGame/img/wood_panel_explain1.png"));
            explainPageImages[1] = ImageIO.read(new File("src/cardGame/img/wood_panel_explain2.png"));
            explainPageImages[2] = ImageIO.read(new File("src/cardGame/img/wood_panel_explain3.png"));
            explainPageImages[3] = ImageIO.read(new File("src/cardGame/img/wood_panel_explain4.png"));

            arrowLeftImage = ImageIO.read(new File("src/cardGame/img/arrow_left.png"));
            arrowRightImage = ImageIO.read(new File("src/cardGame/img/arrow_right.png"));
        } catch (IOException e) {
            System.err.println("설명 패널 이미지 로드 실패: " + e.getMessage());
        }
    }

    /**
     * 설명 화면 전체 UI를 생성하고 페이지 이동 버튼을 연결합니다.
     * Builds the full help screen UI and connects page navigation buttons.
     */
    public JPanel showExplanation() {
        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        panel.setLayout(null);

        
        
        
        int titleW = 500;
        int titleH = 150;
        int titleX = (SCREEN_W - titleW) / 2;

        
        int titleY = 0;

        TitleBoard titleBoard = new TitleBoard("遊び方");
        titleBoard.setBounds(titleX, titleY, titleW, titleH);
        panel.add(titleBoard);

        
        
        
        int explainW = 900;
        int explainH = 700;

        


        int explainOffsetY = -60;

        int explainX = (SCREEN_W - explainW) / 2;
        int explainY = (SCREEN_H - explainH) / 2 + explainOffsetY;

        explainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                );
                g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                );

                BufferedImage currentImage = explainPageImages[currentPage];
                if (currentImage != null) {
                    g2.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);
                }

                drawPageNumber(g2, currentPage + 1, getWidth(), getHeight());

                g2.dispose();
            }
        };

        explainPanel.setOpaque(false);
        explainPanel.setLayout(null);
        explainPanel.setBounds(explainX, explainY, explainW, explainH);
        panel.add(explainPanel);

        
        
        
        
        int textAreaH = 65; 

        



        int panelToTextGap = 72; 

        


        int textToButtonGap = 40;

        








        int bottomGroupOffsetY = -80;

        int backBtnW = 300;
        int backBtnH = 72;
        int arrowSize = 72;
        int arrowGapFromMenu = 100;

        int descriptionY = explainY + explainH + panelToTextGap + bottomGroupOffsetY;

        descriptionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                );

                drawPageDescription(g2, getWidth(), getHeight());

                g2.dispose();
            }
        };

        descriptionPanel.setOpaque(false);

        



        descriptionPanel.setBounds(
                explainX - 80,
                descriptionY,
                explainW + 160,
                textAreaH
        );
        panel.add(descriptionPanel);

        int controlY = descriptionY + textAreaH + textToButtonGap;
        int backBtnX = (SCREEN_W - backBtnW) / 2;

        WoodButton backBtn = new WoodButton("メニューへ");
        backBtn.setBounds(backBtnX, controlY, backBtnW, backBtnH);
        panel.add(backBtn);

        JButton leftArrow = createArrowButton(arrowLeftImage);
        leftArrow.setBounds(
                backBtnX - arrowGapFromMenu - arrowSize,
                controlY,
                arrowSize,
                arrowSize
        );
        panel.add(leftArrow);

        JButton rightArrow = createArrowButton(arrowRightImage);
        rightArrow.setBounds(
                backBtnX + backBtnW + arrowGapFromMenu,
                controlY,
                arrowSize,
                arrowSize
        );
        panel.add(rightArrow);

        leftArrow.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);

            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });

        rightArrow.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);

            if (currentPage < totalPages - 1) {
                currentPage++;
                updatePage();
            }
        });

        backBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });

        return panel;
    }

    private void updatePage() {
        if (explainPanel != null) {
            explainPanel.repaint();
        }

        if (descriptionPanel != null) {
            descriptionPanel.repaint();
        }
    }

    


    /**
     * 상단 원형 배지 안에 현재 페이지 번호를 그립니다.
     * Draws the current page number inside the top circular badge.
     */
    private void drawPageNumber(Graphics2D g2, int pageNumber, int panelW, int panelH) {
        int numberCenterX = panelW / 2;

        






        int numberCenterY = (int) (panelH * 0.178);

        


        int numberOffsetX = 0;
        int numberOffsetY = 8;

        


        int fontSize = 44;

        String text = String.valueOf(pageNumber);
        Font font = createFont(fontSize, Font.BOLD);

        drawOutlinedCenteredTextByCenter(
                g2,
                text,
                font,
                numberCenterX + numberOffsetX,
                numberCenterY + numberOffsetY,
                Color.WHITE,
                new Color(92, 47, 19),
                3
        );
    }

    


    /**
     * 현재 페이지에 맞는 하단 설명 문구를 그립니다.
     * Draws the bottom description text for the current page.
     */
    private void drawPageDescription(Graphics2D g2, int areaW, int areaH) {
        String[] lines = PAGE_TEXTS[currentPage];

        int centerX = areaW / 2;

        


        int fontSize = 28;

        


        int lineGap = 34;

        




        int singleLineY = 42;

        


        int firstLineY = 24;

        


        int secondLineY = firstLineY + lineGap;

        Font font = createFont(fontSize, Font.BOLD);
        Color fillColor = Color.WHITE;
        Color outlineColor = new Color(92, 47, 19);

        if (lines.length == 1) {
            drawOutlinedCenteredText(
                    g2,
                    lines[0],
                    font,
                    centerX,
                    singleLineY,
                    fillColor,
                    outlineColor,
                    3
            );
        } else {
            drawOutlinedCenteredText(
                    g2,
                    lines[0],
                    font,
                    centerX,
                    firstLineY,
                    fillColor,
                    outlineColor,
                    3
            );

            drawOutlinedCenteredText(
                    g2,
                    lines[1],
                    font,
                    centerX,
                    secondLineY,
                    fillColor,
                    outlineColor,
                    3
            );
        }
    }

    



    private void drawOutlinedCenteredTextByCenter(
            Graphics2D g2,
            String text,
            Font font,
            int centerX,
            int centerY,
            Color fillColor,
            Color outlineColor,
            int outlineSize
    ) {
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int baselineY = centerY + (fm.getAscent() - fm.getDescent()) / 2;

        drawOutlinedCenteredText(
                g2,
                text,
                font,
                centerX,
                baselineY,
                fillColor,
                outlineColor,
                outlineSize
        );
    }

    



    private void drawOutlinedCenteredText(
            Graphics2D g2,
            String text,
            Font font,
            int centerX,
            int baselineY,
            Color fillColor,
            Color outlineColor,
            int outlineSize
    ) {
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int x = centerX - (textW / 2);

        g2.setColor(outlineColor);
        for (int dx = -outlineSize; dx <= outlineSize; dx++) {
            for (int dy = -outlineSize; dy <= outlineSize; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                g2.drawString(text, x + dx, baselineY + dy);
            }
        }

        g2.setColor(fillColor);
        g2.drawString(text, x, baselineY);
    }

    private JButton createArrowButton(BufferedImage image) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (image != null) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );

                    g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                    g2.dispose();
                }
            }
        };

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private Font createFont(int size, int style) {
        int resolvedStyle = style | Font.BOLD;
        String[] fontNames = {
                "Yu Gothic UI",
                "Meiryo",
                "MS Gothic",
                "Hiragino Sans",
                "Dialog"
        };

        for (String fontName : fontNames) {
            Font font = new Font(fontName, resolvedStyle, size);

            if (font.getFamily() != null) {
                return font;
            }
        }

        return new Font("Dialog", resolvedStyle, size);
    }
}
