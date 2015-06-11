package sg.gov.msf.bbss.apputils.ui.helper;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by bandaray on 15/1/2015.
 */
public class ViewHolder {

    public <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
