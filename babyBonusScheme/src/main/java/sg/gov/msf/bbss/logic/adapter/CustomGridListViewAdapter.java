package sg.gov.msf.bbss.logic.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.logic.adapter.util.CustomGridListViewItem;

/**
 * Created by bandaray
 */
public class CustomGridListViewAdapter extends ArrayAdapter<CustomGridListViewItem> {
	private Context context;
    private int layoutResourceId;
    private boolean isGridView;
    private ArrayList<CustomGridListViewItem> data = new ArrayList<CustomGridListViewItem>();

	public CustomGridListViewAdapter(Context context, int layoutResourceId,
                                     ArrayList<CustomGridListViewItem> data, boolean isGridView) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
        this.isGridView = isGridView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.tvItemTitle);
			holder.imageItem = (ImageView) row.findViewById(R.id.ivItemImage);
            holder.txtBadge = (TextView) row.findViewById(R.id.tvItemBadge);

			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		final CustomGridListViewItem item = data.get(position);

        if(isGridView){
            holder.txtTitle.setText(item.getTitle().toUpperCase(Locale.ENGLISH));
        } else {
            holder.txtTitle.setText(item.getTitle());
            if (position % 2 == 0) {
                row.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));
            } else {
                row.setBackgroundColor(Color.WHITE);
            }
        }

        if (item.getBadgeCount() > 0) {
            holder.txtBadge.setVisibility(View.VISIBLE);
            holder.txtBadge.setText(String.valueOf(item.getBadgeCount()));
        } else {
            holder.txtBadge.setVisibility(View.GONE);
        }

		holder.imageItem.setImageBitmap(item.getImage());

		return row;

	}

	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;
        TextView txtBadge;

	}
}