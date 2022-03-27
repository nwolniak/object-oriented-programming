package Classes;

import javafx.animation.AnimationTimer;

public class GameEngineTimer extends AnimationTimer {
    private static final long FRAMES_PER_SECOND = 60L;
    private static final long INTERVAL = 10000000L;
    private long last = 0;
    private final GameEngine gameEngine;
    private boolean isStopped;
    private int ticks;


    public GameEngineTimer(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.isStopped = true;
        this.ticks = 0;
    }

    @Override
    public void handle(long now) {
        if (now - this.last > INTERVAL / FRAMES_PER_SECOND) {
            this.gameEngine.run();
            this.ticks += 1;
            this.last = now;
            if (this.ticks > 60) {
                this.ticks = 1;
            }
        }
    }

    public int getTicks() {
        return this.ticks;
    }

    public boolean getIsStopped() {
        return this.isStopped;
    }

    public void setIsStopped(boolean status) {
        this.isStopped = status;
    }
}
