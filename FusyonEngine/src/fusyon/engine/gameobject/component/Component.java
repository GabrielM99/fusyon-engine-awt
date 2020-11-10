package fusyon.engine.gameobject.component;

import fusyon.engine.gameobject.GameObject;
import fusyon.engine.gameobject.IUpdatable;
import fusyon.engine.main.Engine;

import static fusyon.engine.main.Engine.engine;

/** Script-like objects that can be attached to GameObjects to modify their behaviours.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public abstract class Component implements IUpdatable {

    /** A flag that indicates if the Component is able to be updated and rendered or not. */
    private boolean isActive;

    /** The name of the Component. */
    private String name;
    /** The GameObject that contains the Component. */
    private GameObject parent;

    /** Initializes a new Component.
     *
     * @param name  A name for the Component.
     */
    public Component(String name){
        this.name = name;

        isActive = true;
    }

    /** Called before the start method. */
    public void awake(){}

    /** Called after a regular update.
     *
     * @param delta The time passed (>= 1) since the last frame.
     */
    public void lateUpdate(float delta){}

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public GameObject getParent() {
        return parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
