package fusyon.engine.gameobject;

import fusyon.engine.util.Vector2;

import java.awt.*;

public class Camera extends GameObject {

    public float distance = 1;

    public Vector2 offset = Vector2.zero;
    public Color backgroundColor = new Color(255, 255, 255);

    public Camera() {
        super("Camera");
    }
}
