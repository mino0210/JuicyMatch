package cardGame.game.components;

import cardGame.game.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 나무 버튼 컴포넌트 - 3상태 + 호버 사운드 (완전 불투명 버전)
 * Wood button - 3 states + hover sound (fully opaque version)
 */
public class WoodButton extends JButton {
    
    // 이미지
    // Images
    private BufferedImage normalImage;
    private BufferedImage hoverImage;
    private BufferedImage pressedImage;
    private BufferedImage currentImage;
    
    // 텍스트
    // Text
    private String buttonText;
    
    // 색상
    // Colors
    private static final Color TEXT_COLOR = new Color(255, 250, 240);
    private static final Color SHADOW_COLOR = new Color(101, 67, 33, 150);
    
    // 크기
    // Size
    private int buttonWidth = 300;
    private int buttonHeight = 70;
    
    // 호버 사운드 (전역 throttling)
    // Hover sound (global throttling)
    private static long lastHoverSoundTime = 0;
    private static final long HOVER_THROTTLE_MS = 100;
    private boolean hoverSoundEnabled = true;
    
    public WoodButton(String text) {
        this(text, 300, 70);
    }
    
    public WoodButton(String text, int width, int height) {
        this.buttonText = text;
        this.buttonWidth = width;
        this.buttonHeight = height;
        loadImages();
        setupButton();
    }
    
    private void loadImages() {
        try {
            // 이미지를 로드하면서 알파 채널을 강제로 불투명하게 변경
            // Load images and force alpha channel to fully opaque
            normalImage = makeOpaque(ImageIO.read(new File("src/cardGame/img/wood_button_normal.png")));
            hoverImage = makeOpaque(ImageIO.read(new File("src/cardGame/img/wood_button_hover.png")));
            pressedImage = makeOpaque(ImageIO.read(new File("src/cardGame/img/wood_button_pressed.png")));
            currentImage = normalImage;
        } catch (IOException e) {
            System.err.println("버튼 이미지 로드 실패: " + e.getMessage());
            // Failed to load button images
        }
    }
    
    /**
     * 이미지의 알파 채널을 모두 불투명하게 변환
     * Convert all alpha channel pixels to fully opaque
     */
    private BufferedImage makeOpaque(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                // 알파가 일정 임계값 이상이면 완전 불투명으로 변환
                // If alpha is above threshold, make fully opaque
                if (alpha > 30) {
                    rgb = (0xFF << 24) | (rgb & 0x00FFFFFF);
                }
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }
    
    private void setupButton() {
        setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        // 호버 시 회색 반투명 효과 비활성화
        // Disable hover translucent effect
        setRolloverEnabled(false);
        
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentImage = hoverImage;
                
                if (hoverSoundEnabled && isEnabled()) {
                    long now = System.currentTimeMillis();
                    if (now - lastHoverSoundTime > HOVER_THROTTLE_MS) {
                        lastHoverSoundTime = now;
                        new Thread(() -> {
                            try {
                                Sound s = new Sound();
                                s.play("hover_wood.wav", false, -20.0f);
                            } catch (Exception ex) {
                                // 호버 사운드 실패는 조용히 무시
                                // Silently ignore hover sound failure
                            }
                        }).start();
                    }
                }
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                currentImage = normalImage;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                currentImage = pressedImage;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (contains(e.getPoint())) {
                    currentImage = hoverImage;
                } else {
                    currentImage = normalImage;
                }
                repaint();
            }
        });
    }
    
    private Font createFont(int size, int style) {
        String[] fontNames = {"Yu Gothic UI", "Meiryo", "MS Gothic", "Hiragino Sans"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, style, size);
            if (font.getFamily().equals(fontName)) {
                return font;
            }
        }
        return new Font("Dialog", style, size);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 알파 합성을 SRC_OVER로 강제 - 완전히 불투명하게 그림
        // Force alpha composite to SRC_OVER - draws fully opaque
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        int width = getWidth();
        int height = getHeight();
        
        if (currentImage != null) {
            g2.drawImage(currentImage, 0, 0, width, height, null);
        }
        
        if (buttonText != null && !buttonText.isEmpty()) {
            int fontSize = Math.min(width / 10, 28);
            g2.setFont(createFont(fontSize, Font.BOLD));
            
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(buttonText);
            int textHeight = fm.getAscent();
            
            int x = (width - textWidth) / 2;
            int y = (height + textHeight) / 2 - 3;
            
            g2.setColor(SHADOW_COLOR);
            g2.drawString(buttonText, x + 2, y + 2);
            
            g2.setColor(TEXT_COLOR);
            g2.drawString(buttonText, x, y);
        }
        
        g2.dispose();
    }
    
    public void setButtonText(String text) {
        this.buttonText = text;
        repaint();
    }
    
    public void setButtonSize(int width, int height) {
        this.buttonWidth = width;
        this.buttonHeight = height;
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        revalidate();
        repaint();
    }
    
    public void setHoverSoundEnabled(boolean enabled) {
        this.hoverSoundEnabled = enabled;
    }
}
