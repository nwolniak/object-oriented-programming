package Classes;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.*;


public class GameEngine implements IShieldBrokenObserver, IBallHitLowerBoundObserver {
    private final WorldMap map;
    private final Pane world;
    private final TextField pointsField;
    private final TextField lifesField;
    private final Platform platform;
    private final GameEngineTimer gameEngineTimer;
    private KeyEvent keyPressed;
    private final int NUMBER_OF_BRICKS = 56;


    private int platformWidthBonusTicks;
    private int platformSpeedBonusTicks;
    private int ballSpeedBonusTicks;


    private static final Random random = new Random();

    private final Vector2d mapSize;

    private final List<Ball> balls = new ArrayList<>();
    private final List<Ball> ballsToRemove = new ArrayList<>();

    private final List<Bonus> bonuses = new ArrayList<Bonus>();
    private final List<Bonus> bonusesToRemove = new ArrayList<Bonus>();

    private final List<Vector2d> positions = new ArrayList<>();
    private final Map<Vector2d, Brick> bricksPositions = new HashMap<>();

    private int points;
    private int lifes;

    public GameEngine(Pane world, TextField pointsField, TextField lifesField, Vector2d mapSize, int moveSpeed) {
        this.world = world;
        this.pointsField = pointsField;
        this.lifesField = lifesField;
        this.mapSize = mapSize;
        this.map = new WorldMap(mapSize);

        this.points = 0;
        this.lifes = 3;
        this.platform = new Platform(this.map, new Vector2d(mapSize.x / 2, 620), 100, 20);
        this.world.getChildren().add(this.platform.getRectangle());
        this.map.addPlatform(this.platform);
        this.gameEngineTimer = new GameEngineTimer(this);

        this.createNewBall();
        this.updatePointsField();
        this.updateLifesField();
        this.spawnBricks(NUMBER_OF_BRICKS);
        this.draw();
    }

    private void createNewBall() {
        Ball newBall = new Ball(this.map, new Vector2d(this.mapSize.x / 2 - 30, 600), 5);
        newBall.setSpeedMultiplier(5);
        this.balls.add(newBall);
        newBall.addLowerBoundObserver(this);
        this.world.getChildren().add(newBall.getCircle());
        this.map.addBall(newBall);
    }

    public void start() {
        this.gameEngineTimer.start();
        this.gameEngineTimer.setIsStopped(false);

    }

    public void stop() {
        this.gameEngineTimer.stop();
        this.gameEngineTimer.setIsStopped(true);

    }

    private void hitBallFromPlatform(Ball ball) {
        if (this.platform.getMovementState() == MovementState.GOING_LEFT) {
            if (ball.getPosition().x == this.platform.getLowerRight().x) {
                this.hitBallFromLeftBound(ball);
            } else if (ball.getLowerRight().x == this.platform.getUpperLeft().x) {
                this.hitBallFromRightBound(ball);
            } else {
                ball.setDx(-1);
                ball.changeDirection(false, true);
                ball.setMovementState(MovementState.GOING_LEFT_UP);
            }

        } else if (this.platform.getMovementState() == MovementState.GOING_RIGHT) {
            if (ball.getLowerRight().x == this.platform.getUpperLeft().x) {
                this.hitBallFromLeftBound(ball);
            } else if (ball.getPosition().x == this.platform.getLowerRight().x) {
                this.hitBallFromRightBound(ball);
            } else {
                ball.setDx(1);
                ball.changeDirection(false, true);
                ball.setMovementState(MovementState.GOING_RIGHT_UP);
            }

        } else if (this.platform.getMovementState() == MovementState.MOTIONLESS) {
            this.hitBallFromLowerBound(ball);
        }
    }

    private void hitBallFromBrick(Ball ball, Brick brick) {
        if (ball.getPosition().x == brick.getLowerRight().x) {
            this.hitBallFromRightBound(ball);
        } else if (ball.getPosition().x + ball.getWidth() == brick.getUpperLeft().x) {
            this.hitBallFromLeftBound(ball);
        } else if (ball.getLowerRight().y == brick.getUpperLeft().y) {
            this.hitBallFromLowerBound(ball);
        } else if (ball.getPosition().y == brick.getLowerRight().y) {
            this.hitBallFromUpperBound(ball);
        }
        brick.hitted();
    }

