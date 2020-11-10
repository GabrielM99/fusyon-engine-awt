package fusyon.engine.gfx;

import fusyon.engine.main.Settings;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.image.BufferedImage;
import java.util.*;

/** Represents a model to be rendered into the screen.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class RenderModel {

    private boolean useInstanceData;
    private boolean useIDInstanceData;
    private boolean useLocalPosition;
    private boolean keepProportion = true;
    private boolean isOverlay;

    /** The order on which the RenderModel will be rendered. */
    private int layerOrder;
    /** The current instance ID, used to keep track of the GameObject being rendered with this RenderModel */
    private int instanceID;

    /** Default offset relative to the GameObject's position. */
    private Vector2f offset = Vector2f.zero;
    /** Default scale of the sprite. */
    private Vector2f scale = Vector2f.one;
    private Vector2f origin = Vector2f.zero;
    private Vector2f lastSize = Vector2f.zero;
    /** Default sprite that will be rendered. */
    private BufferedImage sprite;
    /** The sorting layer used to organize render layer order into groups. */
    private Settings.SortingLayers sortingLayer;

    private List<Long> idInstanceDataList;
    /** An instance data map for sprites, where the key is the instance ID. */
    private Map<Long, BufferedImage> spriteInstanceDataMap;
    /** An instance data map for offsets, where the key is the instance ID. */
    private Map<Long, Vector2f> offsetInstanceDataMap;
    /** An instance data map for scales, where the key is the instance ID. */
    private Map<Long, Vector2f> scaleInstanceDataMap;

    /** Initializes a new RenderModel.
     *
     * @param layerOrder    A layer order to be rendered at.
     * @param offset        A default offset relative to the GameObject's position..
     * @param scale         A default scale for the sprite.
     * @param sprite        A default sprite to be rendered.
     */
    public RenderModel(int layerOrder, Vector2f offset, Vector2f scale, BufferedImage sprite) {
        this.layerOrder = layerOrder;
        this.offset = offset;
        this.scale = scale;
        this.sprite = sprite;

        sortingLayer = Settings.SortingLayers.DEFAULT;
        idInstanceDataList = new ArrayList<>();
        spriteInstanceDataMap = new TreeMap<>();
        offsetInstanceDataMap = new TreeMap<>();
        scaleInstanceDataMap = new TreeMap<>();
    }

    /** Initializes a new RenderModel.
     *
     * @param layerOrder    A layer order to be rendered at.
     * @param sortingLayer  A layer group.
     * @param offset        A default offset relative to the GameObject's position..
     * @param scale         A default scale for the sprite.
     * @param sprite        A default sprite to be rendered.
     */
    public RenderModel(int layerOrder, Settings.SortingLayers sortingLayer, Vector2f offset, Vector2f scale, BufferedImage sprite){
        this.layerOrder = layerOrder;
        this.offset = offset;
        this.scale = scale;
        this.sprite = sprite;
        this.sortingLayer = sortingLayer;

        idInstanceDataList = new ArrayList<>();
        spriteInstanceDataMap = new TreeMap<>();
        offsetInstanceDataMap = new TreeMap<>();
        scaleInstanceDataMap = new TreeMap<>();
    }

    public RenderModel(int layerOrder, Settings.SortingLayers sortingLayer, BufferedImage sprite) {
        this.layerOrder = layerOrder;
        this.sprite = sprite;
        this.sortingLayer = sortingLayer;

        idInstanceDataList = new ArrayList<>();
        spriteInstanceDataMap = new TreeMap<>();
        offsetInstanceDataMap = new TreeMap<>();
        scaleInstanceDataMap = new TreeMap<>();
    }

    public int getLayerOrder() {
        return layerOrder;
    }

    public void setLayerOrder(int layerOrder) {
        this.layerOrder = layerOrder;
    }

    public Vector2f getOffset() {
        return useInstanceData ? (useIDInstanceData ? getOffsetInstanceData(getIDInstanceData(instanceID)) : getOffsetInstanceData(instanceID)) : offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    public Vector2f getScale() {
        return useInstanceData ? (useIDInstanceData ? getScaleInstanceData(getIDInstanceData(instanceID)) : getScaleInstanceData(instanceID)) : scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    public BufferedImage getSprite() {
        return useInstanceData ? (useIDInstanceData ? getSpriteInstanceData(getIDInstanceData(instanceID)) : getSpriteInstanceData(instanceID)) : sprite;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    public void setInstanceID(int instanceID) {
        this.instanceID = instanceID;
    }

    public long getIDInstanceData(int index) {
        return idInstanceDataList.get(index);
    }

    public void addIDInstanceData(long id){
        idInstanceDataList.add(id);
    }

    public void removeIDInstanceData(long id){
        idInstanceDataList.remove((Object) id);
    }

    public BufferedImage getSpriteInstanceData(long index) {
        return spriteInstanceDataMap.get(index);
    }

    public void addSpriteInstanceData(long index, BufferedImage sprite){
        spriteInstanceDataMap.put(index, sprite);
    }

    public void removeSpriteInstanceData(long index){
        spriteInstanceDataMap.remove(index);
    }

    public Vector2f getOffsetInstanceData(long index) {
        return offsetInstanceDataMap.get(index);
    }

    public void addOffsetInstanceData(long index, Vector2f scale){
        offsetInstanceDataMap.put(index, scale);
    }

    public void removeOffsetInstanceData(long index){
        offsetInstanceDataMap.remove(index);
    }

    public Vector2f getScaleInstanceData(long index) {
        return scaleInstanceDataMap.get(index);
    }

    public void addScaleInstanceData(long index, Vector2f offset){
        scaleInstanceDataMap.put(index, offset);
    }

    public void removeScaleInstanceData(long index){
        scaleInstanceDataMap.remove(index);
    }

    public void increaseInstanceID() {
        instanceID++;
    }

    public Settings.SortingLayers getSortingLayer() {
        return sortingLayer;
    }

    public void setSortingLayer(Settings.SortingLayers sortingLayer) {
        this.sortingLayer = sortingLayer;
    }

    public void setUseIDInstanceData(boolean useIDInstanceData) {
        this.useIDInstanceData = useIDInstanceData;
    }

    public boolean isUseLocalPosition() {
        return useLocalPosition;
    }

    public void setUseLocalPosition(boolean useLocalPosition) {
        this.useLocalPosition = useLocalPosition;
    }

    public boolean isOverlay() {
        return isOverlay;
    }

    public void setOverlay(boolean overlay) {
        isOverlay = overlay;
    }

    public void setUseInstanceData(boolean useInstanceData) {
        this.useInstanceData = useInstanceData;
    }

    public boolean isKeepProportion() {
        return keepProportion;
    }

    public void setKeepProportion(boolean keepProportion) {
        this.keepProportion = keepProportion;
    }

    public Vector2f getSize() {
        return sprite == null ? Vector2f.zero : new Vector2f(sprite.getWidth(), sprite.getHeight());
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }
}
