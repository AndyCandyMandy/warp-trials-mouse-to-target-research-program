module com.example.cmpt481_term_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cmpt481_term_project to javafx.fxml;
    exports com.example.cmpt481_term_project;
}