import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class ProductShop {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ShopFrame frame = new ShopFrame();
            frame.setVisible(true);
        });
    }
}

class ShopFrame extends JFrame {
    private final List<Product> products = new ArrayList<>();
    private final DetailPanel detailPanel;
    private final List<ProductCard> productCards = new ArrayList<>();
    private int selectedIndex = 0;

    ShopFrame() {
        setTitle("Adidas Product Shop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1240, 660);
        setLocationRelativeTo(null);

        createProducts();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel content = new JPanel(new BorderLayout(24, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(46, 18, 22, 18));

        detailPanel = new DetailPanel(products.get(0));
        content.add(detailPanel, BorderLayout.WEST);
        content.add(createProductList(), BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void createProducts() {
        products.add(new Product("4DFWD PULSE SHOES", "This product is excluded from all promotional discounts and offers.", "Adidas", "$160.00", "img1.png"));
        products.add(new Product("FORUM MID SHOES", "This product is excluded from all promotional discounts and offers.", "Adidas", "$100.00", "img2.png"));
        products.add(new Product("SUPERNOVA SHOES", "NMD City Stock 2", "Adidas", "$150.00", "img3.png"));
        products.add(new Product("Adidas", "NMD City Stock 2", "Adidas", "$160.00", "img4.png"));
        products.add(new Product("Adidas", "NMD City Stock 2", "Adidas", "$120.00", "img5.png"));
        products.add(new Product("4DFWD PULSE SHOES", "This product is excluded from all promotional discounts and offers.", "Adidas", "$160.00", "img6.png"));
        products.add(new Product("4DFWD PULSE SHOES", "This product is excluded from all promotional discounts and offers.", "Adidas", "$160.00", "img1.png"));
        products.add(new Product("FORUM MID SHOES", "This product is excluded from all promotional discounts and offers.", "Adidas", "$100.00", "img2.png"));
    }

    private JPanel createProductList() {
        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 10));
        grid.setBackground(Color.WHITE);

        for (int i = 0; i < products.size(); i++) {
            ProductCard card = new ProductCard(products.get(i), i == selectedIndex);
            final int index = i;
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectProduct(index);
                }
            });

            productCards.add(card);
            grid.add(card);
        }

        return grid;
    }

    private void selectProduct(int index) {
        if (index == selectedIndex) {
            return;
        }

        selectedIndex = index;
        for (int i = 0; i < productCards.size(); i++) {
            productCards.get(i).setSelected(i == selectedIndex);
        }

        detailPanel.showProduct(products.get(index));
    }
}

class DetailPanel extends JPanel {
    private Product product;
    private Product nextProduct;
    private BufferedImage currentImage;
    private BufferedImage nextImage;
    private final Timer timer;
    private float animation = 1f;

    DetailPanel(Product startProduct) {
        product = startProduct;
        currentImage = ImageStore.load(startProduct.imageFile);

        setPreferredSize(new Dimension(310, 520));
        setBackground(Color.WHITE);

        timer = new Timer(18, e -> updateAnimation());
    }

    void showProduct(Product newProduct) {
        nextProduct = newProduct;
        nextImage = ImageStore.load(newProduct.imageFile);
        animation = 0f;
        timer.restart();
    }

    private void updateAnimation() {
        animation += 0.08f;
        if (animation >= 1f) {
            animation = 1f;
            product = nextProduct;
            currentImage = nextImage;
            timer.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Product textProduct = timer.isRunning() && animation > 0.5f ? nextProduct : product;
        drawChangingImage(g2);
        drawInfo(g2, textProduct);

        g2.dispose();
    }

    private void drawChangingImage(Graphics2D g2) {
        if (timer.isRunning()) {
            float oldAlpha = 1f - animation;
            float newAlpha = animation;

            Graphics2D oldG = (Graphics2D) g2.create();
            oldG.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, oldAlpha));
            drawImage(oldG, currentImage, (int) (-18 * animation));
            oldG.dispose();

            Graphics2D newG = (Graphics2D) g2.create();
            newG.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, newAlpha));
            drawImage(newG, nextImage, (int) (18 * (1f - animation)));
            newG.dispose();
        } else {
            drawImage(g2, currentImage, 0);
        }
    }

    private void drawImage(Graphics2D g2, Image image, int offsetX) {
        if (image == null) {
            return;
        }

        ImageStore.drawFit(g2, image, offsetX + 5, 22, 300, 195);
    }

    private void drawInfo(Graphics2D g2, Product item) {
        g2.setColor(new Color(174, 181, 188));
        g2.drawLine(0, 252, 310, 252);

        g2.setColor(new Color(78, 78, 78));
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString(item.name, 0, 292);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString(item.price, 0, 326);

        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.drawString(item.brand, 0, 351);

        g2.setColor(new Color(166, 166, 166));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        drawWrappedText(g2, item.description, 0, 377, 290, 21);
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int width, int lineHeight) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        String line = "";

        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(test) > width) {
                g2.drawString(line, x, y);
                line = word;
                y += lineHeight;
            } else {
                line = test;
            }
        }

        if (!line.isEmpty()) {
            g2.drawString(line, x, y);
        }
    }
}

