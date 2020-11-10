package fusyon.engine.gameobject.component;

import fusyon.engine.gfx.RenderModel;
import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.main.Settings;
import fusyon.engine.util.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextRenderer extends Component{

    private boolean needUpdate = true;

    private int width;
    private int height;
    private int lastWidth;
    private int lastHeight;

    private String text;
    private Color color;
    private Font font;;
    private Alignment alignment = Alignment.LEFT;
    private SpriteRenderer spriteRenderer;
    private BufferedImage defaultSprite = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private Vector2f offset = new Vector2f(0, 0);

    public TextRenderer(String text, Color color, Font font) {
        super("TextRenderer");

        this.text = text;
        this.font = font;
        this.color = color;
    }

    @Override
    public void start() {
        spriteRenderer = (SpriteRenderer) getParent().getComponent("SpriteRenderer");
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(Graphics g) {
        if(needUpdate){
            updateText();
            updateOffset();

            needUpdate = false;
        }
    }

    @Override
    public void destroy() {

    }

    private void updateText(){
        if(text.equals("")) return;

        Graphics2D graphics2D = defaultSprite.createGraphics();

        graphics2D.setFont(font);

        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        width = fontMetrics.stringWidth(text);
        height = fontMetrics.getHeight();

        BufferedImage sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = sprite.createGraphics();

        graphics2D.setFont(font);
        graphics2D.setColor(color);
        graphics2D.drawString(text, 0, fontMetrics.getAscent());
        graphics2D.dispose();

        spriteRenderer.renderModel.setSprite(sprite);
    }

    private void updateOffset(){
        int width = this.width - lastWidth;
        int height = this.height - lastHeight;

        float newOffsetX = 0;
        float newOffsetY = 0;

        if (alignment == Alignment.CENTER) {
            newOffsetX = -width / 2f;
        }else if(alignment == Alignment.RIGHT){
            newOffsetX = -width;
        }

        offset.x += newOffsetX - offset.x;
        offset.y += newOffsetY - offset.y;
        lastWidth = this.width;
        lastHeight = this.height;

        spriteRenderer.renderModel.setOffset(spriteRenderer.renderModel.getOffset().add(offset));
    }

    public enum Alignment{
        LEFT,
        CENTER,
        RIGHT
    }

    public String getText() {
        return text;
    }

    public void setText(String text){
        this.text = text;

        needUpdate = true;
    }

    public void setColor(Color color) {
        this.color = color;

        needUpdate = true;
    }

    public void setFont(Font font) {
        this.font = font;

        needUpdate = true;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
}
