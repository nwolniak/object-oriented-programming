package lab1;

import static java.lang.System.out;

public class World {

    public static void main(String[] args) {
        try {
            MoveDirection[] directions = new OptionsParser().parse(args);
            IWorldMap map = new GrassField(10);
            Vector2d[] positions = { new Vector2d(2,2), new Vector2d(3,4)};
            IEngine engine = new SimulationEngine(directions, map, positions);
            engine.run();
            out.println(map.toString());
        } catch (IllegalArgumentException ex){
            out.println(ex.getMessage());
            System.exit(0);
        }

    }
}

