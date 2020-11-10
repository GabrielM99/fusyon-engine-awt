package fusyon.engine.main;

import fusyon.engine.gfx.RendererHandler;

/** This is the main class for the engine, and must be only called statically.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class Engine implements Runnable{

    /** Global variable to access the engine instance. */
    public static final Engine engine = new Engine();

    /** Flag that indicates whether the game loop is running or not. */
    private boolean isRunning;

    /** Current setted scene, which will be updated and rendered within the game loop. */
    private Scene scene;
    /** Current setted display, where the engine will try to draw on. */
    private Display display;
    /** Main game thread. */
    private Thread thread;

    /** Creates a new thread, starts the game loop if a display and a scene are setted and setups RendererHandler. */
    public void start(){
        if(display == null){
            System.err.println("[ENGINE ERROR]: Display has not been setted.");
            return;
        }

        if(scene == null){
            System.err.println("[ENGINE ERROR]: There is no active Scene.");
            return;
        }

        isRunning = true;
        thread = new Thread(this);

        RendererHandler.setup();
        thread.start();
    }

    /** Stops the engine from running, closing the display.
     * TODO: Terminate main thread.
     */
    public void stop(){
        display.close();
    }

    /** The main game loop, where everything is updated and rendered while isRunning flag is true.
     *
     * It calculates the amount of time passed between each tick in a variable called delta, in order to keep update
     * calls consistent. A delta lesser than 1 means not sufficient time has passed since the last frame. Otherwise,
     * a delta greater than 1 means that update calls are behind. Render calls are called whenever is possible, not being
     * limited by a tick factor, but instead by computational speed.
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int ticks = 0;

        while(isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while(delta >= 1) {
                update((float) (1 / delta / amountOfTicks));

                ticks++;
                delta--;
            }

            if(isRunning) {
                render();

                frames++;
            }

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

                System.out.println("FPS: " + frames + " TICKS: " + ticks);

                ticks = 0;
                frames = 0;
            }
        }

        stop();
    }

    /** Handles engine's essential updates, as from the scene and event classes.
     *
     * @param delta The current amount of time (>= 1) passed between each frame, used to compensate behind calls.
     */
    public void update(float delta){
        if(scene != null){
            scene.update(delta);
        }

        display.getKeyboardHandler().update();
        display.getMouseHandler().update();
    }

    /** Calls each stage of the render process. */
    public void render(){
        RendererHandler.prepare();
        RendererHandler.render();
        RendererHandler.display();
    }

    public Scene getScene() {
        return scene;
    }

    /** Sets a new scene to be rendered and updated by the engine.
     *
     * The start method is called within the new scene. The destroy method is called within the old scene.
     *
     * @param scene A valid scene, that is not null nor the same as the current.
     */
    public void setScene(Scene scene){
        if(scene == null || scene == this.scene) return;

        if(this.scene != null){
            scene.destroy();
        }

        this.scene = scene;

        scene.start();
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }
}
