package controller;

import DBConnect.DBConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.BookTM;
import util.BorrowTM;
import util.MemberTM;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BorrowController {
    public AnchorPane root;
    public ImageView imgNewBorrow;
    public JFXButton btnBorrow;
    public JFXButton btnSave;
    public JFXButton btnCancel;
    public JFXTextField txtDate1;
    public JFXTextField txtMemberName;
    public JFXTextField txtBorrowId;
    public JFXTextField txtBookTitle;
    public JFXTextField txtAuthor;
    public JFXTextField txtTotal;
    public JFXComboBox<BookTM> cmbIsbn;
    public JFXComboBox<MemberTM> cmbNic;
    public TableView<BorrowTM> tblBorrow;

    public void initialize(){
        FadeTransition ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        txtDate1.setText(dateFormat.format(date));
        generateBorrowId();

        tblBorrow.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        tblBorrow.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblBorrow.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblBorrow.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("author"));


        loadNICs();
        cmbNic.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MemberTM>() {
            @Override
            public void changed(ObservableValue<? extends MemberTM> observable, MemberTM oldValue, MemberTM newValue) {
                if(newValue == null){
                    txtMemberName.clear();
                    return;
                }
                txtMemberName.setText(newValue.getName());
            }
        });
        loadISBNs();
        cmbIsbn.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookTM>() {
            @Override
            public void changed(ObservableValue<? extends BookTM> observable, BookTM oldValue, BookTM newValue) {
                if(newValue == null){
                    txtBorrowId.clear();
                    txtBookTitle.clear();
                    txtAuthor.clear();
                    btnSave.setDisable(true);
                    return;
                }
                //To check availability of the book
                isAvailable();
                txtBookTitle.setText(newValue.getBookTitle());
                txtAuthor.setText(newValue.getAuthor());
                btnSave.setText("SAVE");
            }

        });
        tblBorrow.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BorrowTM>() {
            @Override
            public void changed(ObservableValue<? extends BorrowTM> observable, BorrowTM oldValue, BorrowTM selectedBook) {
                BorrowTM selectedItem = tblBorrow.getSelectionModel().getSelectedItem();

                if (selectedBook == null) {
                    return;
                }
                String selectedIsbn = selectedBook.getIsbn();
                ObservableList<BookTM> items = cmbIsbn.getItems();
                for (BookTM book : items) {
                    if (book.getIsbn().equals(selectedIsbn)) {
                        cmbIsbn.getSelectionModel().select(book);
                        txtBookTitle.setText(book.getBookTitle() + "");
                        txtAuthor.setText(book.getAuthor() + "");
                    }
                }

            }
        });
    }
    public void imgNewBorrow_OnMouseClicked(MouseEvent mouseEvent) {
        btnSave.setText("SAVE");
        cmbIsbn.getSelectionModel().clearSelection();
        txtTotal.clear();
        cmbNic.getSelectionModel().clearSelection();
        txtMemberName.clear();
        btnSave.setDisable(false);
        btnCancel.setDisable(false);
        tblBorrow.getItems().clear();
        tblBorrow.refresh();
        generateBorrowId();
    }

    public void btnBorrow_OnAction(ActionEvent actionEvent) {
        String borrowedDate = txtDate1.getText();
        String nic = cmbNic.getSelectionModel().getSelectedItem().toString();
        int affected = 0;
        ObservableList<BorrowTM> borrowedbookDetails = tblBorrow.getItems();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("INSERT INTO Borrow VALUES (?,?,?,?)");
            for(BorrowTM borrowedBooks : borrowedbookDetails){
                pstm.setObject(1,borrowedBooks.getBorrowId());
                pstm.setObject(2,nic);
                pstm.setObject(3,borrowedBooks.getIsbn());
                pstm.setObject(4,borrowedDate);
                affected = pstm.executeUpdate();
            }
            if(affected == 0){
                new Alert(Alert.AlertType.ERROR,"Couldn't insert to Borrow table, Select Books",ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.INFORMATION,"Added Successfully",ButtonType.OK).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        generateBorrowId();
    }

    public void btnCancel_OnAction(ActionEvent actionEvent) {
        imgNewBorrow.requestFocus();
        cmbNic.getSelectionModel().clearSelection();
        txtMemberName.clear();
        cmbIsbn.getSelectionModel().clearSelection();
        txtBorrowId.clear();
        txtBookTitle.clear();
        txtAuthor.clear();
        txtTotal.clear();
        tblBorrow.getItems().clear();
    }
    @SuppressWarnings("Duplicates")
    public void imgDelete_OnMouseClicked(MouseEvent mouseEvent) {
        String borrowID = txtBorrowId.getText();
        String isbn = cmbIsbn.getSelectionModel().getSelectedItem().toString();
        String title = txtBookTitle.getText();
        String author = txtAuthor.getText();
        ObservableList<BorrowTM> books = tblBorrow.getItems();
        books.remove(tblBorrow.getSelectionModel().getSelectedItem());
        tblBorrow.refresh();


        if(tblBorrow.getSelectionModel().getSelectedIndex() == -1){
            cmbIsbn.getSelectionModel().clearSelection();
            txtBookTitle.clear();
            txtAuthor.clear();
            generateBorrowId();
        }
        if (books.get(books.size() - 1).equals(-1)){
            generateBorrowId();
        }else {
            BorrowTM lastBook = books.get(books.size() - 1);
            String lastId = lastBook.getBorrowId();
            String number = lastId.substring(1, 4);
            int newId = Integer.parseInt(number) + 1;

            if (newId < 10) {
                txtBorrowId.setText("B00" + newId);
            } else if (newId < 100) {
                txtBorrowId.setText("B0" + newId);
            } else {
                txtBorrowId.setText("O" + newId);
            }
        }
    }
    @SuppressWarnings("Duplicates")
    public void btnSave_OnAction(ActionEvent actionEvent) {
        if (cmbIsbn.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "You need to select a book", ButtonType.OK).show();
            cmbIsbn.requestFocus();
            return;
        }
        String borrowedDate = txtDate1.getText();
        String isbn = cmbIsbn.getSelectionModel().getSelectedItem().toString();
        String borrowId = txtBorrowId.getText();
        String title = txtBookTitle.getText();
        String author = txtAuthor.getText();

        if(btnSave.getText().equals("SAVE")) {
            ObservableList<BorrowTM> books = tblBorrow.getItems();
            books.add(new BorrowTM(borrowId, isbn, title, author));

            BorrowTM lastOrder = books.get(books.size()-1);
            String lastId=lastOrder.getBorrowId();
            String number=lastId.substring(1,4);
            int newId = Integer.parseInt(number)+1;
            if(newId<10){
                txtBorrowId.setText("B00" + newId);
            }else if(newId<100){
                txtBorrowId.setText("B0" +newId);
            }else{
                txtBorrowId.setText("O"+newId);
            }
        }
        calculateTotalBooks();
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

    public void generateBorrowId(){
        int maxId = 0;
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("SELECT * FROM Borrow ORDER BY borrow_id DESC LIMIT 1");
            ResultSet rst = pstm.executeQuery();
            while(rst.next()) {
                String borrowID = rst.getString("borrow_id");
                int id = Integer.parseInt(borrowID.replace("B",""));
                if(maxId<id){
                    maxId=id;
                }
                maxId=maxId+1;
                String newId = "";
                if(maxId<10){
                    newId="B00"+maxId;
                }else if(maxId<100){
                    newId="B0"+maxId;
                }else{
                    newId="B"+maxId;
                }
                txtBorrowId.setText(newId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("Duplicates")
    public void loadISBNs(){
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("SELECT * FROM Book");
            ResultSet rst = pstm.executeQuery();
            ObservableList<BookTM> isbns = cmbIsbn.getItems();
            isbns.clear();
            while(rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                String edition = rst.getString("edition");
                isbns.add(new BookTM(isbn,title,author,edition));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("Duplicates")
    public void loadNICs(){
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("SELECT * FROM Member");
            ResultSet rst = pstm.executeQuery();
            ObservableList<MemberTM> members = cmbNic.getItems();
            members.clear();
            while(rst.next()) {
                String nic = rst.getString("nic");
                String name = rst.getString("name");
                String address = rst.getString("address");
                members.add(new MemberTM(nic,name,address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void calculateTotalBooks(){
        ObservableList<BorrowTM> borrowedBooks = tblBorrow.getItems();
        int total = borrowedBooks.size();
        if(total > 5){
            new Alert(Alert.AlertType.ERROR,"Cannot borrow more than 5 books",ButtonType.OK).show();
            return;
        }else {
            txtTotal.setText(Integer.toString(total));
        }
    }
    public void isAvailable() {
        String isbn = cmbIsbn.getSelectionModel().getSelectedItem().toString();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("SELECT b.isbn\n" +
                            "FROM Borrow b LEFT OUTER JOIN `Return` r ON b.borrow_id=r.borrow_id " +
                            "WHERE r.borrow_id IS NULL AND b.isbn=?");
            pstm.setObject(1, isbn);
            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                String isbn1 = rst.getString("isbn");
                new Alert(Alert.AlertType.ERROR,"The Book is currently Unavailable",ButtonType.OK).show();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

