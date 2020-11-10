package fusyon.engine.gameobject.component;

import fusyon.engine.event.MouseHandler;
import fusyon.engine.gameobject.Text;
import fusyon.engine.main.Physics;
import fusyon.engine.util.ColliderHit;
import fusyon.engine.util.IButton;
import fusyon.engine.util.ICollider;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

import static fusyon.engine.main.Engine.engine;

public class ButtonScript extends Component {

    public Vector2f offset = Vector2f.zero;
    public Vector2f size = Vector2f.zero;

    private RectangleCollider rectangleCollider;
    private SpriteRenderer spriteRenderer;
    private BufferedImage defaultSprite;
    private BufferedImage hoveredSprite;
    private BufferedImage pressedSprite;
    private IButton iButton;

    public ButtonScript() {
        super("Button");

        rectangleCollider = new RectangleCollider(offset, size);
    }

    public ButtonScript(Vector2f offset, Vector2f size) {
        super("Button");

        this.offset = offset;
        this.size = size;

        rectangleCollider = new RectangleCollider(offset, size);
    }

    @Override
    public void start() {
        spriteRenderer = (SpriteRenderer) getParent().getComponent("SpriteRenderer");

        rectangleCollider.setParent(getParent());
        rectangleCollider.setCanvasCollider(true);
        rectangleCollider.start();
        rectangleCollider.setICollider(new ICollider() {
            @Override
            public void onCollisionEnter(ColliderHit colliderHit) {

            }

            @Override
            public void onMouseEnter() {
                List<Integer> mousePressedButtonList = new ArrayList<>(MouseHandler.getPressedButtonList());
                List<Integer> mouseReleasedButtonList = new ArrayList<>(MouseHandler.getReleasedButtonList());

                spriteRenderer.renderModel.setSprite(hoveredSprite);

                if(mousePressedButtonList.size() > 0){
                    spriteRenderer.renderModel.setSprite(pressedSprite);

                    if(iButton != null) iButton.onMousePress(mousePressedButtonList.get(0));
                }else if(mouseReleasedButtonList.size() > 0){
                    spriteRenderer.renderModel.setSprite(pressedSprite);

                    if(iButton != null) iButton.onMouseClick(mouseReleasedButtonList.get(0));
                }

                if(iButton != null) iButton.onMouseEnter();
            }
        });
    }

    @Override
    public void update(float delta) {
        spriteRenderer.renderModel.setSprite(defaultSprite);
        rectangleCollider.update(delta);
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void destroy() {

    }

    public void setDefaultSprite(BufferedImage defaultSprite) {
        if(defaultSprite != null){
            this.size = new Vector2f(defaultSprite.getWidth(), defaultSprite.getHeight());
        }

        this.defaultSprite = defaultSprite;
    }

    public void setHoveredSprite(BufferedImage hoveredSprite) {
        if(hoveredSprite != null && defaultSprite == null){
            this.size = new Vector2f(hoveredSprite.getWidth(), hoveredSprite.getHeight());
        }

        this.hoveredSprite = hoveredSprite;
    }

    public void setPressedSprite(BufferedImage pressedSprite) {
        if(pressedSprite != null && defaultSprite == null && hoveredSprite == null){
            this.size = new Vector2f(pressedSprite.getWidth(), pressedSprite.getHeight());
        }

        this.pressedSprite = pressedSprite;
    }

    public void setIButton(IButton iButton) {
        this.iButton = iButton;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        rectangleCollider.setActive(active);
    }
}
