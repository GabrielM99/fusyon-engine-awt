package fusyon.engine.gameobject.component;

import fusyon.engine.util.ColliderHit;
import fusyon.engine.gameobject.Tile;
import fusyon.engine.main.Physics;
import fusyon.engine.util.TileChunk;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TilemapCollider extends Component {

    private TilemapRenderer tilemapRenderer;

    private Map<Dimension, Collider> colliderMap = new HashMap<>();

    public TilemapCollider() {
        super("TilemapCollider");
    }

    @Override
    public void start() {
        tilemapRenderer = (TilemapRenderer) getParent().getComponent("TilemapRenderer");

        List<TileChunk> tileChunkList = new ArrayList<>(tilemapRenderer.getTileChunkMap().values());

        for(TileChunk tileChunk : tileChunkList){
            for(int x = 0; x < tilemapRenderer.chunkSize.x; x++){
                for(int y = 0; y < tilemapRenderer.chunkSize.y; y++){
                    addTileCollider(new Vector2(tileChunk.getPosition().x * tileChunk.getSize().x + x, tileChunk.getPosition().y * tileChunk.getSize().y + y));
                }
            }
        }
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void destroy() {

    }

    public void addTileCollider(Vector2 position){
        Tile tile = tilemapRenderer.getTile(position);

        if(tile == null) return;

        RectangleCollider rectangleCollider = tile.getRectangleCollider();

        if(!rectangleCollider.isActive()) return;

        Vector2f offset = rectangleCollider.offset;
        Vector2f size = rectangleCollider.size;
        rectangleCollider = new RectangleCollider(getParent().getPosition(), new Vector2f(position).multiply(tilemapRenderer.pixelsPerUnit).subtract(getParent().getPosition()).add(offset), size);

        rectangleCollider.setParent(getParent());
        colliderMap.put(new Dimension(position.x, position.y), rectangleCollider);
        Physics.addCollider(rectangleCollider);
    }

    public void removeTileCollider(Vector2 position){
        Dimension key = new Dimension(position.x, position.y);
        Collider collider = colliderMap.get(key);

        if(collider != null){
            Physics.removeCollider(collider);
            colliderMap.remove(key);
        }
    }
}
