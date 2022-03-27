package Classes;

import Classes.Animal;
import Classes.Grass;

import java.util.List;

public interface IPositionChanged {
    // inform observer which position is no longer occupied bu this particular animal
    void animalAtOldPosition(Animal atOldPosition);
    // inform observer which position is occupied by this particular animal
    void animalAtNewPosition(Animal atNewPosition);
    //inform observer about death and what position should look for to remove animal (reference to animal)
    void passedToAnimalHeaven(Animal deadAnimal);
}
