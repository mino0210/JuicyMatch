package cardGame.game;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    private Color outlineColor;

    public CustomLabel(String text, Color textColor, Color outlineColor, Font font) {
        super(text);
        this.setForeground(textColor); 
        this.outlineColor = outlineColor; 
        this.setFont(font); 
        this.setHorizontalAlignment(CENTER); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 글씨 테두리
        g2.setFont(getFont());
        g2.setColor(outlineColor); 
        g2.setStroke(new BasicStroke(10f)); 
        g2.drawGlyphVector(getFont().createGlyphVector(g2.getFontRenderContext(), getText()), 0, getHeight() / 2 + getFont().getSize() / 2);

        // 내부 글씨
        g2.setColor(getForeground()); 
        super.paintComponent(g2);

        g2.dispose();
    }
}