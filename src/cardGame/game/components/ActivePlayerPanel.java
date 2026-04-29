package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * ターン表示効果付きスコアパネル
 * Glow効果でアクティブプレイヤーを強調
 */
public class ActivePlayerPanel extends JPanel {
    private static final Color CREAM_BG = new Color(255, 248, 230);
    private static final Color MAPLE_ORANGE = new Color(255, 143, 0);
    private static final Color GLOW_COLOR = new Color(255, 143, 0, 100);
    
    private boolean isActive = false;
    private float glowAlpha = 0.0f;
    private Timer glowTimer;
    private boolean glowIncreasing = true;
    
    public ActivePlayerPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(5, 5));
        
        // Glowアニメーションタイマー
        glowTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isActive) {
                    if (glowIncreasing) {
                        glowAlpha += 0.05f;
                        if (glowAlpha >= 1.0f) {
                            glowAlpha = 1.0f;
                            glowIncreasing = false;
                        }
                    } else {
                        glowAlpha -= 0.05f;
                        if (glowAlpha <= 0.3f) {
                            glowAlpha = 0.3f;
                            glowIncreasing = true;
                        }
                    }
                    repaint();
                }
            }
        });
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            glowTimer.start();
        } else {
            glowTimer.stop();
            glowAlpha = 0.0f;
        }
        repaint();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int arc = 15;
        
        // 影
        g2.setColor(new Color(150, 70, 0, 100));
        g2.fill(new RoundRectangle2D.Double(3, 6, width - 6, height - 6, arc, arc));
        
        // 背景
        g2.setColor(CREAM_BG);
        g2.fill(new RoundRectangle2D.Double(0, 0, width - 6, height - 10, arc, arc));
        
        // アクティブ時のGlow効果
        if (isActive && glowAlpha > 0) {
            // 外側の大きいGlow
            g2.setColor(new Color(255, 143, 0, (int)(60 * glowAlpha)));
            g2.setStroke(new BasicStroke(8f));
            g2.draw(new RoundRectangle2D.Double(0, 0, width - 7, height - 11, arc, arc));
            
            // 中間Glow
            g2.setColor(new Color(255, 143, 0, (int)(100 * glowAlpha)));
            g2.setStroke(new BasicStroke(5f));
            g2.draw(new RoundRectangle2D.Double(1, 1, width - 8, height - 12, arc, arc));
        }
        
        // 枠線 (アクティブ時は太く)
        g2.setColor(MAPLE_ORANGE);
        float borderWidth = isActive ? 4.0f : 2.5f;
        g2.setStroke(new BasicStroke(borderWidth));
        g2.draw(new RoundRectangle2D.Double(2, 2, width - 10, height - 14, arc, arc));
        
        g2.dispose();
    }
}
