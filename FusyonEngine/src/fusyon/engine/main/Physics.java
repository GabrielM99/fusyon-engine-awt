package fusyon.engine.main;

import fusyon.engine.event.MouseHandler;
import fusyon.engine.gameobject.Canvas;
import fusyon.engine.gameobject.GameObject;
import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.util.ColliderHit;
import fusyon.engine.gameobject.component.Collider;
import fusyon.engine.gameobject.component.RectangleCollider;
import fusyon.engine.gameobject.component.RigidBody;
import fusyon.engine.util.*;

import java.util.ArrayList;
import java.util.List;

import static fusyon.engine.main.Engine.engine;

/** Handles all GameObject's physics.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class Physics {

    /** The ColliderQuadTree that stores every Collider from the scene. */
    private ColliderQuadTree colliderTree = new ColliderQuadTree(new FloatRectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE), 16);

    /** Overlaps a rectangle, checking for a collision in its area.
     *
     * @param position  A world position.
     * @param size      A area to overlap.
     * @return          The first ColliderHit containing collision data (null if no collision is detected).
     */
    public static ColliderHit overlapRectangle(Vector2f position, Vector2f size){
        return overlapRectangle(new RectangleCollider(position, Vector2f.zero, size));
    }

    /** Overlaps a rectangle, checking for collisions in its area.
     *
     * @param position  A world position.
     * @param size      A area to overlap.
     * @return          A list of ColliderHits containing collision data (null if no collision is detected).
     */
    public static List<ColliderHit> overlapRectangleAll(Vector2f position, Vector2f size){
        return overlapRectangleAll(new RectangleCollider(position, Vector2f.zero, size));
    }

    /** Overlaps a RectangleCollider, checking a collision in the specified RectangleCollider's area.
     *
     * @param rectangleCollider The RectangleCollider checking for collisions.
     * @return                  The first ColliderHit containing collision data (null if no collision is detected).
     */
    public static ColliderHit overlapRectangle(RectangleCollider rectangleCollider){
        ColliderHit colliderHit = null;
        Vector2f size = rectangleCollider.size;
        Vector2f position = rectangleCollider.position;
        Vector2f offset = rectangleCollider.offset;

        List<Collider> colliderList = engine.getScene().getPhysics().getColliderTree().query(new FloatRectangle(position.x + offset.x, position.y + offset.y, size.x, size.y));

        for(Collider collider : colliderList){
            if(collider == rectangleCollider || !collider.isActive() || collider.isCanvasCollider()) continue;

            if(collider instanceof RectangleCollider && (colliderHit = checkRectRectCollision(rectangleCollider, (RectangleCollider) collider)) != null){

                rectangleCollider.onCollisionEnter(colliderHit);
                collider.onCollisionEnter(new ColliderHit(rectangleCollider.getParent(), rectangleCollider, (RigidBody) rectangleCollider.getParent().getComponent("RigidBody"), colliderHit.getContactDirection().multiply(-1)));
                break;
            }
        }

        return colliderHit;
    }

    /** Overlaps a RectangleCollider, checking a collision in the specified RectangleCollider's area.
     *
     * @param rectangleCollider The RectangleCollider checking for collisions.
     * @return                  A list of ColliderHits containing collision data (null if no collision is detected).
     */
    public static List<ColliderHit> overlapRectangleAll(RectangleCollider rectangleCollider){
        Vector2f size = rectangleCollider.size;
        Vector2f position = rectangleCollider.position;
        Vector2f offset = rectangleCollider.offset;
        List<ColliderHit> colliderHits = new ArrayList<>();

        List<Collider> colliderList = engine.getScene().getPhysics().getColliderTree().query(new FloatRectangle(position.x + offset.x, position.y + offset.y, size.x, size.y));

        for(Collider collider : colliderList){
            if(collider == rectangleCollider || !collider.isActive() || collider.isCanvasCollider()) continue;

            ColliderHit colliderHit;

            if(collider instanceof RectangleCollider && (colliderHit = checkRectRectCollision(rectangleCollider, (RectangleCollider) collider)) != null){
                colliderHits.add(colliderHit);
                rectangleCollider.onCollisionEnter(colliderHit);
                collider.onCollisionEnter(new ColliderHit(rectangleCollider.getParent(), rectangleCollider, (RigidBody) rectangleCollider.getParent().getComponent("RigidBody"), colliderHit.getContactDirection().multiply(-1)));
            }
        }

        return colliderHits;
    }

    /** Raycast from the mouse local position to any intersectable Canvas.
     *
     * @return  A list of ColliderHits containing collision data (null if no collision is detected).
     */
    public static List<ColliderHit> mouseCanvasOverlapAll(){
        RectangleCollider rectangleCollider = new RectangleCollider(MouseHandler.getMousePosition(), MouseHandler.getMouseSize());
        Vector2f size = rectangleCollider.size;
        Vector2f position = rectangleCollider.position;
        Vector2f offset = rectangleCollider.offset;
        List<ColliderHit> colliderHits = new ArrayList<>();

        List<Collider> colliderList = engine.getScene().getPhysics().getColliderTree().query(new FloatRectangle(position.x + offset.x, position.y + offset.y, size.x, size.y));

        for(Collider collider : colliderList){
            if(collider == rectangleCollider || !collider.isActive() || collider.isIgnoreMouseRaycast() || !collider.isCanvasCollider()) continue;

            ColliderHit colliderHit;
            GameObject gameObject = collider.getParent();
            Vector2f colliderSize = collider.size;

            if(gameObject instanceof Canvas){
                Canvas canvas = (Canvas) gameObject;

                if(canvas.isKeepProportion()){
                    Vector2f stretchFactor = RendererHandler.getStretchFactor();
                    collider.size = new Vector2f(colliderSize.x * stretchFactor.x, colliderSize.y * stretchFactor.y);
                }
            }

            if(collider instanceof RectangleCollider && (colliderHit = checkRectRectCollision(rectangleCollider, (RectangleCollider) collider)) != null){
                colliderHits.add(colliderHit);
                collider.onMouseEnter();
            }

            collider.size = colliderSize;
        }

        if(colliderHits.size() > 0) MouseHandler.setMouseOverCanvas(true);

        return colliderHits;
    }

    /** Raycast from the mouse world position to any intersectable Canvas.
     *
     * @return  A list of ColliderHits containing collision data (null if no collision is detected).
     */
    public static List<ColliderHit> mouseOverlapAll(){
        RectangleCollider rectangleCollider = new RectangleCollider(MouseHandler.getMouseWorldPosition(), MouseHandler.getMouseSize());
        Vector2f size = rectangleCollider.size;
        Vector2f position = rectangleCollider.position;
        Vector2f offset = rectangleCollider.offset;
        List<ColliderHit> colliderHits = new ArrayList<>();

        List<Collider> colliderList = engine.getScene().getPhysics().getColliderTree().query(new FloatRectangle(position.x + offset.x, position.y + offset.y, size.x, size.y));

        for(Collider collider : colliderList){
            if(collider == rectangleCollider || !collider.isActive() || collider.isIgnoreMouseRaycast() || collider.isCanvasCollider()) continue;

            ColliderHit colliderHit;

            if(collider instanceof RectangleCollider && (colliderHit = checkRectRectCollision(rectangleCollider, (RectangleCollider) collider)) != null){
                colliderHits.add(colliderHit);
                collider.onMouseEnter();
            }
        }

        return colliderHits;
    }

    /** Check the collision between two RectangleColliders.
     *
     * @param a A RectangleCollider.
     * @param b Another RectangleCollider.
     * @return  The ColliderHit containing collision data (null if there is not a collision).
     */
    private static ColliderHit checkRectRectCollision(RectangleCollider a, RectangleCollider b){
        Vector2f positionA = a.position, offsetA = a.offset, sizeA = a.size;
        Vector2f positionB = b.position, offsetB = b.offset, sizeB = b.size;

        if(Math.max(positionA.x + offsetA.x, positionA.x + sizeA.x + offsetA.x) >= Math.min(positionB.x + offsetB.x, positionB.x + sizeB.x + offsetB.x) &&
                Math.min(positionA.x + offsetA.x, positionA.x + sizeA.x + offsetA.x) <= Math.max(positionB.x + offsetB.x, positionB.x + sizeB.x + offsetB.x) &&
                Math.max(positionA.y + offsetA.y, positionA.y + sizeA.y + offsetA.y) >= Math.min(positionB.y + offsetB.y, positionB.y + sizeB.y + offsetB.y) &&
                Math.min(positionA.y + offsetA.y, positionA.y + sizeA.y + offsetA.y) <= Math.max(positionB.y + offsetB.y, positionB.y + sizeB.y + offsetB.y)){

            return new ColliderHit(b.getParent(), b, (RigidBody) b.getParent().getComponent("RigidBody"), b.getCenter().subtract(a.getCenter()).normalize());
        }

        return null;
    }

    public static boolean addCollider(Collider collider){
        return engine.getScene().getPhysics().getColliderTree().insert(collider);
    }

    public static boolean removeCollider(Collider collider){
        return engine.getScene().getPhysics().getColliderTree().remove(collider);
    }

    public ColliderQuadTree getColliderTree() {
        return colliderTree;
    }
}
