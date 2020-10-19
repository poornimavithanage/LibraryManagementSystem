package controller;

import DBConnect.DBConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.BookTM;


import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManageBookController {
    public AnchorPane root;
    public TableView<BookTM> tblBooks;
    public JFXTextField txtEdition;
    public JFXTextField txtTitle;
    public JFXButton btnSave;
    public JFXButton btnCancel;
    public ImageView imgNewBook;
    public JFXTextField txtIsbn;
    public JFXTextField txtAuthor;

    public void initialize(){
        FadeTransition ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        tblBooks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblBooks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        tblBooks.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tblBooks.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("edition"));
        loadBooks();

        tblBooks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookTM>() {
            @Override
            public void changed(ObservableValue<? extends BookTM> observable, BookTM oldValue, BookTM newValue) {
                BookTM selectedItem = tblBooks.getSelectionModel().getSelectedItem();

                if(selectedItem==null){
                    btnCancel.setDisable(true);
                    txtIsbn.clear();
                    txtTitle.clear();
                    txtAuthor.clear();
                    txtEdition.clear();
                    return;
                }
                btnSave.setText("UPDATE BOOK");
                btnSave.setDisable(false);
                btnCancel.setDisable(false);
                txtTitle.setDisable(false);
                txtIsbn.setDisable(false);
                txtAuthor.setDisable(false);
                txtEdition.setDisable(false);
                txtIsbn.setText(selectedItem.getIsbn());
                txtTitle.setText(selectedItem.getBookTitle());
                txtAuthor.setText(selectedItem.getAuthor());
                txtEdition.setText(selectedItem.getEdition());
            }
        });
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        String isbn = txtIsbn.getText();
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String edition = txtEdition.getText();
        if(btnSave.getText().equals("SAVE BOOK")) {
            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO Book VALUES (?,?,?,?)");
                pstm.setObject(1, isbn);
                pstm.setObject(2, title);
                pstm.setObject(3, author);
                pstm.setObject(4,edition);
                int affected = pstm.executeUpdate();

                if (affected > 0) {
                    loadBooks();
                    tblBooks.getSelectionModel().clearSelection();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save information", ButtonType.OK).show();
                    txtIsbn.requestFocus();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("UPDATE Book SET isbn=?,title=?,author=?,edition=? WHERE isbn ='"+isbn+"'");
                pstm.setObject(1, isbn);
                pstm.setObject(2, title);
                pstm.setObject(3, author);
                pstm.setObject(4,edition);
                int affected = pstm.executeUpdate();
                if(affected>0){
                    new Alert(Alert.AlertType.CONFIRMATION, "Updated Successfully", ButtonType.OK).show();
                }
                loadBooks();
                tblBooks.getSelectionModel().clearSelection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadBooks() {
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("Select * from Book");
            ObservableList<BookTM> books = tblBooks.getItems();
            books.clear();
            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                String edition = rst.getString("edition");
                books.add(new BookTM(isbn, title, author,edition));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void imgBack_OnMouseClicked(MouseEvent mouseEvent) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene mainScene = new Scene(root);
            Stage mainStage = (Stage)this.root.getScene().getWindow();
            mainStage.setScene(mainScene);
            mainStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imgNewBook_OnMouseClicked(MouseEvent mouseEvent) {
        btnSave.setText("SAVE BOOK");
        txtIsbn.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtEdition.clear();
        btnSave.setDisable(false);
        btnCancel.setDisable(false);
        loadBooks();
    }

    public void imgDelete_OnAction(MouseEvent mouseEvent) {
        String isbn = txtIsbn.getText();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("DELETE  FROM Book WHERE isbn=?");
            pstm.setObject(1,isbn);
            int affected = pstm.executeUpdate();
            if(affected>0){
                new Alert(Alert.AlertType.CONFIRMATION, "Deleted Successfully", ButtonType.OK).show();
            }
            loadBooks();
            tblBooks.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        btnSave.setText("SAVE BOOK");
    }

    public void btnCancel_OnAction(ActionEvent actionEvent) {
        imgNewBook.requestFocus();
        txtIsbn.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtEdition.clear();
        loadBooks();
    }
}

