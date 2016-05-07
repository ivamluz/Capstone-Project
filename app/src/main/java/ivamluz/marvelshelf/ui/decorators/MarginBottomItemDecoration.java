package ivamluz.marvelshelf.ui.decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by iluz on 5/7/16.
 * <p/>
 * http://stackoverflow.com/a/27037230
 */
public class MarginBottomItemDecoration extends RecyclerView.ItemDecoration {

    private final int mMarginBottom;

    public MarginBottomItemDecoration(int marginBottom) {
        this.mMarginBottom = marginBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mMarginBottom;
    }
}