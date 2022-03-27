package Classes;

import Classes.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class setBackgrounds {

    private final Background bg;

    public setBackgrounds(String savannaPath, String junglePath, double jungleRatio, Vector2d mapSize) {
        Image savannaImage = new Image(savannaPath, true);
        Image jungleImage = new Image(junglePath, true);
        BackgroundImage savannaBG = new BackgroundImage(
                savannaImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1, 1, true, true, false, false)
        );
        BackgroundImage jungleBG = new BackgroundImage(jungleImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(null, (double) mapSize.x / 2 - (double) mapSize.x * jungleRatio / 2 - 0.5, false, null, (double) mapSize.y / 2 - (double) mapSize.y * jungleRatio / 2 - 0.5, false),
                new BackgroundSize(jungleRatio, jungleRatio, true, true, false, false)
        );
        BackgroundImage[] bgs = new BackgroundImage[2];
        bgs[0] = savannaBG;
        bgs[1] = jungleBG;
        this.bg = new Background(bgs);
    }

    public Background getBg() {
        return this.bg;
    }
}
