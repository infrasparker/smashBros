package src.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ControlHandler {
    public static Map createControls(String[] keys, int[] hotkeys) {
        Map ctrls = new HashMap<String, Integer>();
        for (String key : keys) {
            int index = Arrays.asList(keys).indexOf(key);
            ctrls.put(keys[index], hotkeys[index]);
        }
        return ctrls;
    }
}
