package Classes;

import javafx.scene.paint.Color;

public enum EnergyState {
    HEALTHY {
        public Color getColor() {
            return Color.BROWN;
        }
    },
    MEDIUM {
        public Color getColor() {
            return Color.CHOCOLATE;
        }
    },
    WEAK {
        public Color getColor() {
            return Color.BURLYWOOD;
        }
    },
    DEAD {
        public Color getColor() {
            return Color.WHITE;
        }
    };

    public abstract Color getColor();
}
