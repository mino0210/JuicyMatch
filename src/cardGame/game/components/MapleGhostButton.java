package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 메이플스토리 스타일의 고스트 버튼 (돌아가기/취소 용)
 * MapleStory-style ghost button (for back/cancel actions)
 * 특징: 투명 배경, 주황색 테두리 및 텍스트, 호버 효과
 * Features: transparent background, orange border and text, hover effect
 */
public class MapleGhostButton extends JButton {
    private static final Color MAPLE_ORANGE = new Color(255, 143, 0);
    private static final Color MAPLE_ORANGE_HOVER = new Color(255, 165, 50);
    private static final Color MAPLE_ORANGE_PRESSED = new Color(230, 120, 0);
    
    private boolean isPressed = false;
    private boolean isHovered = false;
    
    public MapleGhostButton(String text) {
        super(text);
        setupButton();
    }
    
    public MapleGhostButton(String text, int width, int height) {
        super(text);
        setPreferredSize(new Dimension(width, height));
        setupButton();
    }
    
    private void setupButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(MAPLE_ORANGE);
        setFont(new Font("맑은 고딕", Font.BOLD, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 마우스 이벤트 리스너
        // Mouse event listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int arc = 15;
        
        // 배경 (호버 시 반투명 주황)
        // Background (translucent orange on hover)
        if (isHovered || isPressed) {
            Color bgColor = isPressed ? new Color(255, 143, 0, 40) : new Color(255, 143, 0, 20);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        }
        
        // 테두리
        // Border
        Color borderColor = isPressed ? MAPLE_ORANGE_PRESSED : 
                           isHovered ? MAPLE_ORANGE_HOVER : MAPLE_ORANGE;
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(new RoundRectangle2D.Double(1, 1, width - 2, height - 2, arc, arc));
        
        // 텍스트 색상 업데이트
        // Update text color
        setForeground(borderColor);
        
        g2.dispose();
        
        // 텍스트 그리기
        // Draw text
        super.paintComponent(g);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(Math.max(size.width + 40, 120), Math.max(size.height + 20, 45));
    }
}
