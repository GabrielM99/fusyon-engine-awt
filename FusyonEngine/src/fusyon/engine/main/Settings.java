package fusyon.engine.main;

import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static Map<String, Integer> inputList = new HashMap<String, Integer>();

    public static void addInput(String name, int code){
        inputList.put(name, code);
    }

    public static int getInput(String name){
        return inputList.get(name);
    }

    public enum SortingLayers{
        BACKGROUND,
        DEFAULT,
        UI
    }
}
