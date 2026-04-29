package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 메이플 스타일 점수판 패널
 * Maple-style scoreboard panel
 * 특징: 주황색 테두리, 크림 배경, 하단 그림자
 * Features: orange border, cream background, bottom shadow
 */
public class ScorePanel extends JPanel {
    private static final Color CREAM_BG = new Color(255, 248, 230);
    private static final Color MAPLE_ORANGE = new Color(255, 143, 0);
    private static final Color SHADOW_COLOR = new Color(150, 70, 0, 100);
    
    private String title;
    private boolean isActive = false;
    
    public ScorePanel(String title) {
        this.title = title;
        setOpaque(false);
        setLayout(new BorderLayout(5, 5));
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int arc = 15;
        
        // 하단 그림자
        // Bottom shadow
        g2.setColor(SHADOW_COLOR);
        g2.fill(new RoundRectangle2D.Double(3, 6, width - 6, height - 6, arc, arc));
        
        // 배경
        // Background
        g2.setColor(CREAM_BG);
        g2.fill(new RoundRectangle2D.Double(0, 0, width - 6, height - 10, arc, arc));
        
        // 테두리 (활성 시 두껍게)
        // Border (thicker when active)
        g2.setColor(MAPLE_ORANGE);
        float borderWidth = isActive ? 3.5f : 2.5f;
        g2.setStroke(new BasicStroke(borderWidth));
        g2.draw(new RoundRectangle2D.Double(1, 1, width - 8, height - 12, arc, arc));
        
        // Glow 효과 (활성 시)
        // Glow effect (when active)
        if (isActive) {
            g2.setColor(new Color(255, 143, 0, 50));
            g2.setStroke(new BasicStroke(6f));
            g2.draw(new RoundRectangle2D.Double(1, 1, width - 8, height - 12, arc, arc));
        }
        
        g2.dispose();
    }
}
