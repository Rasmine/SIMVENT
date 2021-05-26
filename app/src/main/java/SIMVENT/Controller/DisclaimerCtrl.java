package SIMVENT.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DisclaimerCtrl {

    @FXML
    private Button displayEvaluationButton;

    @FXML //Når der trykkes "Yes, show me my evaluation", instantierer denne metode EvaluationView og EvaluationCtrl
    void handleDisplayEvaluationButton(ActionEvent event) throws IOException {
            Parent evaluationView = FXMLLoader.load(getClass().getResource("/EvaluationView.fxml"));
            Stage evaluation = (Stage) displayEvaluationButton.getScene().getWindow();
            evaluation.setTitle("Evaluation");
            evaluation.setScene(new Scene(evaluationView));
        }
    }
