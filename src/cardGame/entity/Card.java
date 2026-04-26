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
    private int cardWidth = 150;
    private int cardHeight = 210;
    private Sound cardFlip = new Sound();
    private Color borderColor;
    private boolean isRevealed = false;
    public static String cardTheme = "fruit";

    public Card(int number) {
        this.number = number;
        this.matched = false;
        this.borderColor = null;

        // 초기 상태: 뒷면
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

    public int getId() {
        return number;
    }

    // [수정] flip(): 단순히 이미지만 바꾸는 것이 아니라 상태를 명확히 제어하고 화면을 갱신합니다.
    public void flip() {
        if (matched) return;

        if (isRevealed) {
            hide(); // 앞면이면 숨김
        } else {
            reveal(); // 뒷면이면 보여줌
        }
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
        if (matched) {
            match(true);
        }
    }

    public JButton getButton() {
        return this;
    }

    public int getNumber() {
        return number;
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

        if (x < 0 || y < 0 || x >= img.getWidth(null) || y >= img.getHeight(null)) {
            return false;
        }

        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        int alpha = (bufferedImage.getRGB(x, y) >> 24) & 0xff;
        return alpha > 0;
    }

    // [수정] reveal(): 앞면으로 고정하고 상태 업데이트
    public void reveal() {
        if (matched) return;
        String cardName = "/" + cardTheme + "card" + checkNum(number) + ".png";
        String imagePath = FrontImagePath + cardName;
        setIcon(resizeImageIcon(imagePath, cardWidth, cardHeight));
        cardFlip.Sound(CardSoundPath, false, 0);
        isRevealed = true; // 상태 강제 업데이트
        revalidate();
        repaint();
    }

    // [수정] hide(): 뒷면으로 고정하고 상태 업데이트
    public void hide() {
        if (matched) return;
        setIcon(resizeImageIcon(BackImagePath, cardWidth, cardHeight));
        isRevealed = false; // 상태 강제 업데이트
        revalidate();
        repaint();
    }

    public boolean isMatched() {
        return matched;
    }

    public void match(boolean isUser) {
        this.matched = true;
        this.isRevealed = true; // 매칭된 카드는 앞면 상태임
        this.borderColor = isUser ? new Color(153, 204, 255) : new Color(153, 0, 0);
        setBorderPainted(true);
        setBorder(BorderFactory.createLineBorder(borderColor, 5));
        revalidate();
        repaint();
    }

    // 매칭 성공 시 리스트 등에 표시할 작은 아이콘 가져오기 (cardTheme + number)
    public ImageIcon getMatchedImageIcon() {
        String cardName = "/" + cardTheme + checkNum(number) + ".png";
        String imagePath = FrontImagePath + cardName;
        double scale = 0.45;
        int scaledWidth = (int) (cardWidth * scale);
        int scaledHeight = (int) (cardHeight * scale);
        return resizeImageIcon(imagePath, scaledWidth, scaledHeight);
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
}