package cardGame.game.panels;

import cardGame.database.RecordDAO;
import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.game.components.ImagePanel;
import cardGame.game.components.TitleBoard;
import cardGame.game.components.WoodButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 랭킹 화면
 * - ranking_panel.png 내부 스크롤 구조
 * - 첫 줄 포함 모든 랭킹 줄이 큰 보드판 내부에 들어가도록 조정
 * - 상세 펼침은 보드판 내부 스크롤로 처리
 */
public class RankingPanel extends JPanel {
    private final GameController gameController;
    private final User loginedUser;
    private final Sound sound = new Sound();

    private BufferedImage rankingPanelImage;
    // 내 점수 보기 토글 / My record toggle
    private boolean showMyRecordOnly = false;
    private JPanel rankingListPanelRef;

    // 화면 크기
    // =========================
    // =========================
    private static final int SCREEN_W = 1920;
    private static final int SCREEN_H = 1080;

    // 상단 간판
    // Top signboard
    // 로프 끝이 화면 상단에 닿도록 위치 조정 (설명, 선택 화면과 동일)
    // Position so rope tips touch screen top (same as explanation, select screens)
    private static final int TITLE_W = 500;
    private static final int TITLE_H = 150;
    private static final int TITLE_X = (SCREEN_W - TITLE_W) / 2;
    private static final int TITLE_Y = 0;

    // 뒤의 큰 보드판 (ranking_panel.png)
    // =========================
    // =========================
    private static final int PANEL_W = 1250;
    private static final int PANEL_H = 950;
    private static final int PANEL_X = (SCREEN_W - PANEL_W) / 2;

    /*
     * [가이드] [Guide]
     * 이 값을 줄이면 큰 보드판 전체가 위로 올라갑니다.
     * Reducing this value moves the whole large board upward.
     * 이번엔 "현재보다 조금 더 위" 요청 반영하여 기존보다 올려둠.
     * Set higher than before to reflect "slightly higher than current" request.
     */
    private static final int PANEL_Y = -10;

    // 보드판 내부 랭킹 리스트 표시 영역
    // =========================
    // =========================
    /*
     * [가이드] [Guide]
     * LIST_Y를 키우면 보드판 안에서 랭킹 줄 묶음이 아래로 내려갑니다.
     * Increasing LIST_Y moves ranking rows down inside the board.
     * 첫 줄이 보드판 테두리 밖에 "걸치는" 문제를 해결하기 위해
     * To solve the issue of first row "overlapping" the board border,
     * 이 값을 조금 더 크게 잡았습니다.
     * this value is set slightly larger.
     */
    private static final int LIST_W = 1140;
    private static final int LIST_X = (PANEL_W - LIST_W) / 2;
    private static final int LIST_Y = 225;
    private static final int LIST_H = 595;

    /*
     * 스크롤 내부 맨 위 여백.
     * Top inner margin of scroll area.
     * 첫 줄이 너무 위에 붙는 느낌이면 이 값을 늘리면 됩니다.
     * Increase this value if the first row feels too close to the top.
     */
    private static final int TOP_GAP_IN_LIST = 6;

    // 랭킹 행 크기
    // =========================
    // =========================
    private static final int ROW_W = 1085;
    private static final int ROW_H = 92;
    private static final int ROW_GAP = 14;
    private static final int EXPANDED_EXTRA_H = 210;

    // 뒤로가기 버튼
    // Back button
    // =========================
    // =========================
    private static final int BACK_BTN_W = 300;
    private static final int BACK_BTN_H = 70;
    private static final int BACK_BTN_X = (SCREEN_W - BACK_BTN_W) / 2;
    private static final int BACK_BTN_Y = 880;

