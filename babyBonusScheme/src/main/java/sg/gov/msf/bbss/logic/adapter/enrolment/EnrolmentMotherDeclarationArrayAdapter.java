package sg.gov.msf.bbss.logic.adapter.enrolment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.logic.type.ChildDeclarationType;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;

/**
 * Created by bandaray
 */
public class EnrolmentMotherDeclarationArrayAdapter extends ArrayAdapter<ChildDeclarationType> {

    private static Class CHILD_ITEM_CLASS = ChildItem.class;

    private Context context;
    private Adapter adapter;
    private List<ChildDeclarationType> declarationData;
    private ChildListType childListType;
    private int resourceId;
    private LayoutInflater inflater = null;
    private BbssApplication app;

    public EnrolmentMotherDeclarationArrayAdapter(Context context, int resourceId,
                                                  ChildDeclarationType[] declarationData) {
        super(context, resourceId, 0, declarationData);

        this.context = context;
        this.adapter = this;
        this.declarationData = Arrays.asList(declarationData);
        this.resourceId = resourceId;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null) {
            convertView = inflater.inflate(resourceId, null, false);
        }

        if (position % 2 == 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        ChildDeclarationType declarationType = declarationData.get(position);

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(declarationType.getString(context));

        return convertView;
    }
}
