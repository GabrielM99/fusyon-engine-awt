package fusyon.engine.util;

import fusyon.engine.gameobject.component.Collider;
import java.util.*;
import java.util.List;

public class ColliderQuadTree {

    private boolean isSubdivided;

    private int capacity;

    private FloatRectangle bounds;
    private ColliderQuadTree parent;
    private ColliderQuadTree topLeftChild;
    private ColliderQuadTree topRightChild;
    private ColliderQuadTree bottomLeftChild;
    private ColliderQuadTree bottomRightChild;

    private List<Collider> colliderList;

    public ColliderQuadTree(FloatRectangle bounds, int capacity){
        this.bounds = bounds;
        this.capacity = capacity;

        isSubdivided = false;
        colliderList = new ArrayList<>();
    }

    public ColliderQuadTree(ColliderQuadTree parent, FloatRectangle bounds, int capacity){
        this.parent = parent;
        this.bounds = bounds;
        this.capacity = capacity;

        isSubdivided = false;
        colliderList = new ArrayList<>();
    }

    public boolean insert(Collider collider){
        if(colliderList.contains(collider) || !isOverlaping(new FloatRectangle(collider.position.x + collider.offset.x, collider.position.y + collider.offset.y, collider.size.x, collider.size.y), bounds)) return false;

        if(colliderList.size() + 1 > capacity){
            if(!isSubdivided) subdivide();

            topLeftChild.insert(collider);
            topRightChild.insert(collider);
            bottomLeftChild.insert(collider);
            bottomRightChild.insert(collider);
        }else{
            colliderList.add(collider);
        }

        return true;
    }

    public void subdivide(){
        topLeftChild = new ColliderQuadTree(this, new FloatRectangle(bounds.x, bounds.y, bounds.width / 2, bounds.height / 2), capacity);
        topRightChild = new ColliderQuadTree(this, new FloatRectangle(bounds.x + bounds.width / 2, bounds.y, bounds.width / 2, bounds.height / 2), capacity);
        bottomLeftChild = new ColliderQuadTree(this, new FloatRectangle(bounds.x, bounds.y + bounds.height / 2, bounds.width / 2, bounds.height / 2), capacity);
        bottomRightChild = new ColliderQuadTree(this, new FloatRectangle(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2, bounds.width / 2, bounds.height / 2), capacity);
        isSubdivided = true;
    }

    public void unsubdivide(){
        if(isSubdivided){
            if(!topLeftChild.getColliderList().isEmpty() || !topRightChild.getColliderList().isEmpty() || !bottomLeftChild.getColliderList().isEmpty() || !bottomRightChild.getColliderList().isEmpty()){
                return;
            }

            topLeftChild = null;
            topRightChild = null;
            bottomLeftChild = null;
            bottomRightChild = null;
            isSubdivided = false;
        }
    }

    public List<Collider> query(FloatRectangle bounds){
        TreeSet<Collider> colliderSet = new TreeSet<>((o1, o2) -> o1 == o2 ? 0 : 1);

        if(isOverlaping(bounds, this.bounds)){
            for(Collider collider : this.colliderList){
                if(isOverlaping(new FloatRectangle(collider.position.x + collider.offset.x, collider.position.y + collider.offset.y, collider.size.x, collider.size.y), bounds)) {
                    colliderSet.add(collider);
                }
            }

            if(isSubdivided){
                colliderSet.addAll(topLeftChild.query(bounds));
                colliderSet.addAll(topRightChild.query(bounds));
                colliderSet.addAll(bottomLeftChild.query(bounds));
                colliderSet.addAll(bottomRightChild.query(bounds));
            }
        }

        return new ArrayList<>(colliderSet);
    }

    public boolean remove(Collider collider) {
        if (!isOverlaping(new FloatRectangle(collider.position.x + collider.offset.x, collider.position.y + collider.offset.y, collider.size.x, collider.size.y), bounds))
            return false;

        boolean wasRemoved = false;

        if (isSubdivided) wasRemoved = topLeftChild.remove(collider) || wasRemoved;
        if (isSubdivided) wasRemoved = topRightChild.remove(collider) || wasRemoved;
        if (isSubdivided) wasRemoved = bottomLeftChild.remove(collider) || wasRemoved;
        if (isSubdivided) wasRemoved = bottomRightChild.remove(collider) || wasRemoved;

        if(colliderList.remove(collider)){
            if(parent != null){
                //parent.unsubdivide();
            }

            wasRemoved = true;
        }

        return wasRemoved;
    }

    public List<Collider> toList(){
        List<Collider> colliderList = new ArrayList<>();

        for(Collider collider : this.colliderList){
            colliderList.add(collider);
        }

        if(isSubdivided){
            colliderList.addAll(topLeftChild.query(bounds));
            colliderList.addAll(topRightChild.query(bounds));
            colliderList.addAll(bottomLeftChild.query(bounds));
            colliderList.addAll(bottomRightChild.query(bounds));
        }

        return colliderList;
    }

    private boolean isOverlaping(FloatRectangle a, FloatRectangle b) {
        if(Math.max(a.x, a.x + a.width) >= Math.min(b.x, b.x + b.width) &&
                Math.min(a.x, a.x + a.width) <= Math.max(b.x, b.x + b.width) &&
                Math.max(a.y, a.y + a.height) >= Math.min(b.y, b.y + b.height) &&
                Math.min(a.y, a.y + a.height) <= Math.max(b.y, b.y + b.height)){

            return true;
        }

        return false;
    }

    public boolean isSubdivided() {
        return isSubdivided;
    }

    public void setSubdivided(boolean subdivided) {
        isSubdivided = subdivided;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public FloatRectangle getBounds() {
        return bounds;
    }

    public void setBounds(FloatRectangle bounds) {
        this.bounds = bounds;
    }

    public ColliderQuadTree getTopLeftChild() {
        return topLeftChild;
    }

    public void setTopLeftChild(ColliderQuadTree topLeftChild) {
        this.topLeftChild = topLeftChild;
    }

    public ColliderQuadTree getTopRightChild() {
        return topRightChild;
    }

    public void setTopRightChild(ColliderQuadTree topRightChild) {
        this.topRightChild = topRightChild;
    }

    public ColliderQuadTree getBottomLeftChild() {
        return bottomLeftChild;
    }

    public void setBottomLeftChild(ColliderQuadTree bottomLeftChild) {
        this.bottomLeftChild = bottomLeftChild;
    }

    public ColliderQuadTree getBottomRightChild() {
        return bottomRightChild;
    }

    public void setBottomRightChild(ColliderQuadTree bottomRightChild) {
        this.bottomRightChild = bottomRightChild;
    }

    public List<Collider> getColliderList() {
        return colliderList;
    }

    public void setColliderList(List<Collider> colliderList) {
        this.colliderList = colliderList;
    }
}
