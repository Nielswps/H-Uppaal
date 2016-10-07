package SW9.issues;

import javafx.beans.Observable;
import jiconfont.icons.GoogleMaterialDesignIcons;

import java.util.function.Predicate;

public class Error<T> extends Issue<T> {

    @Override
    protected GoogleMaterialDesignIcons getIcon() {
        return GoogleMaterialDesignIcons.ERROR;
    }

    public Error(final Predicate<T> presentPredicate, final T subject, final Observable... observables) {
        super(presentPredicate, subject, observables);
    }
}