package src.genericGameObjects;

import java.util.Map;

public interface Controllable extends Updatable {
    public abstract void keyTrigger(Map<String, Boolean> keys);
}
