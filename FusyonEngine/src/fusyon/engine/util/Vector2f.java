package fusyon.engine.util;

import java.util.Objects;

public class Vector2f {

    public float x, y;

    public static final Vector2f zero = new Vector2f(0, 0);
    public static final Vector2f one = new Vector2f(1, 1);

    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2 vector){
        this.x = vector.x;
        this.y = vector.y;
    }

    public boolean compare(Vector2f v){
        if(x == v.x && y == v.y){
            return true;
        }

        return false;
    }

    public Vector2f add(Vector2f vector){
        float x = this.x + vector.x;
        float y = this.y + vector.y;

        return new Vector2f(x, y);
    }

    public Vector2f subtract(Vector2f vector){
        float x = this.x - vector.x;
        float y = this.y - vector.y;

        return new Vector2f(x, y);
    }

    public Vector2f multiply(float value){
        float x = this.x * value;
        float y = this.y * value;

        return new Vector2f(x, y);
    }

    public Vector2f divide(float value){
        float x = this.x / value;
        float y = this.y / value;

        return new Vector2f(x, y);
    }

    public float magnitude(){
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2f normalize(){
        float magnitude = magnitude();

        if(magnitude == 0)  return new Vector2f(0, 0);

        return new Vector2f(x / magnitude, y / magnitude);
    }

    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return x == vector2.x &&
                y == vector2.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
