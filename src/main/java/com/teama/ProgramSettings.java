package com.teama;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.Path;
import javafx.beans.property.*;

public class ProgramSettings {

    private static class SettingsGetter {
        private static final ProgramSettings instance = new ProgramSettings();
    }

    public static synchronized ProgramSettings getInstance() {
        return ProgramSettings.SettingsGetter.instance;
    }

    private ObjectProperty<Path> currentDisplayedPath = new SimpleObjectProperty<>();
    private ObjectProperty<MapNode> pathOriginNode = new SimpleObjectProperty<>();
    private ObjectProperty<MapNode> pathEndNode = new SimpleObjectProperty<>();

    public void setCurrentDisplayedPathProp(Path getCurrentDisplayedPath) {
        this.currentDisplayedPath.set(getCurrentDisplayedPath);
    }

    private BooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

    public BooleanProperty getIsLoggedInProp() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn.set(isLoggedIn);
    }

    public void setPathOriginNodeProp(MapNode pathOriginNode) {
        this.pathOriginNode.set(pathOriginNode);
    }

    public void setPathEndNodeProp(MapNode pathEndNode) {
        this.pathEndNode.set(pathEndNode);
    }

    public ReadOnlyObjectProperty<Path> getCurrentDisplayedPathProp() {
        return currentDisplayedPath;
    }
    public ReadOnlyObjectProperty<MapNode> getPathOriginNodeProp() { return pathOriginNode; }
    public ReadOnlyObjectProperty<MapNode> getPathEndNodeProp() { return pathEndNode; }


}
