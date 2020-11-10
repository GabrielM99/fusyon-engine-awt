package fusyon.engine.util;

public class GameMath {

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float lerp(float currentValue, float finalValue, float dt){
        float difference = finalValue - currentValue;

        return currentValue + difference * dt;
    }

    public static Vector2f lerp(Vector2f currentValue, Vector2f finalValue, float dt){
        float x = lerp(currentValue.x, finalValue.x, dt);
        float y = lerp(currentValue.y, finalValue.y, dt);

        return new Vector2f(x, y);
    }

    public static float biasFunction(float x, float bias) {
        float k = (1 - bias) * (1 - bias) * (1 - bias);

        return (x * k) / (x * k - x + 1);
    }
}
