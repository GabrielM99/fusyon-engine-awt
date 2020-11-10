package fusyon.engine.gameobject.component;

import fusyon.engine.gfx.RenderModel;
import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.main.Settings;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class SpriteRenderer extends Component{

    public RenderModel renderModel;

    public SpriteRenderer(RenderModel renderModel){
        super("SpriteRenderer");

        this.renderModel = renderModel;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(Graphics g) {
        RendererHandler.request(renderModel, getParent());
    }

    @Override
    public void destroy() {

    }
}
