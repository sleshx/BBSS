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
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.common.CashGift;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;

/**
 * Created by bandaray
 */
public class FamilyViewCashGiftArrayAdapter extends ArrayAdapter<CashGift> {

    private static Class CASH_GIFT_CLASS = CashGift.class;
    private static Class BANK_CLASS = Bank.class;
    private static Class BANK_ACCOUNT_CLASS = BankAccount.class;

    private Context context;
    private List<CashGift> cashGifts;
    private int resourceId;

    private LinearLayout llBankName;
    private LinearLayout llBankAccount;
    private LinearLayout llAmount;
    private LinearLayout llPaidDate;
    private LinearLayout llScheduledDate;

    public FamilyViewCashGiftArrayAdapter(Context context, List<CashGift> cashGiftList) {
        super(context, 0, cashGiftList);

        this.context = context;
        this.cashGifts = cashGiftList;
        this.resourceId = R.layout.fragment_family_view_cash_gift_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, null, false);

            llBankName = viewHolder.get(convertView, R.id.cg_listview_row_1);
            llBankAccount = viewHolder.get(convertView, R.id.cg_listview_row_2);
            llAmount = viewHolder.get(convertView, R.id.cg_listview_row_3);
            llPaidDate = viewHolder.get(convertView, R.id.cg_listview_row_4);
            llScheduledDate = viewHolder.get(convertView, R.id.cg_listview_row_5);

            viewHolder.get(llAmount, R.id.tvDollar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CashGift cashGift = cashGifts.get(position);
        displayCashGiftList(context, convertView, viewHolder, cashGift);

        if (cashGift.getPaidDate() == null) {
            llPaidDate.setVisibility(View.GONE);
            llScheduledDate.setVisibility(View.VISIBLE);
        } else if (cashGift.getScheduledDate() == null) {
            llPaidDate.setVisibility(View.VISIBLE);
            llScheduledDate.setVisibility(View.GONE);
        }

        if (position % 2 == 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    private void displayCashGiftList(Context context, View convertView,
                                            ViewHolder viewHolder, CashGift cashGift){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        try {

            //---BANK NAME
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_NAME);
            viewMeta.setIncludeTagId(R.id.cg_listview_row_1);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(BANK_CLASS, viewMeta);

            ModelViewSynchronizer<Bank> cgBankModelViewSynchronizer =
                    new ModelViewSynchronizer<Bank>(BANK_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            cgBankModelViewSynchronizer.setLabels();
            cgBankModelViewSynchronizer.displayDataObject(cashGift.getBankAccount().getBank());

            //---BANK ACCOUNT NO:
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.cg_listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_common_account_no);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(BANK_ACCOUNT_CLASS, viewMeta);

            ModelViewSynchronizer<BankAccount> cgBankAccountModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BANK_ACCOUNT_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            cgBankAccountModelViewSynchronizer.setLabels();
            cgBankAccountModelViewSynchronizer.displayDataObject(cashGift.getBankAccount());

            //---CASH GIFT AMOUNT
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(CashGift.FIELD_CG_AMOUNT);
            viewMeta.setIncludeTagId(R.id.cg_listview_row_3);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CASH_GIFT_CLASS, viewMeta);

            if (cashGift.getPaidDate() != null) {
                //---CASH GIFT PAID DATE
                viewMeta = new ModelPropertyViewMeta(CashGift.FIELD_CG_DATE_PAID);
                viewMeta.setIncludeTagId(R.id.cg_listview_row_4);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.DATE);

                metaDataList.add(CASH_GIFT_CLASS, viewMeta);
            }

            if (cashGift.getScheduledDate() != null) {
                //---CASH GIFT SCHEDULED DATE
                viewMeta = new ModelPropertyViewMeta(CashGift.FIELD_CG_DATE_SCHEDULED);
                viewMeta.setIncludeTagId(R.id.cg_listview_row_5);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.DATE);

                metaDataList.add(CASH_GIFT_CLASS, viewMeta);
            }

            ModelViewSynchronizer<CashGift> cashGiftModelViewSynchronizer =
                    new ModelViewSynchronizer<CashGift>(CASH_GIFT_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            cashGiftModelViewSynchronizer.setLabels();
            cashGiftModelViewSynchronizer.displayDataObject(cashGift, viewHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
