package fusyon.engine.gfx;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/** Stores data about an animation.
 *
 * @author Gabriel de Mello (Fusyon).
 */
public class AnimatedImage {

	/** List containing all the frames of the animation. */
	private ArrayList<BufferedImage> spriteList;

	/**	Initializes a new AnimatedImage.
	 *
	 * @param sprites	A sequence of sprites (frames).
	 */
	public AnimatedImage(BufferedImage... sprites) {
		spriteList = new ArrayList<BufferedImage>();
		
		for(BufferedImage s : sprites) {
			addSprite(s);
		}
	}

	/**	Adds a new sprite (frame) into the sprite list.
	 *
	 * @param sprite	A sprite (frame) to be added.
	 */
	public void addSprite(BufferedImage sprite) {
		spriteList.add(sprite);
	}

	public ArrayList<BufferedImage> getSpriteList() {
		return spriteList;
	}
}
