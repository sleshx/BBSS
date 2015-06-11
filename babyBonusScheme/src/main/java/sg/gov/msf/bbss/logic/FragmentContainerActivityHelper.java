package sg.gov.msf.bbss.logic;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizardPagerAdapter;
import sg.gov.msf.bbss.logic.masterdata.MasterDataCache;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class FragmentContainerActivityHelper {

    private FragmentWizardPagerAdapter pagerAdapter;
    private Context context;

    public FragmentContainerActivityHelper(Context context) {
        this.context = context;
    }

    public FragmentContainerActivityHelper(Context context,
                                           FragmentWizardPagerAdapter pagerAdapter) {
        this.context = context;
        this.pagerAdapter = pagerAdapter;
    }

    public ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.OnPageChangeListener() {

                int currentPosition = 0;

                @Override
                public void onPageSelected(int newPosition) {
                    boolean isValidationRequired = newPosition > currentPosition; //TODO >

//                    FragmentWizard fragmentToHide = (FragmentWizard) pagerAdapter.getItem(currentPosition);
//                    if (fragmentToHide.onPauseFragment(isValidationRequired)) {
//                        return;
//                    }

                    FragmentWizard fragmentToShow = (FragmentWizard)pagerAdapter.getItem(newPosition);
                    if (fragmentToShow.onResumeFragment()) {
                        return;
                    }

                    currentPosition = newPosition;
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) { }

                public void onPageScrollStateChanged(int arg0) { }
            };

    public void setFragmentInstructions(View rootView, int currentPageNo,
                                        int titleStringId, int descriptionStringId,
                                        boolean isMandatoryMessageRequired, int pageCount,
                                        String errorMessages) {

        LinearLayout headerLayout = (LinearLayout) rootView.findViewById(R.id.screen_instructions);

        TextView tvTitle = (TextView) headerLayout.findViewById(R.id.tvInstructionTitle);
        if (titleStringId > 0) {
            tvTitle.setText(titleStringId);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        WebView wvDescription = (WebView) headerLayout.findViewById(R.id.wvInstructionDesc);
        if (descriptionStringId > 0) {
            wvDescription.setVisibility(View.VISIBLE);
            wvDescription.loadDataWithBaseURL(null, StringHelper.getJustifiedString(context,
                    descriptionStringId, R.color.theme_creme),"text/html", "utf-8", null);
            //wvDescription.loadData(data, "text/html", "utf-8");
        } else {
            wvDescription.setVisibility(View.GONE);
        }

        WebView wvError = (WebView) headerLayout.findViewById(R.id.wvErrorDesc);
        if (!errorMessages.equals(AppConstants.EMPTY_STRING)) {
            wvError.setVisibility(View.VISIBLE);
            wvError.loadDataWithBaseURL(null, StringHelper.getJustifiedErrorString(context,
                    errorMessages, R.color.theme_creme),"text/html", "utf-8", null);
            //wvError.loadData(data, "text/html", "utf-8");
        } else {
            wvError.setVisibility(View.GONE);
        }

        TextView tvMandatory = (TextView) headerLayout.findViewById(R.id.tvInstructionMandatory);
        if (isMandatoryMessageRequired) {
            tvMandatory.setVisibility(View.VISIBLE);
        } else {
            tvMandatory.setVisibility(View.GONE);
        }

        TextView tvStepNo = (TextView) headerLayout.findViewById(R.id.tvInstructionStepNo);
        tvStepNo.setText(context.getString(R.string.text_app_instructions_step_number,
                (currentPageNo + 1), pageCount));
    }

    public void setFragmentTitle(View rootView, int titleStringId) {
        TextView title = (TextView) rootView.findViewById(R.id.tvPageTitle);
        title.setText(StringHelper.getStringByResourceId(context, titleStringId));
    }

    //----------------------------------------------------------------------------------------------

    public void createOkToCancelMessageBox(final MessageBoxButtonClickListener listener) {
        Log.i(getClass().getName(), "----------createOkToCancelMessageBox()");

        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.alert_do_you_want_to_cancel),
                true, true, R.string.btn_ok, true, R.string.btn_cancel,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickPositiveButton()");

                        listener.onClickPositiveButton(dialog, id);
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickNegativeButton()");
                    }
                });
    }

    //----------------------------------------------------------------------------------------------

    public void displayBanks(final ArrayAdapter<Bank> adapter){
        MasterDataCache masterDataCache = new MasterDataCache(context);
        masterDataCache.getBanks(MasterDataType.BANK,
                new MasterDataListener<Bank[]>() {
                    @Override
                    public void onMasterData(Bank[] masterDataItems) {
                        adapter.addAll(masterDataItems);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void displayBankMa(final ArrayAdapter<Bank> adapter){
        MasterDataCache masterDataCache = new MasterDataCache(context);
        masterDataCache.getBanks(MasterDataType.BANK_MA,
                new MasterDataListener<Bank[]>() {
                    @Override
                    public void onMasterData(Bank[] masterDataItems) {
                        adapter.addAll(masterDataItems);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void displayGenericData(MasterDataType masterDataType,
                                   final ArrayAdapter<GenericDataItem> adapter) {
        MasterDataCache masterDataCache = new MasterDataCache(context);
        masterDataCache.getGenericDataItems(masterDataType,
                new MasterDataListener<GenericDataItem[]>() {
                    @Override
                    public void onMasterData(GenericDataItem[] masterDataItems) {
                        adapter.addAll(masterDataItems);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
