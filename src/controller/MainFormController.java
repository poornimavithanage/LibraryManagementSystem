package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainFormController {
    public AnchorPane root;
    public JFXButton btnMember;
    public JFXButton btnBook;
    public JFXButton btnBorrow;
    public JFXButton btnReturn;
    public AnchorPane subFormsAnchorPane;

    public void btnMember_OnAction(ActionEvent actionEvent) {
        loadMember();
    }

    public void btnBook_OnAction(ActionEvent actionEvent) {
        loadBooks();
    }

    public void btnBorrow_OnAction(ActionEvent actionEvent) {
        borrowBooks();
    }

    public void btnReturn_OnAction(ActionEvent actionEvent) {
        returnBooks();
    }

    public void btnMember_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER){
            loadMember();
        }
    }

    public void btnBook_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            loadBooks();
        }
    }

    public void btnBorrow_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            borrowBooks();
        }
    }

    public void btnReturn_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            returnBooks();
        }
    }

    private void loadMember() {
        try {
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/ManageMember.fxml"));
            subFormsAnchorPane.getChildren().setAll(pane);
        } catch (IOException e) {
            System.out.println("There's problem with loading the Manage member Form");
            e.printStackTrace();
        }
    }

    private void loadBooks() {
        try {
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/ManageBook.fxml"));
            subFormsAnchorPane.getChildren().setAll(pane);
        } catch (IOException e) {
            System.out.println("There's problem with loading the Manage Books Form");
            e.printStackTrace();
        }
    }

    private void borrowBooks() {
        try {
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/ManageBorrowBooks.fxml"));
            subFormsAnchorPane.getChildren().setAll(pane);
        } catch (IOException e) {
            System.out.println("There's problem with borrow book form");
            e.printStackTrace();
        }
    }

    private void returnBooks() {
        try {
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/ManageReturnBooks.fxml"));
            subFormsAnchorPane.getChildren().setAll(pane);
        } catch (IOException e) {
            System.out.println("There's problem with return book form");
            e.printStackTrace();
        }
    }

}
