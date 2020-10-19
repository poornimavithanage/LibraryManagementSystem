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
import util.MemberTM;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManageMemberController {
    public TextField txtMemberID;
    public TextField txtMemberName;
    public AnchorPane root;
    public TableView<MemberTM> tblMembers;
    public JFXButton btnSave;
    public JFXTextField txtMemberAddress;
    public ImageView imgNewMember;
    public JFXButton btnCancel;

    public void initialize(){
        FadeTransition ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        tblMembers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("nic"));
        tblMembers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblMembers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        loadMembers();

        tblMembers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MemberTM>() {
            @Override
            public void changed(ObservableValue<? extends MemberTM> observable, MemberTM oldValue, MemberTM newValue) {
                MemberTM selectedItem = tblMembers.getSelectionModel().getSelectedItem();

                if(selectedItem==null){
                    btnCancel.setDisable(true);
                    txtMemberID.clear();
                    txtMemberName.clear();
                    txtMemberAddress.clear();
                    return;
                }
                btnSave.setText("UPDATE MEMBER");
                btnSave.setDisable(false);
                btnCancel.setDisable(false);
                txtMemberName.setDisable(false);
                txtMemberAddress.setDisable(false);
                txtMemberID.setText(selectedItem.getNic());
                txtMemberName.setText(selectedItem.getName());
                txtMemberAddress.setText(selectedItem.getAddress());
            }
        });
    }


    public void loadMembers(){
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("Select * from Member");
            ObservableList<MemberTM> members = tblMembers.getItems();
            members.clear();
            while(rst.next()){
                String id = rst.getString("nic");
                String name = rst.getString("name");
                String address = rst.getString("address");
                members.add(new MemberTM(id,name,address));
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

    public void imgNewMember_OnMouseClicked(MouseEvent mouseEvent) {
        btnSave.setText("SAVE MEMBER");
        txtMemberName.clear();
        txtMemberAddress.clear();
        btnSave.setDisable(false);
        btnCancel.setDisable(false);

    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        String nic = txtMemberID.getText();
        String name = txtMemberName.getText();
        String address = txtMemberAddress.getText();
        if(btnSave.getText().equals("SAVE MEMBER")) {
            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO Member VALUES (?,?,?)");
                pstm.setObject(1, nic);
                pstm.setObject(2, name);
                pstm.setObject(3, address);
                int affected = pstm.executeUpdate();

                if (affected > 0) {
                    loadMembers();
                    tblMembers.getSelectionModel().clearSelection();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save information", ButtonType.OK).show();
                    txtMemberName.requestFocus();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("UPDATE Member SET nic=?,name=?,address=? WHERE nic ='"+nic+"'");
                pstm.setObject(1, nic);
                pstm.setObject(2, name);
                pstm.setObject(3, address);
                int affected = pstm.executeUpdate();
                if(affected>0){
                    new Alert(Alert.AlertType.CONFIRMATION, "Updated Successfully", ButtonType.OK).show();
                }
                loadMembers();
                tblMembers.getSelectionModel().clearSelection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void imgDelete_OnMouseClicked(MouseEvent mouseEvent) {
        String nic = txtMemberID.getText();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("DELETE  FROM Member WHERE nic=?");
            pstm.setObject(1,nic);
            int affected = pstm.executeUpdate();
            if(affected>0){
                new Alert(Alert.AlertType.CONFIRMATION, "Deleted Successfully", ButtonType.OK).show();
            }
            loadMembers();
            tblMembers.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        btnSave.setText("SAVE MEMBER");
    }

    public void btnCancel_OnAction(ActionEvent actionEvent) {
        imgNewMember.requestFocus();
        txtMemberID.clear();
        txtMemberName.clear();
        txtMemberAddress.clear();
    }
}
