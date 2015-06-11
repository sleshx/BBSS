package sg.gov.msf.bbss.view.home.familyview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.DatePickerPopupCreator;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 * Modified to save from and to dates by chuanhe
 */
public class H02FamilyViewChildDevAccDetailsFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class CHILD_CLASS = Child.class;
    private static Class ADULT_CLASS = Adult.class;
    private static Class CDA_BANK_ACCOUNT_CLASS = CdaBankAccount.class;

    private View rootView;
    private Context context;
    private BbssApplication app;
    private FamilyViewFragmentContainerActivity fragmentContainer;

    private boolean isChildVisible = true;

    private EditText etFrom;
    private EditText etTo;

    private ModelViewSynchronizer<Child> childModelViewSynchronizer;
    private ModelViewSynchronizer<Adult> cdaTrusteeModelViewSynchronizer;
    private ModelViewSynchronizer<CdaBankAccount> cdaBankModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static H02FamilyViewChildDevAccDetailsFragment newInstance(int index) {
        H02FamilyViewChildDevAccDetailsFragment fragment = new H02FamilyViewChildDevAccDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_family_view_child_dev_acc_details, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((FamilyViewFragmentContainerActivity) context);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        displayData();
        setButtonClicks();

        return rootView;
    }

    //--- WIZARD NAVIGATION ------------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        app.getChildStatement().setFromDate(StringHelper.parseDate(etFrom.getText().toString()));
        app.getChildStatement().setToDate(StringHelper.parseDate(etTo.getText().toString()));

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, true);
        fragmentContainer.setBottomButtonClick(rootView, R.id.btnFirstInOne, CURRENT_POSITION, true);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        ChildStatement childStmt = app.getChildStatement();

        setChildSectionVisibility(rootView, childStmt.isChildDataDisplayed());

        //---Child
        childModelViewSynchronizer = new ModelViewSynchronizer<Child>(CHILD_CLASS,
                getChildMetadata(), rootView, AppConstants.EMPTY_STRING);
        childModelViewSynchronizer.setLabels();
        childModelViewSynchronizer.setHeaderTitle(R.id.section_child_particulars,
                R.string.label_child);
        childModelViewSynchronizer.displayDataObject(childStmt.getChildItem().getChild());

        //---CDA Trustee
        cdaTrusteeModelViewSynchronizer = new ModelViewSynchronizer<Adult>(ADULT_CLASS,
                getCdaTrusteeMetaData(), rootView, AppConstants.EMPTY_STRING);
        cdaTrusteeModelViewSynchronizer.setLabels();
        cdaTrusteeModelViewSynchronizer.setHeaderTitle(R.id.section_cdat_particulars,
                R.string.label_adult_type_cdat);
        cdaTrusteeModelViewSynchronizer.displayDataObject(childStmt.getChildDevAccountTrustee());

        //---Child Development Bank Account
        cdaBankModelViewSynchronizer = new ModelViewSynchronizer<CdaBankAccount>(CDA_BANK_ACCOUNT_CLASS,
                getChildDevAccountMetaData(), rootView, AppConstants.EMPTY_STRING);
        cdaBankModelViewSynchronizer.setLabels();
        cdaBankModelViewSynchronizer.setHeaderTitle(R.id.section_cda_details,
                R.string.label_child_dev_acc_detail);
        cdaBankModelViewSynchronizer.displayDataObject(childStmt.getCdaBankAccount());

        //--- To From Date
        displayFromToDates();

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView,
                R.string.title_activity_family_view_cda_details);
        fragmentContainer.setInstructions(rootView.findViewById(R.id.screen_instructions),
                CURRENT_POSITION, 0, false);
    }

    private void displayFromToDates() {
        TextView tvHeader = (TextView) rootView.findViewById(R.id.section_cda_matching_history);
        TextView tvFrom = (TextView) rootView.findViewById(R.id.cda_matching_from_date).
                findViewById(R.id.tvLabel);
        TextView tvTo = (TextView) rootView.findViewById(R.id.cda_matching_to_date).
                findViewById(R.id.tvLabel);

        etFrom = (EditText) rootView.findViewById(R.id.cda_matching_from_date).
                findViewById(R.id.etValue);
        etTo = (EditText) rootView.findViewById(R.id.cda_matching_to_date).
                findViewById(R.id.etValue);

        boolean isShow = app.getChildStatement().getChildItem().isShowGovtMatching() &&
                app.getChildStatement().getChildDevAccountHistories().size() > 0;
        setDateSortSectionSectionVisibility(rootView, isShow);

        tvHeader.setText(R.string.label_cda_matching_history);
        tvFrom.setText(R.string.label_family_view_from_date);
        tvTo.setText(R.string.label_family_view_to_date);

        etFrom.setFocusable(false);
        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerPopupCreator.loadDatePickerDialog(context, etFrom, false,
                        R.string.label_family_view_from_date);
            }
        });

        etTo.setFocusable(false);
        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerPopupCreator.loadDatePickerDialog(context, etTo, false,
                        R.string.label_family_view_to_date);
            }
        });
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getChildMetadata() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(getActivity());
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---BIRTH CERTIFICATE NO
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTH_CERT_NO);
            viewMeta.setIncludeTagId(R.id.family_view_child_birth_cert);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.family_view_child_name);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTHDAY
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.family_view_child_birthday);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(CHILD_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    private ModelPropertyViewMetaList getCdaTrusteeMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---NRIC
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NRIC);
            viewMeta.setIncludeTagId(R.id.family_view_cdat_id);
            viewMeta.setLabelResourceId(R.string.label_adult_id_no);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.family_view_cdat_name);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---BIRTHDAY
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.family_view_cdat_birthday);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(ADULT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    private ModelPropertyViewMetaList getChildDevAccountMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(getActivity());
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---BANK NAME
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_BANK);
            viewMeta.setIncludeTagId(R.id.cda_bank_name);
            viewMeta.setLabelResourceId(R.string.label_child_dev_acc_bank);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            //---BANK ACCOUNT
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.cda_bank_acc_no);
            viewMeta.setLabelResourceId(R.string.label_common_account_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            //---CDA EXPIRY
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CDA_EXPIRY);
            viewMeta.setIncludeTagId(R.id.cda_expiry_date);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            //---CDA CAP
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CDA_CAP);
            viewMeta.setIncludeTagId(R.id.cda_cap);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            //---CDA REMAINING CAP
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CDA_REMAINING_CAP);
            viewMeta.setIncludeTagId(R.id.cda_remaining_cap);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            //---CDA TOTAL DEPOSIT
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CDA_TOT_DEPOSIT);
            viewMeta.setIncludeTagId(R.id.cda_total_deposit);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            //CDA GOVT. MATCHING
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CDA_TOT_GOV_MATCHING);
            viewMeta.setIncludeTagId(R.id.cda_total_govt_matching);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void setChildSectionVisibility(View view, boolean isAlreadyDisplayed) {
        int visible = isAlreadyDisplayed ? View.GONE : View.VISIBLE;

        (view.findViewById(R.id.section_child_particulars)).setVisibility(visible);
        (view.findViewById(R.id.family_view_child_birth_cert)).setVisibility(visible);
        (view.findViewById(R.id.family_view_child_name)).setVisibility(visible);
        (view.findViewById(R.id.family_view_child_birthday)).setVisibility(visible);

        if (!isAlreadyDisplayed) {
            app.getChildStatement().setChildDataDisplayed(true);
        }
    }

    private void setDateSortSectionSectionVisibility(View view, boolean isShow) {
        int visible = isShow ? View.VISIBLE : View.GONE;

        (view.findViewById(R.id.section_cda_matching_history)).setVisibility(visible);
        (view.findViewById(R.id.cda_matching_from_date)).setVisibility(visible);
        (view.findViewById(R.id.cda_matching_to_date)).setVisibility(visible);
    }
}
