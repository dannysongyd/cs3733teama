package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.teama.ProgramSettings;
import com.teama.controllers.PathfindingController;
import com.teama.controllers.SearchBarController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.MapNodeData;
import com.teama.mapsubsystem.pathfinding.DirectionAdapter;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.Direction;
import com.teama.mapsubsystem.pathfinding.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirections;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;


public class DirectionController extends HamburgerController{

    Boolean accessibilityMode = false;
    private MapDrawingSubsystem mapDrawing = MapDrawingSubsystem.getInstance();
    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();
    private PathfindingController pathfindingController;


//    private MapDrawingSubsystem mapDrawing = MapDrawingSubsystem.getInstance();
//    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();
//    private MapNode tempMapNodeStart = MapSubsystem.getInstance().getNode("AHALL00901");
//    private MapNode tempMapNodeEnd = MapSubsystem.getInstance().getNode("AHALL00401");
//    private Path tempPath = mapSubsystem.getPath(tempMapNodeStart,tempMapNodeEnd);




    @FXML
    private ImageView goBtn;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

//    @FXML
//    private JFXTextField yourLocationBar;

    @FXML
    private JFXComboBox<String> yourLocationBar;

    @FXML
    private ImageView accessibilityBtn;

    @FXML
    private JFXComboBox<String> destinationBar;

    @FXML
    private ImageView switchBtn;

    @FXML
    private ImageView closeBtn;

    @FXML
    private TableView<DirectionAdapter> textDirections;

    @FXML
    private TableColumn<String, Text> stepCol;

    @FXML
    private TableColumn<String, Text> descriptionCol;

    @FXML
    private TableColumn<String, Text> distanceCol;

    @FXML
    private TableColumn<String, ImageView> directionCol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    void onGoBtnClicked(MouseEvent event) {



        MapNode newOrigin  = mapSubsystem.getNodeByDescription(yourLocationBar.getEditor().getText(),true);
        MapNode newEnd = mapSubsystem.getNodeByDescription(destinationBar.getEditor().getText(),true);
        pathfindingController.genPath(newOrigin,newEnd);

        System.out.println("Start location is:" + yourLocationBar.getEditor().getText());
        System.out.println("Destination is:" + destinationBar.getEditor().getText());




    }
    @FXML
    void onAccessibilityBtnClicked(MouseEvent event) {
        System.out.println(accessibilityMode);
        if(!accessibilityMode) {
            accessibilityBtn.setBlendMode(BlendMode.SRC_ATOP);
            accessibilityMode = true;
        }else if(accessibilityMode) {
            accessibilityBtn.setBlendMode(BlendMode.SOFT_LIGHT);
            accessibilityMode = false;
        }

        System.out.println();
    }
    @Override public Pane getParentPane(){
        //TODO return the anchorpane that everything is stored in
        return mainPane;
    }
    //TODO make this return the fxml path
    @Override public String getFXMLPath(){
        return "/Direction2.fxml";
    }
    @Override public void onOpen(){}
    @Override public void onClose(){}

    @FXML
    void onDestinationBarClicked(ActionEvent event) {



    }

    // Dealing with the closeBtn
    @FXML
    void onClosedBtnClicked(MouseEvent event) {
        //sets the listener for closing to true so that the app closes
        this.closing.set(true);
    }

    @FXML
    void onClosedBtnHovered(MouseEvent event) {
        closeBtn.setBlendMode(BlendMode.SRC_ATOP);
    }

    @FXML
    void onClosedBtnExited(MouseEvent event) {
        closeBtn.setBlendMode(BlendMode.SOFT_LIGHT);
    }

    //Dealing with the SwitchBtn
    @FXML
    void onSwitchBtnHovered(MouseEvent event) {
        switchBtn.setBlendMode(BlendMode.SRC_ATOP);
    }

    @FXML
    void onSwitchBtnExited(MouseEvent event) {
        switchBtn.setBlendMode(BlendMode.SOFT_LIGHT);
    }

    @FXML
    void onSwitchBtnClicked(MouseEvent event) {
        String yourLocation = yourLocationBar.getEditor().getText();
        String destination = destinationBar.getEditor().getText();
        yourLocationBar.getEditor().setText(destination);
        destinationBar.getEditor().setText(yourLocation);

        onGoBtnClicked(null);
    }


    @FXML
    void onYourLocationBarClicked(ActionEvent event) {

    }
//    stepCol.setText("stepNum");
//    descriptionCol.setText("description");
//    distanceCol.setText("distance");
//    directionCol.setText("direction");



    public void initialize(){






//        ProgramSettings.getInstance().setPathOriginNodeProp(tempMapNodeStart);
//        ProgramSettings.getInstance().setPathEndNodeProp(tempMapNodeEnd);
//        ProgramSettings.getInstance().setCurrentDisplayedPathProp(tempPath);

        stepCol.setText("Step");
        descriptionCol.setText("Description");
        distanceCol.setText("Distance");
        directionCol.setText("Direction");



        ReadOnlyObjectProperty<Path> pathObjectProperty = ProgramSettings.getInstance().getCurrentDisplayedPathProp();

        if(pathObjectProperty.getValue() != null) {
            putDirectionsOnScreen(pathObjectProperty.getValue());
        }
        pathObjectProperty.addListener((a, b, currentPath) -> {
            putDirectionsOnScreen(currentPath);
        });

        stepCol.setCellValueFactory(
                new PropertyValueFactory<>("stepNum"));
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        distanceCol.setCellValueFactory(
                new PropertyValueFactory<>("distance"));
        directionCol.setCellValueFactory(
                new PropertyValueFactory<>("direction"));
        textDirections.setFixedCellSize(75); // cells need to be bigger than default

        textDirections.setRowFactory(tv -> {
            TableRow<DirectionAdapter> row = new TableRow<>();
            row.setAlignment(Pos.CENTER);
            return row;
        });

        SearchBarController searchbar1 = new SearchBarController(yourLocationBar, false);
        SearchBarController searchbar2 = new SearchBarController(destinationBar, false);


        ProgramSettings.getInstance().getPathOriginNodeProp().addListener((a) -> {
            yourLocationBar.getEditor().setText(ProgramSettings.getInstance().getPathEndNodeProp().getValue().getLongDescription());
        });

        ProgramSettings.getInstance().getPathEndNodeProp().addListener((a) -> {
            destinationBar.getEditor().setText(ProgramSettings.getInstance().getPathEndNodeProp().getValue().getLongDescription());
        });

        //SearchBarController searchBarController = new SearchBarController(originNodeCombo, true);



        // Make a listener on the tableview to focus on the node relating to the direction when selected
        textDirections.getSelectionModel().selectedItemProperty().addListener((a) -> {
            mapDrawing.setViewportCenter(textDirections.getSelectionModel().getSelectedItem().getLocToFocus());
        });

    }
    private ArrayList<Long> filterFloorListeners = new ArrayList<>();
    private DirectionsGenerator directionsGenerator = new TextualDirections();
    private void putDirectionsOnScreen(Path path) {
        TextDirections directions = directionsGenerator.generateDirections(path);
        ObservableList<DirectionAdapter> directionVals = FXCollections.observableArrayList();
        int num = 1;
        for(Direction d : directions.getDirections()) {
            directionVals.add(new DirectionAdapter(num, d));
            num++;
        }
        textDirections.setItems(directionVals);

    }

    public void setFinder(PathfindingController finder)
    {
        this.pathfindingController=finder;
    }




}
