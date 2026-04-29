package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;




public class TitleBoard extends JPanel {
    
    
    
    private static BufferedImage boardImage;
    
    
    
    private String titleText;
    
    
    
    private static final Color TEXT_COLOR = new Color(255, 250, 240); 
    private static final Color SHADOW_COLOR = new Color(101, 67, 33, 200); 
    
    
    
    private int boardWidth = 500;
    private int boardHeight = 150;
    
    
    
    static {
        loadStaticImage();
    }
    
    


    private static void loadStaticImage() {
        try {
            URL url = TitleBoard.class.getResource("/cardGame/img/title_board_empty.png");
            if (url != null) {
                boardImage = ImageIO.read(url);
                return;
            }
            File f = new File("src/cardGame/img/title_board_empty.png");
            if (f.exists()) boardImage = ImageIO.read(f);
        } catch (IOException e) {
            System.err.println("タイトルボード画像の読み込み失敗: " + e.getMessage());
        }
    }
    
    


    public TitleBoard(String text) {
        this(text, 500, 150);
    }
    
    public TitleBoard(String text, int width, int height) {
        this.titleText = text;
        this.boardWidth = width;
        this.boardHeight = height;
        
        setupPanel();
    }
    
    


    private void setupPanel() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setMaximumSize(new Dimension(boardWidth, boardHeight));
        setMinimumSize(new Dimension(boardWidth, boardHeight));
        setOpaque(false);
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
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        int width = getWidth();
        int height = getHeight();
        
        
        
        if (boardImage != null) {
            g2.drawImage(boardImage, 0, 0, width, height, null);
        }
        
        
        
        if (titleText != null && !titleText.isEmpty()) {
            
            
            int fontSize = Math.min(width / 8, 48);
            if (titleText.length() > 6) {
                fontSize = Math.min(width / 10, 36);
            }
            g2.setFont(createFont(fontSize, Font.BOLD));
            
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(titleText);
            int textHeight = fm.getAscent();
            
            int x = (width - textWidth) / 2;
            int y = (height + textHeight) / 2 - 5;
            
            
            
            g2.setColor(SHADOW_COLOR);
            g2.drawString(titleText, x + 3, y + 3);
            
            
            
            g2.setColor(TEXT_COLOR);
            g2.drawString(titleText, x, y);
        }
        
        g2.dispose();
    }
    
    


    public void setTitleText(String text) {
        this.titleText = text;
        repaint();
    }
}
