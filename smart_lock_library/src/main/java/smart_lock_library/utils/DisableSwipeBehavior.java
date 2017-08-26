package smart_lock_library.utils;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.view.View;

public class DisableSwipeBehavior extends SwipeDismissBehavior<Snackbar.SnackbarLayout> {

    @Override
    public boolean canSwipeDismissView(@NonNull View view) {
        return true;
    }
}
