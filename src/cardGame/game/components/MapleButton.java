package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * どうぶつ神経衰弱 + メイプルストーリー スタイルのボタン
 * 特徴: オレンジ背景、白文字、二重影、クリック効果
 */
public class MapleButton extends JButton {
    private static final Color MAPLE_ORANGE = new Color(255, 143, 0);
    private static final Color MAPLE_ORANGE_HOVER = new Color(255, 165, 50);
    private static final Color MAPLE_ORANGE_PRESSED = new Color(230, 120, 0);
    private static final Color SHADOW_DARK = new Color(150, 70, 0);
    private static final Color SHADOW_LIGHT = new Color(200, 100, 0);
    
    private boolean isPressed = false;
    private boolean isHovered = false;
    
    public MapleButton(String text) {
        super(text);
        setupButton();
    }
    
    public MapleButton(String text, int width, int height) {
        super(text);
        setPreferredSize(new Dimension(width, height));
        setupButton();
    }
    
    private void setupButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        
        // 日本語フォント設定 (どうぶつ神経衰弱 スタイル)
        // MS ゴシック、メイリオ、ヒラギノ角ゴ などを試す
        String[] fontNames = {"Meiryo", "MS Gothic", "Yu Gothic", "Hiragino Sans", "맑은 고딕"};
        Font baseFont = null;
        for (String fontName : fontNames) {
            Font testFont = new Font(fontName, Font.BOLD, 18);
            if (!testFont.getFamily().equals("Dialog")) {
                baseFont = testFont;
                break;
            }
        }
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.BOLD, 18);
        }
        setFont(baseFont);
        
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // マウスイベントリスナー
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
        int arc = 15; // 둥근 모서리 반경
        
        // 눌림 효과 오프셋
        // Pressed effect offset
        int yOffset = isPressed ? 4 : 0;
        
        // 첫 번째 그림자 (어두운)
        // First shadow (dark)
        g2.setColor(SHADOW_DARK);
        g2.fill(new RoundRectangle2D.Double(4, 8 + yOffset, width - 8, height - 8, arc, arc));
        
        // 두 번째 그림자 (밝은)
        // Second shadow (light)
        g2.setColor(SHADOW_LIGHT);
        g2.fill(new RoundRectangle2D.Double(2, 6 + yOffset, width - 4, height - 6, arc, arc));
        
        // 메인 버튼
        // Main button
        Color buttonColor = isPressed ? MAPLE_ORANGE_PRESSED : 
                           isHovered ? MAPLE_ORANGE_HOVER : MAPLE_ORANGE;
        g2.setColor(buttonColor);
        g2.fill(new RoundRectangle2D.Double(0, 0 + yOffset, width - 4, height - 12, arc, arc));
        
        // 하이라이트 효과
        // Highlight effect
        GradientPaint highlight = new GradientPaint(
            0, 0 + yOffset, new Color(255, 255, 255, 80),
            0, height / 3, new Color(255, 255, 255, 0)
        );
        g2.setPaint(highlight);
        g2.fill(new RoundRectangle2D.Double(2, 2 + yOffset, width - 8, height / 3, arc, arc));
        
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
