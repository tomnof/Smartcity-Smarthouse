package il.ac.technion.cs.eldery.sensors.simulators.stove.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/** @author Sharon
 * @since 9.12.16 */
public class Controller implements Initializable {
  @FXML public Button onOffButton;
  @FXML public Label tempLabel;
  @FXML public Slider tempSlider;

  @Override public void initialize(URL location, ResourceBundle __) {
    // TODO: Sharon implement controller and add XML javafx file
  }
}
