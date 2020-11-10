package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.LightScript;

import java.awt.*;

public class Light extends GameObject {

    public Light(int innerRadius, int outerRadius, Color color, float intensity) {
        super("Light");

        addComponent(new LightScript(innerRadius, outerRadius, color, intensity));
    }
}
