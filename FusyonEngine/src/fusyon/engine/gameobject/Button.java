package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.ButtonScript;
import fusyon.engine.gfx.RenderModel;
import fusyon.engine.util.Vector2f;

public class Button extends Canvas{

    public ButtonScript buttonScript;

    public Button(Vector2f offset, Vector2f size, RenderModel renderModel, AnchorPoint anchorPoint) {
        super("Button", renderModel, anchorPoint);

        setSize(size);
        addComponent(buttonScript = new ButtonScript(offset, size));
    }
}
