package fusyon.engine.gfx;

import fusyon.engine.gameobject.Camera;
import fusyon.engine.gameobject.GameObject;
import fusyon.engine.gameobject.Canvas;
import fusyon.engine.gameobject.component.LightScript;
import fusyon.engine.main.Display;
import fusyon.engine.util.GameMath;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import static fusyon.engine.main.Engine.*;

/** Handles all render requests and light calculations.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class RendererHandler {

    /** A flag that indicates if the RendererHandler should compute dynamic lights or not.
     *
     * Turn it off for better performance.
     */
    private static boolean useDynamicLights = true;

    /** Used to improve performance on light rendering.
     *
     * It scales the light map resolution down to an 1/n proportion, where n is the desired light map scale. The higher
     * means more fast calculations and less precise lights. Please, only use >0 numbers that are power of two
     * (e.g. 1, 2, 4, 8, 16, 32...).
     */
    private static int lightMapScale = 8;
    private static int lightMapWidth;
    private static int lightMapHeight;
    /** Shadow's color/the color that will be displayed when ambient light intensity is equals 1. */
    private static int ambientLightColor;

    /** Stores the ambient light intensity as a float. Must be a float value between 0 (none) and 1 (full intensity). */
    private static float ambientLightIntensity = 0f;
    /**  */
    private static float cameraDistance;

    /** Stores the light map bits as an array of integers. Used by all lights for the calculation process. */
    private static int lightMapArray[];

    /** Image that contains all the outputs from light calculation. */
    private static BufferedImage lightMap;
    /** Holds a reference for the CameraScript to get some attributes, like distance and background color. */
    private static Camera camera;
    private static Vector2f cameraPosition;
    private static Vector2 cameraOffset;
    private static Vector2f stretchFactor;
    /** Manages all drawing stuff. */
    private static Graphics2D graphics2D;
    /** Deals with all graphic's memory. */
    private static BufferStrategy bufferStrategy;
    /** Used for subpixel rendering with float point precision. */
    private static AffineTransform affineTransform;
    /** Reference for the current running Display. **/
    private static Display display;

    /** Number of buffers that will be used by the BufferStrategy. Caution is advised when modifying this number.*/
    private static final int numBuffers = 2;

    /** The color that will be painted when cleaning the screen. */
    private static final Color defaultBackgroundColor = Color.WHITE;

    /** Keep tracks of all the relations between RenderModels and GameObjects for memory save. */
    private static Map<RenderModel, List<GameObject>> batchMap = new HashMap<RenderModel, List<GameObject>>();

    /** Set things up when starting the engine.
     *
     * It creates a new BufferStrategy with the desired number of buffers. If using dynamic lights, it will also create
     * the light map and store all the pixel data in the array.
     */
    public static void setup(){
        affineTransform = new AffineTransform();
        display = engine.getDisplay();

        System.setProperty("sun.java2d.opengl", "true");
        display.getCanvas().createBufferStrategy(numBuffers);

        if(useDynamicLights) {
            lightMapWidth = (Display.baseWidth / lightMapScale) + 1;
            lightMapHeight = (Display.baseHeight / lightMapScale) + 1;
            lightMap = new BufferedImage(lightMapWidth, lightMapHeight, BufferedImage.TYPE_INT_ARGB);
            lightMapArray = ((DataBufferInt) lightMap.getRaster().getDataBuffer()).getData();
        }
    }

    /**
     * Makes a request for the RendererHandler to draw.
     *
     * When calling this method, it will initially check for a valid RenderModel and GameObject. With both being valid,
     * the method will check for other GameObjects being rendered with the same RenderModel. In the case of other
     * GameObjects existing, the new GameObject will be added to the list, thus using the same RenderModel for rendering.
     * This can save memory for many objects trying to render similar RenderModels over and over.
     *
     * @param renderModel   A valid RenderModel to be rendered.
     * @param object        A valid object that is trying to be rendered.
     */
    public static void request(RenderModel renderModel, GameObject object){
        if(renderModel == null || object == null) return;

        List<GameObject> objectList = batchMap.get(renderModel);

        if(objectList != null){
            objectList.add(object);
        }else{
            objectList = new ArrayList<>();

            objectList.add(object);
        }

        batchMap.put(renderModel, objectList);
    }

    /** Apply some rendering settings to make things looks better. */
    private static void applyRenderingSettings(){
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    }

    /**
     * Prepare for render.
     *
     * Initial setup for BufferStrategy and Graphics, including the apply of graphical settings and screen clearing.
     * It will also clear the light map if using dynamic lights. In the end, render calls will be made to get
     * render requests across all objects in the Scene.
     */
    public static void prepare() {
        bufferStrategy = display.getCanvas().getBufferStrategy();
        graphics2D = (Graphics2D) bufferStrategy.getDrawGraphics();
        cameraDistance = 1f;
        cameraPosition = Vector2f.zero;
        cameraOffset = Vector2.zero;

        applyRenderingSettings();

        if (engine.getScene().getCamera() != null){
            camera = engine.getScene().getCamera();
            cameraDistance = camera.distance;
            cameraPosition = camera.getPosition().multiply(cameraDistance);
            stretchFactor = getStretchFactor();
            cameraPosition.x *= stretchFactor.x;
            cameraPosition.y *= stretchFactor.y;
            cameraOffset = camera.offset.subtract(new Vector2(display.getWidth() / 2, display.getHeight() / 2));
        }

        graphics2D.setColor(camera == null ? defaultBackgroundColor : camera.backgroundColor);
        graphics2D.fillRect(0, 0, display.getWidth(), display.getHeight());

        if (useDynamicLights){
            for(int x = 0; x < lightMapWidth; x++){
                for(int y = 0; y < lightMapHeight; y++){
                    setLightMap(x, y, ((int) (255 * ambientLightIntensity) << 24) | ambientLightColor);
                }
            }
        }

        if(engine.getScene() != null) engine.getScene().render(graphics2D);
    }

    /** Where everything is actually rendered.
     *
     * When called, all the RenderModels from the batch map are ordered by their layer order. The camera distance is
     * used to calculate the camera position. Then, all RenderModels are iterated over along with their corresponding
     * GameObjects. The appropriate instance data is get based on the object's instance id and the model is rendered
     * with subpixel rendering provided by AffineTransform. Only the sprites that are inside the portview are rendered.
     * The light map is renderer afterwards.
     */
    public static void render(){
        List<RenderModel> renderModelList = new ArrayList<>(batchMap.keySet());
        List<RenderModel> overlayRenderModelList = new ArrayList<>();

        sortRenderModelListByLayerOrder(renderModelList);

        for(RenderModel renderModel : renderModelList){
            if(renderModel.isOverlay()){
                overlayRenderModelList.add(renderModel);
                continue;
            }

            drawRenderModel(renderModel);
        }

        drawLightMap();

        for(RenderModel renderModel : overlayRenderModelList){
            drawRenderModel(renderModel);
        }
    }

    private static void drawRenderModel(RenderModel renderModel){
        List<GameObject> objectList = batchMap.get(renderModel);

        renderModel.setInstanceID(0);

        for(GameObject object : objectList) {
            BufferedImage sprite = renderModel.getSprite();
            Vector2f offset = renderModel.getOffset();
            Vector2f scale = renderModel.getScale();

            renderModel.increaseInstanceID();

            if(sprite == null) continue;

            int spriteWidth = sprite.getWidth();
            int spriteHeight = sprite.getHeight();

            double width = (double) spriteWidth * scale.x;;
            double height = (double) spriteHeight * scale.y;;
            double x = object.getPosition().x;
            double y = object.getPosition().y;

            Vector2f stretchFactor = getStretchFactor();
            Vector2f stretch = getStretch();
            Vector2f cameraPosition;

            if(renderModel.isUseLocalPosition()){
                cameraPosition = Vector2f.zero;

                if(renderModel.isKeepProportion()) {
                    width *= stretchFactor.x;
                    height *= stretchFactor.y;
                    x += offset.x * stretchFactor.x;
                    y += offset.y * stretchFactor.y;
                }else{
                    x += offset.x;
                    y += offset.y;
                }
            }else{
                cameraPosition = RendererHandler.cameraPosition;
                width *= stretch.x;
                height *= stretch.y;
                x = (x + offset.x) * stretch.x - cameraOffset.x;
                y = (y + offset.y) * stretch.y - cameraOffset.y;
            }

            if (x + width >= cameraPosition.x && y + height >= cameraPosition.y && x < cameraPosition.x + display.getWidth() && y < cameraPosition.y + display.getHeight()) {
                affineTransform.setToIdentity();
                affineTransform.translate(x - cameraPosition.x, y - cameraPosition.y);
                affineTransform.scale(width / spriteWidth, height / spriteHeight);
                graphics2D.drawImage(sprite, affineTransform, null);
            }
        }
    }

    private static void drawLightMap(){
        if(useDynamicLights){
            float scaleX = lightMapScale * stretchFactor.x;
            float scaleY = lightMapScale * stretchFactor.y;

            affineTransform.setToIdentity();
            affineTransform.translate(
                    (display.getWidth() / 2f) - (lightMapWidth * scaleX) / 2f,
                    (display.getHeight() / 2f) - (lightMapHeight * scaleY) / 2f
            );
            affineTransform.scale(scaleX, scaleY);
            graphics2D.drawImage(lightMap, affineTransform, null);
        }
    }

    /** Displays all the render on the screen, disposing from all data held. */
    public static void display(){
        graphics2D.dispose();
        bufferStrategy.show();
        batchMap.clear();
    }

    /** Renders a light in a determined position.
     *
     * This method, like the render method, utilizes the camera distance to calculate the camera position and, thus,
     * predicting whether the light will be shown on portview or not, before making all the calculations.
     *
     * @param lightScript   The script on which the light attributes are stored.
     * @param position      The world position to be rendered at.
     */
    public static void renderLight(LightScript lightScript, Vector2f position){
        if(useDynamicLights) {
            int outerRadius = lightScript.outerRadius;
            int positionX = (int) ((position.x * cameraDistance - cameraPosition.x / stretchFactor.x - -display.baseWidth / 2 - outerRadius) / lightMapScale);
            int positionY = (int) ((position.y * cameraDistance - cameraPosition.y / stretchFactor.y - -display.baseHeight / 2 - outerRadius) / lightMapScale);

            if (positionX >= -outerRadius && positionX < lightMapWidth && positionY >= -outerRadius && positionY < lightMapHeight) {
                int diameter = lightScript.getDiameter() / lightMapScale;
                int color = lightScript.color.getRGB();

                for (int i = 0; i < diameter; i++) {
                    for (int j = 0; j < diameter; j++) {
                        processLight(lightScript, color, positionX, positionY, i, j);
                    }
                }
            }
        }
    }

    /** Calculates the light on the light map.
     *
     * This method gets all color channels from the Light and the light map. From both r, g, and b channels, it gets
     * the maximum (brightest) values and draws the corresponding pixel into the lightmap. Both alpha channels are just
     * multiplied.
     *
     * @param lightScript   The script on which the light attributes are stored.
     * @param x             The light's pixel x position component.
     * @param y             The light's pixel y position component.
     */
    private static void processLight(LightScript lightScript, int color, int positionX, int positionY, int x, int y){
        int diameter = lightScript.getDiameter() / lightMapScale;
        int lightMapValue = getLightMap(x + positionX, y + positionY);

        int ambientPixelIntensity = ((lightMapValue & 0xff000000) >> 24) & 0x000000ff;

        int ambientPixelColor = lightMapValue & 0x00ffffff;
        int ar = (ambientPixelColor & 0x00ff0000) >> 16;
        int ag = (ambientPixelColor & 0x0000ff00) >> 8;
        int ab = ambientPixelColor & 0x000000ff;

        lightMapValue = lightScript.getLightMapArray()[x + y * diameter];

        int lightPixelIntensity = ((lightMapValue & 0xff000000) >> 24) & 0x000000ff;
        int lightPixelColor = lightScript.useLightmap ? lightMapValue & 0x00ffffff : color & 0x00ffffff;
        int lr = (lightPixelColor & 0x00ff0000) >> 16;
        int lg = (lightPixelColor & 0x0000ff00) >> 8;
        int lb = lightPixelColor & 0x000000ff;

        float biasFunctionValue = GameMath.biasFunction((lightPixelIntensity / 255f), GameMath.clamp(lightScript.blendSmoothness, 0, 1));

        int a = (ambientPixelIntensity * lightPixelIntensity) / 255;
        int r = lr + (int) ((ar - lr) * biasFunctionValue);
        int g = lg + (int) ((ag - lg) * biasFunctionValue);
        int b = lb + (int) ((ab - lb) * biasFunctionValue);

        setLightMap(x + positionX, y + positionY, (a << 24) | (r << 16) | (g << 8) | b);
    }

    /** Sort a RenderModel list by layer order.
     *
     * @param renderModelList   A list containing RenderModels.
     */
    private static void sortRenderModelListByLayerOrder(List<RenderModel> renderModelList) {
        Collections.sort(renderModelList, (a, b) -> {
            if(a.getSortingLayer() == b.getSortingLayer()){
                return a.getLayerOrder() - b.getLayerOrder();
            }else{
                return a.getSortingLayer().ordinal() - b.getSortingLayer().ordinal();
            }
        });
    }

    /** Gets a specific pixel's color as an int.
     *
     * @param x Local position's x component.
     * @param y Local position's y component.
     * @return  An integer corresponding to the argb channels from the pixel (0 if x and y re out of bounds).
     */
    public static int getLightMap(int x, int y){
        if(x >= lightMapWidth || x < 0 || y >= lightMapHeight || y < 0) return 0;

        return lightMapArray[x + y * lightMapWidth];
    }

    /** Sets the color of a pixel determined by x and y.
     *
     * @param x Local position's x component.
     * @param y Local position's y component.
     * @param r A value to the red channel.
     * @param g A value to the green channel.
     * @param b A value to the blue channel.
     * @param a A value to the alpha channel
     */
    public static void setLightMap(int x, int y, int r, int g, int b, int a){
        if(x >= lightMapWidth || x < 0 || y >= lightMapHeight || y < 0) return;

        lightMapArray[x + y * lightMapWidth] = (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Sets the color of a pixel determined by x and y.
     *
     * @param x Local position's x component.
     * @param y Local position's y component.
     * @param color A value to the color (in the format 'argb').
     */
    public static void setLightMap(int x, int y, int color){
        if(x >= lightMapWidth || x < 0 || y >= lightMapHeight || y < 0) return;

        lightMapArray[x + y * lightMapWidth] = color;
    }

    public static int getAmbientLightColor() {
        return ambientLightColor;
    }

    public static void setAmbientLightColor(Color color) {
        ambientLightColor = color.getRGB() & 0x00ffffff;
    }

    public static void setAmbientLightColor(int r, int g, int b) {
        ambientLightColor = (r << 16) | (g << 8) | b;
    }

    public static float getAmbientLightIntensity() {
        return GameMath.clamp(ambientLightIntensity, 0, 1);
    }

    /** Sets the intensity (a.k.a. alpha channel) for the light map.
     *
     * @param value A float value between 0 and 1, where 0 is none and 1 is full intensity.
     */
    public static void setAmbientLightIntensity(float value) {
        value = GameMath.clamp(value, 0, 1);

        ambientLightIntensity = value;
    }

    public static int getLightMapScale() {
        return lightMapScale;
    }

    public static Vector2f getStretchFactor(){
        float horizontalStretchFactor = display.getScreenWidthRatio();
        float verticalStretchFactor = display.getScreenHeightRatio();

        if(horizontalStretchFactor < verticalStretchFactor){
            horizontalStretchFactor = verticalStretchFactor;
        }else if(verticalStretchFactor < horizontalStretchFactor){
            verticalStretchFactor = horizontalStretchFactor;
        }

        return new Vector2f(horizontalStretchFactor, verticalStretchFactor);
    }

    public static Vector2f getStretch(){
        return getStretchFactor().multiply(cameraDistance);
    }
}
