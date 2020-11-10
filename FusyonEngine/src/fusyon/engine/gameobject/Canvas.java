package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.SpriteRenderer;
import fusyon.engine.gfx.RenderModel;
import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.main.Display;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.image.BufferedImage;

import static fusyon.engine.main.Engine.engine;

public class Canvas extends GameObject {

    public RenderModel renderModel;

    private static final Vector2[] anchorNormalizedPositionArray = {
            new Vector2(0, 0),
            new Vector2(1, 0),
            new Vector2(2, 0),
            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(2, 1),
            new Vector2(0, 2),
            new Vector2(1, 2),
            new Vector2(2, 2)
    };

    private Vector2f size = Vector2f.zero;
    private AnchorPoint anchorPoint = AnchorPoint.TOP_LEFT;

    public Canvas(String name, RenderModel renderModel, AnchorPoint anchorPoint) {
        super(name);

        this.renderModel = renderModel;

        if (anchorPoint != null) {
            this.anchorPoint = anchorPoint;
        }

        if (renderModel != null) {
            renderModel.setUseLocalPosition(true);
            renderModel.setOverlay(true);
            addComponent(new SpriteRenderer(renderModel));
        }
    }

    public enum AnchorPoint{
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        LEFT,
        CENTER,
        RIGHT,
        BOTTOM_LEFT,
        BOTTOM,
        BOTTOM_RIGHT
    }

    @Override
    public Vector2f getPosition() {
        Vector2f localPosition = this.localPosition;

        if(getParent() == null || (getParent() != null && getParent().getChildrenList().size() == 0)) {
            Vector2f stretchFactor = RendererHandler.getStretchFactor();
            localPosition = new Vector2f(this.localPosition.x * stretchFactor.x, this.localPosition.y * stretchFactor.y);
        }

        return getRelativePosition().add(localPosition);
    }

    public Vector2f getRelativePosition(){
        Vector2 anchorPosition = anchorNormalizedPositionArray[anchorPoint.ordinal()];
        GameObject objectParent = getParent();

        if(objectParent != null && objectParent instanceof Canvas) {
            Canvas canvasParent = (Canvas) objectParent;
            Vector2f positionParent = objectParent.getPosition();
            Vector2f sizeParent = canvasParent.getSize();

            if(canvasParent.renderModel != null && canvasParent.isKeepProportion()){
                Vector2f stretchFactor = RendererHandler.getStretchFactor();
                sizeParent = new Vector2f(sizeParent.x * stretchFactor.x, sizeParent.y * stretchFactor.y);
            }

            return new Vector2f(positionParent.x + anchorPosition.x * (sizeParent.x / 2f), positionParent.y + anchorPosition.y * (sizeParent.y / 2f));
        }

        Display display = engine.getDisplay();

        return new Vector2f(anchorPosition.x * (display.getCanvasWidth() / 2f), anchorPosition.y * (display.getCanvasHeight() / 2f));
    }

    @Override
    public void setPosition(Vector2f position) {
        this.localPosition = position;
    }

    @Override
    public void setLocalPosition(Vector2f localPosition) {
        setPosition(localPosition);
    }

    public boolean isKeepProportion(){
        return renderModel.isKeepProportion();
    }

    public void setKeepProportion(boolean keepProportion){
        renderModel.setKeepProportion(keepProportion);
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public AnchorPoint getAnchorPoint() {
        return anchorPoint;
    }

    public void setAnchorPoint(AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }
}
