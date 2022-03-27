package Classes;

import Classes.Simulation;
import Classes.Vector2d;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Controller {
    private Simulation firstSimulation;
    private Simulation secondSimulation;
    private FirstWorldTimer firstClock;
    private SecondWorldTimer secondClock;


    private Vector2d mapSize;
    private double startEnergy;
    private double moveEnergy;
    private double plantEnergy;
    private int numberOfAnimals;
    private double jungleRatio;
    private static final double CONST_VIEW_SIZE = 500;

    @FXML
    private Pane firstWorld;

    @FXML
    private Pane secondWorld;

    @FXML
    private Button resetFirstButton;

    @FXML
    private Button startFirstButton;

    @FXML
    private Button stopFirstButton;

    @FXML
    private Button stepFirstButton;

    @FXML
    private Button resetSecondButton;

    @FXML
    private Button startSecondButton;

    @FXML
    private Button stopSecondButton;

    @FXML
    private Button stepSecondButton;

    @FXML
    private Button firstDominantAnimals;

    @FXML
    private Button secondDominantAnimals;

    @FXML
    private TextField firstAnimalsSize;

    @FXML
    private TextField firstPlantsSize;

    @FXML
    private TextField firstAvgEnergy;

    @FXML
    private TextField firstAvgLife;

    @FXML
    private TextField firstAvgChildren;

    @FXML
    private TextField firstDominantGenes;

    @FXML
    private TextField secondAnimalsSize;

    @FXML
    private TextField secondPlantsSize;

    @FXML
    private TextField secondAvgEnergy;

    @FXML
    private TextField secondAvgLife;

    @FXML
    private TextField secondAvgChildren;

    @FXML
    private TextField secondDominantGenes;

    @FXML
    private TextField firstWorldAnimalGenotypes;

    @FXML
    private TextField firstAnimalFollowing;

    @FXML
    private TextField secondWorldAnimalGenotypes;

    @FXML
    private TextField secondAnimalFollowing;

    @FXML
    private Button firstWriteToFile;

    @FXML
    private Button secondWriteToFile;


    @FXML
    private void initialize() {
        //READ PARAMETERS
        JSONreader myJSONreader = new JSONreader();
        this.mapSize = new Vector2d(myJSONreader.getWidth(), myJSONreader.getHeight());
        this.startEnergy = myJSONreader.getStartEnergy();
        this.moveEnergy = myJSONreader.getMoveEnergy();
        this.plantEnergy = myJSONreader.getPlantEnergy();
        this.jungleRatio = myJSONreader.getJungleRatio();
        this.numberOfAnimals = myJSONreader.getNumberOfAnimals();

        //SET BUTTONS DISABLED
        this.disableFirstButtons(false, true, true, true, true, true);
        this.disableSecondButtons(false, true, true, true, true, true);
        this.firstClock = new FirstWorldTimer(this);
        this.secondClock = new SecondWorldTimer(this);

        //SET BACKGROUND IMAGE
        setBackgrounds settingBackgrounds = new setBackgrounds(getClass().getResource("BackgroundSavanna.png").toString(), getClass().getResource("JungleBackground.jpg").toString(), this.jungleRatio, this.mapSize);

        //SET SIZES AND SCALE MAPS
        this.firstWorld.setPrefSize(this.mapSize.x, this.mapSize.y);
        this.firstWorld.setBackground(settingBackgrounds.getBg());
        this.firstWorld.setScaleX(CONST_VIEW_SIZE / this.mapSize.x);
        this.firstWorld.setScaleY(CONST_VIEW_SIZE / this.mapSize.y);
        this.firstWorld.setTranslateX(CONST_VIEW_SIZE / 2);

        this.secondWorld.setPrefSize(this.mapSize.x, this.mapSize.y);
        this.secondWorld.setBackground(settingBackgrounds.getBg());
        this.secondWorld.setScaleX(CONST_VIEW_SIZE / this.mapSize.x);
        this.secondWorld.setScaleY(CONST_VIEW_SIZE / this.mapSize.y);
        this.secondWorld.setTranslateX(-CONST_VIEW_SIZE / 2);

        //MOUSE CLICKED ON MAP HANDLERS (LEFT CLICK - show genotypes , RIGHT CLICK - chose animal to follow)
        this.firstWorld.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (firstClock.getIsStopped() && firstClock.getTicks() > 0) { // is started simulation
                    if (event.isPrimaryButtonDown()) {
                        Vector2d mousePosition = new Vector2d((int) event.getX(), (int) event.getY());
                        try {
                            firstWorldAnimalGenotypes.setText(firstSimulation.animalClickedGenotypesToString(mousePosition));
                        } catch (IllegalArgumentException ex) {
                            firstWorldAnimalGenotypes.setText(ex.getMessage());
                        }
                    } else if (event.isSecondaryButtonDown()) {
                        Vector2d mousePosition = new Vector2d((int) event.getX(), (int) event.getY());
                        try {
                            firstAnimalFollowing.setText("Animal chosen at position : " + firstSimulation.animalClickedPosition(mousePosition).toString() + " , run Simulation !");
                            firstSimulation.addAnimalToFollow(mousePosition);
                        } catch (IllegalArgumentException ex) {
                            firstAnimalFollowing.setText(ex.getMessage());
                        }
                    }
                }
            }
        });
        this.secondWorld.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (secondClock.getIsStopped() && secondClock.getTicks() > 0) { // is started simulation
                    if (event.isPrimaryButtonDown()) {
                        Vector2d mousePosition = new Vector2d((int) event.getX(), (int) event.getY());
                        try {
                            secondWorldAnimalGenotypes.setText(secondSimulation.animalClickedGenotypesToString(mousePosition));
                        } catch (IllegalArgumentException ex) {
                            secondWorldAnimalGenotypes.setText(ex.getMessage());
                        }
                    } else if (event.isSecondaryButtonDown()) {
                        Vector2d mousePosition = new Vector2d((int) event.getX(), (int) event.getY());
                        try {
                            secondAnimalFollowing.setText("Animal chosen at position : " + secondSimulation.animalClickedPosition(mousePosition).toString() + " , run Simulation !");
                            secondSimulation.addAnimalToFollow(mousePosition);
                        } catch (IllegalArgumentException ex) {
                            secondAnimalFollowing.setText(ex.getMessage());
                        }
                    }
                }
            }
        });
    }

    //BUTTONS ON CLICK ACTIONS (sample.fxml)
    @FXML
    private void resetFirst() {
        this.firstClock.setIsStopped(true);
        this.stopFirst();
        this.disableFirstButtons(false, false, true, false, true, true);
        this.firstWorld.getChildren().clear();
        this.firstSimulation = new Simulation(firstWorld, this.numberOfAnimals, this.startEnergy, this.moveEnergy, this.plantEnergy, this.jungleRatio);
        this.showFirstStatistics();
    }

    @FXML
    private void resetSecond() {
        this.secondClock.setIsStopped(true);
        this.stopSecond();
        this.disableSecondButtons(false, false, true, false, true, true);
        this.secondWorld.getChildren().clear();
        this.secondSimulation = new Simulation(secondWorld, this.numberOfAnimals, this.startEnergy, this.moveEnergy, this.plantEnergy, this.jungleRatio);
        this.showSecondStatistics();
    }

    @FXML
    private void startFirst() {
        this.firstClock.start();
        this.firstClock.setIsStopped(false);
        this.disableFirstButtons(true, true, false, true, true, true);
    }

    @FXML
    private void startSecond() {
        this.secondClock.start();
        this.secondClock.setIsStopped(false);
        this.disableSecondButtons(true, true, false, true, true, true);
    }

    @FXML
    private void stopFirst() {
        this.disableFirstButtons(false, false, true, false, false, false);
        try {
            if (this.firstSimulation.isAnimalToFollowChosen()) {
                int[] animalFollowingStatistics = this.firstSimulation.getAnimalFollowingStatistics();
                this.firstAnimalFollowing.setText("Children : " + animalFollowingStatistics[0] + " , Descendants : " + animalFollowingStatistics[1] +
                        " , Death age : " + animalFollowingStatistics[2]);
            }
        } catch (NullPointerException ex) {
            this.firstAnimalFollowing.setText(ex.getMessage());
        }
        this.firstClock.setIsStopped(true);
        this.firstClock.stop();

    }

    @FXML
    private void stopSecond() {
        this.disableSecondButtons(false, false, true, false, false, false);
        try {
            if (this.secondSimulation.isAnimalToFollowChosen()) {
                int[] animalFollowingStatistics = this.secondSimulation.getAnimalFollowingStatistics();
                this.secondAnimalFollowing.setText("Children : " + animalFollowingStatistics[0] + " , Descendants : " + animalFollowingStatistics[1] +
                        " , Death age : " + animalFollowingStatistics[2]);
            }
        } catch (NullPointerException ex) {
            this.secondAnimalFollowing.setText(ex.getMessage());
        }

        this.secondClock.setIsStopped(true);
        this.secondClock.stop();
    }

    @FXML
    public void stepFirst() {
        this.firstSimulation.removeDeadAnimals();
        this.firstSimulation.plant();
        this.firstSimulation.move();
        this.firstSimulation.checkCollisions();
        this.firstSimulation.draw();
        this.firstClock.addTick();
        this.firstSimulation.uploadFileStatistics();
        if (this.firstClock.getIsStopped()) {
            this.firstWriteToFile.setDisable(false);
            this.firstDominantAnimals.setDisable(false);
        }
        try {
            if (this.firstSimulation.isAnimalToFollowChosen()) {
                int[] animalFollowingStatistics = this.firstSimulation.getAnimalFollowingStatistics();
                this.firstAnimalFollowing.setText("Children : " + animalFollowingStatistics[0] + " , Descendants : " + animalFollowingStatistics[1] +
                        " , Death age : " + animalFollowingStatistics[2]);
            }
        } catch (NullPointerException ex) {
            this.firstAnimalFollowing.setText(ex.getMessage());
        }
        this.showFirstStatistics();
    }

    @FXML
    public void stepSecond() {
        this.secondSimulation.removeDeadAnimals();
        this.secondSimulation.plant();
        this.secondSimulation.move();
        this.secondSimulation.checkCollisions();
        this.secondSimulation.draw();
        this.secondClock.addTick();
        this.secondSimulation.uploadFileStatistics();
        if (this.secondClock.getIsStopped()) {
            this.secondWriteToFile.setDisable(false);
            this.secondDominantAnimals.setDisable(false);
        }
        try {
            if (this.secondSimulation.isAnimalToFollowChosen()) {
                int[] animalFollowingStatistics = this.secondSimulation.getAnimalFollowingStatistics();
                this.secondAnimalFollowing.setText("Children : " + animalFollowingStatistics[0] + " , Descendants : " + animalFollowingStatistics[1] +
                        " , Death age : " + animalFollowingStatistics[2]);
            }
        } catch (NullPointerException ex) {
            this.secondAnimalFollowing.setText(ex.getMessage());
        }
        this.showSecondStatistics();
    }

    @FXML
    private void showFirstAnimalsWithDominantGenes() {
        this.firstSimulation.showAnimalsWithDominantGenes();
    }

    @FXML
    private void showSecondAnimalsWithDominantGenes() {
        this.secondSimulation.showAnimalsWithDominantGenes();
    }

    @FXML
    private void firstStatisticsWriteToFile() {
        try {
            File firstWorldStatistics = new File("firstWorldStatistics.txt");
            FileWriter fw = new FileWriter(firstWorldStatistics);
            PrintWriter pw = new PrintWriter(fw);
            int ticks = this.firstClock.getTicks(); // liczba n epok od poczatku symulacji
            pw.println("Liczba epok od poczatku symulacji : " + ticks);
            pw.println("Srednia liczba wszystkich zwierząt : " + (this.firstSimulation.getAllAnimalsNumber() / ticks));
            pw.println("Srednia liczba szystkich roślin : " + (this.firstSimulation.getAllGrassesNumber() / ticks));
            pw.println("Dominujacy genotyp : " + (this.firstSimulation.getAllDominantGenotype()));
            pw.println("Sredni poziom energii dla zyjacych zwierząt : " + (this.firstSimulation.getAllAvgEnergy() / ticks));
            pw.println("Srednia dlugosc zycia dla martwych zwierzat : " + (this.firstSimulation.getAllAvgLifeTime() / ticks));
            pw.println("Srednia liczba dzieci dla zyjacych zwierzat : " + (this.firstSimulation.getAllAnimalsChildrenNumber() / ticks));
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void secondStatisticsWriteToFile() {
        try {
            File secondtWorldStatistics = new File("secondWorldStatistics.txt");
            FileWriter fw = new FileWriter(secondtWorldStatistics);
            PrintWriter pw = new PrintWriter(fw);
            int ticks = this.secondClock.getTicks(); // liczba n epok od poczatku symulacji
            pw.println("Liczba epok od poczatku symulacji : " + ticks);
            pw.println("Srednia liczba wszystkich zwierząt : " + (this.secondSimulation.getAllAnimalsNumber() / ticks));
            pw.println("Srednia liczba szystkich roślin : " + (this.secondSimulation.getAllGrassesNumber() / ticks));
            pw.println("Srednia liczba wystapien dominujacego genotypu : " + (this.secondSimulation.getAllDominantGenotype() / ticks));
            pw.println("Sredni poziom energii dla zyjacych zwierząt : " + (this.secondSimulation.getAllAvgEnergy() / ticks));
            pw.println("Srednia dlugosc zycia dla martwych zwierzat : " + (this.secondSimulation.getAllAvgLifeTime() / ticks));
            pw.println("Srednia liczba dzieci dla zyjacych zwierzat : " + (this.secondSimulation.getAllAnimalsChildrenNumber() / ticks));
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // DISABLE BUTTONS
    private void disableFirstButtons(boolean resetFirstButton, boolean startFirstButton, boolean stopFirstButton, boolean stepFirstButton, boolean firstDominantAnimals, boolean firstWriteToFile) {
        this.resetFirstButton.setDisable(resetFirstButton);
        this.startFirstButton.setDisable(startFirstButton);
        this.stopFirstButton.setDisable(stopFirstButton);
        this.stepFirstButton.setDisable(stepFirstButton);
        this.firstDominantAnimals.setDisable(firstDominantAnimals);
        this.firstWriteToFile.setDisable(firstWriteToFile);
    }

    private void disableSecondButtons(boolean resetSecondButton, boolean startSecondButton, boolean stopSecondButton, boolean stepSecondButton, boolean secondDominantAnimals, boolean secondWriteToFile) {
        this.resetSecondButton.setDisable(resetSecondButton);
        this.startSecondButton.setDisable(startSecondButton);
        this.stopSecondButton.setDisable(stopSecondButton);
        this.stepSecondButton.setDisable(stepSecondButton);
        this.secondDominantAnimals.setDisable(secondDominantAnimals);
        this.secondWriteToFile.setDisable(secondWriteToFile);
    }

    //SHOW STATISTICS (pkt 4)
    private void showFirstStatistics() {
        this.firstAnimalsSize.setText(" " + this.firstSimulation.getAnimalsSize());
        this.firstPlantsSize.setText(" " + this.firstSimulation.getPlantsSize());
        this.firstAvgLife.setText(" " + this.firstSimulation.getAvgAnimalsLife());
        int[] dominantGenes = this.firstSimulation.getDomimantGenes();
        this.firstDominantGenes.setText(" 0: " + dominantGenes[0] + " , 1: " + dominantGenes[1] + " , 2: " + dominantGenes[2] +
                " , 3: " + dominantGenes[3] + " , 4: " + dominantGenes[4] + " , 5: " + dominantGenes[5] +
                " , 6: " + dominantGenes[6] + " , 7:" + dominantGenes[7]);
        this.firstAvgEnergy.setText(" " + this.firstSimulation.getAvgEnergy());
        this.firstAvgChildren.setText(" " + this.firstSimulation.getAvgChildren());
    }

    private void showSecondStatistics() {
        this.secondAnimalsSize.setText(" " + this.secondSimulation.getAnimalsSize());
        this.secondPlantsSize.setText(" " + this.secondSimulation.getPlantsSize());
        this.secondAvgLife.setText(" " + this.secondSimulation.getAvgAnimalsLife());
        int[] dominantGenes = this.secondSimulation.getDomimantGenes();
        this.secondDominantGenes.setText(" 0: " + dominantGenes[0] + " , 1: " + dominantGenes[1] + " , 2: " + dominantGenes[2] +
                " , 3: " + dominantGenes[3] + " , 4: " + dominantGenes[4] + " , 5: " + dominantGenes[5] +
                " , 6: " + dominantGenes[6] + " , 7:" + dominantGenes[7]);
        this.secondAvgEnergy.setText(" " + this.secondSimulation.getAvgEnergy());
        this.secondAvgChildren.setText(" " + this.secondSimulation.getAvgChildren());
    }
}
