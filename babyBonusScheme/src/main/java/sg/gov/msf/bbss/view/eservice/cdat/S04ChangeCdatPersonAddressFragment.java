package sg.gov.msf.bbss.view.eservice.cdat;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.meta.ViewPositionType;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.AddressType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdat;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S04ChangeCdatPersonAddressFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private static Class ADDRESS_CLASS = Address.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ModelViewSynchronizer<Address> addressModelViewSynchronizer;
    private ChangeCdatFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;
    private boolean isAddressLocal;


    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S04ChangeCdatPersonAddressFragment newInstance(int index) {
        S04ChangeCdatPersonAddressFragment fragment = new S04ChangeCdatPersonAddressFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_person_address, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((ChangeCdatFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();
        Address address = addressModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = addressModelViewSynchronizer.getValidationInfo();

        serviceChangeCdat.getCdaTrustee().setPostalAddress(address);
        serviceChangeCdat.clearClientPageValidations(CURRENT_POSITION);
        serviceChangeCdat.addSectionPage(SerializedNames.SEC_ADDRESS, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            serviceChangeCdat.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        isAddressLocal = app.getServiceChangeCdat().getCdaTrustee().getAddressType().equals(
                AddressType.LOCAL);
        addressModelViewSynchronizer = new ModelViewSynchronizer<Address>(
                Address.class, getMetaData(), rootView,
                SerializedNames.SEC_ADDRESS);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInTwo, CURRENT_POSITION, true);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();
        Address address = serviceChangeCdat.getCdaTrustee().getPostalAddress();

        if(address == null){
            address = new Address();
        }

        addressModelViewSynchronizer.setLabels();
        addressModelViewSynchronizer.setHeaderTitle(R.id.section_address,
                R.string.label_services_new_cdat_header);
        addressModelViewSynchronizer.displayDataObject(address);

        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true, errorMessage);

        hideUnwantedIncludeLayouts();
        showAddress();
    }

    private String displayValidationErrors() {
        ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceChangeCdat.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceChangeCdat.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ADDRESS);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                addressModelViewSynchronizer.displayValidationErrors(errorMessageList);

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

        try {

            if(isAddressLocal) {
                //--Postal Code
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_POSTAL_CODE);
                viewMeta.setIncludeTagId(R.id.edit_local_postal_code);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.INTEGER);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_POST_CODE);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_POSTAL_CODE);
                viewMeta.setViewClickListener(new FetchAddressFromPostalCodeClickListener());

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Floor Number
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_FLOOR_NO);
                viewMeta.setIncludeTagId(R.id.edit_local_unit_no);
                viewMeta.setLabelResourceId(R.string.label_address_unit_no);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_UNIT_NO);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_UNIT_NO);
                viewMeta.setViewPositionType(ViewPositionType.ONE);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Unit Number
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_UNIT_NO);
                viewMeta.setIncludeTagId(R.id.edit_local_unit_no);
                viewMeta.setLabelResourceId(R.string.label_address_unit_no);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_UNIT_NO);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_UNIT_NO);
                viewMeta.setViewPositionType(ViewPositionType.TWO);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Block / House Number
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_BLOCK_HOUSE_NO);
                viewMeta.setIncludeTagId(R.id.edit_local_block_no);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_BLK_HOUSE_NO);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Street Name
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_STREET_NAME);
                viewMeta.setIncludeTagId(R.id.edit_local_street_name);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_STREET);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_STREET);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Building Name
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_BUILDING_NAME);
                viewMeta.setIncludeTagId(R.id.edit_local_building_name);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_BUILDING);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_BUILDING);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

            } else {
                //---Address 1
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_ADDRESS1);
                viewMeta.setIncludeTagId(R.id.edit_foreign_address1);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_ADDRESS1);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_FOREIGN_1);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Address 2
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_ADDRESS2);
                viewMeta.setIncludeTagId(R.id.edit_foreign_address2);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_ADDRESS2);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_FOREIGN_2);

                metaDataList.add(ADDRESS_CLASS, viewMeta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void hideUnwantedIncludeLayouts() {
        ((LinearLayout) rootView.findViewById(R.id.screen_1buttons)).setVisibility(View.GONE);
        ((LinearLayout) rootView.findViewById(R.id.screen_3buttons)).setVisibility(View.GONE);
    }

    private void showAddress() {
        LinearLayout postalCode = (LinearLayout) rootView.findViewById(R.id.edit_local_postal_code);
        LinearLayout unitNo = (LinearLayout) rootView.findViewById(R.id.edit_local_unit_no);
        LinearLayout blockNo = (LinearLayout) rootView.findViewById(R.id.edit_local_block_no);
        LinearLayout street = (LinearLayout) rootView.findViewById(R.id.edit_local_street_name);
        LinearLayout building = (LinearLayout) rootView.findViewById(R.id.edit_local_building_name);
        LinearLayout address1 = (LinearLayout) rootView.findViewById(R.id.edit_foreign_address1);
        LinearLayout address2 = (LinearLayout) rootView.findViewById(R.id.edit_foreign_address2);

        if (isAddressLocal) {
            postalCode.setVisibility(View.VISIBLE);
            unitNo.setVisibility(View.VISIBLE);
            blockNo.setVisibility(View.VISIBLE);
            street.setVisibility(View.VISIBLE);
            building.setVisibility(View.VISIBLE);
            address1.setVisibility(View.GONE);
            address2.setVisibility(View.GONE);
        } else {
            postalCode.setVisibility(View.GONE);
            unitNo.setVisibility(View.GONE);
            blockNo.setVisibility(View.GONE);
            street.setVisibility(View.GONE);
            building.setVisibility(View.GONE);
            address1.setVisibility(View.VISIBLE);
            address2.setVisibility(View.VISIBLE);
        }
    }

    //--- LISTENERS --------------------------------------------------------------------------------

    private class FetchAddressFromPostalCodeClickListener
            implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            servicesHelper.displayAddressByPostalCode(addressModelViewSynchronizer);
        }
    }
}
