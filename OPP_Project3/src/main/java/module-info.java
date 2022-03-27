module app.opp_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens Classes to javafx.fxml;
    exports Classes;
}