package lab1;

import java.util.Comparator;

public class xMapBoundaryComparator implements Comparator<IMapElement> {

    @Override
    public int compare(IMapElement firstElement, IMapElement secondElement) {

        if (firstElement.getPosition().equals(secondElement.getPosition())) {
            if (firstElement instanceof Animal && secondElement instanceof Animal) {
                return 0;
            } else if (firstElement instanceof Animal) {
                return 1;
            } else {
                return -1;
            }
        } else if (firstElement.getPosition().x > secondElement.getPosition().x) {
            return 1;
        } else if (firstElement.getPosition().x == secondElement.getPosition().x) {
            if (firstElement instanceof Animal) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }


}
