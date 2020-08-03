package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegController {
    private static final Logger logger = Logger.getLogger(RegController.class.getName());
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nicknameField;
    @FXML
    public Button signUp;
    @FXML
    public Button cancel;
    @FXML
    public TextArea messageArea;

    public void tryToSignup(ActionEvent actionEvent) {
        controller.tryToReg(loginField.getText().trim(),
                passwordField.getText().trim(),
                nicknameField.getText().trim());
    }

    public void cancel(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            ((Stage) loginField.getScene().getWindow()).close();
        });
    }

    public void addSystemMessage(String message) {
        messageArea.appendText(message + "\n");
        logger.log(Level.INFO, message);
    }
}
