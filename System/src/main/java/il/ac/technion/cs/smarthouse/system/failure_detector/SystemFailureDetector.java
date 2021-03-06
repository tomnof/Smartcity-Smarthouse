package il.ac.technion.cs.smarthouse.system.failure_detector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.system.SystemMode;
import il.ac.technion.cs.smarthouse.utils.Communicate;
import il.ac.technion.cs.smarthouse.utils.JavaFxHelper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Enables a failure detector
 * 
 * @author Yarden
 * @since 14-06-2017
 */
public enum SystemFailureDetector {
    ;
    static final Logger log = LoggerFactory.getLogger(SystemFailureDetector.class);

    private static boolean isExceptionFromHere(Throwable e) {
        return Stream.of(e.getStackTrace()).filter(c -> SystemFailureDetector.class.getName().equals(c.getClassName())
                        || ErrorPopupApp.class.getName().equals(c.getClassName())).count() > 0;
    }

    private static String logException(Thread t, Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null)
            cause = cause.getCause();

        final String errTxt = "Uncaught exception from thread [" + t.getName() + "]\nException: " + e.toString()
                        + "\nCause: " + cause.toString();
        log.error(errTxt, e);
        return errTxt;
    }

    public static void enable(final SystemMode m) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            final String errTxt = logException(t, e);

            if (JavaFxHelper.isJavaFxThreadStarted())
                if (!isExceptionFromHere(e))
                    JavaFxHelper.startGui(new ErrorPopupApp(t, e, errTxt, true), false);
                else {
                    JavaFxHelper.closeAllOpenedStages();
                    JavaFxHelper.startGui(new ErrorPopupApp(t, e, errTxt, false), false);
                }
        });
    }

    private static class ErrorPopupApp extends Application {
        private static Alert oldAlert;

        @SuppressWarnings("unused") final Thread t;
        final Throwable e;
        final String errTxt;
        final boolean enableTryToRecover;

        public ErrorPopupApp(final Thread t, final Throwable e, final String errTxt, boolean enableTryToRecover) {
            this.t = t;
            this.e = e;
            this.errTxt = errTxt;
            this.enableTryToRecover = enableTryToRecover;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            if (oldAlert != null && oldAlert.isShowing()) {
                oldAlert.close();
                oldAlert = null;
            }

            ButtonType reportType = new ButtonType("Send Report", ButtonBar.ButtonData.LEFT);
            ButtonType tryToRecover = new ButtonType("Try to Recover", ButtonBar.ButtonData.LEFT);
            Alert a = new Alert(AlertType.ERROR, errTxt, reportType, tryToRecover, ButtonType.CLOSE);
            oldAlert = a;
            if (!enableTryToRecover)
                a.getButtonTypes().remove(tryToRecover);
            a.getDialogPane().setPrefSize(420, 250);
            a.setTitle("Uncaught exception");
            a.showAndWait().ifPresent(response -> {
                if (response == reportType || response == tryToRecover) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.setTitle("Error Report");
                    TextArea userInput = new TextArea();
                    Text text = new Text("Please describe what happened when the error occurred (optional):");
                    Button reportButton = new Button("Send");
                    reportButton.setOnMouseClicked(event -> {
                        String input = userInput.getText();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        Communicate.throughEmailFromHere("smarthouse5777@gmail.com",
                                        "We got a new report...\n\n" + errTxt + "\n\n"
                                                        + (input == null || "".equals(input)
                                                                        ? "The user did not add a description of the error."
                                                                        : "User Description:\n" + input)
                                                        + "\n\nStack Trace:\n" + sw.toString());
                        ((Stage) reportButton.getScene().getWindow()).close();
                    });
                    Pane pane1 = new Pane();
                    Pane pane2 = new Pane();
                    HBox hbox1 = new HBox(text, pane1);
                    hbox1.setPadding(new Insets(0, 0, 5, 0));
                    HBox hbox2;
                    if (response != tryToRecover)
                        hbox2 = new HBox(pane2, reportButton);
                    else {
                        Button skipButton = new Button("Skip");
                        skipButton.setOnMouseClicked(event -> ((Stage) skipButton.getScene().getWindow()).close());
                        hbox2 = new HBox(pane2, reportButton, skipButton);
                    }
                    hbox2.setPadding(new Insets(5, 0, 0, 0));
                    hbox2.setSpacing(5);
                    HBox.setHgrow(pane1, Priority.ALWAYS);
                    HBox.setHgrow(pane2, Priority.ALWAYS);
                    VBox vbox = new VBox(hbox1, userInput, hbox2);
                    VBox.setVgrow(userInput, Priority.ALWAYS);
                    vbox.setPadding(new Insets(15));

                    dialogStage.setScene(new Scene(vbox));
                    dialogStage.showAndWait();

                }

                if (response != tryToRecover)
                    System.exit(-1);

            });
        }

    }
}
