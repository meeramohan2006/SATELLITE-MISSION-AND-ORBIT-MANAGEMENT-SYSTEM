package gui.components;

import gui.theme.Theme;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * Background panel that paints the Earth-from-space panoramic visual
 * with atmospheric blue glow and smooth alpha overlay.
 */
public class BackgroundPanel extends JPanel {

    private Image backgroundImage;
    private final float overlayAlpha;
    private final String imagePath;

    public BackgroundPanel(String imageResourcePath, float overlayAlpha) {
        this.imagePath = imageResourcePath;
        this.overlayAlpha = overlayAlpha;
        setLayout(null);
        loadImage();
    }

    private void loadImage() {
        try {
            // Try loading from resource stream
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is == null && !imagePath.startsWith("/")) {
                is = getClass().getResourceAsStream("/" + imagePath);
            }
            if (is != null) {
                backgroundImage = ImageIO.read(is);
                return;
            }

            // Try local file system
            File file = new File(imagePath);
            if (!file.exists()) {
                file = new File("src/resources" + (imagePath.startsWith("/") ? "" : "/") + imagePath);
            }
            if (file.exists()) {
                backgroundImage = ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("[BackgroundPanel] Could not load image " + imagePath + ", using procedural space fallback.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (backgroundImage != null) {
            // Draw scaled background image maintaining aspect ratio cover
            int imgW = backgroundImage.getWidth(null);
            int imgH = backgroundImage.getHeight(null);

            if (imgW > 0 && imgH > 0) {
                double scale = Math.max((double) w / imgW, (double) h / imgH);
                int nw = (int) (imgW * scale);
                int nh = (int) (imgH * scale);
                int x = (w - nw) / 2;
                int y = (h - nh) / 2;
                g2.drawImage(backgroundImage, x, y, nw, nh, null);
            }
        } else {
            // Procedural space and curved Earth horizon fallback
            GradientPaint spaceGrad = new GradientPaint(0, 0, new Color(4, 8, 20), 0, h, new Color(10, 25, 55));
            g2.setPaint(spaceGrad);
            g2.fillRect(0, 0, w, h);

            // Atmospheric Earth curve
            g2.setColor(new Color(0, 150, 255, 60));
            g2.fillOval(-w / 2, h / 2, w * 2, h);

            g2.setColor(new Color(0, 220, 255, 30));
            g2.fillOval(-w / 2, h / 2 - 20, w * 2, h);
        }

        // Dark tint overlay for readability of UI elements
        if (overlayAlpha > 0f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
            g2.setColor(Theme.BG_DARK);
            g2.fillRect(0, 0, w, h);
        }

        g2.dispose();
    }
}
