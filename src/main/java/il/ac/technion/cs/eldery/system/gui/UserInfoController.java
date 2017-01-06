package il.ac.technion.cs.eldery.system.gui;

import java.net.URL;
import java.util.ResourceBundle;

import il.ac.technion.cs.eldery.system.EmergencyLevel;
import il.ac.technion.cs.eldery.system.userInformation.Contact;
import il.ac.technion.cs.eldery.system.userInformation.UserInformation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class UserInfoController implements Initializable {

    private UserInformation user;

    @FXML public TextField userNameField;
    @FXML public TextField userIDField;
    @FXML public TextField userPhoneNumField;
    @FXML public TextField userHomeAddressField;
    @FXML public Button userSaveField;

    @FXML public TextField addNameField;
    @FXML public TextField addIDField;
    @FXML public TextField addPhoneField;
    @FXML public TextField addEmailField;
    @FXML public ChoiceBox<EmergencyLevel> addELevelField;
    @FXML public Button saveButton;

    private static boolean validateUserInput(String name, String id, String phone, String address) {
        return name != null && id != null && phone != null && address != null && !"".equals(name) && !"".equals(id) && !"".equals(phone)
                && !"".equals(address) && name.chars().allMatch(Character::isLetter) && id.chars().allMatch(Character::isDigit)
                && phone.chars().allMatch(Character::isDigit);
    }

    @Override public void initialize(URL arg0, ResourceBundle arg1) {

        addELevelField.getItems().addAll(EmergencyLevel.CALL_EMERGENCY_CONTACT, EmergencyLevel.SMS_EMERGENCY_CONTACT, EmergencyLevel.NOTIFY_ELDERLY,
                EmergencyLevel.CONTACT_POLICE, EmergencyLevel.CONTACT_FIRE_FIGHTERS, EmergencyLevel.CONTACT_HOSPITAL);

        userSaveField.setOnAction(event -> {

            final String name = userNameField.getText();
            final String id = userIDField.getText();
            final String phoneNum = userPhoneNumField.getText();
            final String address = userHomeAddressField.getText();

            if (validateUserInput(name, id, phoneNum, address)) {
                user = new UserInformation(name, id, phoneNum, address);
                userNameField.setEditable(false);
                userIDField.setEditable(false);
            } else {
                final Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Bad Input");
                alert.setContentText("Make sure to enter only valid names and phone numbers");
                alert.showAndWait();
            }
        });

        saveButton.setOnAction(event -> {
            final String name = addNameField.getText();
            final String id = addIDField.getText();
            final String phoneNum = addPhoneField.getText();
            final String email = addEmailField.getText();
            if (validateUserInput(name, id, phoneNum, email)) {
                user.addContact(new Contact(name, id, phoneNum, email), addELevelField.getValue());
                addNameField.clear();
                addIDField.clear();
                addPhoneField.clear();
                addEmailField.clear();
            } else {
                final Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Bad Input");
                alert.setContentText("Make sure to enter only valid names and phone numbers");
                alert.showAndWait();
            }
        });

    }

}