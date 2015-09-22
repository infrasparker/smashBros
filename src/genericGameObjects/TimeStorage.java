package src.genericGameObjects;

public class TimeStorage {
    public int timer, time0, overTimer;
    private boolean countDown;
    public boolean locked;
    
//    TimeStorage constructor that counts up
    public TimeStorage() {
        this(0, false, false);
    }

//    TimeStorage object that counts down from start
    public TimeStorage(int start) {
        this(start, true, false);
    }
    
    public TimeStorage(int start, boolean l) {
        this(start, true, l);
    }
    
    public TimeStorage(int start, boolean c, boolean l) {
        time0 = start;
        timer = time0;
        overTimer = 0;
        countDown = c;
        locked = l;
    }
    
    public void update() {
        if (!locked) {
            if (timer > 0 && countDown) {
                timer--;
            }
            else if (!countDown) {
                timer++;
            }
        }
    }
    
    public void reset() {
        timer = time0;
    }
}
