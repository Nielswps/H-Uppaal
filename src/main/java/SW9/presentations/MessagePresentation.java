package SW9.presentations;

import SW9.utility.colors.Color;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;

import static javafx.scene.paint.Color.TRANSPARENT;

public class MessagePresentation extends HBox {

    public MessagePresentation() {
        final URL location = this.getClass().getResource("MessagePresentation.fxml");

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.load(location.openStream());

            // Initialize here
            initializeHover();

        } catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private void initializeHover() {
        setOnMouseEntered(event -> {
            setBackground(new Background(new BackgroundFill(
                    Color.GREY.getColor(Color.Intensity.I300),
                    CornerRadii.EMPTY,
                    Insets.EMPTY
            )));
        });

        setOnMouseExited(event -> {
            setBackground(new Background(new BackgroundFill(
                    TRANSPARENT,
                    CornerRadii.EMPTY,
                    Insets.EMPTY
            )));
        });
    }

}
