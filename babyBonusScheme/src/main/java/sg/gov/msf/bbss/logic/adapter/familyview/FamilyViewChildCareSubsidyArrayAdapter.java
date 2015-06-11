package sg.gov.msf.bbss.logic.adapter.familyview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.model.entity.common.ChildCareSubsidy;

/**
 * Created by bandaray
 */
public class FamilyViewChildCareSubsidyArrayAdapter extends ArrayAdapter<ChildCareSubsidy> {

    private static Class CHILD_CARE_SUBSIDY_CLASS = ChildCareSubsidy.class;

    private Context context;
    private List<ChildCareSubsidy> ccSubsidy;
    private int resourceId;

    private LinearLayout llSubsidyCenterName;
    private LinearLayout llSubsidyMonth;
    private LinearLayout llSubsidyAmount;

    public FamilyViewChildCareSubsidyArrayAdapter(Context context,
                                                  List<ChildCareSubsidy> ccSubsidy) {
        super(context, 0, ccSubsidy);

        this.context = context;
        this.ccSubsidy = ccSubsidy;
        this.resourceId = R.layout.fragment_family_view_child_care_sub_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, null, false);

            llSubsidyCenterName = viewHolder.get(convertView, R.id.ccs_listview_row_1);
            llSubsidyMonth = viewHolder.get(convertView, R.id.ccs_listview_row_2);
            llSubsidyAmount = viewHolder.get(convertView, R.id.ccs_listview_row_3);

            viewHolder.get(llSubsidyAmount, R.id.tvDollar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        displayChildCareSubsidyList(context, convertView, viewHolder, ccSubsidy.get(position));

        if (position % 2 == 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    private void displayChildCareSubsidyList(Context context, View convertView,
                                             ViewHolder viewHolder,
                                             ChildCareSubsidy subsidy){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        try {
            metaDataList = new ModelPropertyViewMetaList(context);

            //---SUBSIDY NAME
            viewMeta = new ModelPropertyViewMeta(ChildCareSubsidy.FIELD_SUBSIDY_NAME);
            viewMeta.setIncludeTagId(R.id.ccs_listview_row_1);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CARE_SUBSIDY_CLASS, viewMeta);

            //---SUBSIDY MONTH
            viewMeta = new ModelPropertyViewMeta(ChildCareSubsidy.FIELD_SUBSIDY_MONTH);
            viewMeta.setIncludeTagId(R.id.ccs_listview_row_2);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CARE_SUBSIDY_CLASS, viewMeta);

            //---SUBSIDY AMOUNT
            viewMeta = new ModelPropertyViewMeta(ChildCareSubsidy.FIELD_SUBSIDY_AMOUNT);
            viewMeta.setIncludeTagId(R.id.ccs_listview_row_3);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CHILD_CARE_SUBSIDY_CLASS, viewMeta);


            ModelViewSynchronizer<ChildCareSubsidy> cashGiftModelViewSynchronizer =
                    new ModelViewSynchronizer<ChildCareSubsidy>(CHILD_CARE_SUBSIDY_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            cashGiftModelViewSynchronizer.setLabels();
            cashGiftModelViewSynchronizer.displayDataObject(subsidy, viewHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
