package fusyon.engine.gameobject.component;

import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.util.GameMath;
import fusyon.engine.util.Vector2;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.util.Random;

public class LightScript extends Component{

    public boolean useLightmap = false;

    public int innerRadius;
    public int outerRadius;

    public float intensity;
    public float blendSmoothness = 0.25f;

    public Color color;

    private int diameter;

    private int lightMapArray[];

    public LightScript(int innerRadius, int outerRadius, Color color, float intensity) {
        super("LightScript");

        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.color = color;
        this.intensity = intensity;
    }

    @Override
    public void start() {
        updateLight();
    }

    Random random = new Random();

    @Override
    public void update(float delta) {}

    @Override
    public void render(Graphics g) {
        RendererHandler.renderLight(this, getParent().getPosition());
    }

    @Override
    public void destroy() {

    }

    public void updateLight(){
        diameter = outerRadius * 2;
        intensity = GameMath.clamp(intensity, 0, 1);

        int lightmapScale = RendererHandler.getLightMapScale();
        int scaledInnerRadius = this.innerRadius / lightmapScale;
        int scaledOuterRadius = this.outerRadius / lightmapScale;
        int scaledDiameter = this.diameter / lightmapScale;
        int color = this.color.getRGB() & 0x00ffffff;

        lightMapArray = new int[scaledDiameter * scaledDiameter];

        for(int x = 0; x < scaledDiameter; x++){
            for(int y = 0; y < scaledDiameter; y++){
                double distance = Math.sqrt((x - scaledOuterRadius) * (x - scaledOuterRadius) + (y - scaledOuterRadius) * (y - scaledOuterRadius));

                int a = 255;
                int r = (((color & 0x00ff0000) >> 16) & 0xff);
                int g = (((color & 0x0000ff00) >> 8) & 0xff);
                int b = (color & 0x000000ff) & 0xff;

                if(distance < scaledInnerRadius){
                    a *= intensity;

                    lightMapArray[x + y * scaledDiameter] = ((255 - a) << 24) | (r << 16) | (g << 8) | b;
                }else if(distance < scaledOuterRadius){
                    double power = 1 - ((distance - scaledInnerRadius) / (scaledOuterRadius - scaledInnerRadius));

                    a *= intensity * power;

                    lightMapArray[x + y * scaledDiameter] = ((255 - a) << 24) | (r << 16) | (g << 8) | b;
                }else{
                    lightMapArray[x + y * scaledDiameter] = 0xffffffff;
                }
            }
        }
    }

    public int getDiameter() {
        return diameter;
    }

    public int[] getLightMapArray() {
        return lightMapArray;
    }
}
