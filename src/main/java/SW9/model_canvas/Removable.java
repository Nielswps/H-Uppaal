package SW9.model_canvas;

import SW9.utility.helpers.MouseTrackable;

public interface Removable extends MouseTrackable {

    boolean select();

    void styleSelected();

    void deselect();

    void styleDeselected();

    boolean remove();

    void reAdd();
}
