module com.example.warp_trial_program {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.warp_trial_program to javafx.fxml;
    exports com.example.warp_trial_program;
}