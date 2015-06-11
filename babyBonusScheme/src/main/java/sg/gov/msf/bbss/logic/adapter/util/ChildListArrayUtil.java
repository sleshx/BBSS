package sg.gov.msf.bbss.logic.adapter.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.apputils.util.AgeCalculator;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by bandaray
 * Modified to add more methods by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 *
 */
public class ChildListArrayUtil {

    //--- Family View ------------------------------------------------------------------------------

    public static void displayFamilyViewChildList(Context context, View convertView,
                                                  ViewHolder viewHolder, ChildItem childItem,
                                                  BbssApplication app){

        double total = 0;

        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

//        LoginUserType loginType = LoginManager.getSessionContainer().getLoginType();
//        boolean isParent = loginType == LoginUserType.PARENT;
//        boolean isCdaTrustee = loginType == LoginUserType.CDA_TRUSTEE;
//        boolean isNaHolder = loginType == LoginUserType.NOMINATED_ACCOUNT_HOLDER;

        viewHolder.get(convertView, R.id.listview_row_1);
        viewHolder.get(convertView, R.id.listview_row_2);
        viewHolder.get(convertView, R.id.listview_row_3);
        viewHolder.get(convertView, R.id.listview_row_4);

        int age = AgeCalculator.getAgeByBirthMonthAndYear(childItem.getChild().getDateOfBirth());
        int yearMonths = (age < 0) ? R.string.label_family_view_months : R.string.label_family_view_years;

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(String.format("%s, %s %s", childItem.getChild().getName(), Math.abs(age),
                StringHelper.getStringByResourceId(context, yearMonths)));

        try {
            metaDataList = new ModelPropertyViewMetaList(context);

            if (childItem.isShowCG()) {
                //---CASH GIFT AMOUNT
                viewMeta = new ModelPropertyViewMeta(ChildItem.FIELD_CASH_GIFT_AMOUNT);
                viewMeta.setIncludeTagId(R.id.listview_row_1);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

                metaDataList.add(ChildItem.class, viewMeta);

                total = total + childItem.getCashGiftAmount();
            } else {
                convertView.findViewById(R.id.listview_row_1).setVisibility(View.GONE);
            }

            if (childItem.isShowGovtMatching()) {
                //---GOVT. MATCHING AMOUNT
                viewMeta = new ModelPropertyViewMeta(ChildItem.FIELD_GOVERNMENT_MATCHING_AMOUNT);
                viewMeta.setIncludeTagId(R.id.listview_row_2);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

                metaDataList.add(ChildItem.class, viewMeta);

                total = total + childItem.getGovMatchingAmount();
            } else {
                convertView.findViewById(R.id.listview_row_2).setVisibility(View.GONE);
            }

            if (childItem.isShowChildCareSubsidy()) {
                //---CHILD CARE SUBSIDY AMOUNT
                viewMeta = new ModelPropertyViewMeta(ChildItem.FIELD_CHILDCARE_SUBSIDY_AMOUNT);
                viewMeta.setIncludeTagId(R.id.listview_row_3);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

                metaDataList.add(ChildItem.class, viewMeta);

                total = total + childItem.getChildCareSubsidyAmount();
            } else {
                convertView.findViewById(R.id.listview_row_3).setVisibility(View.GONE);
            }

            //--- Total Amount Received
            viewMeta = new ModelPropertyViewMeta(ChildItem.FIELD_TOTAL_AMOUNT_RECEIVED);
            viewMeta.setIncludeTagId(R.id.listview_row_4);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(ChildItem.class, viewMeta);

            childItem.setTotalAmountReceived(total);

            //Make the total bold
            View totalView = convertView.findViewById(R.id.listview_row_4);
            ((TextView)totalView.findViewById(R.id.tvLabel)).setTypeface(null, Typeface.BOLD);
            ((TextView)totalView.findViewById(R.id.tvDollar)).setTypeface(null, Typeface.BOLD);
            ((TextView)totalView.findViewById(R.id.tvValue)).setTypeface(null, Typeface.BOLD);

            ModelViewSynchronizer<ChildItem> synchronizer = new
                    ModelViewSynchronizer<ChildItem>(ChildItem.class, metaDataList, convertView,
                    AppConstants.EMPTY_STRING);
            synchronizer.setLabels();
            synchronizer.displayDataObject(childItem, viewHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Change NAH -------------------------------------------------------------------------------

    public static void displayNominatedAccHolderChildList(Context context, View convertView,
                                                          ViewHolder viewHolder,
                                                          ChildItem childItem){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        viewHolder.get(convertView, R.id.listview_row_1);
        viewHolder.get(convertView, R.id.listview_row_2);

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            //--- CURRENT NAH
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_services_current_nah);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(Adult.class, viewMeta);

            ModelViewSynchronizer<Adult> nahModelViewSynchronizer =
                    new ModelViewSynchronizer<Adult>(Adult.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            nahModelViewSynchronizer.setLabels();
            nahModelViewSynchronizer.displayDataObject(childItem.getNominatedAccountHolder());

            //---BANK NO
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_services_bank_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(BankAccount.class, viewMeta);


            ModelViewSynchronizer<BankAccount> bankModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BankAccount.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            bankModelViewSynchronizer.setLabels();
            bankModelViewSynchronizer.displayDataObject(childItem.getBankAccount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Change NAN -------------------------------------------------------------------------------

    public static void displayNominatedAccNumberChildList(Context context, View convertView,
                                                          ViewHolder viewHolder,
                                                          ChildItem childItem){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        viewHolder.get(convertView, R.id.listview_row_1);

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            //--- ACCOUNT NO
            metaDataList = new ModelPropertyViewMetaList(context);
            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_common_account_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(BankAccount.class, viewMeta);

            ModelViewSynchronizer<BankAccount> bankModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BankAccount.class, metaDataList,
                            convertView,AppConstants.EMPTY_STRING);
            bankModelViewSynchronizer.setLabels();
            bankModelViewSynchronizer.displayDataObject(childItem.getBankAccount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Change CDA Trustee -----------------------------------------------------------------------

    public static void displayCdaTrusteeChildList(final Context context, View convertView,
                                                  ViewHolder viewHolder,
                                                  final ChildItem childItem){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        viewHolder.get(convertView, R.id.listview_row_1);
        viewHolder.get(convertView, R.id.listview_row_2);
        viewHolder.get(convertView, R.id.listview_row_3);
        viewHolder.get(convertView, R.id.listview_row_4);

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            //--- CDA NO
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT );
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_services_cda_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(BankAccount.class, viewMeta);

            ModelViewSynchronizer<BankAccount> nahModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BankAccount.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            nahModelViewSynchronizer.setLabels();
            nahModelViewSynchronizer.displayDataObject(childItem.getBankAccount());

            //--- BANK'S T&C
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_NAME);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_services_cda_bank_tc);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(Bank.class, viewMeta);

            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_TC_URL);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setEditType(ModelPropertyEditType.HYPER_LINK);

            metaDataList.add(Bank.class, viewMeta);

            ModelViewSynchronizer<Bank> bankModelViewSynchronizer =
                    new ModelViewSynchronizer<Bank>(Bank.class, metaDataList,
                            convertView,AppConstants.EMPTY_STRING);
            bankModelViewSynchronizer.setLabels();
            bankModelViewSynchronizer.displayDataObject(childItem.getBankAccount().getBank());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Change CDA Bank --------------------------------------------------------------------------

    public static void displayCdaBankChildList(Context context, View convertView,
                                               ViewHolder viewHolder,
                                               ChildItem childItem){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        viewHolder.get(convertView, R.id.listview_row_1);
        viewHolder.get(convertView, R.id.listview_row_2);

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            //--- CDA NO
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT );
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_services_cda_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(BankAccount.class, viewMeta);

            ModelViewSynchronizer<BankAccount> nahModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BankAccount.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            nahModelViewSynchronizer.setLabels();
            nahModelViewSynchronizer.displayDataObject(childItem.getBankAccount());

            //--- CDA BANK
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_NAME);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_services_cda_bank);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(Bank.class, viewMeta);

            ModelViewSynchronizer<Bank> bankModelViewSynchronizer =
                    new ModelViewSynchronizer<Bank>(Bank.class, metaDataList,
                            convertView,AppConstants.EMPTY_STRING);
            bankModelViewSynchronizer.setLabels();
            bankModelViewSynchronizer.displayDataObject(childItem.getBankAccount().getBank());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Transfer CDA to PSEA ---------------------------------------------------------------------

    public static void displayTransferPseaChildList(Context context, View convertView,
                                                    ViewHolder viewHolder,
                                                    ChildItem childItem){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        viewHolder.get(convertView, R.id.listview_row_1);
        viewHolder.get(convertView, R.id.listview_row_2);

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            metaDataList = new ModelPropertyViewMetaList(context);

            //--- CDA NO
            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_services_cda_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_BANK_NAME);
            metaDataList.add(BankAccount.class, viewMeta);

            ModelViewSynchronizer<BankAccount> nahModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BankAccount.class, metaDataList,
                            convertView, SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS);
            nahModelViewSynchronizer.setLabels();
            nahModelViewSynchronizer.displayDataObject(childItem.getBankAccount());

