package Classes;

import javafx.animation.AnimationTimer;

public class SecondWorldTimer extends AnimationTimer {
    private static final long FRAMES_PER_SECOND = 4L;
    private static final long INTERVAL = 1000000000L;
    private long last = 0;
    private final Controller controller;
    private boolean isStopped;
    private int ticks;

    public SecondWorldTimer(Controller controller) {
        this.controller = controller;
        this.isStopped = true;
        this.ticks = 0;
    }

    @Override
    public void handle(long now) {
        if (now - this.last > INTERVAL / FRAMES_PER_SECOND) {
            this.controller.stepSecond();
            this.last = now;
        }
    }

    public boolean getIsStopped() {
        return this.isStopped;
    }

    public void setIsStopped(boolean status) {
        this.isStopped = status;
    }

    public void addTick(){
        this.ticks ++;
    }

    public int getTicks() {
        return this.ticks;
    }
}