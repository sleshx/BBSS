package sg.gov.msf.bbss.view.enrolment.main;


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
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.task.GetLocalAddressTask;
import sg.gov.msf.bbss.logic.type.AddressType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class E13CdaTrusteeAddressFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class ADDRESS_CLASS = Address.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<Address> addressModelViewSynchronizer;
    private boolean isAddressLocal;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E13CdaTrusteeAddressFragment newInstance(int index) {
        E13CdaTrusteeAddressFragment fragment = new E13CdaTrusteeAddressFragment();
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
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        Address address = addressModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = addressModelViewSynchronizer.getValidationInfo();

        enrolmentForm.getCdaTrustee().setPostalAddress(address);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_CDAT_ADDRESS, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            enrolmentForm.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        isAddressLocal = app.getEnrolmentForm().getCdaTrustee().getAddressType().equals(
                AddressType.LOCAL);
        addressModelViewSynchronizer = new ModelViewSynchronizer<Address>(
                ADDRESS_CLASS, getMetaData(), rootView, SerializedNames.SEC_ENROLMENT_CDAT_ADDRESS);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        int instructionDesc = 0;

        Address address = app.getEnrolmentForm().getCdaTrustee().getPostalAddress();
        if(address == null){
            switch (app.getEnrolmentForm().getCdaTrusteeType()) {
                case FATHER:
                    address = app.getEnrolmentForm().getFather().getPostalAddress();
                    break;
                case MOTHER:
                    address = app.getEnrolmentForm().getMother().getPostalAddress();
                    break;
                case TRUSTEE:
                    address = new Address();
                    break;
            }
            if (address == null) {
                address = new Address();
            }
        }

        addressModelViewSynchronizer.setLabels();
        addressModelViewSynchronizer.setHeaderTitle(R.id.section_address,
                R.string.label_cda_trustee_long);
        addressModelViewSynchronizer.displayDataObject(address);

        instructionDesc = ((isAddressLocal) ? R.string.label_enrolment_fill_section_below_local_address :
                R.string.label_enrolment_fill_section_below_foreign_address);
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, instructionDesc,
                true, errorMessage);

        hideUnwantedIncludeLayouts();
        showAddress();
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_CDAT_ADDRESS);

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

    //--- HELPERS ----------------------------------------------------------------------------------

    private void hideUnwantedIncludeLayouts() {
        rootView.findViewById(R.id.screen_1buttons).setVisibility(View.GONE);
        rootView.findViewById(R.id.screen_2buttons).setVisibility(View.GONE);
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

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        boolean isFocusable = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);

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
                viewMeta.setFocusable(isFocusable);

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
                viewMeta.setFocusable(isFocusable);

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
                viewMeta.setFocusable(isFocusable);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Block / House Number
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_BLOCK_HOUSE_NO);
                viewMeta.setIncludeTagId(R.id.edit_local_block_no);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_BLK_HOUSE_NO);
                viewMeta.setFocusable(isFocusable);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Street Name
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_STREET_NAME);
                viewMeta.setIncludeTagId(R.id.edit_local_street_name);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_STREET);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_STREET);
                viewMeta.setFocusable(isFocusable);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Building Name
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_BUILDING_NAME);
                viewMeta.setIncludeTagId(R.id.edit_local_building_name);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_BUILDING);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_BUILDING);
                viewMeta.setFocusable(isFocusable);

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
                viewMeta.setFocusable(isFocusable);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Address 2
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_ADDRESS2);
                viewMeta.setIncludeTagId(R.id.edit_foreign_address2);
                viewMeta.setMandatory(true);
                viewMeta.setEditable(true);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_ADDRESS2);
                viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADDRESS_FOREIGN_2);
                viewMeta.setFocusable(isFocusable);

                metaDataList.add(ADDRESS_CLASS, viewMeta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- LISTENERS --------------------------------------------------------------------------------

    private class FetchAddressFromPostalCodeClickListener
            implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final Address localAddress = addressModelViewSynchronizer.getDataObject();

            if(localAddress == null || localAddress.getPostalCode()==0){
                return;
            }

            GetLocalAddressTask addressTask =
                    new GetLocalAddressTask(context, new MasterDataListener<Address>() {
                        @Override
                        public void onMasterData(Address address) {
                            if(address == null){
                                address = new Address();
                                address.setPostalCode(localAddress.getPostalCode());

                                MessageBox.show(context,
                                        StringHelper.getStringByResourceId(context, R.string.error_common_invalid_postalcode),
                                        true, true, R.string.btn_ok, false, 0, null);
                            }

                            addressModelViewSynchronizer.displayDataObject(address);
                        }
                    });

            addressTask.execute(localAddress.getPostalCode());
        }
    }
}