            //--- CDA BANK
            metaDataList = new ModelPropertyViewMetaList(context);

            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_NAME);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_services_cda_bank);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_BANK_NAME);
            metaDataList.add(Bank.class, viewMeta);

            ModelViewSynchronizer<Bank> bankModelViewSynchronizer =
                    new ModelViewSynchronizer<Bank>(Bank.class, metaDataList,
                            convertView, SerializedNames.SN_BANK_NAME);
            bankModelViewSynchronizer.setLabels();
            bankModelViewSynchronizer.displayDataObject(childItem.getBankAccount().getBank());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Change Birth Order -----------------------------------------------------------------------

    public static void displayChangeBirthOrderChildList(Context context, View convertView,
                                                        ViewHolder viewHolder,
                                                        ChildItem childItem){

        TextView tvLabel = (TextView) convertView.findViewById(R.id.change_birth_order_step_two_name);
        tvLabel.setText(childItem.getChild().getName());
        TextView tvLael2 = (TextView) convertView.findViewById(R.id.change_birth_order_step_two_order);
        tvLael2.setText(""+childItem.getChild().getBirthOrder());

    }

    //--- Acceptance of CDA Bank T&C ---------------------------------------------------------------

    public static void displayAcceptanceOfCdaBankTAndC(Context context, View convertView,
                                         ViewHolder viewHolder,
                                         ChildItem childItem){
        ModelPropertyViewMetaList metaDataList = null;
        ModelPropertyViewMeta viewMeta = null;

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            metaDataList = new ModelPropertyViewMetaList(context);

            //--- CDA NO
            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_services_cda_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_BANK_NAME);

            metaDataList.add(BankAccount.class, viewMeta);

            ModelViewSynchronizer<BankAccount> nahModelViewSynchronizer =
                    new ModelViewSynchronizer<BankAccount>(BankAccount.class, metaDataList,
                            convertView, SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS);
            nahModelViewSynchronizer.setLabels();
            nahModelViewSynchronizer.displayDataObject(childItem.getBankAccount());

            metaDataList = new ModelPropertyViewMetaList(context);

            //--- BANK T&C
            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_NAME);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_services_cda_bank_tc);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_BANK_TC_URL);

            metaDataList.add(Bank.class, viewMeta);

            viewMeta = new ModelPropertyViewMeta(Bank.FIELD_BANK_TC_URL);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setEditType(ModelPropertyEditType.HYPER_LINK);

            metaDataList.add(Bank.class, viewMeta);

            ModelViewSynchronizer<Bank> bankModelViewSynchronizer =
                    new ModelViewSynchronizer<Bank>(Bank.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            bankModelViewSynchronizer.setLabels();
            bankModelViewSynchronizer.displayDataObject(childItem.getBankAccount().getBank());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- Open CDA ---------------------------------------------------------------------------------

    public static void displayOpenCdaChildList(Context context, View convertView,
                                               ViewHolder viewHolder,
                                               ChildItem childItem){
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        TextView tvLabel = (TextView) convertView.findViewById(R.id.tvListRowHeader);
        tvLabel.setText(childItem.getChild().getName());

        try {
            //--- CURRENT TRUSTEE
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelResourceId(R.string.label_services_open_cda_trustee);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);

            metaDataList.add(Adult.class, viewMeta);

            //--- RELATIONSHIP
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_RELATIONSHIP);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelResourceId(R.string.label_services_open_cda_relationship);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_RELATIONSHIP);

            metaDataList.add(Adult.class, viewMeta);


            ModelViewSynchronizer<Adult> adultModelViewSynchronizer =
                    new ModelViewSynchronizer<Adult>(Adult.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            adultModelViewSynchronizer.setLabels();
            adultModelViewSynchronizer.displayDataObject(childItem.getChildDevAccTrustee());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
