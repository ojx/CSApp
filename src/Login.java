import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;


class Login extends BorderPane {
    private static final String LOGIN_SCRIPT_URL = "https://mrjonescs.com/csapp/auth.php";

    private static final int MESSAGE_DISPLAY_SECONDS = 5;
    private static final int DELAY_FORM_RESET_SECONDS = 3;
    private static final int FADE_MILLISECONDS = 1000;

    private static final String USER_NAME_REG_EX = "[a-zA-Z0-9]*";
    private static final String PASSWORD_REG_EX = "[a-zA-Z0-9]*";

    private TextField userNameField;
    private PasswordField passwordField;
    private Label messageLabel;
    private ProgressIndicator indicator;
    private Button authenticateButton;
    private GridPane gridPane;
    private Node previouslyDisplayed;
    private Timeline delayedReset;
    private FadeTransition messageFade;

    private Callback<User, Void> authenticated;


    Login(Callback<User, Void> authenticated) {

        this.authenticated = authenticated;
        previouslyDisplayed = null;


        authenticateButton = new Button("Authenticate");
        authenticateButton.setOnAction(((e)->validate()));

        gridPane = new GridPane();

        ImageView imageView = new ImageView(new Image("imgs/tdsb.png"));

       // gridPane.setGridLinesVisible(true);

        userNameField = new TextField();
        passwordField = new PasswordField();
        messageLabel = new Label();
        indicator = new ProgressIndicator();

        delayedReset = new Timeline(new KeyFrame(Duration.seconds(DELAY_FORM_RESET_SECONDS), event -> resetForm()));

        messageFade = new FadeTransition(Duration.millis(FADE_MILLISECONDS));
        messageFade.setNode(messageLabel);
        messageFade.setFromValue(1);
        messageFade.setToValue(0);
        messageFade.setDelay(Duration.seconds(MESSAGE_DISPLAY_SECONDS));


        gridPane.addRow(0, new Label("Username:"), userNameField);
        gridPane.addRow(1, new Label("Password:"), passwordField);
        displayNode(authenticateButton);
        gridPane.add(messageLabel, 0,3,2,1);

        GridPane.setHalignment(messageLabel, HPos.CENTER);
        GridPane.setHalignment(authenticateButton, HPos.RIGHT);
        GridPane.setValignment(authenticateButton, VPos.TOP);
       // gridPane.addRow(3, messageLabel, authenticateButton);

        setCenter(new VBox(new HBox(imageView), gridPane));


        EventHandler<KeyEvent> userKeyHandler = keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) {
                passwordField.requestFocus();
            }
        };

        EventHandler<KeyEvent> passwordKeyHandler = keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) {
                validate();
            }
        };

        userNameField.setOnKeyPressed(userKeyHandler);
        passwordField.setOnKeyPressed(passwordKeyHandler);



       // prefWidthProperty().bind(scene.widthProperty());

       //
        userNameField.setText("owainj");
        passwordField.setText("snowflake");
    }



    private void displayNode(Node node) {

        if (previouslyDisplayed != null)
            gridPane.getChildren().remove(previouslyDisplayed);

        previouslyDisplayed = node;

        gridPane.add(node,0,2,2,1);
    }

    private void validate() {
        String userName = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userName.length() == 0) {
            displayMessage("Please enter a username");
            userNameField.requestFocus();
        } else if (!userName.matches(USER_NAME_REG_EX)) {
            displayMessage("Enter a valid username");
            userNameField.requestFocus();
        } else if (password.length() == 0) {
            displayMessage("Please enter a password");
            passwordField.requestFocus();
        } else if (!password.matches(PASSWORD_REG_EX)) {
            displayMessage("Enter a valid password");
            passwordField.requestFocus();
        } else {
            authenticate();
        }

    }

    private void authenticate() {

        userNameField.setDisable(true);
        passwordField.setDisable(true);
        messageLabel.setText("");

        String userName = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        gridPane.getChildren().remove(authenticateButton);
        displayNode(indicator);
        GridPane.setHalignment(indicator, HPos.RIGHT);

        FormData data = new FormData();
        data.add("username", userName);
        data.add("password", password);

        HttpRequestTask.post(LOGIN_SCRIPT_URL, data, new HttpResponseHandler() {
            @Override
            public void handle(String response) {
                if (response == null) {
                   displayMessage("Server connection error");

                    delayedReset.play();
                    return;
                }

                System.out.println(response);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    displayMessage("Invalid server response");

                    e.printStackTrace();

                    delayedReset.play();
                    return;
                }

                if (!jsonObject.has("success") || !(boolean) jsonObject.get("success")) {
                    displayMessage(jsonObject.has("message") ? (String)jsonObject.get("message") : "Login failure");

                    delayedReset.play();
                    return;
                }

                authenticated.call(new User((JSONObject)jsonObject.get("data")));
               // JSONArray parentArray = jsonObject.getJSONArray("Data");
               // JSONObject finalObject = parentArray.getJSONObject(0);

            }
        });


    }

    private void resetForm() {
        userNameField.setDisable(false);
        passwordField.setDisable(false);

        displayNode(authenticateButton);
    }

    private void displayMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setOpacity(1);

        messageFade.play();
    }


}
