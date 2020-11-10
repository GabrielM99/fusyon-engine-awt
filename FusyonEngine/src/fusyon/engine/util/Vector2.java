package fusyon.engine.util;

import java.util.Objects;

public class Vector2 {

    public int x, y;

    public static final Vector2 zero = new Vector2(0, 0);
    public static final Vector2 one = new Vector2(1, 1);

    public Vector2(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2f vector){
        this.x = (int) vector.x;
        this.y = (int) vector.y;
    }

    public boolean compare(Vector2 vector){
        if(x == vector.x && y == vector.y){
            return true;
        }

        return false;
    }

    public Vector2 add(Vector2 vector){
        int x = this.x + vector.x;
        int y = this.y + vector.y;

        return new Vector2(x, y);
    }

    public Vector2 subtract(Vector2 vector){
        int x = this.x - vector.x;
        int y = this.y - vector.y;

        return new Vector2(x, y);
    }

    public Vector2 multiply(float value){
        int x = (int) (this.x * value);
        int y = (int) (this.y * value);

        return new Vector2(x, y);
    }

    public Vector2 divide(float value){
        if(value == 0) System.err.println("A vector can't be divided by zero.");

        int x = (int) (this.x / value);
        int y = (int) (this.y / value);

        return new Vector2(x, y);
    }

    public float magnitude(){
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2f normalize(){
        double magnitude = magnitude();

        return new Vector2f((float) (x / magnitude), (float) (y / magnitude));
    }

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
