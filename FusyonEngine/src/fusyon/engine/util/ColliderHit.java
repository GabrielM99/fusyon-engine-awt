package fusyon.engine.util;

import fusyon.engine.gameobject.GameObject;
import fusyon.engine.gameobject.component.Collider;
import fusyon.engine.gameobject.component.RigidBody;
import fusyon.engine.util.Vector2f;

public class ColliderHit {

    private GameObject gameObject;
    private Collider collider;
    private RigidBody rigidBody;
    private Vector2f contactDirection;

    public ColliderHit(GameObject gameObject, Collider collider, RigidBody rigidBody, Vector2f contactDirection) {
        this.gameObject = gameObject;
        this.collider = collider;
        this.rigidBody = rigidBody;
        this.contactDirection = contactDirection;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public Collider getCollider() {
        return collider;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public Vector2f getContactDirection() {
        return contactDirection;
    }
}
