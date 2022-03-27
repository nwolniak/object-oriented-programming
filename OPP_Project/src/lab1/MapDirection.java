package lab1;

public enum MapDirection {
    NORTH("Północ", new Vector2d(0, 1)),
    SOUTH("Południe", new Vector2d(0, -1)),
    WEST("Zachód", new Vector2d(-1, 0)),
    EAST("Wschód", new Vector2d(1, 0));

    private final String direction;
    private final Vector2d position;

    MapDirection(String direction, Vector2d position) {
        this.direction = direction;
        this.position = position;

    }


    public String toString() {
        return this.direction;
    }

    public MapDirection next() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                return null;
        }
    }

    public MapDirection previous() {
        switch (this) {
            case NORTH:
                return WEST;
            case WEST:
                return SOUTH;
            case SOUTH:
                return EAST;
            case EAST:
                return NORTH;
            default:
                return null;
        }
    }

    public Vector2d toUnitVector() {
        return this.position;
    }
}
