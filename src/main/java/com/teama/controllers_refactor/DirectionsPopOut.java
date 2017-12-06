package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXComboBox;
import com.teama.ProgramSettings;
import com.teama.controllers.SearchBarController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.mapsubsystem.pathfinding.DirectionAdapter;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextualDirections;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;

public class DirectionsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;

    @FXML
    private TableView<DirectionAdapter> textDirections;

    @FXML
    private TableColumn<String, Integer> stepCol;

    @FXML
    private TableColumn<String, Image> imageCol;

    @FXML
    private TableColumn<String, String> descriptionCol;

    @FXML
    private TableColumn<String, String> distanceCol;

    @FXML
    private JFXComboBox<String> originNodeCombo;

    @FXML
    private JFXComboBox<String> filterBox;

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);

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
        imageCol.setCellValueFactory(
                new PropertyValueFactory<>("image"));


        textDirections.setFixedCellSize(100); // cells need to be bigger than default

        // Factory for each row, set to have the text wrap
        textDirections.setRowFactory(tv -> {
            TableRow<DirectionAdapter> row = new TableRow<>();
            row.setWrapText(true);
            return row;
        });

        // Populate the combo box and allow fuzzy search by tying it to a search controller
        SearchBarController searchBarController = new SearchBarController(originNodeCombo, true);

        // When the origin combo box changes selected value, set the origin of the path to the new one
        originNodeCombo.getSelectionModel().selectedItemProperty().addListener((Observable a) -> {
            System.out.println("CHANGED SELECTION "+originNodeCombo.getSelectionModel().getSelectedItem());
            MapNode selectedNode = MapSubsystem.getInstance().getNodeByDescription(originNodeCombo.getSelectionModel().getSelectedItem(), true);
            if(selectedNode != null) {
                // Tell path controller to make a new path
                ProgramSettings.getInstance().setPathOriginNodeProp(selectedNode);
            }
        });

        // Populate the filter box
        filterBox.getItems().add("");
        for(NodeType nType : NodeType.values()) {
            if(!nType.equals(NodeType.HALL)) {
                filterBox.getItems().add(nType.toString());
            }
        }

        // Make a listener on the filter box that when it changes the nodes of that type
        // are displayed differently
        filterBox.getSelectionModel().selectedItemProperty().addListener((a1, b1, c1) -> {
            for(long id : filterFloorListeners) {
                mapDrawing.detachListener(id);
            }
            updateFilter();
            drawShortestPathToFilter();
            filterFloorListeners.add(mapDrawing.attachFloorChangeListener((a, b, c) -> {
                updateFilter();
                drawShortestPathToFilter();
            }));
        });
    }

    private MapDrawingSubsystem mapDrawing = MapDrawingSubsystem.getInstance();
    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();

    private ArrayList<Long> filterFloorListeners = new ArrayList<>();
    private void updateFilter() {
        mapDrawing.refreshMapNodes();
        Collection<MapNode> floorNodes = mapSubsystem.getVisibleFloorNodes(mapDrawing.getCurrentFloor()).values();
        for (MapNode n : floorNodes) {
            if (n.getNodeType().equals(NodeType.fromValue(filterBox.getSelectionModel().getSelectedItem()))) {
                // Display on floor differently
                mapDrawing.drawNode(n, 15, Color.RED);
            }
        }
    }

    private void drawShortestPathToFilter() {
        // Find the shortest path to the given nodetype
        Path shortest = mapSubsystem.findClosest(NodeType.fromValue(filterBox.getSelectionModel().getSelectedItem()));
        System.out.println("SHORTEST FOUND TO "+shortest.getEndNode().getId());
        //ProgramSettings.getInstance().setPathEndNodeProp(shortest.getEndNode());
        //ProgramSettings.getInstance().setCurrentDisplayedPathProp(shortest);
    }

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

        // make the combo box automatically change to the start of the path
        originNodeCombo.getEditor().setText(path.getStartNode().getLongDescription());
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
        // Redisplay all floor nodes to get rid of the filter
        MapDrawingSubsystem.getInstance().refreshMapNodes();
    }

    @Override
    public String getFXMLPath() {
        return "/DirectionsPopOut.fxml";
    }
}
