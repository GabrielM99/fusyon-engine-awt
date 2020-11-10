package fusyon.engine.event;

import fusyon.engine.main.Settings;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TreeSet;

public class KeyboardHandler extends KeyAdapter{

	private static TreeSet<Integer> pressedKeyList = new TreeSet<Integer>();
	private static TreeSet<Integer> releasedKeyList = new TreeSet<Integer>();
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		pressedKeyList.add(key);
		
		if(releasedKeyList.contains(key)) {
			releasedKeyList.remove(key);
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		pressedKeyList.remove(key);
		releasedKeyList.add(key);
	}
	
	public static boolean getKeyDown(int key) {
		if(pressedKeyList.contains(key)) {
			return true;
		}
		
		return false;
	}

	public static boolean getKeyDown(String name){
		return getKeyDown(Settings.getInput(name));
	}
	
	public static boolean getKeyUp(int key) {
		if(releasedKeyList.contains(key)) {
			return true;
		}
		
		return false;
	}

	public static boolean getKeyUp(String name){
		return getKeyUp(Settings.getInput(name));
	}
	
	public void update() {
		releasedKeyList.clear();
	}
}
