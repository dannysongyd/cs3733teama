package com.teama.controllers_refactor;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.login.AccessType;
import com.teama.login.LoginSubsystem;
import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StaffPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    private InterpreterStaff staffToInsert;
    @FXML
    private JFXButton btnAdd;

    @FXML
    private TableView<InterpreterTableAdapter> InterpInfoTable;
    @FXML
    private TableColumn<InterpreterTableAdapter,String> firstCol, lastCol, langCol;
    @FXML
    private TableColumn<InterpreterTableAdapter,String> certCol, phoneCol, emailCol;
    @FXML
    private JFXCheckBox English;

    @FXML
    private JFXCheckBox German;

    @FXML
    private JFXCheckBox French;

    @FXML
    private JFXCheckBox Spanish;

    @FXML
    private JFXCheckBox Russian;

    @FXML
    private JFXCheckBox Cantonese;

    @FXML
    private JFXCheckBox Luxembourgish;

    @FXML
    private JFXCheckBox Moldovan;

    @FXML
    private JFXCheckBox Ukranian;

    @FXML
    private JFXCheckBox ASL;

    @FXML
    private JFXTextField FirstName;

    @FXML
    private JFXTextField Email;

    @FXML
    private JFXTextField PhoneNo;

    @FXML
    private JFXTextField LastName;

    @FXML
    private JFXComboBox<CertificationType> Certifications;

    @FXML
    private JFXComboBox<Provider> Providers;

    @FXML
    private JFXButton Cancel;

    @FXML
    private JFXButton btnSubmit;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private ArrayList<JFXCheckBox> languageBoxList = new ArrayList<>();
    private ObservableList<InterpreterTableAdapter> tableVals;
    private Boolean editing;
    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);
        initComboBoxes();
        initForm();
        initInterpColumns();
        LoginSubsystem login = LoginSubsystem.getInstance();
        if(login.getSystemUser().getAccess()!= AccessType.ADMIN){
            disableEditor();
        }
        editing =false;
    }
    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @Override
    public void onClose() {
        System.out.println("CLOSE STAFF");
    }

    @Override
    public String getFXMLPath() {
        return "/StaffPopOut.fxml";
    }

    //START OF STAFF STUFF
    @FXML
    private void onAddStaff(ActionEvent event){

    }

    private void popUpInterpInfo(InterpreterStaff staff){
        setInterpreter(staff);
    }

    private void initInterpColumns(){
        firstCol.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        lastCol.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));
        langCol.setCellValueFactory(
                new PropertyValueFactory<>("languages"));
        certCol.setCellValueFactory(
                new PropertyValueFactory<>("certification"));
        phoneCol.setCellValueFactory(
                new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(
                new PropertyValueFactory<>("email"));
        tableVals =  FXCollections.observableArrayList();
        for(InterpreterStaff interp: getInterpreterStaff()){
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
        InterpInfoTable.setRowFactory(tv -> {
            TableRow<InterpreterTableAdapter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty()) {
                    InterpreterTableAdapter clickedRow = row.getItem();
                    popUpInterpInfo(clickedRow.getInterpreter());
                    editing=true;
                }
            });
            return row ;
        });
    }
    //This is called by the add/modify popout to allow the list to update based on the DB
    public void updateInterpList(){
        System.out.println("updating list");
        tableVals =  FXCollections.observableArrayList();
        for(InterpreterStaff interp: getInterpreterStaff()){
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
    }
    private ArrayList<InterpreterStaff> getInterpreterStaff(){
        return InterpreterSubsystem.getInstance().getAllStaff();
    }

    @FXML
    private void deleteInterpeter(ActionEvent event){
        String firstName = staffToInsert.getFirstName();
        String lastName = staffToInsert.getLastName();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Interpreter");
        alert.setHeaderText("Remove Interpreter from database");
        alert.setContentText("Are your sure you want to delete \n"+firstName +" "
                +lastName+" from the database.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            InterpreterSubsystem.getInstance().removeStaff(staffToInsert.getStaffID());
            blankEditor();
        } else {
            alert.close();
        }
    }
    @FXML
    private void cancelInterpreter(ActionEvent event) {
        blankEditor();
    }
    @FXML
    private void submitInterpreter(ActionEvent e){
        try {
            Set<ContactInfoTypes> contactTypes = new HashSet<ContactInfoTypes>();
            contactTypes.add(ContactInfoTypes.EMAIL);
            contactTypes.add(ContactInfoTypes.TEXT);
            contactTypes.add(ContactInfoTypes.PHONE);
            System.out.println(Providers.getValue());
            Provider provider = Providers.getValue();
            String phoneNumber = PhoneNo.getText();
            String email = Email.getText();
            ContactInfo contactInfo = new ContactInfo(contactTypes, phoneNumber, email, provider);
            GenericStaffInfo staffInfo = new GenericStaffInfo(FirstName.getText(), LastName.getText(), contactInfo);
            Set<Language> langs = new HashSet<>();
            for (JFXCheckBox box : languageBoxList) {
                if (box.selectedProperty().get()) {
                    langs.add(Language.valueOf(box.getText()));
                }
            }
            CertificationType certification = Certifications.getSelectionModel().getSelectedItem();

            if (!editing) {
                InterpreterStaff interpreterStaff = new InterpreterStaff(staffInfo, new InterpreterInfo(langs, certification));
                InterpreterSubsystem.getInstance().addStaff(interpreterStaff);
            } else {
                InterpreterStaff interpreterStaff = new InterpreterStaff(staffInfo, new InterpreterInfo(staffToInsert.getStaffID(), langs, certification));
                InterpreterSubsystem.getInstance().updateStaff(interpreterStaff);
            }
        }
        catch(NullPointerException pointer){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid information");
            alert.setContentText("Invalid or empty field, please check information and \n submit again");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    private void initComboBoxes(){
        for(Provider provider : Provider.values()){
            Providers.getItems().add(provider);
        }
        for(CertificationType certification: CertificationType.values()){
            Certifications.getItems().add(certification);
        }
    }
    private void initForm(){
        if(languageBoxList.size()==0) {
            languageBoxList.add(English);
            languageBoxList.add(Spanish);
            languageBoxList.add(French);
            languageBoxList.add(German);
            languageBoxList.add(Russian);
            languageBoxList.add(Cantonese);
            languageBoxList.add(Luxembourgish);
            languageBoxList.add(Moldovan);
            languageBoxList.add(Ukranian);
            languageBoxList.add(ASL);
        }
        if(staffToInsert!=null) {
            for (Language language : staffToInsert.getLanguages()) {
                for (JFXCheckBox box : languageBoxList) {
                    if (box.getText().equals(language.toString())){
                        box.selectedProperty().setValue(true);
                    }
                }
            }
            FirstName.setText(staffToInsert.getFirstName());
            LastName.setText(staffToInsert.getLastName());
            Providers.setValue(staffToInsert.getProvider());
            Certifications.setValue(staffToInsert.getCertification());
            PhoneNo.setText(staffToInsert.getPhone());
            Email.setText(staffToInsert.getEmail());
        }

    }
    private void disableEditor(){
        English.setDisable(true); Spanish.setDisable(true); French.setDisable(true);
        German.setDisable(true); Russian.setDisable(true); Cantonese.setDisable(true);
        Luxembourgish.setDisable(true); Moldovan.setDisable(true); Moldovan.setDisable(true);
        Ukranian.setDisable(true); ASL.setDisable(true);
        FirstName.setEditable(false); LastName.setEditable(false);
        PhoneNo.setEditable(false); Providers.setDisable(true);
        Email.setEditable(false); Certifications.setDisable(true);
        btnSubmit.setVisible(false); btnDelete.setVisible(false);
    }
    public void setInterpreter(InterpreterStaff interpreter){
        staffToInsert=interpreter;
        //System.out.println(interpreter.getFirstName());
        initForm();
        // FirstName.setText(interpreter.getFirstName());
    }
    private void blankEditor(){
        for(JFXCheckBox checkBox: languageBoxList){
            checkBox.selectedProperty().setValue(false);
        }
        FirstName.clear();LastName.clear();PhoneNo.clear(); Providers.getSelectionModel().clearSelection();
        Email.clear(); Certifications.getSelectionModel().clearSelection(); PhoneNo.clear();
        Providers.setValue(null); Certifications.setValue(null);
    }

}
