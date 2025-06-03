package app.gui.components.menu;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.Interpolator;
import com.formdev.flatlaf.util.CubicBezierEasing;

/**
 * Класс Animation для анимации элементов меню (появление и скрытие элементов)
 */
public class Animation {
    private final SubmenuItem submenuItem; //

    /**
     * Конструктор Animation
     * @param submenuItem объект, для которого будет выполняться анимация
     */
    public Animation(SubmenuItem submenuItem) {
        this.submenuItem = submenuItem;
    }

    /**
     * Запускает анимацию для указанного элемента меню
     * @param show значение, указывающее на показ/скрытие меню
     */
    public void run(boolean show) {
        Animator animator = new Animator(230, new TimingTarget() {
            @Override
            public void timingEvent(float fraction) {
                float f = show ? fraction : 1f - fraction;
                submenuItem.setAnimate(f);
            }

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public void repeat() {
            }
        });
        //интерполятор кривой Безье для анимации (плавное начало и окончание)
        Interpolator myInterpolator = CubicBezierEasing.EASE_IN_OUT::interpolate;
        animator.setInterpolator(myInterpolator);
        animator.start();
    }
}
