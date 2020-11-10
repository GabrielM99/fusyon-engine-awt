package fusyon.engine.gameobject.component;

import fusyon.engine.util.ColliderHit;
import fusyon.engine.main.Physics;
import fusyon.engine.util.Vector2f;

import java.util.List;

public class RectangleCollider extends Collider{

    public RectangleCollider(Vector2f offset, Vector2f size) {
        super("RectangleCollider", offset, size);
    }

    public RectangleCollider(Vector2f position, Vector2f offset, Vector2f size) {
        super("RectangleCollider", position, offset, size);
    }

    @Override
    public ColliderHit overlapCollider() {
        return Physics.overlapRectangle(this);
    }

    @Override
    public List<ColliderHit> overlapColliderAll() {
        return Physics.overlapRectangleAll(this);
    }

    public Vector2f getCenter(){
        return position.add(offset).add(size.divide(2));
    }
}
