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
import sg.gov.msf.bbss.model.entity.common.ChildDevAccountHistory;

/**
 * Created by bandaray
 */
public class FamilyViewCdaHistoryArrayAdapter extends ArrayAdapter<ChildDevAccountHistory> {

    private static Class CDA_HISTORY_CLASS = ChildDevAccountHistory.class;

    private Context context;
    private List<ChildDevAccountHistory> cdaHistory;
    private int resourceId;

    private LinearLayout llCdaDepositAmt;
    private LinearLayout llCdaDepositDate;
    private LinearLayout llGovtMatchingAmt;
    private LinearLayout llGovtMatchingDate;

    public FamilyViewCdaHistoryArrayAdapter(Context context,
                                            List<ChildDevAccountHistory> cdaHistory) {
        super(context, 0, cdaHistory);

        this.context = context;
        this.cdaHistory = cdaHistory;
        this.resourceId = R.layout.fragment_family_view_cda_history_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, null, false);

            llCdaDepositAmt = viewHolder.get(convertView, R.id.cdah_listview_row_1);
            llCdaDepositDate = viewHolder.get(convertView, R.id.cdah_listview_row_2);
            llGovtMatchingAmt = viewHolder.get(convertView, R.id.cdah_listview_row_3);
            llGovtMatchingDate = viewHolder.get(convertView, R.id.cdah_listview_row_4);

            viewHolder.get(llCdaDepositAmt, R.id.tvDollar);
            viewHolder.get(llGovtMatchingAmt, R.id.tvDollar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        displayChildDevAccountHistoryList(context, convertView, viewHolder, cdaHistory.get(position));

        if (position % 2 == 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    private void displayChildDevAccountHistoryList(Context context, View convertView,
                                                   ViewHolder viewHolder,
                                                   ChildDevAccountHistory history){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        try {
            metaDataList = new ModelPropertyViewMetaList(context);

            //---CDA DEPOSIT AMOUNT
            viewMeta = new ModelPropertyViewMeta(ChildDevAccountHistory.
                    FIELD_CDA_HISTORY_DEPOSIT_AMT);
            viewMeta.setIncludeTagId(R.id.cdah_listview_row_1);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CDA_HISTORY_CLASS, viewMeta);

            //---CDA DEPOSIT DATE
            viewMeta = new ModelPropertyViewMeta(ChildDevAccountHistory.
                    FIELD_CDA_HISTORY_DEPOSIT_DATE);
            viewMeta.setIncludeTagId(R.id.cdah_listview_row_2);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(CDA_HISTORY_CLASS, viewMeta);

            //---GOVT MATCHING AMOUNT
            viewMeta = new ModelPropertyViewMeta(ChildDevAccountHistory.
                    FIELD_CDA_HISTORY_MATCHED_AMT);
            viewMeta.setIncludeTagId(R.id.cdah_listview_row_3);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CDA_HISTORY_CLASS, viewMeta);

            //---CASH GIFT PAID DATE-- this is the matched date
            viewMeta = new ModelPropertyViewMeta(ChildDevAccountHistory.
                    FIELD_CDA_HISTORY_MATCHED_DATE);
            viewMeta.setIncludeTagId(R.id.cdah_listview_row_4);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(CDA_HISTORY_CLASS, viewMeta);

            ModelViewSynchronizer<ChildDevAccountHistory> cashGiftModelViewSynchronizer =
                    new ModelViewSynchronizer<ChildDevAccountHistory>(CDA_HISTORY_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            cashGiftModelViewSynchronizer.setLabels();
            cashGiftModelViewSynchronizer.displayDataObject(history, viewHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
