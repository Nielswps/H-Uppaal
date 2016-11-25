package SW9.presentations;

import SW9.utility.colors.Color;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.io.IOException;
import java.net.URL;
import java.util.function.BiConsumer;

import static SW9.presentations.CanvasPresentation.GRID_SIZE;
import static javafx.scene.paint.Color.TRANSPARENT;

public class TagPresentation extends StackPane {

    private final static Color backgroundColor = Color.GREY;
    private final static Color.Intensity backgroundColorIntensity = Color.Intensity.I50;
    private LineTo l2;
    private LineTo l3;

    public TagPresentation() {
        final URL location = this.getClass().getResource("TagPresentation.fxml");

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.load(location.openStream());

            initializeShape();
            initializeLabel();

        } catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private void initializeTextAid() {
        final JFXTextField textField = (JFXTextField) lookup("#textField");

        textField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.contains(" ")) {
                final String updatedString = newText.replace(" ", "_");
                textField.setText(updatedString);
            }
        });
    }

    private void initializeLabel() {
        final Label label = (Label) lookup("#label");
        final JFXTextField textField = (JFXTextField) lookup("#textField");
        final Path shape = (Path) lookup("#shape");

        textField.setPadding(new Insets(2, 0, 0, 0));

        final int padding = 8 + 4;

        label.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            final double newWidth = Math.max(newBounds.getWidth(), 10);

            textField.setMinWidth(newWidth + 4);
            textField.setMaxWidth(newWidth + 4);

            l2.setX(newWidth + padding);
            l3.setX(newWidth + padding);

            setMinWidth(newWidth + padding);
            setMaxWidth(newWidth + padding);

            textField.setMinHeight(GRID_SIZE * 2);
            textField.setMaxHeight(GRID_SIZE * 2);

            if (getWidth() > 5000) {
                setWidth(5);
            }

            // Fixes the jumping of the shape when the text field is empty
            if (textField.getText().isEmpty()) {
                shape.setLayoutX(0);
            }
        });

        label.textProperty().bind(new When(textField.textProperty().isNotEmpty()).then(textField.textProperty()).otherwise(textField.promptTextProperty()));

        textField.focusedProperty().addListener((obs, oldFocused, newFocused) -> {
            if (!newFocused && textField.getText().isEmpty()) {
                setOpacity(0);
            }
        });
    }

    private void initializeShape() {
        final int WIDTH = 5000;
        final int HEIGHT = GRID_SIZE * 2;

        final Path shape = (Path) lookup("#shape");

        final MoveTo start = new MoveTo(0, 0);

        l2 = new LineTo(WIDTH, 0);
        l3 = new LineTo(WIDTH, HEIGHT);
        final LineTo l4 = new LineTo(0, HEIGHT);
        final LineTo l6 = new LineTo(0, 0);

        shape.getElements().addAll(start, l2, l3, l4, l6);

        shape.setFill(backgroundColor.getColor(backgroundColorIntensity));
        shape.setStroke(backgroundColor.getColor(backgroundColorIntensity.next(5)));
    }

    public void bindToColor(final ObjectProperty<Color> color, final ObjectProperty<Color.Intensity> intensity) {
        bindToColor(color, intensity, false);
    }

    public void bindToColor(final ObjectProperty<Color> color, final ObjectProperty<Color.Intensity> intensity, final boolean doColorBackground) {
        final BiConsumer<Color, Color.Intensity> recolor = (newColor, newIntensity) -> {

            final JFXTextField textField = (JFXTextField) lookup("#textField");
            textField.setUnFocusColor(TRANSPARENT);
            textField.setFocusColor(newColor.getColor(newIntensity));

            if (doColorBackground) {
                final Path shape = (Path) lookup("#shape");
                shape.setFill(newColor.getColor(newIntensity.next(-1)));
                shape.setStroke(newColor.getColor(newIntensity.next(-1).next(2)));

                textField.setStyle("-fx-prompt-text-fill: rgba(255, 255, 255, 0.6); -fx-text-fill: " + newColor.getTextColorRgbaString(newIntensity) + ";");
                textField.setFocusColor(newColor.getTextColor(newIntensity));
            } else {
                textField.setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 0.6);");
            }

        };

        color.addListener(observable -> recolor.accept(color.get(), intensity.get()));
        intensity.addListener(observable -> recolor.accept(color.get(), intensity.get()));

        recolor.accept(color.get(), intensity.get());
    }

    public void setAndBindString(final StringProperty string) {
        final JFXTextField textField = (JFXTextField) lookup("#textField");

        textField.textProperty().unbind();
        textField.setText(string.get());
        string.bind(textField.textProperty());
    }

    public void setPlaceholder(final String placeholder) {
        final JFXTextField textField = (JFXTextField) lookup("#textField");

        textField.setPromptText(placeholder);
    }

    public void replaceSpace() {
        initializeTextAid();
    }

    public boolean textFieldIsFocused() {
        final JFXTextField textField = (JFXTextField) lookup("#textField");

        return textField.isFocused();
    }
}
