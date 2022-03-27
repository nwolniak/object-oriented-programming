package Classes;
import Classes.Animal;
import Classes.Grass;

import java.util.List;

public interface IMapObserver {
    // inform about animal added to map
    void animalAdded(Animal animal);
    // inform about animal removed from map
    void animalRemoved(Animal animal);
    // point to simulation which animals should be feed
    void feedAnimals(List<Animal> strongestAnimals, Grass grass);
}
