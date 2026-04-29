package cardGame.entity;

import cardGame.game.Sound;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import static cardGame.game.GameController.*;

public class Card extends JButton {
    private int number;
    private boolean matched;
    private int cardWidth = 136; 
    private int cardHeight = 170; 

    private Sound cardFlip = new Sound();
    private Sound successSound = new Sound();

    private Color borderColor;
    private boolean isRevealed = false;
    public static String cardTheme = "fruit";

    public Card(int number) {
        this.number = number;
        this.matched = false;
        this.borderColor = null;

        setIcon(resizeImageIcon(BackImagePath, cardWidth, cardHeight));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(cardWidth, cardHeight));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isOpaquePixel(e.getX(), e.getY())) {
                    doClick();
                }
            }
        });
    }

    public int getId() { return number; }
    public int getNumber() { return number; }
    public boolean isMatched() { return matched; }

    public void flip() {
        if (matched) return;
        if (isRevealed) {
            hide();
        } else {
            reveal(true);
        }
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
        if (matched) {
            match(true);
        }
    }

    public void reveal(boolean playSound) {
        if (matched) return;
        String folder = cardTheme.equals("vegetable") ? "vegtable" : "fruit";
        String filePrefix = cardTheme;
        String cardName = "/" + folder + "/" + filePrefix + "card" + checkNum(number) + ".png";
        String imagePath = FrontImagePath + cardName;
        setIcon(resizeImageIcon(imagePath, cardWidth, cardHeight));

        if (playSound) {
            cardFlip.play("Card_Flip.wav", false, 0);
        }

        isRevealed = true;
        revalidate();
        repaint();
    }

    public void reveal() {
        reveal(true);
    }

    public void hide() {
        if (matched) return;
        setIcon(resizeImageIcon(BackImagePath, cardWidth, cardHeight));
        isRevealed = false;
        revalidate();
        repaint();
    }

    public void match(boolean isUser) {
        this.matched = true;
        this.isRevealed = true;
        this.borderColor = isUser ? new Color(153, 204, 255) : new Color(153, 0, 0);
        successSound.play("success_match.wav", false, -5.0f);
        setBorderPainted(true);
        setBorder(BorderFactory.createLineBorder(borderColor, 5));
        revalidate();
        repaint();
    }

    private ImageIcon resizeImageIcon(String imagePath, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath)));
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("이미지 로드 실패: " + imagePath);
            return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        }
    }

    private boolean isOpaquePixel(int x, int y) {
        ImageIcon icon = (ImageIcon) getIcon();
        if (icon == null) return false;
        Image img = icon.getImage();
        if (x < 0 || y < 0 || x >= img.getWidth(null) || y >= img.getHeight(null)) return false;
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        int alpha = (bufferedImage.getRGB(x, y) >> 24) & 0xff;
        return alpha > 0;
    }

    private String checkNum(int num) {
        if (num >= 10) return "" + num;
        return "0" + num;
    }

    public void reset() {
        this.matched = false;
        this.isRevealed = false;
        this.borderColor = null;
        setBorderPainted(false);
        setIcon(resizeImageIcon(BackImagePath, cardWidth, cardHeight));
        revalidate();
        repaint();
    }

    public ImageIcon getMatchedImageIcon() {
        String folder = cardTheme.equals("vegetable") ? "vegtable" : "fruit";
        String filePrefix = cardTheme;
        String cardName = "/" + folder + "/" + filePrefix + checkNum(number) + ".png";
        String imagePath = FrontImagePath + cardName;
        int scaledWidth = 35;
        int scaledHeight = 35;
        return resizeImageIcon(imagePath, scaledWidth, scaledHeight);
    }
}
