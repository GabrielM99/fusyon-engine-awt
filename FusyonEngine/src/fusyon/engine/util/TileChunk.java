package fusyon.engine.util;

import fusyon.engine.gameobject.Tile;

public class TileChunk {

    private boolean isVisible;

    private int tileCount;

    private Vector2 position;
    private Vector2 size;

    private Tile[][] tileArray;

    public TileChunk(Vector2 position, Vector2 size) {
        this.position = position;
        this.size = size;

        isVisible = true;
        tileArray = new Tile[size.x][size.y];
    }

    public boolean addTile(Tile tile, Vector2 position){
        if(tile == null || position.x >= size.x || position.x < 0 || position.y >= size.y || position.y < 0) return false;

        if(tileArray[position.x][position.y] == null){
            tileArray[position.x][position.y] = tile;
            tileCount++;

            return true;
        }

        return false;
    }

    public boolean removeTile(Vector2 position){
        if(position.x >= size.x || position.x < 0 || position.y >= size.y || position.y < 0) return false;

        if(tileArray[position.x][position.y] != null){
            tileArray[position.x][position.y] = null;
            tileCount--;

            return true;
        }

        return false;
    }

    public Tile getTile(Vector2 position){
        if(position.x >= size.x || position.x < 0 || position.y >= size.y || position.y < 0) return null;

        return tileArray[position.x][position.y];
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getSize() {
        return size;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    public Tile[][] getTileArray() {
        return tileArray;
    }

    public void setTileArray(Tile[][] tileArray) {
        this.tileArray = tileArray;
    }

    public int getTileCount() {
        return tileCount;
    }
}
