package fusyon.engine.main;

import fusyon.engine.event.KeyboardHandler;
import fusyon.engine.event.MouseHandler;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;

/** This class is used to create displays that can be draw in and receive user inputs.
 *
 * @author Gabriel de Mello (Fusyon)
 */
public class Display {

	public static int baseWidth = 1600;
	public static int baseHeight = 900;
	public static int minimumWidth = 640;
	public static int minimumHeight = 360;

	/** The x component of the original Display's size. */
	private int width;
	/** The y compoonent of the original Display's size. */
	private int height;

	/** The JFrame containing the application. */
	private JFrame frame;
	/** Title of the application. */
	private String title;
	/** The class responsible for managing keyboard inputs. */
	private KeyboardHandler keyboardHandler;
	/** The class responsible for managing mouse inputs. */
	private MouseHandler mouseHandler;
	/** The area on the screen where everything is rendered. */
	private Canvas canvas;

	/** Initializes a new Display.
	 *
	 * @param width		The x component of the Display's size.
	 * @param height	The y component of the Display's size.
	 * @param title		Title of the application.
	 */
	public Display(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		frame = new JFrame();
		keyboardHandler = new KeyboardHandler();
		mouseHandler = new MouseHandler();
		canvas = new Canvas();
		
		frame.setTitle(title);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(false);
	    //frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		canvas.setFocusable(true);
		canvas.addKeyListener(keyboardHandler);
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseWheelListener(mouseHandler);
		
		frame.add(canvas);
		frame.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		frame.setVisible(true);
	}

	/** Closes the Display. */
	public void close() {
		if(frame == null) return;
			
		frame.setVisible(false);
		frame.dispose();
	}

	public int getWidth() {
		return frame.getBounds().width;
	}

	public int getHeight() {
		return frame.getBounds().height;
	}

	public int getCanvasWidth() {
		return frame.getContentPane().getWidth();
	}

	public int getCanvasHeight() {
		return frame.getContentPane().getHeight();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public KeyboardHandler getKeyboardHandler() {
		return keyboardHandler;
	}

	public void setKeyboardHandler(KeyboardHandler keyboardHandler) {
		this.keyboardHandler = keyboardHandler;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

	public void setMouseHandler(MouseHandler mouseHandler) {
		this.mouseHandler = mouseHandler;
	}

	public float getScreenWidthRatio(){
		return (float) getWidth() / (float) baseWidth;
	}

	public float getScreenHeightRatio(){
		return (float) getHeight() / (float) baseHeight;
	}
}
