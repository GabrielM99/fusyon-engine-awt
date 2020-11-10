package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.*;
import fusyon.engine.gfx.RenderModel;
import fusyon.engine.main.Settings;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;
import fusyon.polaria.util.IInteractable;
import fusyon.polaria.util.world.World;

import java.awt.image.BufferedImage;

public class Tile extends GameObject {

    private SpriteRenderer spriteRenderer;
    private RectangleCollider rectangleCollider;
    private Animator animator;

    public Tile(String name, BufferedImage sprite, boolean isCollidable) {
        super(name);

        addComponent(spriteRenderer = new SpriteRenderer(new RenderModel(0, Settings.SortingLayers.BACKGROUND, Vector2f.zero, Vector2f.one, sprite)));
        addComponent(rectangleCollider = new RectangleCollider(Vector2f.zero, new Vector2f(sprite.getWidth(), sprite.getHeight())));
        addComponent(animator = new Animator());

        rectangleCollider.setActive(isCollidable);
    }

    public void updateTile(Vector2 position, TilemapRenderer tilemapRenderer){}

    public SpriteRenderer getSpriteRenderer() {
        return spriteRenderer;
    }

    public void setSpriteRenderer(SpriteRenderer spriteRenderer) {
        this.spriteRenderer = spriteRenderer;
    }

    public RectangleCollider getRectangleCollider() {
        return rectangleCollider;
    }

    public void setRectangleCollider(RectangleCollider rectangleCollider) {
        this.rectangleCollider = rectangleCollider;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }
}