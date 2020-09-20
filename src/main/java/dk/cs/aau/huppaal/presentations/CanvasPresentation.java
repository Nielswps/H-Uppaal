package dk.cs.aau.huppaal.presentations;

import dk.cs.aau.huppaal.controllers.CanvasController;
import dk.cs.aau.huppaal.utility.UndoRedoStack;
import dk.cs.aau.huppaal.utility.helpers.CanvasDragHelper;
import dk.cs.aau.huppaal.utility.helpers.MouseTrackable;
import dk.cs.aau.huppaal.utility.helpers.ZoomHelper;
import dk.cs.aau.huppaal.utility.keyboard.Keybind;
import dk.cs.aau.huppaal.utility.keyboard.KeyboardTracker;
import dk.cs.aau.huppaal.utility.mouse.MouseTracker;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Screen;

import java.io.IOException;
import java.net.URL;

public class CanvasPresentation extends Pane implements MouseTrackable {

    public static final int GRID_SIZE = 10;
    public static MouseTracker mouseTracker;

    private final DoubleProperty x = new SimpleDoubleProperty(0);
    private final DoubleProperty y = new SimpleDoubleProperty(0);

    private final CanvasController controller;

    public CanvasPresentation() {
        final URL location = this.getClass().getResource("CanvasPresentation.fxml");

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        mouseTracker = new MouseTracker(this);

        KeyboardTracker.registerKeybind(KeyboardTracker.UNDO, new Keybind(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN), UndoRedoStack::undo));
        KeyboardTracker.registerKeybind(KeyboardTracker.REDO, new Keybind(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN), UndoRedoStack::redo));

        //Add keybindings for zoom functionality
        KeyboardTracker.registerKeybind(KeyboardTracker.ZOOM_IN, new Keybind(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.SHORTCUT_DOWN), ZoomHelper::zoomIn));
        KeyboardTracker.registerKeybind(KeyboardTracker.ZOOM_OUT, new Keybind(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.SHORTCUT_DOWN), ZoomHelper::zoomOut));
        KeyboardTracker.registerKeybind(KeyboardTracker.RESET_ZOOM, new Keybind(new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.SHORTCUT_DOWN), ZoomHelper::resetZoom));
        KeyboardTracker.registerKeybind(KeyboardTracker.ZOOM_TO_FIT, new Keybind(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHORTCUT_DOWN), ZoomHelper::zoomToFit));

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.load(location.openStream());
            controller = fxmlLoader.getController();

            initializeGrid();

            /*

            // Center on the component
            controller.component.heightProperty().addListener(observable -> {
                setTranslateY(getHeight() / 2 - controller.component.getHeight() / 2);
                setTranslateX(getWidth() / 2 - controller.component.getWidth() / 2);
            });

            // Move the component half a grid size to align it to the grid
            controller.component.setLayoutX(GRID_SIZE / 2);
            controller.component.setLayoutY(GRID_SIZE / 2);

            */
            CanvasDragHelper.makeDraggable(this, mouseEvent -> mouseEvent.getButton().equals(MouseButton.SECONDARY));
        } catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private void initializeGrid() {
        final Grid grid = new Grid(GRID_SIZE);
        getChildren().add(grid);
        grid.toBack();

        //When the translation coordinates are changed, make sure that it is handled for the grid as well
        this.translateXProperty().addListener(((observable, oldValue, newValue) -> grid.handleTranslateX(oldValue.doubleValue(), newValue.doubleValue(), this.scaleXProperty().doubleValue())));
        this.translateYProperty().addListener(((observable, oldValue, newValue) -> grid.handleTranslateY(oldValue.doubleValue(), newValue.doubleValue(), this.scaleYProperty().doubleValue())));

        ZoomHelper.setGrid(grid);
    }

    @Override
    public DoubleProperty xProperty() {
        return x;
    }

    @Override
    public DoubleProperty yProperty() {
        return y;
    }

    @Override
    public double getX() {
        return xProperty().get();
    }

    @Override
    public double getY() {
        return yProperty().get();
    }

    public CanvasController getController() {
        return controller;
    }

    @Override
    public MouseTracker getMouseTracker() {
        return mouseTracker;
    }

    public static class Grid extends Parent {

        public Grid(final int gridSize) {
            //Get the screen size (multiplied by 1.5 to account for zoom)
            int screenWidth = (int) (Screen.getPrimary().getBounds().getWidth() / ZoomHelper.minZoomFactor);
            int screenHeight = (int) (Screen.getPrimary().getBounds().getHeight() / ZoomHelper.minZoomFactor);

            setTranslateX(gridSize * 0.5);
            setTranslateY(gridSize * 0.5);

            // Add vertical lines to cover the screen, even when zoomed out
            int i = (int) -screenWidth;
            while (i * gridSize - gridSize < screenWidth) {
                Line line = new Line(i * gridSize, -screenHeight, i * gridSize, screenHeight);
                line.getStyleClass().add("grid-line");
                getChildren().add(line);
                i++;
            }

            // Add horizontal lines to cover the screen, even when zoomed out
            i = (int) -screenHeight;
            while (i * gridSize - gridSize < screenHeight) {
                Line line = new Line(-screenWidth, i * gridSize, screenWidth, i * gridSize);
                line.getStyleClass().add("grid-line");
                getChildren().add(line);
                i++;
            }
        }

        public void handleTranslateX(double oldValue, double newValue, double scale) {
            //Move the grid in the opposite direction of the canvas drag, to keep its location on screen
            this.setTranslateX(this.getTranslateX() + (newValue - oldValue) / -scale);
        }

        public void handleTranslateY(double oldValue, double newValue, double scale) {
            //Move the grid in the opposite direction of the canvas drag, to keep its location on screen
            this.setTranslateY(this.getTranslateY() + (newValue - oldValue) / -scale);
        }
    }
}
