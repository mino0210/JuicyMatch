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
 * 설명 화면 - 페이지형 튜토리얼 패널
 * Explanation screen with 4 tutorial pages.
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

    /**
     * 페이지별 하단 설명 문구
     */
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

    public JPanel showExplanation() {
        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        panel.setLayout(null);

        // =========================
        // 타이틀 위치 조정값 / Title position tuning
        // =========================
        int titleW = 500;
        int titleH = 150;
        int titleX = (SCREEN_W - titleW) / 2;

        // 타이틀만 위/아래로 움직이고 싶으면 이 값 수정
        int titleY = 0;

        TitleBoard titleBoard = new TitleBoard("遊び方");
        titleBoard.setBounds(titleX, titleY, titleW, titleH);
        panel.add(titleBoard);

        // =========================
        // 설명 패널 위치 / 크기 조정값
        // =========================
        int explainW = 900;
        int explainH = 700;

        /*
         * 설명 패널 자체의 Y 위치
         */
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

        // =========================
        // 하단 설명 텍스트 / 메뉴버튼 / 화살표 위치 조정값
        // 설명 패널과 분리해서 조정할 수 있게 따로 뺐습니다.
        // =========================
        int textAreaH = 65; //90

        /*
         * 설명 이미지와 하단 텍스트 사이 기본 간격
         * 이 값은 explainPanel 기준 기본 위치 계산에 사용됩니다.
         */
        int panelToTextGap = 72; //24

        /*
         * 하단 텍스트와 메뉴/화살표 버튼 사이 간격
         */
        int textToButtonGap = 40;

        /*
         * 하단 그룹 전체 이동값
         * 대상: 설명 텍스트 + 왼쪽 화살표 + 오른쪽 화살표 + メニューへ 버튼
         *
         * 값 증가: 하단 그룹이 아래로 이동
         * 값 감소: 하단 그룹이 위로 이동
         *
         * 현재 화면에서 텍스트와 버튼을 위로 끌어올리고 싶다고 하셨으므로 -80 적용
         */
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

        /*
         * 설명 텍스트 영역
         * X, W를 넓게 잡아 긴 일본어 문장이 잘리지 않게 했습니다.
         */
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
     * 원형 번호 위치 세부조정용 메서드
     */
    private void drawPageNumber(Graphics2D g2, int pageNumber, int panelW, int panelH) {
        int numberCenterX = panelW / 2;

        /*
         * 숫자 원형 배지 중심 Y
         * 값 증가: 숫자가 아래로 이동
         * 값 감소: 숫자가 위로 이동
         *
         * 현재 0.178은 wood_panel_explain1~4 기준 원형 중앙에 맞춘 값입니다.
         */
        int numberCenterY = (int) (panelH * 0.178);

        /*
         * 숫자 미세조정
         */
        int numberOffsetX = 0;
        int numberOffsetY = 8;

        /*
         * 숫자 크기
         */
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
     * 하단 설명 문구 위치 세부조정용 메서드
     */
    private void drawPageDescription(Graphics2D g2, int areaW, int areaH) {
        String[] lines = PAGE_TEXTS[currentPage];

        int centerX = areaW / 2;

        /*
         * 설명 문구 크기
         */
        int fontSize = 28;

        /*
         * 2줄 문구 줄 간격
         */
        int lineGap = 34;

        /*
         * 1줄 문구 Y 위치
         * 값 증가: 아래로 이동
         * 값 감소: 위로 이동
         */
        int singleLineY = 42;

        /*
         * 2줄 문구 첫 줄 Y 위치
         */
        int firstLineY = 24;

        /*
         * 2줄 문구 둘째 줄 Y 위치
         */
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

    /**
     * centerY를 기준으로 텍스트를 세로 중앙 정렬해서 그림
     * 숫자처럼 원형 안에 넣어야 하는 텍스트에 사용합니다.
     */
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

    /**
     * baselineY 기준 중앙 정렬 텍스트
     * 설명 문구처럼 baseline을 직접 조정하고 싶은 텍스트에 사용합니다.
     */
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