package Classes;

public enum MapDirection {
    NORTH("Północ", new Vector2d(0, 1)),
    NORTH_EAST("Północny wschód", new Vector2d(1, 1)),
    NORTH_WEST("Północny zachód", new Vector2d(-1, 1)),
    SOUTH("Południe", new Vector2d(0, -1)),
    SOUTH_EAST("Południowy wschód", new Vector2d(1, -1)),
    SOUTH_WEST("Południowy zachód", new Vector2d(-1, -1)),
    WEST("Zachód", new Vector2d(-1, 0)),
    EAST("Wschód", new Vector2d(1, 0));

    private final String direction;
    private final Vector2d orientation;

    MapDirection(String direction, Vector2d orientation) {
        this.direction = direction;
        this.orientation = orientation;
    }

    public MapDirection next() {
        switch (this) {
            case NORTH:
                return NORTH_EAST;
            case NORTH_EAST:
                return EAST;
            case EAST:
                return SOUTH_EAST;
            case SOUTH_EAST:
                return SOUTH;
            case SOUTH:
                return SOUTH_WEST;
            case SOUTH_WEST:
                return WEST;
            case WEST:
                return NORTH_WEST;
            case NORTH_WEST:
                return NORTH;
            default:
                throw new IllegalArgumentException(this + " does not have next MapDirection");
        }
    }

    public Vector2d toUnitVector() {
        return this.orientation;
    }
}
