package fusyon.engine.gameobject.component;

import fusyon.engine.util.ColliderHit;
import fusyon.engine.main.Physics;
import fusyon.engine.util.ICollider;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.util.List;

public abstract class Collider extends Component{

    public Vector2f position = Vector2f.zero;
    public Vector2f offset;
    public Vector2f size;

    private boolean isTrigger;
    private boolean isCanvasCollider;
    private boolean ignoreMouseRaycast;

    private Vector2f lastPosition;
    private ICollider iCollider;

    public Collider(String name, Vector2f offset, Vector2f size) {
        super(name);

        this.offset = offset;
        this.size = size;
    }

    public Collider(String name, Vector2f position, Vector2f offset, Vector2f size) {
        super(name);

        this.position = position;
        this.offset = offset;
        this.size = size;
    }

    public abstract ColliderHit overlapCollider();
    public abstract List<ColliderHit> overlapColliderAll();

    @Override
    public void start() {
        Physics.addCollider(this);

        lastPosition = position;
    }

    @Override
    public void update(float delta) {
        updatePosition();
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void destroy() {
        Physics.removeCollider(this);
    }

    private void updatePosition(){
        Vector2f newPosition;

        if(getParent() != null){
            newPosition = getParent().getPosition();
        }else{
            return;
        }

        if(!newPosition.compare(lastPosition)){
            if(Physics.removeCollider(this)) {
                position = newPosition;
                Physics.addCollider(this);
                lastPosition = position;
            }
        }
    }

    public Vector2f getCenter(){
        return getParent().getPosition().add(offset);
    }

    public void onCollisionEnter(ColliderHit colliderHit){
        if(iCollider != null) iCollider.onCollisionEnter(colliderHit);
    }

    public void onMouseEnter(){
        if(iCollider != null) iCollider.onMouseEnter();
    }

    public boolean isCanvasCollider() {
        return isCanvasCollider;
    }

    public void setCanvasCollider(boolean canvasCollider) {
        isCanvasCollider = canvasCollider;
    }

    public void setICollider(ICollider iCollider) {
        this.iCollider = iCollider;
    }

    public boolean isIgnoreMouseRaycast() {
        return ignoreMouseRaycast;
    }

    public void setIgnoreMouseRaycast(boolean ignoreMouseRaycast) {
        this.ignoreMouseRaycast = ignoreMouseRaycast;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }
}
