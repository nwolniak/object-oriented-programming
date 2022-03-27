package Classes;


import java.util.ArrayList;
import java.util.List;

public class WorldMap implements IShieldBrokenObserver {

    private final Vector2d upperLeft;
    private final Vector2d lowerRight;
    private Platform platform;


    private final List<Ball> balls = new ArrayList<Ball>();

    private final List<Brick> bricks = new ArrayList<Brick>();
    private final List<AbstractBallMovingElement> bonuses = new ArrayList<AbstractBallMovingElement>();


    public WorldMap(Vector2d mapSize) {
        this.upperLeft = new Vector2d(0, 0);
        this.lowerRight = mapSize;
    }


    public boolean canMoveTo(IMapElement mapElement, Vector2d position) {

        if (position.follows(this.upperLeft) && position.add(new Vector2d(mapElement.getWidth(), mapElement.getHeight())).precedes(lowerRight)) {
            return true;
        }
        return false;
    }

    public boolean isHittingPlatform(AbstractBallMovingElement ballMovingElement) {
        if ((ballMovingElement.getPosition().follows(this.platform.getUpperLeft()) && ballMovingElement.getPosition().precedes(this.platform.getLowerRight())) ||
                (ballMovingElement.getLowerRight().follows(this.platform.getUpperLeft()) && ballMovingElement.getLowerRight().precedes(this.platform.getLowerRight()))) {
            return true;
        }
        return false;
    }

    public Ball isHittingBall(Ball other) {
        for (Ball ball : this.balls) {
            if (ball.equals(other)) {
                if ((ball.getPosition().precedes(other.getLowerRight()) && ball.getPosition().follows(other.getPosition())) ||
                        (ball.getLowerRight().precedes(other.getLowerRight()) && ball.getLowerRight().follows(other.getPosition()))) {
                    return ball;
                }
            }
        }
        return null;
    }

    public Brick brickHitByBall(Ball ball) {
        for (Brick brick : this.bricks) {
            if ((ball.getPosition().follows(brick.getUpperLeft()) && ball.getPosition().precedes(brick.getLowerRight())) ||
                    (ball.getLowerRight().follows(brick.getUpperLeft()) && ball.getLowerRight().precedes(brick.getLowerRight()))) {
                return brick;
            }
        }
        return null;
    }


    public boolean isOnLeftBound(Vector2d position) {
        return position.x == this.upperLeft.x;
    }

    public boolean isOnRightBound(Vector2d position) {
        return position.x == this.lowerRight.x;
    }

    public boolean isOnUpperBound(Vector2d position) {
        return position.y == this.upperLeft.y;
    }

    public boolean isOnLowerBound(Vector2d position) {
        return position.y == this.lowerRight.y;
    }


    public void addPlatform(Platform platform) {
        this.platform = platform;
    }

    public void addBall(Ball ball) {
        this.balls.add(ball);
    }

    public void deleteBall(Ball ball) {
        this.balls.remove(ball);
    }

    public boolean canPlaceAt(Vector2d position) {
        return position.follows(this.upperLeft) && position.precedes(this.lowerRight);
    }

    public void addBrick(Brick brick) {
        this.bricks.add(brick);
        brick.addObserver(this);
    }

    public void addBonus(AbstractBallMovingElement bonus) {
        this.bonuses.add(bonus);
    }

    public void deleteBonus(AbstractBallMovingElement bonus) {
        this.bonuses.remove(bonus);
    }

    public void draw() {
        this.platform.draw();
        for (Ball ball : this.balls) {
            ball.draw();
        }
        for (AbstractBallMovingElement widthMultiplierBonus : this.bonuses) {
            widthMultiplierBonus.draw();
        }
    }


    public Platform getPlatform() {
        return this.platform;
    }

    public Vector2d getLowerRight() {
        return this.lowerRight;
    }

    public Vector2d getUpperLeft() {
        return this.upperLeft;
    }


    @Override
    public void shieldBroken(Brick brick) {
        this.bricks.remove(brick);
    }
}