    private void spawnBricks(int numberOfBricks) {
        Vector2d xShift = new Vector2d(60, 0);
        Vector2d yShift = new Vector2d(0, 50);
        Vector2d startingPosition = new Vector2d(20, 20);
        Vector2d nextPosition = new Vector2d(20, 20);
        for (int i = 1; i <= numberOfBricks; i++) {
            this.createNewBrick(nextPosition);
            this.positions.add(nextPosition);
            nextPosition = nextPosition.add(xShift);
            if (i % 8 == 0) {
                nextPosition = startingPosition.add(yShift);
                startingPosition = startingPosition.add(yShift);
            }
        }
    }

    private void createNewBrick(Vector2d position) {
        Brick newBrick = new Brick(position, 40, 20, 1);
        int randInt = random.nextInt(20);
        if (randInt % 5 == 0) {
            Bonus bonus = new Bonus(this.map, position, 5, BonusType.PLATFORM_WIDTH_BONUS);
            bonus.setSpeedMultiplier(5);
            bonus.addLowerBoundObserver(this);
            newBrick.setBonus(bonus);
        } else if (randInt % 9 == 0) {
            Bonus bonus = new Bonus(this.map, position, 5, BonusType.BALL_SPEED_BONUS);
            bonus.setSpeedMultiplier(5);
            bonus.addLowerBoundObserver(this);
            newBrick.setBonus(bonus);
        } else if (randInt % 6 == 0) {
            Bonus bonus = new Bonus(this.map, position, 5, BonusType.PLATFORM_SPEED_BONUS);
            bonus.setSpeedMultiplier(5);
            bonus.addLowerBoundObserver(this);
            newBrick.setBonus(bonus);
        } else if (randInt % 13 == 0) {
            Bonus bonus = new Bonus(this.map, position, 5, BonusType.NEW_BALL_BONUS);
            bonus.setSpeedMultiplier(5);
            bonus.addLowerBoundObserver(this);
            newBrick.setBonus(bonus);
        }
        this.bricksPositions.put(position, newBrick);
        this.map.addBrick(newBrick);
        newBrick.addObserver(this);
        this.world.getChildren().add(newBrick.getRectangle());
    }

    public void endlessMode() {
        int randIndex = random.nextInt(this.positions.size());
        if (!this.bricksPositions.containsKey(this.positions.get(randIndex))) {
            this.createNewBrick(this.positions.get(randIndex));
        }
    }

    private void hitBothBalls(Ball ball, Ball other) {
        if (ball.getPosition().x == other.getLowerRight().x) {
            this.hitBallFromLeftBound(ball);
            this.hitBallFromRightBound(other);
        } else if (ball.getLowerRight().x == other.getPosition().x) {
            this.hitBallFromLeftBound(other);
            this.hitBallFromRightBound(ball);
        } else if (ball.getPosition().y == other.getLowerRight().y) {
            this.hitBallFromLowerBound(other);
            this.hitBallFromUpperBound(ball);
        } else if (ball.getLowerRight().y == other.getPosition().y) {
            this.hitBallFromLowerBound(ball);
            this.hitBallFromUpperBound(other);
        }
    }


    private void hitBallFromLeftBound(Ball ball) {
        MovementState ballMovementState = ball.getMovementState();
        ball.changeDirection(true, false);
        if (ballMovementState == MovementState.GOING_LEFT_UP) {
            ball.setMovementState(MovementState.GOING_RIGHT_UP);
        } else if (ballMovementState == MovementState.GOING_LEFT_DOWN) {
            ball.setMovementState(MovementState.GOING_RIGHT_DOWN);
        } else if (ballMovementState == MovementState.GOING_LEFT) {
            ball.setMovementState(MovementState.GOING_RIGHT);
        }
    }

