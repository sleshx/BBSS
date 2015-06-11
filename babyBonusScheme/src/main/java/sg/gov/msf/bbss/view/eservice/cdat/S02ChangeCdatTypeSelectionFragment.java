package sg.gov.msf.bbss.view.eservice.cdat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.adapter.DisplayChildArrayAdapter;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.task.DataCodes;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.entity.people.CdaTrustee;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdat;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S02ChangeCdatTypeSelectionFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private static int PERSON_PARTICULARS_PAGE_INDEX = 2;
    private static int PERSON_ADDRESS_PAGE_INDEX = 3;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private View listFooterView;
    private View listHeaderView;

    private DisplayChildArrayAdapter adapter;
    private List<ChildItem> childItems;
    private ModelViewSynchronizer<CdaTrustee> adultModelViewSynchronizer;
    private ChangeCdatFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S02ChangeCdatTypeSelectionFragment newInstance(int index) {
        S02ChangeCdatTypeSelectionFragment fragment = new S02ChangeCdatTypeSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName() , "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((ChangeCdatFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName() , "----------onPauseFragment()");

        ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();
        CdaTrustee cdaTrustee = adultModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = adultModelViewSynchronizer.getValidationInfo();

        serviceChangeCdat.setCdaTrustee(cdaTrustee);
        serviceChangeCdat.clearClientPageValidations(CURRENT_POSITION);
        serviceChangeCdat.addSectionPage(SerializedNames.SEC_SERVICE_CHANGE_CDAT_PARTICULARS, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            serviceChangeCdat.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        if (isValidationRequired) {
            return  BabyBonusValidationHandler.validateSameRelationshipType(context,
                    ChildListType.CHANGE_CDAT,
                    serviceChangeCdat.getCdaTrustee().getRelationshipType(),
                    serviceChangeCdat.getChildItems(), adapter);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        childItems = app.getServiceChangeCdat().getChildItems();
        adapter = new DisplayChildArrayAdapter(context, childItems, ChildListType.CHANGE_CDAT);

        listFooterView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_service_list_footer_with_type, null); //Have to do here because of ModelViewSynchronizer

        adultModelViewSynchronizer = new ModelViewSynchronizer<CdaTrustee>(
                CdaTrustee.class, getMetaData(),
                listFooterView, SerializedNames.SEC_SERVICE_CHANGE_CDAT_PARTICULARS);

        displayData(displayValidationErrors());
        setButtonClicks();

        getInitialRelationshipTypeIsThirdParty();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        Log.i(getClass().getName(), "----------setButtonClicks()");

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInTwo, CURRENT_POSITION, true);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Listview - Header
        listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Listview - Populate Data
        listView.setAdapter(adapter);
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, true,errorMessage);

        //Dropdown - Populate Data
        CdaTrustee cdat = app.getServiceChangeCdat().getCdaTrustee();

        if(cdat == null){
            cdat = new CdaTrustee();
        }

        adultModelViewSynchronizer.setLabels();
        adultModelViewSynchronizer.setHeaderTitle(R.id.section_new_type_header,
                R.string.label_services_new_cdat_header);
        adultModelViewSynchronizer.displayDataObject(cdat);
    }

    private String displayValidationErrors() {
        ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceChangeCdat.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceChangeCdat.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_CDAT_PARTICULARS);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                adultModelViewSynchronizer.displayValidationErrors(errorMessageList);

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }
        return errorMessage;
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        ArrayAdapter<RelationshipType> relationshipTypeAdapter =
                new ArrayAdapter<RelationshipType>(context, android.R.layout.simple_list_item_1,
                        RelationshipType.values());

        ArrayAdapter<GenericDataItem> trusteeChangeReasonAdapter =
                new ArrayAdapter<GenericDataItem>(context, android.R.layout.simple_list_item_1,
                        new ArrayList<GenericDataItem>());

        servicesHelper.displayGenericData(MasterDataType.TRUSTEE_BANK_CHANGE_REASON,
                trusteeChangeReasonAdapter);

        try {
            //New Cdat Type
            viewMeta = new ModelPropertyViewMeta(CdaTrustee.FIELD_RELATIONSHIP);
            viewMeta.setIncludeTagId(R.id.services_new_type);
            viewMeta.setLabelResourceId(R.string.label_services_new_cdat_label);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_RELATIONSHIP);
            viewMeta.setDropDownAdapter(relationshipTypeAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new TypeSelectionChangeListener(relationshipTypeAdapter));

            metaDataList.add(CdaTrustee.class, viewMeta);

            //Change Reason
            viewMeta = new ModelPropertyViewMeta(CdaTrustee.FIELD_CHANGE_REASON);
            viewMeta.setIncludeTagId(R.id.services_change_reason);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_CDAT_CHANGE_REASON);
            viewMeta.setDropDownAdapter(trusteeChangeReasonAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new TrusteeChangeReasonItemSelectionListener(trusteeChangeReasonAdapter));

            metaDataList.add(CdaTrustee.class, viewMeta);

            //Change Reason -- Other
            viewMeta = new ModelPropertyViewMeta(CdaTrustee.FIELD_CHANGE_REASON_OTHER);
            viewMeta.setIncludeTagId(R.id.services_change_reason_other);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_CDAT_CHANGE_REASON_OTHER);

            metaDataList.add(CdaTrustee.class, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void getInitialRelationshipTypeIsThirdParty() {
        CdaTrustee cdat = app.getServiceChangeCdat().getCdaTrustee();
        boolean isThirdParty = false;
        if (cdat != null) {
            RelationshipType selectedRelationshipType = cdat.getRelationshipType();
            if (selectedRelationshipType != null) {
                isThirdParty = selectedRelationshipType.equals(RelationshipType.TRUSTEE);
            }
        }

        app.getServiceChangeCdat().setThirdParty(isThirdParty);
    }

    //--- DROPDOWN SELECTION LISTENERS  ------------------------------------------------------------

    private class TypeSelectionChangeListener
            implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<RelationshipType> adapter;

        public TypeSelectionChangeListener(ArrayAdapter<RelationshipType> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            boolean isThirdParty = adapter.getItem(position).equals(RelationshipType.TRUSTEE);
            app.getServiceChangeCdat().setThirdParty(isThirdParty);

            if (!isThirdParty) {
                if (app.getServiceChangeCdat().getCdaTrustee() != null) {
                    app.getServiceChangeCdat().setCdaTrustee(null);
                    app.getServiceChangeCdat().clearClientPageValidations(PERSON_PARTICULARS_PAGE_INDEX);
                    app.getServiceChangeCdat().clearClientPageValidations(PERSON_ADDRESS_PAGE_INDEX);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class TrusteeChangeReasonItemSelectionListener
            implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<GenericDataItem> adapter;

        public TrusteeChangeReasonItemSelectionListener(ArrayAdapter<GenericDataItem> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            GenericDataItem item = adapter.getItem(position);

            LinearLayout otherReasonView = (LinearLayout)rootView.findViewById(
                    R.id.services_change_reason_other);

            if (item.getName().equals(DataCodes.CHANGE_REASON_OTHER)){
                otherReasonView.setVisibility(View.VISIBLE);
                adultModelViewSynchronizer.setFieldMandatory(CdaTrustee.FIELD_CHANGE_REASON_OTHER, true);

            } else {
                otherReasonView.setVisibility(View.GONE);
                adultModelViewSynchronizer.setFieldMandatory(CdaTrustee.FIELD_CHANGE_REASON_OTHER, false);

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            LinearLayout otherReasonView = (LinearLayout)rootView.findViewById(
                    R.id.services_change_reason_other);
            otherReasonView.setVisibility(View.GONE);
        }
    }
}
