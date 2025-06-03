package app.gui.components.buttons;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Класс Button с анимацией в виде волны
 */
public class Button extends JButton {
    private int targetSize;
    private float animationSize;
    private float alpha; //прозрачность эффекта волны
    private final Animator animator;
    private Point pressedPoint;
    private Color effectColor = new Color(255, 255, 255);

    /**
     * Конструктор Button
     */
    public Button() {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(7, 0, 7, 0));
        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        //обработчик событий
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                targetSize = Math.max(getWidth(), getHeight()) * 2;
                animationSize = 0;
                alpha = 0.5f;
                pressedPoint = e.getPoint();
                if (animator.isRunning()) {
                    animator.stop();
                }
                animator.start();
            }
        });
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                //уменьшаем прозрачность
                if (fraction > 0.5f) {
                    alpha = 1 - fraction;
                }
                //размер анимации
                animationSize = fraction * targetSize;
                repaint();
            }
        };
        animator = new Animator(400, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);
    }

    /**
     * Метод для отрисовки компонента
     */
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //сглаживание
        g2D.setColor(getBackground());
        g2D.fillRoundRect(0, 0, width, height, 14, 14);
        if (pressedPoint != null) {
            g2D.setColor(effectColor);
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha)); //прозрачность
            //круг с центром в точке нажатия
            g2D.fillOval((int) (pressedPoint.x - animationSize / 2), (int) (pressedPoint.y - animationSize /2), (int) animationSize, (int) animationSize);
        }
        g2D.dispose();
        g.drawImage(image, 0, 0, null);
        super.paintComponent(g);
    }
}