    private void hitBallFromRightBound(Ball ball) {
        MovementState ballMovementState = ball.getMovementState();
        ball.changeDirection(true, false);
        if (ballMovementState == MovementState.GOING_RIGHT_UP) {
            ball.setMovementState(MovementState.GOING_LEFT_UP);
        } else if (ballMovementState == MovementState.GOING_RIGHT_DOWN) {
            ball.setMovementState(MovementState.GOING_LEFT_DOWN);
        } else if (ballMovementState == MovementState.GOING_RIGHT) {
            ball.setMovementState(MovementState.GOING_LEFT);
        }
    }

    private void hitBallFromLowerBound(Ball ball) {
        MovementState ballMovementState = ball.getMovementState();
        ball.changeDirection(false, true);
        if (ballMovementState == MovementState.GOING_LEFT_DOWN) {
            ball.setMovementState(MovementState.GOING_LEFT_UP);
        } else if (ballMovementState == MovementState.GOING_RIGHT_DOWN) {
            ball.setMovementState(MovementState.GOING_RIGHT_UP);
        } else if (ballMovementState == MovementState.GOING_DOWN) {
            ball.setMovementState(MovementState.GOING_UP);
        }
    }

    private void hitBallFromUpperBound(Ball ball) {
        MovementState ballMovementState = ball.getMovementState();
        ball.changeDirection(false, true);
        if (ballMovementState == MovementState.GOING_LEFT_UP) {
            ball.setMovementState(MovementState.GOING_LEFT_DOWN);
        } else if (ballMovementState == MovementState.GOING_RIGHT_UP) {
            ball.setMovementState(MovementState.GOING_RIGHT_DOWN);
        } else if (ballMovementState == MovementState.GOING_UP) {
            ball.setMovementState(MovementState.GOING_DOWN);
        }
    }


    public void run() {
        // Bonuses movement
        for (Bonus bonus : this.bonuses) {
            for (int i = 0; i < bonus.getSpeedMultiplier(); i++) {
                bonus.move();
                if (this.map.isHittingPlatform(bonus)) {
                    if (bonus.getBonusType() == BonusType.PLATFORM_WIDTH_BONUS) {
                        this.platform.setWidthMultiplier(bonus.getBonusValue());
                        this.platformWidthBonusTicks = 0;
                    } else if (bonus.getBonusType() == BonusType.BALL_SPEED_BONUS) {
                        for (Ball ball : this.balls) {
                            ball.setSpeedBonus((int) bonus.getBonusValue());
                            this.ballSpeedBonusTicks = 0;
                        }
                    } else if (bonus.getBonusType() == BonusType.PLATFORM_SPEED_BONUS) {
                        this.platform.setSpeedBonus((int) bonus.getBonusValue());
                        this.platformSpeedBonusTicks = 0;
                    } else if (bonus.getBonusType() == BonusType.NEW_BALL_BONUS) {
                        this.createNewBall();
                    }
                    this.bonusesToRemove.add(bonus);
                }
                if (this.map.isOnLowerBound(bonus.getLowerRight())) {
                    this.bonusesToRemove.add(bonus);
                    break;
                }
            }
        }
        this.removeBonusesWaitingToBeRemoved();


        // Balls movement
        int maxSpeedMultiplier = 1;
        for (Ball ball : this.balls) {
            if (ball.getSpeedMultiplier() > maxSpeedMultiplier) {
                maxSpeedMultiplier = ball.getSpeedMultiplier();
            }
        }


        for (int i = 0; i < maxSpeedMultiplier; i++) {
            for (Ball ball : this.balls) {
                if (ball.getSpeedMultiplier() >= i) {
                    ball.move();
                    if (this.map.isHittingPlatform(ball)) {
                        this.hitBallFromPlatform(ball);
                    }
                    if (this.map.brickHitByBall(ball) != null) {
                        this.hitBallFromBrick(ball, this.map.brickHitByBall(ball));
                    }
                    if (this.map.isHittingBall(ball) != null) {
                        this.hitBothBalls(ball, this.map.isHittingBall(ball));
                    }
                    if (this.map.isOnLowerBound(ball.getLowerRight())) {
                        this.ballsToRemove.add(ball);
                        break;
                    }
                }
            }
        }

        this.removeBallsWaitingToBeRemoved();
        this.checkIsAnyBall();

        // Patform movement
        if (keyPressed != null) {
            if (keyPressed.getCode() == KeyCode.A || keyPressed.getCode() == KeyCode.LEFT) {
                this.platform.setMovementState(MovementState.GOING_LEFT);
                this.platform.setDx(-1);
                for (int i = 0; i < this.platform.getSpeedMultiplier(); i++) {
                    this.platform.move();
                }
            }
            if (keyPressed.getCode() == KeyCode.D || keyPressed.getCode() == KeyCode.RIGHT) {
                this.platform.setMovementState(MovementState.GOING_RIGHT);
                this.platform.setDx(1);
                for (int i = 0; i < this.platform.getSpeedMultiplier(); i++) {
                    this.platform.move();
                }
            }
        }
        if (this.gameEngineTimer.getTicks() == 60) {
            this.endlessMode();
        }
        if (this.platformSpeedBonusTicks == 300) {
            this.platform.removeSpeedBonus();
            this.platformSpeedBonusTicks += 1;
        } else if (this.platformSpeedBonusTicks < 300) {
            this.platformSpeedBonusTicks += 1;
        }
        if (this.platformWidthBonusTicks == 300) {
            this.platform.removeWidthMiltiplier();
            this.platformWidthBonusTicks += 1;
        } else if (this.platformWidthBonusTicks < 300) {
            this.platformWidthBonusTicks += 1;
        }
        if (this.ballSpeedBonusTicks == 300) {
            for (Ball ball : this.balls) {
                ball.removeSpeedBonus();
            }
            this.ballSpeedBonusTicks += 1;
        } else if (this.ballSpeedBonusTicks < 300) {
            this.ballSpeedBonusTicks += 1;
        }
        this.draw();
    }

