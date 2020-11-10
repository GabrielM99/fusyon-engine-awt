package fusyon.engine.gameobject;

import java.awt.*;

public interface IUpdatable {

    /** The first method that will be called upon object creation or insertion in a context. */
    void start();

    /** Update method that will be called a limited amount of times per second.
     *
     * @param delta The time passed (>= 1) since the last frame.
     */
    void update(float delta);

    /** Render method that ill be called whenever possible.
     *
     * @param g A reference to Graphics, instantiated in the RendererHandler.
     */
    void render(Graphics g);

    /** The last method that will be called upon object removal of a context. */
    void destroy();
}
