package fusyon.engine.gameobject.component;

import fusyon.engine.util.ColliderHit;
import fusyon.engine.util.GameMath;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.util.List;

public class RigidBody extends Component{

    public float mass = 1;
    public float linearDrag = 0.3f;

    public Vector2f linearVelocity = Vector2f.zero;
    public Collider collider;

    public RigidBody(float mass, float linearDrag) {
        super("RigidBody");

        this.mass = mass;
        this.linearDrag = linearDrag;
    }

    public RigidBody() {
        super("RigidBody");
    }

    @Override
    public void start() {
        collider = (Collider) getParent().getComponent("Collider");
    }

    @Override
    public void update(float delta) {
        linearVelocity = linearVelocity.multiply(GameMath.clamp(1 - linearDrag, 0, 1));

        movePosition(getParent().getPosition().add(linearVelocity.multiply(delta)));
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void destroy() {

    }

    public void applyForce(Vector2f force){
        Vector2f forceDirection = force.normalize();
        force.x *= Math.abs(forceDirection.x);
        force.y *= Math.abs(forceDirection.y);
        Vector2f linearAcceleration = force.divide(mass);
        linearVelocity = linearVelocity.add(linearAcceleration);
    }

    public void movePosition(Vector2f position){
        float stepX = position.x - getParent().getPosition().x;
        float stepY = position.y - getParent().getPosition().y;

        if(stepX == 0 && stepY == 0) return;

        if(collider != null) {
            boolean canMove = true;

            List<ColliderHit> colliderHitList;

            collider.offset.x += stepX;
            colliderHitList = collider.overlapColliderAll();
            collider.offset.x -= stepX;

            for(ColliderHit colliderHit : colliderHitList) {
                if (collider.isTrigger() || colliderHit == null || colliderHit.getCollider().isTrigger()){
                    continue;
                }else{
                    canMove = false;
                    break;
                }
            }

            if(canMove) getParent().setPosition(new Vector2f(position.x, getParent().getPosition().y));

            canMove = true;
            collider.offset.y += stepY;
            colliderHitList = collider.overlapColliderAll();
            collider.offset.y -= stepY;

            for(ColliderHit colliderHit : colliderHitList) {
                if (collider.isTrigger() || colliderHit == null || colliderHit.getCollider().isTrigger()){
                    continue;
                }else{
                    canMove = false;
                    break;
                }
            }

            if(canMove) getParent().setPosition(new Vector2f(getParent().getPosition().x, position.y));
        }else{
            getParent().setPosition(position);
        }
    }
}
