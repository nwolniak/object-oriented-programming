package Classes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;


public class JSONreader {
    private int width;
    private int height;
    private double startEnergy;
    private double moveEnergy;
    private double plantEnergy;
    private double jungleRatio;
    private int numberOfAnimals;

    public JSONreader() {
        JSONParser myJSONparser = new JSONParser();
        try (FileReader reader = new FileReader("src/main/resources/Classes/parameters.json")) {
            // Read JSON file
            Object obj = myJSONparser.parse(reader);
            JSONObject parametersJSONobject = (JSONObject) obj;

            this.width = Integer.parseInt(parametersJSONobject.get("width").toString());
            this.height = Integer.parseInt(parametersJSONobject.get("height").toString());
            this.startEnergy = Double.parseDouble(parametersJSONobject.get("startEnergy").toString());
            this.moveEnergy = Double.parseDouble(parametersJSONobject.get("moveEnergy").toString());
            this.plantEnergy = Double.parseDouble(parametersJSONobject.get("plantEnergy").toString());
            this.jungleRatio = Double.parseDouble(parametersJSONobject.get("jungleRatio").toString());
            this.numberOfAnimals = Integer.parseInt(parametersJSONobject.get("numberOfAnimals").toString());
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public double getStartEnergy() {
        return this.startEnergy;
    }

    public double getMoveEnergy() {
        return this.moveEnergy;
    }

    public double getPlantEnergy() {
        return this.plantEnergy;
    }

    public double getJungleRatio() {
        return this.jungleRatio;
    }

    public int getNumberOfAnimals() {
        return this.numberOfAnimals;
    }
}
