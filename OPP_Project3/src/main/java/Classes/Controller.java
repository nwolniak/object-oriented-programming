package Classes;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


public class Controller {
    private GameEngine gameEngine;
    private final Vector2d mapSize = new Vector2d(500, 700);
    private final int moveSpeed = 5;

    @FXML
    private Pane world;

    @FXML
    private TextField pointsField;

    @FXML
    private TextField lifesField;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button restartButton;

    @FXML
    private void initialize() {
        this.disableButtons(false, true, true);

        this.world.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.gameEngine = new GameEngine(this.world, this.pointsField, this.lifesField, this.mapSize, this.moveSpeed);

    }

    @FXML
    private void start() {
        this.disableButtons(true, false, true);
        this.gameEngine.start();
    }

    @FXML
    private void stop() {
        this.disableButtons(false, true, false);
        this.gameEngine.stop();
    }

    @FXML
    private void restart() {
        this.disableButtons(false, true, true);
        this.world.getChildren().clear();
        this.gameEngine = new GameEngine(this.world, this.pointsField, this.lifesField, this.mapSize, this.moveSpeed);
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (this.gameEngine.getIsStarted()) {
            this.gameEngine.setKeyPressed(event);
        }

    }

    @FXML
    private void onKeyReleased() {
        if (this.gameEngine.getIsStarted()) {
            this.gameEngine.setKeyReleased();
        }

    }

    private void disableButtons(boolean startButton, boolean stopButton, boolean restartButton) {
        this.startButton.setDisable(startButton);
        this.stopButton.setDisable(stopButton);
        this.restartButton.setDisable(restartButton);
    }


}
