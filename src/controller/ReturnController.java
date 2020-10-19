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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.MemberTM;
import util.ReturnTM;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnController {
    public JFXTextField txtTodayDate;
    public JFXTextField txtFee;
    public JFXTextField txtBorrowedDate;
    public TableView<ReturnTM> tblReturn;
    public JFXComboBox<MemberTM> cmbNic;
    public JFXButton btnReturn;
    public JFXButton btnCancel;
    public AnchorPane root;
    public ImageView img_NewReturn;
    public ImageView imgBack;
    public void initialize(){
        FadeTransition ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        /*LocalDate date=LocalDate.now();
        txtTodayDate.setText(date.toString());
        */
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        txtTodayDate.setText(dateFormat.format(date));

        tblReturn.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        tblReturn.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblReturn.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblReturn.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("author"));
        tblReturn.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));

        loadNICs();
        cmbNic.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MemberTM>() {
            @Override
            public void changed(ObservableValue<? extends MemberTM> observable, MemberTM oldValue, MemberTM newValue) {
                if(newValue == null){
                    txtBorrowedDate.clear();
                    return;
                }
                loadReturnTable();
                txtBorrowedDate.clear();
                txtFee.clear();
            }
        });
        tblReturn.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ReturnTM>() {
            @Override
            public void changed(ObservableValue<? extends ReturnTM> observable, ReturnTM oldValue, ReturnTM newValue) {
                if (newValue == null) {
                    return;
                }
                txtBorrowedDate.setText(tblReturn.getSelectionModel().getSelectedItem().getBorrowedDate());
                calculateFee();
            }
        });
    }
    public void img_NewReturn_OnMouseClicked(MouseEvent mouseEvent) {
        cmbNic.getSelectionModel().clearSelection();
        txtBorrowedDate.clear();
        txtFee.clear();
        btnReturn.setDisable(false);
        btnCancel.setDisable(false);
        tblReturn.getItems().clear();
        tblReturn.refresh();
    }

    public void btnReturn_OnAction(ActionEvent actionEvent) {
        String returnedDate = txtTodayDate.getText();
        String borrowedId = tblReturn.getSelectionModel().getSelectedItem().getBorrowId();
        int affected = 0;
        ObservableList<ReturnTM> returnedBookDetails = tblReturn.getItems();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("INSERT INTO `Return` VALUES (?,?)");
            for(ReturnTM returnedBooks : returnedBookDetails){
                pstm.setObject(1,borrowedId);
                pstm.setObject(2,returnedDate);
                affected = pstm.executeUpdate();
            }
            if(affected == 0){
                new Alert(Alert.AlertType.ERROR,"Couldn't insert to Return table, Select Books", ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.INFORMATION,"Added Successfully",ButtonType.OK).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnCancel_OnAction(ActionEvent actionEvent) {
        img_NewReturn.requestFocus();
        cmbNic.getSelectionModel().clearSelection();
        txtBorrowedDate.clear();
        txtFee.clear();
        ReturnTM selectedItem = tblReturn.getSelectionModel().getSelectedItem();
        tblReturn.getItems().remove(selectedItem);
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
    @SuppressWarnings(value = "Duplicates")
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

    public void loadReturnTable(){
        String nic = cmbNic.getSelectionModel().getSelectedItem().toString();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                    ("SELECT br.borrow_id,br.isbn,b.title,b.author,br.date FROM Book b INNER JOIN Borrow br ON b.isbn = br.isbn\n" +
                            "    LEFT OUTER JOIN `Return` r ON br.borrow_id = r.borrow_id  WHERE br.nic=? AND r.borrow_id IS NULL;");
            pstm.setObject(1,nic);
            ResultSet rst = pstm.executeQuery();
            ObservableList<ReturnTM> returnBooks = tblReturn.getItems();
            returnBooks.clear();
            while(rst.next()) {
                String borrowId = rst.getString("borrow_id");
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                String date = rst.getString("date");
                returnBooks.add(new ReturnTM(borrowId,isbn,title,author,date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void calculateFee() {
        String borrowedDate = txtBorrowedDate.getText();
        String returnedDate = txtTodayDate.getText();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat myFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateBefore = myFormat.parse(borrowedDate);
            Date dateAfter = myFormat.parse(returnedDate);
            int difference = dateAfter.getDate() - dateBefore.getDate();
            //long difference = Math.abs(dateAfter.getTime() - dateBefore.getTime());
            //int daysBetween = (int)(difference / (24 * 60 * 60 * 1000));
            //int daysBetween = (int)TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
            //System.out.println(daysBetween);
            if (difference > 7) {
                if(difference<=7){
                    difference=7;
                }else if(difference<=14){
                    difference=14;
                }else if(difference<=21){
                    difference=21;
                }else if(difference<=28){
                    difference=28;
                }else if(difference>28){
                    difference=30;
                }
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement
                        ("SELECT fee FROM Extra where returnedDay=?");
                pstm.setObject(1,difference);
                ResultSet rst = pstm.executeQuery();
                double fee=0.0;
                while (rst.next()) {
                    fee = rst.getDouble("fee");
                }
                if (difference >= 7) {
                    txtFee.setText(Double.toString(fee));
                } else if (difference >= 14) {
                    txtFee.setText(Double.toString(fee));
                } else if (difference >= 21) {
                    txtFee.setText(Double.toString(fee));
                } else if (difference >= 28) {
                    txtFee.setText(Double.toString(fee));
                } else if(difference >= 30){
                    fee = fee + (fee * (difference - 30));
                    txtFee.setText(Double.toString(fee));
                }
            } else {
                txtFee.setText("N/A");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void cmbNic_OnKeyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ENTER){
            loadNICs();
        }
    }
}
