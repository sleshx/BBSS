package sg.gov.msf.bbss.view.eservice.nah;

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
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNah;

/**
 * Created by bandaray
 */
public class S02ChangeNahTypeSelectionFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private static int PERSON_PARTICULARS_PAGE_INDEX = 2;
    private static int PERSON_ADDRESS_PAGE_INDEX = 3;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private View listHeaderView;
    private View listFooterView;

    private DisplayChildArrayAdapter adapter;
    private List<ChildItem> childItems;
    private ModelViewSynchronizer<Adult> adultModelViewSynchronizer;
    private ChangeNahFragmentContainerActivity fragmentContainer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S02ChangeNahTypeSelectionFragment newInstance(int index) {
        S02ChangeNahTypeSelectionFragment fragment = new S02ChangeNahTypeSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName() , "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((ChangeNahFragmentContainerActivity) getActivity());

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName() , "----------onPauseFragment()");

        ServiceChangeNah serviceChangeNah = app.getServiceChangeNah();
        Adult adult = adultModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = adultModelViewSynchronizer.getValidationInfo();

        serviceChangeNah.setNominatedAccHolder(adult);
        serviceChangeNah.clearClientPageValidations(CURRENT_POSITION);
        serviceChangeNah.addSectionPage(SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            serviceChangeNah.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        if (isValidationRequired) {
            return  BabyBonusValidationHandler.validateSameRelationshipType(context,
                    ChildListType.CHANGE_NAH,
                    serviceChangeNah.getNominatedAccHolder().getRelationshipType(),
                    serviceChangeNah.getChildItems(), adapter);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        childItems = app.getServiceChangeNah().getChildItems();
        adapter = new DisplayChildArrayAdapter(context, childItems, ChildListType.CHANGE_NAH);

        adultModelViewSynchronizer = new ModelViewSynchronizer<Adult>(
                Adult.class, getMetaData(),
                rootView, SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS);

        displayData(displayValidationErrors());
        setButtonClicks();

        hideUnwantedIncludeLayouts();
        getInitialRelationshipTypeIsThirdParty();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        LinearLayout llButtons = (LinearLayout) listFooterView.findViewById(R.id.screen_buttons);

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(llButtons, R.id.btnFirstInTwo, CURRENT_POSITION, true);
        fragmentContainer.setCancelButtonClick(llButtons, R.id.btnSecondInTwo);
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
        listFooterView = inflater.inflate(
                R.layout.fragment_service_list_footer_with_type, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Listview - Populate Data
        listView.setAdapter(adapter);
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, true, errorMessage);

        //Dropdown - Populate Data
        Adult nah = app.getServiceChangeNah().getNominatedAccHolder();

        if(nah == null){
            nah = new Adult();
        }

        adultModelViewSynchronizer.setLabels();
        adultModelViewSynchronizer.setHeaderTitle(R.id.section_new_type_header,
                R.string.label_services_new_nah);
        adultModelViewSynchronizer.displayDataObject(nah);
    }

    private String displayValidationErrors() {
        ServiceChangeNah serviceChangeNah = app.getServiceChangeNah();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceChangeNah.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceChangeNah.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS);

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

        try {
            //New NAH Type
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_RELATIONSHIP);
            viewMeta.setIncludeTagId(R.id.services_new_type);
            viewMeta.setLabelResourceId(R.string.label_services_new_nah);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_RELATIONSHIP);
            viewMeta.setDropDownAdapter(relationshipTypeAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new TypeSelectionChangeListener(relationshipTypeAdapter));

            metaDataList.add(Adult.class, viewMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void hideUnwantedIncludeLayouts() {
        listFooterView.findViewById(R.id.services_change_reason).setVisibility(View.GONE);
        listFooterView.findViewById(R.id.services_change_reason_other).setVisibility(View.GONE);
    }

    private void getInitialRelationshipTypeIsThirdParty() {
        Adult nah = app.getServiceChangeNah().getNominatedAccHolder();
        boolean isThirdParty = false;
        if (nah != null) {
            RelationshipType selectedRelationshipType = nah.getRelationshipType();
            if (selectedRelationshipType != null) {
                isThirdParty = selectedRelationshipType.equals(RelationshipType.TRUSTEE);
            }
        }

        app.getServiceChangeNah().setThirdParty(isThirdParty);
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
            app.getServiceChangeNah().setThirdParty(isThirdParty);

            if (!isThirdParty) {
                if (app.getServiceChangeNah().getNominatedAccHolder() != null) {
                    app.getServiceChangeNah().setNominatedAccHolder(null);
                    app.getServiceChangeNah().clearClientPageValidations(PERSON_PARTICULARS_PAGE_INDEX);
                    app.getServiceChangeNah().clearClientPageValidations(PERSON_ADDRESS_PAGE_INDEX);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
