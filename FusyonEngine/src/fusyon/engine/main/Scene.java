package fusyon.engine.main;

import fusyon.engine.gameobject.Camera;
import fusyon.engine.gameobject.component.Component;
import fusyon.engine.gameobject.GameObject;
import fusyon.engine.gameobject.IUpdatable;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Manages, updates and renders all GameObject's presented into it.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public abstract class Scene implements IUpdatable {

    /** The name of the scene. /*/
    private String name;
    /** Current online camera. */
    private Camera camera;
    /** Reference for the physics engine */
    private Physics physics;

    /** A list containing all GameObject's within the scene that will be updated and/or rendered */
    private List<GameObject> objectList;

    /** Initializes a new scene.
     *
     * @param name  A name for the scene.
     */
    public Scene(String name){
        this.name = name;

        objectList = new CopyOnWriteArrayList<>();
        physics = new Physics();
    }


    /** Iterates over each GameObject, updating its components if their are active.
     *
     * @param delta The time passed (>= 1) since the last frame.
     */
    @Override
    public void update(float delta) {
        for(GameObject object : objectList){
            if(object.isActive()){
                for(Component component : object.getComponentList()){
                    if(component.isActive()) {
                        component.update(delta);
                        component.lateUpdate(delta);
                    }
                }
            }
        }
    }

    /** Iterates over each GameObject, rendering its components if their are active.
     *
     * @param g A reference to Graphics, instantiated in the RendererHandler.
     */
    @Override
    public void render(Graphics g) {
        for(GameObject object : objectList){
            if(object.isActive()){
                for(Component component : object.getComponentList()){
                    if(component.isActive()) {
                        component.render(g);
                    }
                }
            }
        }
    }

    /** Adds a new GameObject to the Scene context.
     *
     * If the GameObject is not already presented in the Scene, then it will call the starting methods for the component
     * as well as set the GameObject's position and add its children into the Scene.
     *
     * TODO: GameObjects should store a reference to their Scenes.
     *
     * @param object    A GameObject to add.
     * @param position  A world position.
     */
    public void addObject(GameObject object, Vector2f position){
        if(!objectList.contains(object)){
            object.setPosition(position);

            for(Component component : object.getComponentList()){
                component.awake();
                component.start();
            }

            objectList.add(object);

            for(GameObject children : object.getChildrenList()){
                addObject(children);
            }
        }
    }

    public void addObject(GameObject object){
        if(!objectList.contains(object)){
            for(Component component : object.getComponentList()){
                component.awake();
                component.start();
            }

            objectList.add(object);

            for(GameObject children : object.getChildrenList()){
                addObject(children);
            }
        }
    }

    /** Removes a GameObject from the Scene context.
     *
     * It will call the destroy method on all GameObject's components.
     *
     * @param object    A GameObject to remove.
     */
    public void removeObject(GameObject object){
        for(Component component : object.getComponentList()){
            component.destroy();
        }

        for(GameObject children : object.getChildrenList()){
            removeObject(children);
        }

        objectList.remove(object);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Physics getPhysics() {
        return physics;
    }
}
