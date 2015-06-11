package sg.gov.msf.bbss.logic.adapter.enrolment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.component.CheckableRelativeLayout;
import sg.gov.msf.bbss.model.entity.EnrolmentStatus;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentChildStatus;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class EnrolmentStatusListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<EnrolmentStatus> enrolmentStatusList;

    private List<List<EnrolmentChildStatus>> child = new ArrayList<List<EnrolmentChildStatus>>();


    public EnrolmentStatusListViewAdapter(Context context,
                                          List<EnrolmentStatus> enrolmentStatusList){
        this.context = context;
        this.enrolmentStatusList = enrolmentStatusList;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (EnrolmentStatus enrolmentStatus : enrolmentStatusList) {
            List<EnrolmentChildStatus> childItem = new ArrayList<EnrolmentChildStatus>();
            Collections.addAll(childItem, enrolmentStatus.getEnrolmentChildStatuses());
            child.add(childItem);
        }
    }

    @Override
    public int getGroupCount() {
        return enrolmentStatusList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return enrolmentStatusList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        CheckableRelativeLayout checkableRelativeLayout = (CheckableRelativeLayout)
                inflater.inflate(R.layout.listitem_1rows_single_choice, null, false);

        TextView textView = (TextView)checkableRelativeLayout.findViewById(R.id.tvListRowHeader);
        textView.setText(enrolmentStatusList.get(groupPosition).getAppId());
        return checkableRelativeLayout;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.enrolment_status_child_layout, null, false);
        convertView.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));

        LinearLayout linearLayout1 = (LinearLayout) convertView.findViewById(R.id.enrol_status_item);

        TextView tvLabel_1 = (TextView)linearLayout1.findViewById(R.id.tvLabel);
        TextView tvValue_1 = (TextView)linearLayout1.findViewById(R.id.tvValue);
        tvLabel_1.setText(child.get(groupPosition).get(childPosition).getName());
        tvValue_1.setText(child.get(groupPosition).get(childPosition).getStatus());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
