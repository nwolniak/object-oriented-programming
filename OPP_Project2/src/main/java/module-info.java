module Classes {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json.simple;


    opens Classes to javafx.fxml;
    exports Classes;
}