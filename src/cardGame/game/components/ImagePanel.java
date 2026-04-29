package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 이미지 배경 패널 / Image background panel
 */
public class ImagePanel extends JPanel {

    private BufferedImage backgroundImage;
    private String imagePath;

    public ImagePanel(String imagePath) {
        this.imagePath = imagePath;
        loadImage();
        setLayout(null);
        setOpaque(false);
    }

    private void loadImage() {
        try {
            URL url = getClass().getResource(imagePath);

            if (url != null) {
                backgroundImage = ImageIO.read(url);
                return;
            }

            String filePath = imagePath;

            if (filePath.startsWith("/cardGame/")) {
                filePath = "src" + filePath;
            }

            File file = new File(filePath);

            if (file.exists()) {
                backgroundImage = ImageIO.read(file);
            } else {
                System.err.println("画像が見つかりません: " + imagePath);
                backgroundImage = null;
            }

        } catch (IOException e) {
            System.err.println("画像読み込み失敗 [" + imagePath + "]: " + e.getMessage());
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (backgroundImage != null) {
            int panelW = getWidth();
            int panelH = getHeight();

            int imgW = backgroundImage.getWidth();
            int imgH = backgroundImage.getHeight();

            double scale = Math.max(
                    panelW / (double) imgW,
                    panelH / (double) imgH
            );

            int drawW = (int) Math.round(imgW * scale);
            int drawH = (int) Math.round(imgH * scale);

            int drawX = (panelW - drawW) / 2;
            int drawY = (panelH - drawH) / 2;

            g2.drawImage(backgroundImage, drawX, drawY, drawW, drawH, this);
        } else {
            g2.setColor(new Color(135, 206, 235));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
    }

    public void setImage(String imagePath) {
        this.imagePath = imagePath;
        loadImage();
        revalidate();
        repaint();
    }
}