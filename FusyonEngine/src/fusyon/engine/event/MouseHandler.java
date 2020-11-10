package fusyon.engine.event;

import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.TreeSet;

import fusyon.engine.gameobject.Camera;
import fusyon.engine.gfx.RendererHandler;
import fusyon.engine.main.Display;
import fusyon.engine.main.Engine;
import fusyon.engine.main.Physics;
import fusyon.engine.util.Vector2f;

public class MouseHandler extends MouseAdapter implements MouseWheelListener{

	private static boolean isMouseOverCanvas;

	private static int mouseWheelValue;

	private static Vector2f mousePosition;
	private static Vector2f mouseSize = new Vector2f(1, 1);
	
	private static TreeSet<Integer> pressedButtonList = new TreeSet<Integer>();
	private static TreeSet<Integer> releasedButtonList = new TreeSet<Integer>();

	public MouseHandler(){
		mousePosition = Vector2f.zero;
	}
	
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		
		pressedButtonList.add(button);
		
		if(releasedButtonList.contains(button)) {
			releasedButtonList.remove(button);
		}
	}
	
	public void mouseReleased(MouseEvent e)	{
		int button = e.getButton();
		
		pressedButtonList.remove(button);
		releasedButtonList.add(button);
	}
	
	public static boolean getButtonDown(int button) {
		if(pressedButtonList.contains(button)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean getButtonUp(int button) {
		if(releasedButtonList.contains(button)) {
			return true;
		}
		
		return false;
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelValue = e.getWheelRotation();
	}
	
	public static boolean getMouseWheelUp(){
		return mouseWheelValue > 0;
	}

	public static boolean getMouseWheelDown(){
		return mouseWheelValue < 0;
	}

	public void update(){
		Display display = Engine.engine.getDisplay();

		if(display != null){
			if(display.getCanvas().isShowing()){
				float mouseX = (float) (MouseInfo.getPointerInfo().getLocation().getX() - display.getCanvas().getLocationOnScreen().getX());
				float mouseY = (float) (MouseInfo.getPointerInfo().getLocation().getY() - display.getCanvas().getLocationOnScreen().getY());
				
				mousePosition = new Vector2f(mouseX, mouseY);
				isMouseOverCanvas = false;

				Physics.mouseCanvasOverlapAll();
				Physics.mouseOverlapAll();
			}
		}

		mouseWheelValue = 0;
		releasedButtonList.clear();
	}

	public static Vector2f getMousePosition() {
		return mousePosition;
	}

	public static Vector2f getMouseWorldPosition(){
		Camera camera = Engine.engine.getScene().getCamera();

		if(camera == null){
			return getMousePosition();
		}

		Display display = Engine.engine.getDisplay();

		float cameraDistance = camera.distance;

		Vector2f cameraPosition = camera.getPosition().multiply(cameraDistance);
		Vector2f stretch = RendererHandler.getStretch();
		Vector2f stretchFactor = RendererHandler.getStretchFactor();

		cameraPosition.x *= stretchFactor.x;
		cameraPosition.y *= stretchFactor.y;

		float mousePositionX =  mousePosition.x / stretch.x;
		float mousePositionY =  mousePosition.y / stretch.y;
		float cameraPositionX = (cameraPosition.x + camera.offset.x - display.getWidth() / 2f) / stretch.x;
		float cameraPositionY = (cameraPosition.y + camera.offset.y - display.getHeight() / 2f) / stretch.y;

		return new Vector2f(mousePositionX + cameraPositionX, mousePositionY + cameraPositionY);
	}

	public static TreeSet<Integer> getPressedButtonList() {
		return pressedButtonList;
	}

	public static TreeSet<Integer> getReleasedButtonList() {
		return releasedButtonList;
	}

	public static boolean isMouseOverCanvas() {
		return isMouseOverCanvas;
	}

	public static void setMouseOverCanvas(boolean isMouseOverCanvas) {
		MouseHandler.isMouseOverCanvas = isMouseOverCanvas;
	}

	public static Vector2f getMouseSize() {
		return mouseSize;
	}
}
