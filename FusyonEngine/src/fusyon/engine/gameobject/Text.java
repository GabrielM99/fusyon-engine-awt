package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.TextRenderer;
import fusyon.engine.gfx.RenderModel;

import java.awt.*;

public class Text extends Canvas{

    public TextRenderer textRenderer;

    public Text(String text, Color color, Font font, RenderModel renderModel, AnchorPoint anchorPoint) {
        super("Text", renderModel, anchorPoint);

        addComponent(textRenderer = new TextRenderer(text, color, font));
        setKeepProportion(false);
    }
}
