package com.teama.drawing;

public class DrawMap {
 /*   private Canvas c;
    private GraphicsContext gc;
    private int xOffset, yOffset, imgW, imgH;
    private ImageStash stash;
    private Map<String, ImageStash> bwImgs;
    private Image bwImg;
    private int nodeDim = 3;
    private double zoomUnit = 3;
    private boolean zoomed = false;
    private double curZoom = 1;
    private String curFloor = "G";
    private ArrayList<MapNodeData> nodes;

    //private int nodeTolerance = 3;
    private ScrollPane mapPane;
    private MapSubsystem map;
    private Path curPath;

    public DrawMap(ScrollPane mapPane, Canvas c, int xOffset, int yOffset, int imgW, int imgH) {
        this.c = c;
        this.gc = c.getGraphicsContext2D();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.imgH = imgH;
        this.imgW = imgW;
        this.mapPane = mapPane;
        map = MapSubsystem.getInstance();
        bwImgs = SceneEngine.getHospitalImageMap();

        ArrayList<String> nodeIds = map.getMap().getNodeIds();
        nodes = new ArrayList<>();
        for(String id : nodeIds) {
            nodes.add(map.getMap().getNode(id));
        }
    }
    public int getImgWidth() {
        return imgW;
    }
    public int getImgH() {
        return imgH;
    }
    /**
     * Converts input coordinate system to output coordinate system
     *//*
    public double convUnits(double x, double origMaxX, double curMaxX) {
        return (x*(curMaxX/origMaxX));
    }

    /**
     * Creates new location with the X and Y coordinates suitable for drawing on the map
     * @param loc
     * @return
     *//*
    private Location convNodeCoords(Location loc) {
        Location newLoc = new Location(loc);
        newLoc.setxCoord(loc.getxCoord()+xOffset);
        newLoc.setyCoord(loc.getyCoord()+yOffset);
        return newLoc;
    }

    public void drawNode(MapNodeData n, int size, Paint pointColor) {
        double width = c.getWidth(); // make sure the width and height are updated
        double height = c.getHeight();
        Location drawLoc = convNodeCoords(n.getCoordinate());
        double nodeX = convUnits(drawLoc.getxCoord(), imgW, width);
        double nodeY = convUnits(drawLoc.getyCoord(), imgH, height);
        gc.setFill(pointColor);
        gc.fillOval(nodeX, nodeY, size, size);
    }

    public void drawEdge(Canvas c, MapEdgeData edge, Paint pointColor) {
        double width = c.getWidth(); // make sure the width and height are updated
        double height = c.getHeight();
        Location start = convNodeCoords(edge.getStart().getCoordinate());
        Location end = convNodeCoords(edge.getEnd().getCoordinate());
        gc.setStroke(pointColor);
        gc.strokeLine(
                convUnits(start.getxCoord(), imgW, width),
                convUnits(start.getyCoord(), imgH, height),
                convUnits(end.getxCoord(), imgW, width),
                convUnits(end.getyCoord(), imgH, height));
    }



    public void switchFloor(String newFloor) {
        //renderMap(mapPane.getWidth() * curZoom, mapPane.getHeight() * curZoom, newFloor);
        clearPath();
        render(newFloor);
    }

    public void renderMap(double width, double height) {
        renderMap(width, height, curFloor);
    }

    public void renderMap(double width, double height, String floor) {
        if(bwImg != null && !zoomed) { // if it can be obtained, set the width and height using the aspect ratio
            double aspectRatio = bwImg.getWidth()/bwImg.getHeight();
            double scaledW, scaledH;
            // keep the aspect ratio
            scaledH = (1 / ((1 / (width)) * aspectRatio));
            scaledW = ((height)*aspectRatio);
            if(scaledH > height) {
                c.setHeight(height);
                c.setWidth(scaledW);
                c.setTranslateX((width-scaledW)/2);
                c.setTranslateY(0);
            } else {
                c.setHeight(scaledH);
                c.setWidth(width);
                c.setTranslateY((height-scaledH)/2);
                c.setTranslateX(0);
            }
        } else if(bwImg != null) {
            double aspectRatio = bwImg.getWidth()/bwImg.getHeight();
            double scaledW, scaledH;
            // keep the aspect ratio
            scaledH = (1 / ((1 / (width)) * aspectRatio));
            scaledW = ((height)*aspectRatio);
            if(scaledH > height) {
                c.setHeight(height*curZoom);
                c.setWidth(scaledW*curZoom);
            } else {
                c.setHeight(scaledH*curZoom);
                c.setWidth(width*curZoom);
            }
        } else {
            c.setHeight(height*curZoom);
            c.setWidth(width*curZoom);
        }
        render(floor);
    }
    public void updateCurrFloor(String floor) {
        curFloor = floor;
    }
    public void reRender() {
        render(curFloor);
    }


    private boolean renderHidden = false;

    private void render(String floor) {
        if(!floor.equals(curFloor) || bwImg == null) {
            curFloor = floor;
            bwImg = bwImgs.get(floor).getImage();
        }
        gc.clearRect(0, 0, c.getWidth(), c.getHeight());
        gc.drawImage(bwImg, 0,0, c.getWidth(), c.getHeight());
        for(MapNodeData n : map.getFloorNodes(floor).values()) {
            //drawNodeAnnotation(n);
            if(renderHidden || !n.getNodeType().equals(NodeType.HALL)) { // Don't render hallway nodes
                drawNode(n, nodeDim, Color.BLACK);
            }
        }

        if(curPath != null) {
            drawPath(curPath);
        }
    }

    public void setRenderHidden(boolean renderHidden) {
        this.renderHidden = renderHidden;
    }

    private double oldWidth, oldHeight;

    public void updateSize() {
        double width = mapPane.getWidth();
        double height = mapPane.getHeight();

        if(oldWidth == 0) {
            oldWidth = width;
        }
        if(oldHeight == 0) {
            oldHeight = height;
        }

        double oldHvalue = mapPane.getHvalue();
        double oldVvalue = mapPane.getVvalue();
        mapPane.setVmax(height);
        mapPane.setHmax(width);
        //System.out.println("O: "+oldHvalue+" "+oldVvalue+" C: "+mapPane.getHvalue()+" "+mapPane.getVvalue());
        //System.out.println("OD: "+oldWidth+" "+oldHeight+" ND: "+width+" "+height);
        //zoomed = false;
        //curZoom = 1;
        renderMap(width, height);

        mapPane.setVvalue((height/oldHeight)*oldVvalue);
        mapPane.setHvalue((width/oldWidth)*oldHvalue);

        oldWidth = width;
        oldHeight = height;
    }

    private void drawNodeAnnotation(MapNodeData n) {
        gc.setLineDashes(0);
        gc.setLineWidth(1);
        gc.setLineCap(StrokeLineCap.SQUARE);
        double width = c.getWidth(); // make sure the width and height are updated
        double height = c.getHeight();
        Location drawLoc = convNodeCoords(n.getCoordinate());
        double nodeX = convUnits(drawLoc.getxCoord(), imgW, width);
        double nodeY = convUnits(drawLoc.getyCoord(), imgH, height);
        gc.setFont(Font.font("Calibri", FontWeight.EXTRA_LIGHT, 12));
        gc.strokeText(n.getShortDescription(), nodeX, nodeY);
    }

    public void toggleZoom(double x, double y) {
        if(!zoomed) {
            renderMap(c.getWidth() * zoomUnit, c.getHeight() * zoomUnit);
            mapPane.setHvalue(x); // set so it's in the center of the screen
            mapPane.setVvalue(y);
            zoomed = true;
            curZoom = 3;
        } else {
            zoomed = false;
            curZoom = 1;
            renderMap(mapPane.getWidth(), mapPane.getHeight());
        }
    }

    public void setNodeDim(int nodeDim) {
        this.nodeDim = nodeDim;
    }

    public int getNodeDim() {
        return nodeDim;
    }

    public String getCurFloor() {
        return curFloor;
    }

    public void drawPath(Path path) {
        ArrayList<MapNodeData> pathNodes = path.getNodes();
        gc.setLineDashes(5);
        gc.setLineWidth(2);
        gc.setLineCap(StrokeLineCap.BUTT);
        for(MapEdgeData e : path.getConnectors()) {
            drawEdge(c, e, Color.BLACK);
        }
        // Draw the start and end nodes bigger
        MapNodeData start = pathNodes.get(0);
        MapNodeData end = pathNodes.get(pathNodes.size()-1);
        drawNode(start, nodeDim*3, Color.RED);
        drawNode(end, nodeDim*3, Color.RED);
        //drawNodeAnnotation(start);
        //drawNodeAnnotation(end);
    }

    public void setPath(Path path) {
        curPath = null;
        reRender(); // rerender to remove previous path if there was one
        drawPath(path);
        this.curPath = path;
    }

    public void clearPath() {
        curPath = null;
    }
    public boolean isNodeWithinRegion(Canvas c, MapNodeData m, Integer xcoord, Integer ycoord) {
        int nodeTolerance = 4;
        Location nodeloc = m.getCoordinate();
        // Convert the BW coordinates to the canvas coordinates
        Double canvasX = convUnits(nodeloc.getxCoord(), imgW, c.getWidth());
        Double canvasY = convUnits(nodeloc.getyCoord(), imgH, c.getHeight());
        return ((xcoord <= canvasX+nodeTolerance && xcoord >= canvasX-nodeTolerance)&&
                (ycoord <= canvasY+nodeTolerance && ycoord >= canvasY-nodeTolerance));

    }*/
}