package SW9.presentations;

import SW9.abstractions.Component;
import SW9.code_analysis.CodeAnalysis;
import SW9.utility.colors.Color;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageCollectionPresentation extends VBox {

    private final ObservableList<CodeAnalysis.Message> messages;

    public MessageCollectionPresentation(final Component component, final ObservableList<CodeAnalysis.Message> messages) {
        this.messages = messages;

        final URL location = this.getClass().getResource("MessageCollectionPresentation.fxml");

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.load(location.openStream());

            initializeHeadline(component);
            initializeLine();
            initializeErrorsListener();


        } catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private void initializeErrorsListener() {
        final VBox children = (VBox) lookup("#children");

        final Map<CodeAnalysis.Message, MessagePresentation> messageMessagePresentationMap = new HashMap<>();

        final Consumer<CodeAnalysis.Message> addMessage = (message) -> {
            final MessagePresentation messagePresentation = new MessagePresentation(message.getMessage());
            messageMessagePresentationMap.put(message, messagePresentation);
            children.getChildren().add(messagePresentation);
        };

        messages.forEach(addMessage);
        messages.addListener(new ListChangeListener<CodeAnalysis.Message>() {
            @Override
            public void onChanged(final Change<? extends CodeAnalysis.Message> c) {
                while (c.next()) {
                    c.getAddedSubList().forEach(addMessage::accept);

                    c.getRemoved().forEach(message -> {
                        children.getChildren().remove(messageMessagePresentationMap.get(message));
                        messageMessagePresentationMap.remove(message);
                    });
                }
            }
        });
    }

    private void initializeLine() {
        final VBox children = (VBox) lookup("#children");
        final Line line = (Line) lookup("#line");

        children.getChildren().addListener(new InvalidationListener() {
            @Override
            public void invalidated(final Observable observable) {
                line.setEndY(children.getChildren().size() * 23 + 8);
            }
        });
    }

    private void initializeHeadline(final Component component) {
        final Label headline = (Label) lookup("#headline");
        final Circle indicator = (Circle) lookup("#indicator");
        final Line line = (Line) lookup("#line");

        headline.setText(component.getName());

        final BiConsumer<Color, Color.Intensity> updateColor = (color, intensity) -> {
            indicator.setFill(color.getColor(component.getColorIntensity()));
        };

        updateColor.accept(component.getColor(), component.getColorIntensity());
        component.colorProperty().addListener((observable, oldColor, newColor) -> updateColor.accept(newColor, component.getColorIntensity()));

        line.setStroke(Color.GREY.getColor(Color.Intensity.I400));
    }

}