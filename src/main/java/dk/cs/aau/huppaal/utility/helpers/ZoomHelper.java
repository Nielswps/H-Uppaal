package dk.cs.aau.huppaal.utility.helpers;

import dk.cs.aau.huppaal.controllers.CanvasController;
import dk.cs.aau.huppaal.presentations.CanvasPresentation;
import dk.cs.aau.huppaal.presentations.ComponentPresentation;

public class ZoomHelper {
    private static CanvasPresentation canvasPresentation;
    private static CanvasPresentation.Grid grid;
    public static double minZoomFactor = 0.4;
    public static double maxZoomFactor = 8;

    public static void setCanvas(CanvasPresentation newCanvasPresentation) {
        canvasPresentation = newCanvasPresentation;
    }

    public static void setGrid(CanvasPresentation.Grid newGrid) {
        grid = newGrid;
    }

    public static void zoomIn() {
        double delta = 1.2;
        double newScale = canvasPresentation.getScaleX() * delta;

        //Limit for zooming in
        if(newScale > maxZoomFactor){
            return;
        }

        //Scale canvas
        canvasPresentation.setScaleX(newScale);
        canvasPresentation.setScaleY(newScale);

        centerComponent(newScale);
    }

    public static void zoomOut() {
        double delta = 1.2;
        double newScale = canvasPresentation.getScaleX() / delta;

        //Limit for zooming out
        if(newScale < minZoomFactor){
            return;
        }

        //Scale canvas
        canvasPresentation.setScaleX(newScale);
        canvasPresentation.setScaleY(newScale);

        centerComponent(newScale);
    }

    public static void resetZoom() {
        canvasPresentation.setScaleX(1);
        canvasPresentation.setScaleY(1);

        //Center component
        centerComponent(1);
    }

    public static void zoomToFit() {
        double newScale = Math.min(canvasPresentation.getWidth() / CanvasController.getActiveComponent().getWidth() - 0.1, canvasPresentation.getHeight() / CanvasController.getActiveComponent().getHeight() - 0.2); //0.1 for width and 0.2 for height added for margin

        //Scale canvas
        canvasPresentation.setScaleX(newScale);
        canvasPresentation.setScaleY(newScale);

        centerComponent(newScale);
    }

    private static void centerComponent(double newScale){
        //Center component
        final double gridSize = CanvasPresentation.GRID_SIZE * newScale;
        final double actualHeight = canvasPresentation.getHeight() - ComponentPresentation.TOOL_BAR_HEIGHT;
        double xOffset = newScale * canvasPresentation.getWidth() * 1.0f / 2 - newScale * CanvasController.getActiveComponent().getWidth() * 1.0f / 2;
        double yOffset = newScale * actualHeight * 1.0f / 3 - newScale * CanvasController.getActiveComponent().getHeight() * 1.0f / 3 + ComponentPresentation.TOOL_BAR_HEIGHT / (1.0f / 3); //The offset places the component a bit too high, so 'canvasPresentation.getHeight() / 4' is used to lower it a but

        canvasPresentation.setTranslateX(xOffset - (xOffset % gridSize) + gridSize * 0.5);
        canvasPresentation.setTranslateY(yOffset - (yOffset % gridSize) + gridSize * 0.5);

        //Make sure the grid stays on screen
        grid.setTranslateX(gridSize * 0.5);
        grid.setTranslateY(gridSize * 0.5);
    }
}