    private void removeBonusesWaitingToBeRemoved() {
        for (Bonus bonus : this.bonusesToRemove) {
            this.bonuses.remove(bonus);
            this.map.deleteBonus(bonus);
            this.world.getChildren().remove(bonus.getCircle());
        }
        this.bonusesToRemove.clear();
    }

    private void removeBallsWaitingToBeRemoved() {
        for (Ball ball : this.ballsToRemove) {
            this.balls.remove(ball);
            this.map.deleteBall(ball);
            this.world.getChildren().remove(ball.getCircle());
        }
    }

    private void checkIsAnyBall() {
        if (this.balls.isEmpty()) {
            this.lifes -= 1;
            this.updateLifesField();
            if (this.lifes == 0) {
                this.gameOver();
            }
            this.createNewBall();
        }
    }

    private void draw() {
        this.map.draw();
    }

    public void setKeyPressed(KeyEvent keyPressed) {
        this.keyPressed = keyPressed;
    }

    public void setKeyReleased() {
        this.keyPressed = null;
        this.platform.setMovementState(MovementState.MOTIONLESS);
    }

    public boolean getIsStarted() {
        return !this.gameEngineTimer.getIsStopped();
    }


    public void updatePointsField() {
        this.pointsField.setText("" + this.points);
    }

    public void updateLifesField() {
        this.lifesField.setText("" + this.lifes);
    }

    public void gameOver() {
        this.stop();

    }


    @Override
    public void shieldBroken(Brick brick) {
        this.world.getChildren().remove(brick.getRectangle());
        this.bricksPositions.remove(brick.getPosition());
        if (brick.getBonus() != null) {
            Bonus bonus = brick.getBonus();
            this.world.getChildren().add(bonus.getCircle());
            this.bonuses.add(bonus);
            this.map.addBonus(bonus);
            bonus.addLowerBoundObserver(this);
        }

        this.points += 1;
        this.updatePointsField();
    }

    @Override
    public void hitLowerBound(AbstractBallMovingElement ballElement) {
        if (ballElement instanceof Ball) {
            this.ballsToRemove.add((Ball) ballElement);
            if (this.lifes == 0) {
                this.gameOver();
            }
        } else if (ballElement instanceof Bonus) {
            this.bonusesToRemove.add((Bonus) ballElement);
        }
    }
}
