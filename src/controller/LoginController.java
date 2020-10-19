package controller;

import DBConnect.DBConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    public AnchorPane root;
    public JFXButton btnLogin;
    public JFXTextField txtUsername;
    public JFXPasswordField txtPassword;

    public void initialize(){
        Platform.runLater(() -> {
            txtUsername.requestFocus();
        });
    }
    public void btnLogin_OnAction(ActionEvent actionEvent) {
        Login();
    }


    public void txtUsername_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER){
            txtPassword.requestFocus();
        }
    }

    public void txtPassword_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER){
            btnLogin.requestFocus();
        }
    }

    public void btnLogin_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER){
            Login();
        }
    }

    private void Login() {
        try {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            //Statement stm = connection.createStatement();
            //ResultSet rst = stm.executeQuery("SELECT * FROM User WHERE username='" + username + "' AND password='" + password + "'");
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM User WHERE userName=? AND password=?");
            pstm.setObject(1, username);
            pstm.setObject(2, password);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()){
                Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
                Scene mainScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(mainScene);
                primaryStage.centerOnScreen();
            }else{
                new Alert(Alert.AlertType.ERROR,"Invalid username or password, please try again", ButtonType.OK).show();
                txtUsername.requestFocus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
