package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.Component;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static fusyon.engine.main.Engine.engine;

/** The engine's representation of an object.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class GameObject {

    /** Its world position. */
    protected Vector2f position;
    /** Its local position (relative to the parent's). */
    protected Vector2f localPosition;
    /** A refenrece to the parent, if it exists. */
    protected GameObject parent;

    /** A flag that indicates if the GameObject's Component are able to be updated and rendered or not. */
    private boolean isActive;

    /** The name of the GameObject. */
    private String name;

    /** A list containing all its children. */
    protected List<GameObject> childrenList;
    /** A list containing all its components. */
    private List<Component> componentList;

    /** Initializes a new GameObject.
     *
     * @param name  A name for the GameObject.
     */
    public GameObject(String name){
        this.name = name;

        isActive = true;
        position = Vector2f.zero;
        localPosition = position;
        childrenList = new ArrayList<GameObject>();
        componentList = new ArrayList<Component>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Increases the current world position by a certain amount.
     *
     * @param vector    Amount to increase.
     */
    public void increasePosition(Vector2f vector){
        setPosition(new Vector2f(position.x + vector.x, position.y + vector.y));
    }

    public Vector2f getPosition() {
        return position;
    }

    /** Changes the GameObject's world position.
     *
     * If the GameObject has any children, then their position are modified in relation to their parent's. The
     * GameObject's local position relative to its parent (if any) is also calculated.
     *
     * @param position  A world position.
     */
    public void setPosition(Vector2f position) {
        this.position = position;

        for(GameObject children : childrenList){
            children.setPosition(new Vector2f(position.x + children.localPosition.x, position.y + children.localPosition.y));
        }

        if(parent != null){
            localPosition = new Vector2f(position.x - parent.getPosition().x, position.y - parent.getPosition().y);
        }
    }

    public Vector2f getLocalPosition() {
        return localPosition;
    }

    /** Change the GameObject's local position.
     *
     * If the GameObject doesn't has a parent, its world position will be modified instead. Otherwise, a new world
     * position will be calculated in relation with the new local position and all its children will have a new world
     * position calculated in relation with the new local position.
     *
     * @param localPosition A local position (relative to the parent).
     */
    public void setLocalPosition(Vector2f localPosition) {
        if(parent != null){
            position = new Vector2f(parent.getPosition().x + localPosition.x, parent.getPosition().y + localPosition.y);
        }else{
            setPosition(localPosition);
            return;
        }

        for(GameObject children : childrenList){
            children.setPosition(new Vector2f(position.x + children.localPosition.x, position.y + children.localPosition.y));
        }

        this.localPosition = localPosition;
    }

    public GameObject getParent() {
        return parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;

        setPosition(position);
    }

    /** Adds a new children to the GameObject.
     *
     * @param children  A GameObject to be children.
     */
    public void addChildren(GameObject children){
        if(!childrenList.contains(children)){
            children.setParent(this);
            childrenList.add(children);
        }
    }

    public void addChildren(GameObject children, Vector2f position){
        if(!childrenList.contains(children)){
            children.setParent(this);
            children.setPosition(position);
            childrenList.add(children);
        }
    }

    /** Removes a children from the GameObject.
     *
     * @param children  A GameObject to be removed.
     */
    public void removeChildren(GameObject children){
        children.setParent(null);
        childrenList.remove(children);
    }

    /** Gets a GameObject's Component by name (case sensitive).
     *
     * The method will firstly search for a Component name equal to the provided. If it hasn't found any,
     * then it will search for Components names containing the provided one.
     *
     * @param name  A Component name.
     * @return      The first found Component (null if nothing was found).
     */
    public Component getComponent(String name){
        for(Component component : componentList){
            if(component.getName().equals(name)){
                return component;
            }
        }

        for(Component component : componentList){
            if(component.getName().contains(name)){
                return component;
            }
        }

        return null;
    }

    /** Gets a GameObject's Children's Component by name (case sensitive).
     *
     * This method will firstly search for the Component in the GameObject, and just if nothing was found it will try
     * and search for it in the children.
     *
     * @param name  A Component name.
     * @return      The first found component (null if nothing was found).
     */
    public Component getComponentInChildren(String name){
        Component parentComponent = getComponent(name);

        if(parentComponent != null) return parentComponent;

        for(GameObject children : childrenList) {
            for (Component component : children.getComponentList()) {
                if (component.getName().equals(name)) {
                    return component;
                }
            }
        }

        return null;
    }

    public void addComponent(Component component){
        if(!componentList.contains(component)){
            component.setParent(this);
            componentList.add(component);
        }
    }

    public static void instantiate(GameObject gameObject){
        engine.getScene().addObject(gameObject, gameObject.getPosition());
    }

    public static void destroy(GameObject gameObject){
        engine.getScene().removeObject(gameObject);
    }

    public void removeComponent(Component component){
        component.setParent(null);
        componentList.remove(component);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;

        for (Component component : componentList) {
            component.setActive(active);
        }

        for (GameObject gameObject : childrenList){
            gameObject.setActive(active);
        }
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public List<GameObject> getChildrenList() {
        return childrenList;
    }
}
