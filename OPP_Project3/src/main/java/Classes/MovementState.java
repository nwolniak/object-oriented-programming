package Classes;

public enum MovementState {
    GOING_LEFT(new Vector2d(-1,0)),
    GOING_LEFT_UP(new Vector2d(-1,-1)),
    GOING_LEFT_DOWN(new Vector2d(-1,1)),
    GOING_RIGHT(new Vector2d(1,0)),
    GOING_RIGHT_UP(new Vector2d(1,-1)),
    GOING_RIGHT_DOWN(new Vector2d(1,1)),
    GOING_UP(new Vector2d(0,-1)),
    GOING_DOWN(new Vector2d(0,1)),
    MOTIONLESS(new Vector2d(0,0));

    private final Vector2d direction;

    MovementState(Vector2d direction){
        this.direction = direction;
    }

    public Vector2d getDirection(){
        return this.direction;
    }
    public String toString(){
        return this.name();
    }
}
