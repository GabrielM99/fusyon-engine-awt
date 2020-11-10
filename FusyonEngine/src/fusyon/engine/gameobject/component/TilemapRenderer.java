package fusyon.engine.gameobject.component;

import fusyon.engine.gameobject.GameObject;
import fusyon.engine.gameobject.Tile;
import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.gfx.Resources;
import fusyon.engine.main.Settings;
import fusyon.engine.util.TileChunk;
import fusyon.engine.gfx.RenderModel;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class TilemapRenderer extends Component{

    public int pixelsPerUnit = 16;
    public int renderLayer = 0;

    public Vector2 chunkSize = new Vector2(16, 16);
    public Settings.SortingLayers sortingLayer = Settings.SortingLayers.BACKGROUND;

    private boolean needUpdate;

    private TilemapCollider tilemapCollider;
    private RenderModel renderModel = new RenderModel(renderLayer, sortingLayer, Vector2f.zero, Vector2f.one, null);

    private Map<BufferedImage, BufferedImage> animationSpriteBufferMap = new HashMap<>();
    private Map<Dimension, TileChunk> tileChunkMap = new HashMap<>();
    private Set<Tile> tileSet = new TreeSet<>((s1, s2) -> s1 != null && s1 == s2 ? 0 : 1);

    public TilemapRenderer() {
        super("TilemapRenderer");

        renderModel.setUseInstanceData(true);
        renderModel.setUseIDInstanceData(true);
    }

    @Override
    public void start() {
        tilemapCollider = (TilemapCollider) getParent().getComponent("TilemapCollider");

        for(Tile tile : tileSet){
            tile.getAnimator().setSpriteRenderer(tile.getSpriteRenderer());
        }
    }

    @Override
    public void update(float delta) {
        updateTileAnimation(delta);
    }

    @Override
    public void render(Graphics g) {
        renderTileChunks(g);
    }

    @Override
    public void destroy() {

    }

    public void addTile(Tile tile, Vector2 position){
        if(tile == null) return;

        Vector2 tileChunkPosition = getTileToTileChunkPosition(position);
        Dimension tileChunkMapKey = new Dimension(tileChunkPosition.x, tileChunkPosition.y);
        TileChunk tileChunk = tileChunkMap.get(tileChunkMapKey);

        if(tileChunk == null){
            tileChunkMap.put(tileChunkMapKey, new TileChunk(tileChunkPosition, chunkSize));
            addTile(tile, position);
        }else{
            if(tileChunk.addTile(tile, getTileToTileChunkArrayPosition(position))) {
                if(tilemapCollider != null){
                    tilemapCollider.addTileCollider(position);
                }

                RenderModel tileRenderModel = tile.getSpriteRenderer().renderModel;

                long instanceID = getTileInstanceID(position);

                renderModel.addIDInstanceData(instanceID);
                renderModel.addSpriteInstanceData(instanceID, tileRenderModel.getSprite());
                renderModel.addOffsetInstanceData(instanceID, new Vector2f(position.x * pixelsPerUnit, position.y * pixelsPerUnit));
                renderModel.addScaleInstanceData(instanceID, tileRenderModel.getScale());
                tileSet.add(tile);

                updateNearbyTiles(position);
            }
        }
    }

    public void removeTile(Vector2 position){
        Vector2 tileChunkPosition = getTileToTileChunkPosition(position);
        Dimension tileChunkMapKey = new Dimension(tileChunkPosition.x, tileChunkPosition.y);
        TileChunk tileChunk = tileChunkMap.get(tileChunkMapKey);

        if(tileChunk != null){
            if(tilemapCollider != null){
                tilemapCollider.removeTileCollider(position);
            }

            Vector2 tileChunkArrayPosition = getTileToTileChunkArrayPosition(position);

            if(tileChunk.removeTile(tileChunkArrayPosition)){
                long instanceID = getTileInstanceID(position);

                renderModel.removeIDInstanceData(instanceID);
                renderModel.removeSpriteInstanceData(instanceID);
                renderModel.removeOffsetInstanceData(instanceID);
                renderModel.removeScaleInstanceData(instanceID);
                updateNearbyTiles(position);

                if(tileChunk.getTileCount() <= 0) tileChunkMap.remove(tileChunkMapKey);
            }
        }
    }

    public Tile getTile(Vector2 position){
        Vector2 tileChunkPosition = getTileToTileChunkPosition(position);
        Dimension tileChunkMapKey = new Dimension(tileChunkPosition.x, tileChunkPosition.y);
        TileChunk tileChunk = tileChunkMap.get(tileChunkMapKey);

        if(tileChunk != null){
            return tileChunk.getTile(getTileToTileChunkArrayPosition(position));
        }

        return null;
    }

    private void updateNearbyTiles(Vector2 position){
        for(int x = position.x - 1; x <= position.x + 1; x++){
            for(int y = position.y - 1; y <= position.y + 1; y++) {
                Vector2 nearbyTilePosition = new Vector2(x, y);
                Tile tile = getTile(nearbyTilePosition);

                if (tile != null) {
                    tile.updateTile(nearbyTilePosition, this);
                }
            }
        }
    }

    private long getTileInstanceID(Vector2 position){
        return (((long) position.x) << 32) | (position.y & 0xffffffffL);
    }

    private void renderTileChunks(Graphics g){
        for(TileChunk tileChunk : tileChunkMap.values()){
            for(int x = 0; x < chunkSize.x; x++) {
                for (int y = 0; y < chunkSize.y; y++) {
                    if (tileChunk.getTile(new Vector2(x, y)) != null) {
                        if(needUpdate) {
                            Vector2 tilePosition = new Vector2(tileChunk.getPosition().x * chunkSize.x + x, tileChunk.getPosition().y * chunkSize.y + y);

                            BufferedImage currentSprite = getTileSprite(tilePosition);
                            BufferedImage newSprite = animationSpriteBufferMap.get(currentSprite);

                            if (newSprite != null) setTileSprite(tilePosition, newSprite);
                        }

                        RendererHandler.request(renderModel, getParent());
                    }
                }
            }
        }

        if(needUpdate){
            animationSpriteBufferMap.clear();

            needUpdate = false;
        }
    }

    private void updateTileAnimation(float delta){
        for(Tile tile : tileSet) {
            SpriteRenderer spriteRenderer = tile.getSpriteRenderer();

            if (spriteRenderer != null) {
                RenderModel renderModel = spriteRenderer.renderModel;
                BufferedImage previousSprite = renderModel.getSprite();

                tile.getAnimator().update(delta);

                BufferedImage newSprite = renderModel.getSprite();

                if(previousSprite != newSprite){
                    animationSpriteBufferMap.put(previousSprite, newSprite);

                    needUpdate = true;
                }
            }
        }
    }

    public void setTileSprite(Vector2 position, BufferedImage sprite){
        renderModel.addSpriteInstanceData(getTileInstanceID(position), sprite);
    }

    public BufferedImage getTileSprite(Vector2 position){
        return renderModel.getSpriteInstanceData(getTileInstanceID(position));
    }

    public void setTileScale(Vector2 position, Vector2f scale){
        renderModel.addScaleInstanceData(getTileInstanceID(position), scale);
    }

    private Vector2 getTileToTileChunkPosition(Vector2 position) {
        Vector2 chunkPosition = new Vector2(0, 0);

        if(position.x < 0) {
            chunkPosition.x = (position.x + 1) / chunkSize.x;
            chunkPosition.x--;
        }else {
            chunkPosition.x = position.x / chunkSize.x;
        }

        if(position.y < 0) {
            chunkPosition.y = (position.y + 1) / chunkSize.y;
            chunkPosition.y--;
        }else {
            chunkPosition.y = position.y / chunkSize.y;
        }

        return chunkPosition;
    }

    private Vector2 getTileToTileChunkArrayPosition(Vector2 position){
        Vector2 arrayPosition = new Vector2(Math.abs(position.x % chunkSize.x), Math.abs(position.y % chunkSize.y));

        if(position.x < 0) {
            if(arrayPosition.x == 0) arrayPosition.x = chunkSize.x;
            arrayPosition.x = chunkSize.x - arrayPosition.x;
        }

        if(position.y < 0) {
            if(arrayPosition.y == 0) arrayPosition.y = chunkSize.y;
            arrayPosition.y = chunkSize.y - arrayPosition.y;
        }

        return arrayPosition;
    }

    public Map<Dimension, TileChunk> getTileChunkMap() {
        return tileChunkMap;
    }
}