class ProductCard extends JPanel {
    private final Product product;
    private final Image image;
    private boolean selected;
    private boolean hover;

    ProductCard(Product product, boolean selected) {
        this.product = product;
        this.selected = selected;
        this.image = ImageStore.load(product.imageFile);

        setPreferredSize(new Dimension(205, 250));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawBackground(g2);
        drawText(g2);
        drawProductImage(g2);
        drawFooter(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        Color bg = hover ? new Color(238, 238, 238) : new Color(243, 243, 243);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);

        if (selected) {
            g2.setColor(new Color(72, 132, 255));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
        }
    }

    private void drawText(Graphics2D g2) {
        g2.setColor(new Color(73, 73, 73));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        drawOneLine(g2, product.name, 10, 29, 168);

        g2.setColor(new Color(175, 175, 175));
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        drawOneLine(g2, product.description, 10, 55, 160);
    }

    private void drawProductImage(Graphics2D g2) {
        if (image == null) {
            return;
        }

        ImageStore.drawFit(g2, image, 18, 86, getWidth() - 36, 94);
    }

    private void drawFooter(Graphics2D g2) {
        g2.setColor(new Color(72, 72, 72));
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(product.brand, 10, getHeight() - 15);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(product.price, getWidth() - fm.stringWidth(product.price) - 10, getHeight() - 15);
    }

    private void drawOneLine(Graphics2D g2, String text, int x, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        String value = text;

        while (fm.stringWidth(value) > width && value.length() > 3) {
            value = value.substring(0, value.length() - 4) + "...";
        }

        g2.drawString(value, x, y);
    }
}

class Product {
    final String name;
    final String description;
    final String brand;
    final String price;
    final String imageFile;

    Product(String name, String description, String brand, String price, String imageFile) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.imageFile = imageFile;
    }
}

class ImageStore {
    static BufferedImage load(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file = new File("Lab03", fileName);
        }

        try {
            BufferedImage image = ImageIO.read(file);
            return cropImage(image);
        } catch (Exception e) {
            System.out.println("Khong doc duoc anh: " + fileName);
            return null;
        }
    }

    static void drawFit(Graphics2D g2, Image image, int x, int y, int boxW, int boxH) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        double scale = Math.min((double) boxW / w, (double) boxH / h);
        int drawW = (int) Math.round(w * scale);
        int drawH = (int) Math.round(h * scale);
        int drawX = x + (boxW - drawW) / 2;
        int drawY = y + (boxH - drawH) / 2;

        g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }

    private static BufferedImage cropImage(BufferedImage image) {
        int minX = image.getWidth();
        int minY = image.getHeight();
        int maxX = -1;
        int maxY = -1;
        int margin = 10;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (isProductPixel(image.getRGB(x, y))) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return image;
        }

        minX = Math.max(0, minX - margin);
        minY = Math.max(0, minY - margin);
        maxX = Math.min(image.getWidth() - 1, maxX + margin);
        maxY = Math.min(image.getHeight() - 1, maxY + margin);

        return image.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private static boolean isProductPixel(int rgb) {
        int alpha = (rgb >> 24) & 0xff;
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;

        if (alpha < 20) {
            return false;
        }

        return red < 245 || green < 245 || blue < 245;
    }
}
