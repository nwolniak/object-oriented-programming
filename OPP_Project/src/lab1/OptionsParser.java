package lab1;

import java.util.Arrays;

public class OptionsParser {

    public MoveDirection[] parse(String[] stringsArray) {

        MoveDirection[] directionsArray = new MoveDirection[stringsArray.length];
        int i = 0;
        for (String direction : stringsArray) {
            switch (direction) {
                case "f":
                case "forward":
                    directionsArray[i++] = MoveDirection.FORWARD;
                    break;
                case "b":
                case "backward":
                    directionsArray[i++] = MoveDirection.BACKWARD;
                    break;
                case "r":
                case "right":
                    directionsArray[i++] = MoveDirection.RIGHT;
                    break;
                case "l":
                case "left":
                    directionsArray[i++] = MoveDirection.LEFT;
                    break;
                default: throw new IllegalArgumentException(direction + " is not legal move specification");
            }
        }
        return Arrays.copyOfRange(directionsArray, 0, i);
    }
}
