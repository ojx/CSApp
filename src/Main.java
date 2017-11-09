import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Optional;


public class Main extends Application {
    private static final String TITLE = "Computer Science";
    private static final String STYLE_SHEET = "styles.css";
    private static final String LOGOUT_SCRIPT_URL = "https://mrjonescs.com/csapp/kill.php";

    private User user;
    private BorderPane contentBorderPane;
    private GridPane pageToolBarPane;
    private ToolBar pageToolBarLeft, pageToolBarRight;
    private Stage stage;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        stage.setTitle(TITLE);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());

        contentBorderPane = new BorderPane();

        scene = new Scene(contentBorderPane);
        scene.getStylesheets().add(STYLE_SHEET);


        stage.setScene(scene);
        stage.setResizable(false);

        pageToolBarPane = new GridPane();

        Button logOutButton = new Button("Log Out");
        logOutButton.setGraphic(new ImageView(new Image("imgs/logout.png")));
        logOutButton.setOnAction(e->confirmLogOut());

        Button quitButton = new Button("Quit");
        quitButton.setGraphic(new ImageView(new Image("imgs/quit.png")));
        quitButton.setOnAction(e->confirmQuit());

        pageToolBarLeft = new ToolBar(new MenuButton("New Program"));
        pageToolBarRight = new ToolBar(logOutButton, quitButton);

        pageToolBarPane.add(pageToolBarLeft,0,0);
        pageToolBarPane.add(pageToolBarRight,1,0);

        GridPane.setHalignment(pageToolBarLeft, HPos.LEFT);
        GridPane.setHalignment(pageToolBarRight, HPos.RIGHT);
        GridPane.setHgrow(pageToolBarLeft, Priority.ALWAYS);

        login();

        stage.setOnCloseRequest(e -> {
            if (contentBorderPane.getTop() != null) {
                e.consume();
                confirmQuit();
            }
        });

        stage.show();
    }

    private void confirmLogOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you wish to logout?", ButtonType.YES,
                ButtonType.NO);

        alert.setTitle("Logout");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            logout(false);
        }
    }

    private void confirmQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you wish to quit?", ButtonType.YES,
                ButtonType.NO);

        alert.setTitle("Quit Application");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            logout(true);
        }
    }

    private void login() {
        Callback<User, Void> cb = authenticatedUser -> {
            user = authenticatedUser;
            loginSuccessful();
            return null;
        };

        showScreen(new Login(cb));
    }

    private void logout(boolean quit) {
        removePageToolBar();
        user = null;

        HBox hBox = new HBox(new Label("Logging out . . ."));
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(new ProgressIndicator(), hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(25);

        contentBorderPane.setCenter(vBox);
        HttpRequestTask.get(LOGOUT_SCRIPT_URL, new HttpResponseHandler() {
                    @Override
                    public void handle(String response) {
                        System.out.println("-->"+response);
                        if (quit) {
                            try {
                                stop();
                            } catch (Exception e) {
                                System.exit(0);
                            }
                        } else {
                            login();
                        }
                    }
                });

    }

    public void loginSuccessful() {
        stage.setTitle("Computer Science - " + user.getDisplayName());
        addPageToolBar();
        contentBorderPane.setCenter(null);
    }

    private void showScreen(Pane screen) {
        contentBorderPane.setCenter(screen);
    }

    private void addPageToolBar() {
        contentBorderPane.setTop(pageToolBarPane);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Welcome " + user.getFirstName());
        alert.show();
    }

    private void removePageToolBar() {
        contentBorderPane.setTop(null);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }
}
