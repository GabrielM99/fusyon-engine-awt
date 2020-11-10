package fusyon.engine.gameobject;

import fusyon.engine.gameobject.component.TilemapCollider;
import fusyon.engine.gameobject.component.TilemapRenderer;
import fusyon.engine.util.Vector2;

public class Tilemap extends GameObject {

    public TilemapRenderer tilemapRenderer;
    public TilemapCollider tilemapCollider;

    public Tilemap() {
        super("Tilemap");

        addComponent(tilemapRenderer = new TilemapRenderer());
        addComponent(tilemapCollider = new TilemapCollider());
    }
}
