package Classes;

import javafx.scene.paint.Color;

public enum BonusType {
    BALL_SPEED_BONUS(2,Color.BLUE),
    PLATFORM_WIDTH_BONUS(1.5,Color.RED),
    PLATFORM_SPEED_BONUS(2,Color.YELLOW),
    NEW_BALL_BONUS(1, Color.WHITE);

    private final Color color;
    private final double bonusValue;
    BonusType(double bonusValue,Color color){
        this.bonusValue = bonusValue;
        this.color = color;
    }

    public double getBonusValue(){
        return this.bonusValue;
    }

    public Color getColor(){
        return this.color;
    }

}
