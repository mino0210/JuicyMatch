package cardGame.game.components;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 레벨 뱃지 컴포넌트
 * Level badge component
 * Lv.1 브론즈 / Lv.2 실버 / Lv.3 골드
 * Lv.1 Bronze / Lv.2 Silver / Lv.3 Gold
 */
public class LevelBadge extends JLabel {
    private static final Color BRONZE = new Color(205, 127, 50);
    private static final Color BRONZE_LIGHT = new Color(233, 150, 70);
    private static final Color SILVER = new Color(192, 192, 192);
    private static final Color SILVER_LIGHT = new Color(220, 220, 220);
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color GOLD_LIGHT = new Color(255, 235, 80);
    
    private int level;
    private Color badgeColor;
    private Color badgeLightColor;
    
    public LevelBadge(int level) {
        this.level = level;
        setupBadge();
    }
    
    private void setupBadge() {
        // 레벨에 따른 색상 설정
        // Set color based on level
        switch (level) {
            case 1:
                badgeColor = BRONZE;
                badgeLightColor = BRONZE_LIGHT;
                break;
            case 2:
                badgeColor = SILVER;
                badgeLightColor = SILVER_LIGHT;
                break;
            case 3:
                badgeColor = GOLD;
                badgeLightColor = GOLD_LIGHT;
                break;
            default:
                badgeColor = BRONZE;
                badgeLightColor = BRONZE_LIGHT;
        }
        
        setText("Lv." + level);
        setFont(new Font("맑은 고딕", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
        setBorder(new BadgeBorder());
        setPreferredSize(new Dimension(60, 28));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int arc = 14;
        
        // 그림자
        // Shadow
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fill(new RoundRectangle2D.Double(2, 3, width - 2, height - 2, arc, arc));
        
        // 그라데이션 배경
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, badgeLightColor,
            0, height, badgeColor
        );
        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Double(0, 0, width - 2, height - 3, arc, arc));
        
        // 테두리
        // Border
        g2.setColor(badgeColor.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Double(1, 1, width - 4, height - 5, arc, arc));
        
        // 하이라이트
        // Highlight
        GradientPaint highlight = new GradientPaint(
            0, 0, new Color(255, 255, 255, 100),
            0, height / 2, new Color(255, 255, 255, 0)
        );
        g2.setPaint(highlight);
        g2.fill(new RoundRectangle2D.Double(2, 2, width - 6, height / 2, arc, arc));
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    private class BadgeBorder extends AbstractBorder {
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 12, 5, 12);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 5;
            return insets;
        }
    }
}
