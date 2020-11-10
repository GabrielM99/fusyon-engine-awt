package fusyon.engine.util;

import javafx.scene.shape.Circle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTree {

    private boolean isSubdivided;

    private int capacity;

    private Rectangle bounds;
    private QuadTree topLeftChild;
    private QuadTree topRightChild;
    private QuadTree bottomLeftChild;
    private QuadTree bottomRightChild;

    private List<Point> pointList;

    public QuadTree(Rectangle bounds, int capacity){
        this.bounds = bounds;
        this.capacity = capacity;

        isSubdivided = false;
        pointList = new ArrayList<Point>();
    }

    public boolean insert(Point point){
        if(!bounds.contains(point)) return false;

        if(pointList.size() + 1 > capacity){
            if(!isSubdivided) subdivide();

            if(topLeftChild.insert(point) || topRightChild.insert(point) || bottomLeftChild.insert(point) || bottomRightChild.insert(point)){
                return true;
            }
        }else{
            pointList.add(point);
        }

        return true;
    }

    public void subdivide(){
        topLeftChild = new QuadTree(new Rectangle(bounds.x, bounds.y, bounds.width / 2, bounds.height / 2), capacity);
        topRightChild = new QuadTree(new Rectangle(bounds.x + bounds.width / 2, bounds.y, bounds.width / 2, bounds.height / 2), capacity);
        bottomLeftChild = new QuadTree(new Rectangle(bounds.x, bounds.y + bounds.height / 2, bounds.width / 2, bounds.height / 2), capacity);
        bottomRightChild = new QuadTree(new Rectangle(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2, bounds.width / 2, bounds.height / 2), capacity);
        isSubdivided = true;
    }

    public List<Point> query(Rectangle bounds){
        List<Point> pointList = new ArrayList<>();

        if(this.bounds.intersects(bounds)){
            for(Point point : this.pointList){
                if(bounds.contains(point)) {
                    pointList.add(point);
                }
            }

            if(isSubdivided){
                pointList.addAll(topLeftChild.query(bounds));
                pointList.addAll(topRightChild.query(bounds));
                pointList.addAll(bottomLeftChild.query(bounds));
                pointList.addAll(bottomRightChild.query(bounds));
            }
        }

        return pointList;
    }

    public List<Point> query(Circle bounds){
        List<Point> pointList = new ArrayList<>();

        if(bounds.intersects(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height)){
            for(Point point : this.pointList){
                if(bounds.contains(point.x, point.y)) {
                    pointList.add(point);
                }
            }

            if(isSubdivided){
                pointList.addAll(topLeftChild.query(bounds));
                pointList.addAll(topRightChild.query(bounds));
                pointList.addAll(bottomLeftChild.query(bounds));
                pointList.addAll(bottomRightChild.query(bounds));
            }
        }

        return pointList;
    }

    public List<Point> toList(){
        List<Point> pointList = new ArrayList<>();

        for(Point point : this.pointList){
            pointList.add(point);
        }

        if(isSubdivided){
            pointList.addAll(topLeftChild.query(bounds));
            pointList.addAll(topRightChild.query(bounds));
            pointList.addAll(bottomLeftChild.query(bounds));
            pointList.addAll(bottomRightChild.query(bounds));
        }

        return pointList;
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

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public QuadTree getTopLeftChild() {
        return topLeftChild;
    }

    public void setTopLeftChild(QuadTree topLeftChild) {
        this.topLeftChild = topLeftChild;
    }

    public QuadTree getTopRightChild() {
        return topRightChild;
    }

    public void setTopRightChild(QuadTree topRightChild) {
        this.topRightChild = topRightChild;
    }

    public QuadTree getBottomLeftChild() {
        return bottomLeftChild;
    }

    public void setBottomLeftChild(QuadTree bottomLeftChild) {
        this.bottomLeftChild = bottomLeftChild;
    }

    public QuadTree getBottomRightChild() {
        return bottomRightChild;
    }

    public void setBottomRightChild(QuadTree bottomRightChild) {
        this.bottomRightChild = bottomRightChild;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }
}
