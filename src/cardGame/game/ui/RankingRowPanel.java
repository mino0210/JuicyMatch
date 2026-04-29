package cardGame.game.ui;

import cardGame.database.RecordDAO;
import cardGame.game.Sound;
import cardGame.game.config.RankingLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * 랭킹 목록의 한 행과 펼침 상세 영역을 담당합니다.
 * Handles one ranking row and its expandable detail area.
 */
public class RankingRowPanel extends JPanel {
    private final int rank;
    private final String username;
    private final String displayName;
    private final int totalScore;
    private final Sound clickSound;

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

    public RankingRowPanel(int rank, String username, String displayName, int totalScore, Sound clickSound) {
        this.rank = rank;
        this.username = username;
        this.displayName = displayName;
        this.totalScore = totalScore;
        this.clickSound = clickSound;

        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRowHeight(RankingLayout.ROW_H);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickSound != null) {
                    clickSound.play("click.wav", false, -5.0f);
                }
                toggleExpanded();
            }
        });
    }

    /**
     * 외부 필터에서 특정 행을 자동으로 펼칠 때 사용합니다.
     * Used by filters to expand a specific row automatically.
     */
    public void setExpandedState(boolean shouldExpand) {
        if (expanded != shouldExpand) {
            toggleExpanded();
        }
    }

    private void setRowHeight(int height) {
        Dimension size = new Dimension(RankingLayout.ROW_W, height);
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
        setRowHeight(expanded ? RankingLayout.ROW_H + RankingLayout.EXPANDED_EXTRA_H : RankingLayout.ROW_H);
        revalidate();
        repaint();

        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }

        SwingUtilities.invokeLater(() -> scrollRectToVisible(new Rectangle(0, 0, getWidth(), getHeight())));

        if (expanded && !loaded) {
            loadDetailAsync();
        }
    }

    /**
     * DB에서 레벨별 상세 기록을 비동기로 불러옵니다.
     * Loads level-specific ranking details from the database asynchronously.
     */
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
                    applyLevelStats(stats.get(1), 1);
                    applyLevelStats(stats.get(2), 2);
                    applyLevelStats(stats.get(3), 3);
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

    private void applyLevelStats(int[] stats, int level) {
        if (stats == null) {
            return;
        }
        if (level == 1) {
            lv1Total = safeArrayValue(stats, 0);
            lv1Best = safeArrayValue(stats, 1);
            lv1Count = safeArrayValue(stats, 2);
        } else if (level == 2) {
            lv2Total = safeArrayValue(stats, 0);
            lv2Best = safeArrayValue(stats, 1);
            lv2Count = safeArrayValue(stats, 2);
        } else if (level == 3) {
            lv3Total = safeArrayValue(stats, 0);
            lv3Best = safeArrayValue(stats, 1);
            lv3Count = safeArrayValue(stats, 2);
        }
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
        int w = getWidth();
        int h = RankingLayout.ROW_H;
        GradientPaint gp = new GradientPaint(0, 0, new Color(92, 46, 18, 245), w, h, new Color(60, 25, 8, 245));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, 18, 18);
        g2.setColor(new Color(255, 201, 44));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(2, 2, w - 5, h - 5, 18, 18);
        g2.setColor(new Color(120, 70, 25, 120));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(8, 8, w - 17, h - 17, 14, 14);

        g2.setFont(createFont(26, Font.BOLD));
        GameTextPainter.drawOutlinedText(g2, String.valueOf(rank), 40, 58, new Color(255, 250, 240), new Color(45, 24, 10));
        GameTextPainter.drawOutlinedText(g2, displayName, 150, 58, new Color(255, 250, 240), new Color(45, 24, 10));

        String scoreText = totalScore + " pt";
        FontMetrics fm = g2.getFontMetrics();
        int scoreX = getWidth() - fm.stringWidth(scoreText) - 82;
        GameTextPainter.drawOutlinedText(g2, scoreText, scoreX, 58, new Color(255, 215, 0), new Color(65, 36, 10));
        drawArrow(g2, getWidth() - 34, 46, expanded);
    }

    private void drawArrow(Graphics2D g2, int centerX, int centerY, boolean expanded) {
        Polygon arrow = new Polygon();
        if (!expanded) {
            arrow.addPoint(centerX - 11, centerY - 5);
            arrow.addPoint(centerX + 11, centerY - 5);
            arrow.addPoint(centerX, centerY + 10);
        } else {
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
        int detailY = RankingLayout.ROW_H + 14;
        int detailW = getWidth() - 36;
        int detailH = RankingLayout.EXPANDED_EXTRA_H - 24;
        GradientPaint gp = new GradientPaint(detailX, detailY, new Color(55, 28, 10, 220), detailX + detailW, detailY + detailH, new Color(35, 20, 9, 220));
        g2.setPaint(gp);
        g2.fillRoundRect(detailX, detailY, detailW, detailH, 18, 18);
        g2.setColor(new Color(153, 100, 30, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(detailX, detailY, detailW, detailH, 18, 18);

        if (loading && !loaded) {
            g2.setFont(createFont(24, Font.BOLD));
            GameTextPainter.drawOutlinedText(g2, "読み込み中...", detailX + 35, detailY + 55, new Color(255, 250, 240), new Color(45, 24, 10));
            return;
        }

        drawLevelLine(g2, detailX + 34, detailY + 46, 1, lv1Total, lv1Best, lv1Count);
        drawLevelLine(g2, detailX + 34, detailY + 102, 2, lv2Total, lv2Best, lv2Count);
        drawLevelLine(g2, detailX + 34, detailY + 158, 3, lv3Total, lv3Best, lv3Count);
    }

    private void drawLevelLine(Graphics2D g2, int x, int y, int level, int total, int best, int count) {
        int badgeW = 82;
        int badgeH = 34;
        Color badgeColor = level == 1 ? new Color(255, 205, 40) : level == 2 ? new Color(210, 210, 210) : new Color(255, 160, 25);
        g2.setColor(badgeColor);
        g2.fillRoundRect(x, y - 26, badgeW, badgeH, 8, 8);
        g2.setColor(new Color(55, 30, 10));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y - 26, badgeW, badgeH, 8, 8);

        g2.setFont(createFont(18, Font.BOLD));
        GameTextPainter.drawOutlinedText(g2, "Lv." + level, x + 18, y - 4, new Color(255, 250, 240), new Color(45, 24, 10));
        g2.setFont(createFont(22, Font.BOLD));
        GameTextPainter.drawOutlinedText(g2, "そうごうてん: " + total + " pt", x + 130, y, new Color(255, 250, 240), new Color(45, 24, 10));
        GameTextPainter.drawOutlinedText(g2, "さいこうてん: " + best + " pt", x + 430, y, new Color(255, 215, 0), new Color(45, 24, 10));
        GameTextPainter.drawOutlinedText(g2, count + "かい", x + 780, y, new Color(255, 250, 240), new Color(45, 24, 10));
    }

    private Font createFont(int size, int style) {
        String[] fontNames = {"Yu Gothic UI", "Yu Gothic", "Meiryo", "MS Gothic", "Hiragino Sans"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, style | Font.BOLD, size);
            if (font.getFamily().equals(fontName)) {
                return font;
            }
        }
        return new Font("Dialog", style | Font.BOLD, size);
    }

    private int safeArrayValue(int[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return 0;
        }
        return arr[index];
    }
}