    public RankingPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        loadImages();
    }

    private void loadImages() {
        try {
            rankingPanelImage = ImageIO.read(new File("src/cardGame/img/ranking_panel.png"));
        } catch (IOException e) {
            System.err.println("랭킹 패널 이미지 로드 실패: " + e.getMessage());
            // Failed to load ranking panel image
        }
    }

    public JPanel showRanking() {
        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        panel.setLayout(null);

        // 상단 간판
        // Top signboard
        TitleBoard titleBoard = new TitleBoard("きろく");
        titleBoard.setBounds(TITLE_X, TITLE_Y, TITLE_W, TITLE_H);
        panel.add(titleBoard);

        // 뒤의 큰 보드판
        // Background large board
        JPanel rankingBgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                );
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                if (rankingPanelImage != null) {
                    g2.drawImage(rankingPanelImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(115, 63, 25, 235),
                            getWidth(), getHeight(), new Color(85, 42, 15, 235)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);

                    g2.setColor(new Color(255, 201, 44));
                    g2.setStroke(new BasicStroke(6));
                    g2.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 35, 35);
                }

                g2.dispose();
            }
        };

        rankingBgPanel.setOpaque(false);
        rankingBgPanel.setLayout(null);
        rankingBgPanel.setBounds(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);
        panel.add(rankingBgPanel);

        // 실제 랭킹 줄이 들어가는 패널
        // Panel containing actual ranking rows
        JPanel rankingListPanel = new JPanel();
        rankingListPanel.setLayout(new BoxLayout(rankingListPanel, BoxLayout.Y_AXIS));
        rankingListPanel.setOpaque(false);
        rankingListPanelRef = rankingListPanel;

        // 첫 줄이 보드 상단에 붙지 않도록 내부 여백
        // Inner padding so first row does not stick to board top
        rankingListPanel.add(Box.createVerticalStrut(TOP_GAP_IN_LIST));

        loadRankingItems(rankingListPanel);

        JScrollPane scrollPane = new JScrollPane(rankingListPanel);
        scrollPane.setBounds(LIST_X, LIST_Y, LIST_W, LIST_H);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(28);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(18, 0));
        scrollPane.getVerticalScrollBar().setUI(new RankingScrollBarUI());

        rankingBgPanel.add(scrollPane);

        // 마이레코드 버튼 - ranking_panel.png 우측 상단
        // My record button - top-right of ranking_panel.png
        WoodButton myRecordBtn = new WoodButton("マイレコード", 200, 60);
        myRecordBtn.setBounds(PANEL_W - 230, 130, 200, 60);
        rankingBgPanel.add(myRecordBtn);
        rankingBgPanel.setComponentZOrder(myRecordBtn, 0);

        myRecordBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            showMyRecordOnly = !showMyRecordOnly;
            myRecordBtn.setButtonText(showMyRecordOnly ? "ぜんたい" : "マイレコード");
            // 리스트 다시 로드 / Reload list
            rankingListPanelRef.removeAll();
            rankingListPanelRef.add(Box.createVerticalStrut(TOP_GAP_IN_LIST));
            loadRankingItems(rankingListPanelRef);
            rankingListPanelRef.revalidate();
            rankingListPanelRef.repaint();
        });

        // 뒤로가기 버튼
        // Back button
        WoodButton backBtn = new WoodButton("戻る");
        backBtn.setBounds(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H);
        panel.add(backBtn);

        backBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });

        return panel;
    }

    private void loadRankingItems(JPanel rankingListPanel) {
        try {
            RecordDAO recordDAO = new RecordDAO();
            List<Map<String, Object>> rankings = recordDAO.getGlobalRankings();

            if (rankings == null || rankings.isEmpty()) {
                JLabel emptyLabel = new JLabel("きろくがありません。", SwingConstants.CENTER);
                emptyLabel.setFont(createFont(30, Font.BOLD));
                emptyLabel.setForeground(new Color(255, 250, 240));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                rankingListPanel.add(Box.createVerticalStrut(220));
                rankingListPanel.add(emptyLabel);
                return;
            }

            // 로그인된 사용자 ID / Logged-in user ID
            String loginedUsername = (loginedUser != null) ? loginedUser.getUsername() : null;
            int rank = 1;
            ExpandableRankingRow myRow = null;

            for (Map<String, Object> rankData : rankings) {
                String username = safeString(rankData.get("username"));
                String nickname = safeString(rankData.get("nickname"));
                int totalSum = safeInt(rankData.get("totalSum"));

                String displayName = !nickname.isBlank() ? nickname : username;

                // 마이레코드 모드: 내 항목만 표시 / My record mode: only show my record
                if (showMyRecordOnly && loginedUsername != null && !username.equals(loginedUsername)) {
                    rank++;
                    continue;
                }

                ExpandableRankingRow row = new ExpandableRankingRow(rank, username, displayName, totalSum);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);

                // 내 행이면 표시 / Mark if this is my row
                if (loginedUsername != null && username.equals(loginedUsername)) {
                    myRow = row;
                }

                rankingListPanel.add(row);
                rankingListPanel.add(Box.createVerticalStrut(ROW_GAP));

                rank++;

                if (rank > 50) {
                    break;
                }
            }

            // 마이레코드 모드일 때 내 항목 자동 펼침
            // Auto-expand my row when in my-record mode
            if (showMyRecordOnly && myRow != null) {
                final ExpandableRankingRow finalMyRow = myRow;
                SwingUtilities.invokeLater(() -> {
                    finalMyRow.setExpandedState(true);
                });
            }

        } catch (Exception e) {
            System.err.println("랭킹 데이터 로드 실패: " + e.getMessage());
            // Failed to load ranking data
            e.printStackTrace();

            JLabel errorLabel = new JLabel("データを読み込めませんでした。", SwingConstants.CENTER);
            errorLabel.setFont(createFont(28, Font.BOLD));
            errorLabel.setForeground(new Color(255, 250, 240));
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            rankingListPanel.add(Box.createVerticalStrut(220));
            rankingListPanel.add(errorLabel);
        }
    }

    /**
     * 개별 랭킹 항목
     * Individual ranking item
     * 기본 상태: 등수 / 이름 / 총점 / ▼
     * Default state: rank / name / total score / ▼
     * 확장 상태: 레벨별 상세정보
     * Expanded state: per-level detail info
     */
    private class ExpandableRankingRow extends JPanel {
        private final int rank;
        private final String username;
        private final String displayName;
        private final int totalScore;

        private boolean expanded = false;
        private boolean loading = false;
        private boolean loaded = false;

        private int lv1Total = 0;
        private int lv1Best = 0;
        private int lv1Count = 0;

        private int lv2Total = 0;
        private int lv2Best = 0;
        private int lv2Count = 0;

        private int lv3Total = 0;
        private int lv3Best = 0;
        private int lv3Count = 0;

        ExpandableRankingRow(int rank, String username, String displayName, int totalScore) {
            this.rank = rank;
            this.username = username;
            this.displayName = displayName;
            this.totalScore = totalScore;

            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setRowHeight(ROW_H);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    sound.play("click.wav", false, -5.0f);
                    toggleExpanded();
                }
            });
        }

        private void setRowHeight(int height) {
            Dimension size = new Dimension(ROW_W, height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
        }

        private void toggleExpanded() {
            if (loading) {
                return;
            }

            expanded = !expanded;
            setRowHeight(expanded ? ROW_H + EXPANDED_EXTRA_H : ROW_H);

            revalidate();
            repaint();

            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }

            SwingUtilities.invokeLater(() -> {
                Rectangle visibleRect = new Rectangle(0, 0, getWidth(), getHeight());
                scrollRectToVisible(visibleRect);
            });

            if (expanded && !loaded) {
                loadDetailAsync();
            }
        }

        /**
         * 외부에서 펼침 상태 강제 설정 (마이레코드 자동 펼침용)
         * Force expanded state from outside (for auto-expand in my-record mode)
         */
        public void setExpandedState(boolean shouldExpand) {
            if (expanded != shouldExpand) {
                toggleExpanded();
            }
        }

        private void loadDetailAsync() {
            loading = true;
            repaint();

            SwingWorker<Map<Integer, int[]>, Void> worker = new SwingWorker<>() {
                @Override
                protected Map<Integer, int[]> doInBackground() throws Exception {
                    RecordDAO dao = new RecordDAO();
                    return dao.getUserLevelStats(username);
                }

                @Override
                protected void done() {
                    try {
                        Map<Integer, int[]> stats = get();

                        int[] lv1 = stats.get(1);
                        int[] lv2 = stats.get(2);
                        int[] lv3 = stats.get(3);

                        if (lv1 != null) {
                            lv1Total = safeArrayValue(lv1, 0);
                            lv1Best = safeArrayValue(lv1, 1);
                            lv1Count = safeArrayValue(lv1, 2);
                        }

                        if (lv2 != null) {
                            lv2Total = safeArrayValue(lv2, 0);
                            lv2Best = safeArrayValue(lv2, 1);
                            lv2Count = safeArrayValue(lv2, 2);
                        }

                        if (lv3 != null) {
                            lv3Total = safeArrayValue(lv3, 0);
                            lv3Best = safeArrayValue(lv3, 1);
                            lv3Count = safeArrayValue(lv3, 2);
                        }

                        loaded = true;
                    } catch (Exception ex) {
                        System.err.println("상세 데이터 로드 실패: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        loading = false;
                        repaint();
                        revalidate();
                    }
                }
            };

            worker.execute();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawHeaderRow(g2);

            if (expanded) {
                drawDetailArea(g2);
            }

            g2.dispose();
        }

        private void drawHeaderRow(Graphics2D g2) {
            int x = 0;
            int y = 0;
            int w = getWidth();
            int h = ROW_H;

            GradientPaint gp = new GradientPaint(
                    0, 0,
                    new Color(92, 46, 18, 245),
                    w, h,
                    new Color(60, 25, 8, 245)
            );

            g2.setPaint(gp);
            g2.fillRoundRect(x, y, w, h, 18, 18);

            g2.setColor(new Color(255, 201, 44));
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(x + 2, y + 2, w - 5, h - 5, 18, 18);

            g2.setColor(new Color(120, 70, 25, 120));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x + 8, y + 8, w - 17, h - 17, 14, 14);

            // 등수
            // Rank number
            g2.setFont(createFont(26, Font.BOLD));
            drawOutlinedString(
                    g2,
                    String.valueOf(rank),
                    40,
                    58,
                    new Color(255, 250, 240),
                    new Color(45, 24, 10)
            );

            // 이름
            // Name
            g2.setFont(createFont(26, Font.BOLD));
            drawOutlinedString(
                    g2,
                    displayName,
                    150,
                    58,
                    new Color(255, 250, 240),
                    new Color(45, 24, 10)
            );

            // 총점
            // Total score
            String scoreText = totalScore + " pt";
            g2.setFont(createFont(26, Font.BOLD));
            FontMetrics fm = g2.getFontMetrics();
            int scoreX = getWidth() - fm.stringWidth(scoreText) - 82;

            drawOutlinedString(
                    g2,
                    scoreText,
                    scoreX,
                    58,
                    new Color(255, 215, 0),
                    new Color(65, 36, 10)
            );

            // 화살표
            // Arrow
            drawArrow(g2, getWidth() - 34, 46, expanded);
        }

        private void drawArrow(Graphics2D g2, int centerX, int centerY, boolean expanded) {
            Polygon arrow = new Polygon();

            if (!expanded) {
                // ▼
                arrow.addPoint(centerX - 11, centerY - 5);
                arrow.addPoint(centerX + 11, centerY - 5);
                arrow.addPoint(centerX, centerY + 10);
            } else {
                // ▲
                arrow.addPoint(centerX - 11, centerY + 5);
                arrow.addPoint(centerX + 11, centerY + 5);
                arrow.addPoint(centerX, centerY - 10);
            }

            g2.setColor(new Color(255, 204, 45));
            g2.fillPolygon(arrow);

            g2.setColor(new Color(110, 60, 10));
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(arrow);
        }

        private void drawDetailArea(Graphics2D g2) {
            int detailX = 18;
            int detailY = ROW_H + 14;
            int detailW = getWidth() - 36;
            int detailH = EXPANDED_EXTRA_H - 24;

            GradientPaint gp = new GradientPaint(
                    detailX, detailY,
                    new Color(55, 28, 10, 220),
                    detailX + detailW, detailY + detailH,
                    new Color(35, 20, 9, 220)
            );

            g2.setPaint(gp);
            g2.fillRoundRect(detailX, detailY, detailW, detailH, 18, 18);

            g2.setColor(new Color(153, 100, 30, 180));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(detailX, detailY, detailW, detailH, 18, 18);

            if (loading && !loaded) {
                g2.setFont(createFont(24, Font.BOLD));
                drawOutlinedString(
                        g2,
                        "読み込み中...",
                        detailX + 35,
                        detailY + 55,
                        new Color(255, 250, 240),
                        new Color(45, 24, 10)
                );
                return;
            }

            int row1Y = detailY + 46;
            int row2Y = detailY + 102;
            int row3Y = detailY + 158;

            drawLevelLine(g2, detailX + 34, row1Y, 1, lv1Total, lv1Best, lv1Count);
            drawLevelLine(g2, detailX + 34, row2Y, 2, lv2Total, lv2Best, lv2Count);
            drawLevelLine(g2, detailX + 34, row3Y, 3, lv3Total, lv3Best, lv3Count);
        }

        private void drawLevelLine(Graphics2D g2, int x, int y, int level, int total, int best, int count) {
            int badgeW = 82;
            int badgeH = 34;

            Color badgeColor;
            if (level == 1) {
                badgeColor = new Color(255, 205, 40);
            } else if (level == 2) {
                badgeColor = new Color(210, 210, 210);
            } else {
                badgeColor = new Color(255, 160, 25);
            }

            g2.setColor(badgeColor);
            g2.fillRoundRect(x, y - 26, badgeW, badgeH, 8, 8);

            g2.setColor(new Color(55, 30, 10));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y - 26, badgeW, badgeH, 8, 8);

            g2.setFont(createFont(18, Font.BOLD));
            drawOutlinedString(
                    g2,
                    "Lv." + level,
                    x + 18,
                    y - 4,
                    new Color(255, 250, 240),
                    new Color(45, 24, 10)
            );

            g2.setFont(createFont(22, Font.BOLD));

            drawOutlinedString(
                    g2,
                    "そうてん: " + total + " pt",
                    x + 130,
                    y,
                    new Color(255, 250, 240),
                    new Color(45, 24, 10)
            );

            drawOutlinedString(
                    g2,
                    "さいこうてん: " + best + " pt",
                    x + 430,
                    y,
                    new Color(255, 215, 0),
                    new Color(45, 24, 10)
            );

            drawOutlinedString(
                    g2,
                    count + "かい",
                    x + 780,
                    y,
                    new Color(255, 250, 240),
                    new Color(45, 24, 10)
            );
        }
    }

    private void drawOutlinedString(Graphics2D g2, String text, int x, int y, Color fill, Color outline) {
        g2.setColor(outline);
        g2.drawString(text, x - 2, y);
        g2.drawString(text, x + 2, y);
        g2.drawString(text, x, y - 2);
        g2.drawString(text, x, y + 2);

        g2.setColor(fill);
        g2.drawString(text, x, y);
    }

    private Font createFont(int size, int style) {
        int resolvedStyle = style | Font.BOLD;

        String[] fontNames = {
                "Yu Gothic UI",
                "Yu Gothic",
                "Meiryo",
                "MS Gothic",
                "Hiragino Sans"
        };

        for (String fontName : fontNames) {
            Font font = new Font(fontName, resolvedStyle, size);
            if (font.getFamily().equals(fontName)) {
                return font;
            }
        }

        return new Font("Dialog", resolvedStyle, size);
    }

    private String safeString(Object value) {
        if (value == null) return "";
        return String.valueOf(value);
    }

    private int safeInt(Object value) {
        if (value == null) return 0;

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int safeArrayValue(int[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return 0;
        }
        return arr[index];
    }

    /**
     * 스크롤바 UI
     * Scroll bar UI
     */
    private static class RankingScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(230, 240, 255, 170);
            trackColor = new Color(255, 255, 255, 35);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(trackColor);
            g2.fillRoundRect(
                    trackBounds.x + 4,
                    trackBounds.y,
                    trackBounds.width - 8,
                    trackBounds.height,
                    10,
                    10
            );

            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!scrollbar.isEnabled() || thumbBounds.width <= 0 || thumbBounds.height <= 0) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(thumbColor);
            g2.fillRoundRect(
                    thumbBounds.x + 3,
                    thumbBounds.y,
                    thumbBounds.width - 6,
                    thumbBounds.height,
                    10,
                    10
            );

            g2.dispose();
        }
    }
}