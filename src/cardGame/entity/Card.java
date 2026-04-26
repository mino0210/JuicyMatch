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

    // 각 카드마다 독립적인 사운드 객체를 할당하여 소리가 겹쳐도(뭉개져도) 모두 출력되게 합니다.
    private Sound cardFlip = new Sound();
    private Sound successSound = new Sound();

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

    public int getNumber() {
        return number;
    }

    public boolean isMatched() {
        return matched;
    }

    public JButton getButton() {
        return this;
    }

    // [유지] 사용자가 클릭 시 호출
    public void flip() {
        if (matched) return;

        if (isRevealed) {
            hide();
        } else {
            reveal();
        }
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
        if (matched) {
            match(true);
        }
    }

    /**
     * 카드를 앞면으로 변경하고 소리를 재생합니다.
     * 보드 생성 시 반복문에서 이 메서드가 호출되면 의도하신 '촤라라락' 소리가 납니다.
     */
    public void reveal() {
        if (matched) return;
        String cardName = "/" + cardTheme + "card" + checkNum(number) + ".png";
        String imagePath = FrontImagePath + cardName;
        setIcon(resizeImageIcon(imagePath, cardWidth, cardHeight));

        // 사운드 재생: 파일명만 넘깁니다 (Sound.java가 내부에서 경로 조합)
        // 0은 데시벨 기준이므로 기본 볼륨입니다.
        cardFlip.play("Card_Flip.wav", false, 0);

        isRevealed = true;
        revalidate();
        repaint();
    }

    /**
     * 카드를 뒷면으로 숨깁니다.
     */
    public void hide() {
        if (matched) return;
        setIcon(resizeImageIcon(BackImagePath, cardWidth, cardHeight));
        isRevealed = false;
        revalidate();
        repaint();
    }

    /**
     * 매칭 성공 시 호출 (성공 사운드 추가)
     */
    public void match(boolean isUser) {
        this.matched = true;
        this.isRevealed = true;
        this.borderColor = isUser ? new Color(153, 204, 255) : new Color(153, 0, 0);

        // 매칭 성공 사운드 재생
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

    // 매칭 결과 표시용
    public ImageIcon getMatchedImageIcon() {
        String cardName = "/" + cardTheme + checkNum(number) + ".png";
        String imagePath = FrontImagePath + cardName;
        double scale = 0.45;
        int scaledWidth = (int) (cardWidth * scale);
        int scaledHeight = (int) (cardHeight * scale);
        return resizeImageIcon(imagePath, scaledWidth, scaledHeight);
    }
}