package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {
    @FXML
    public TextField messageField;
    @FXML
    public Button sendBtn;
    @FXML
    public TextArea chatArea;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox chatPanel;
    @FXML
    public ListView<String> clientList;

    private final int PORT = 8806;
    private final String IP_ADDRESS = "localhost";
    private final String CHAT_TITLE_EMPTY = "Chat";
    private static final Logger logger = Logger.getLogger(Controller.class.getName());


    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private Boolean authenticated;
    private String nickname;
    private String login;

    private Stage stage;
    private Stage regStage;
    RegController regController;

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        chatPanel.setVisible(authenticated);
        chatPanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);

        if (!authenticated) {
            nickname = "";
        }

        setTtile(nickname);
        chatArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage) chatArea.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (socket != null && !socket.isClosed()) {
                    try {
                        out.writeUTF("/end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        setAuthenticated(false);
        regStage = createRegWindow();
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    // auth cycle
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/timeout")) {
                            setAuthenticated(false);
                            return;
                        }

                        if (str.startsWith("/authok ")) {
                            nickname = str.split("\\s")[1];
                            login = str.split("\\s")[2];
                            setAuthenticated(true);
                            showLog();
                            break;
                        }

                        if (str.startsWith("/regresult ")) {
                            String result = str.split("\\s")[1];
                            if (result.equals("ok")) {
                                regController.addSystemMessage("Registration successful!");
                            } else {
                                regController.addSystemMessage("Registration failed! \nLogin or username already exists.");
                            }
                        }
                        chatArea.appendText(str);
                    }
                    // work cycle
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                setAuthenticated(false);
                                break;
                            }
                            if (str.startsWith("/clientlist ")) {
                                String[] token = str.split("\\s" );
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }

                        } else {
                            chatArea.appendText(str);
                            writeLog(str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLog(String message) {
        String path = "client/logs/history_" + login + ".txt";
        String pathDir = "client/logs/";
        File file = new File(path);
        File dir = new File(pathDir);

        try {
            if(!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(path, true))) {
                printWriter.print(message);
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Error writing into file");
            e.printStackTrace();
        }
    }

    public void showLog() {
        String path = "client/logs/history_" + login + ".txt";
        File file = new File(path);

        try {
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                    String line = reader.readLine();
                    for (int i = 0; i < 100; i++) {
                        while (line != null) {
                            chatArea.appendText(line);
                            chatArea.appendText("\n");
                            line = reader.readLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
            e.printStackTrace();
        }
    }

    public void sendMessage(ActionEvent actionEvent) {
        try {
            out.writeUTF(messageField.getText() + "\n");
            messageField.requestFocus();
            messageField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(String.format("/auth %s %s", loginField.getText().trim(), passwordField.getText().trim()));
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTtile(String nickname) {
        Platform.runLater(() -> {
            stage.setTitle(CHAT_TITLE_EMPTY + " : " + nickname);
        });
    }

    public void clickClientList(MouseEvent mouseEvent) {
        String receiver = clientList.getSelectionModel().getSelectedItem();
        messageField.setText(String.format("/w %s ", receiver));
    }

    private Stage createRegWindow() {
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = null;
            root = fxmlLoader.load();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root, 400, 300));
            stage.initModality(Modality.APPLICATION_MODAL);

            regController = fxmlLoader.getController();
            regController.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

    public void showRegWindow(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryToReg(String login, String password, String nickname) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(String.format("/reg %s %s %s", login, password, nickname));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
