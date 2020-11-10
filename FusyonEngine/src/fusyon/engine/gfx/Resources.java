package fusyon.engine.gfx;

import fusyon.polaria.util.GameSettings;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/** Manages all the resources within the engine, like images, sounds and fonts.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class Resources {

	/** Map containing all the sprites, with the key being their names. */;
	private static Map<String, BufferedImage> spriteList = new HashMap<String, BufferedImage>();
	/** Map containing all the animations, with the key being their names. */;
	private static Map<String, AnimatedImage> animationList = new HashMap<String, AnimatedImage>();

	/**	Loads all sprite sheet's sprites into a bi-dimensional array.
	 *
	 * @param path			Path to a sprite sheet.
	 * @param spritesWidth	The width of the sprites.
	 * @param spritesHeight	The height of the sprites.
	 * @return				A bi-dimensional array containing all sprites in their original positions.
	 */
	public static BufferedImage[][] loadSpriteSheet(String path, int spritesWidth, int spritesHeight) {
		BufferedImage spriteImage = loadSprite(path);

		BufferedImage[][] spriteArray = new BufferedImage[spriteImage.getWidth() / spritesWidth][spriteImage.getHeight() / spritesHeight];

		for(int y = 0; y < spriteImage.getHeight() / spritesHeight; y++) {
			for(int x = 0; x < spriteImage.getWidth() / spritesWidth; x++) {
				spriteArray[x][y] = cutSprite(spriteImage, x * spritesWidth, y * spritesHeight, spritesWidth, spritesHeight);
			}
		}
		
		return spriteArray;
	}

	/** Rescales a sprite.
	 *
	 * @param sprite	The sprite to be rescaled.
	 * @param width	The new width.
	 * @param height	The new height.
	 * @return		A sprite rescaled (null if sprite is invalid).
	 */
	public static BufferedImage resizeSprite(BufferedImage sprite, int width, int height) {
		if(sprite == null) return null;

	    Image tmp = sprite.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = dimg.createGraphics();
	    
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}

	/** Loads a sprite from the disk.
	 *
	 * @param path	The path on which the sprite is located.
	 * @return		The sprite (null if not found).
	 */
	public static BufferedImage loadSprite(String path) {
		BufferedImage sprite = null;
		
		try {
			sprite = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sprite;
	}

	/**	Cut out a part of a sprite.
	 *
	 * @param sprite	The sprite to be cut.
	 * @param x			The x position on the original sprite.
	 * @param y			The y position on the original sprite.
	 * @param width		The width to be cut out.
	 * @param height	The height to be cut out.
	 * @return			A new cutted sprite.
	 */
	public static BufferedImage cutSprite(BufferedImage sprite, int x, int y, int width, int height) {
		return sprite.getSubimage(x, y, width, height);
	}

	/**	Clones a sprite.
	 *
	 * @param sprite	The sprite to be cloned.
	 * @return			A new cloned sprite.
	 */
	public static BufferedImage cloneSprite(BufferedImage sprite) {
		BufferedImage newImage = new BufferedImage(sprite.getWidth(), sprite.getHeight(), sprite.getType());
		
	    Graphics g = newImage.getGraphics();
	    
	    g.drawImage(sprite, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
	    g.dispose();
	    
		return newImage;
	}

	public static BufferedImage getSprite(String name) {
		return spriteList.get(name);
	}

	public static void addSprite(String name, BufferedImage sprite) {
		spriteList.put(name, sprite);
	}

	public static AnimatedImage getAnimation(String name) {
		return animationList.get(name);
	}

	public static void addAnimation(String name, AnimatedImage animation) {
		animationList.put(name, animation);
	}
}
