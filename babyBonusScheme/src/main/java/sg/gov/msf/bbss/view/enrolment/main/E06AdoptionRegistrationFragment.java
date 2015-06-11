package sg.gov.msf.bbss.view.enrolment.main;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.SupportingDocumentsHelper;
import sg.gov.msf.bbss.logic.adapter.enrolment.EnrolmentAdoptionChildAdapter;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.logic.type.YesNoType;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;

/**
 * Created by bandaray
 */
public class E06AdoptionRegistrationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class CHILD_REG_CLASS = ChildRegistration.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private View listHeaderView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<ChildRegistration> childRegModelViewSynchronizer;
    private List<SupportingFile> supportingFiles = new ArrayList<SupportingFile>();
    private EnrolmentAdoptionChildAdapter adapter;

    private boolean isHeaderLoaded;
    private boolean isFooterLoaded;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E06AdoptionRegistrationFragment newInstance(int index) {
        E06AdoptionRegistrationFragment fragment = new E06AdoptionRegistrationFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.i(getClass().getName(), "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        listView = (ListView) rootView.findViewById(R.id.lvMain);
        isHeaderLoaded = false;
        isFooterLoaded = false;

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName() , "----------onPauseFragment()");

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        childRegModelViewSynchronizer = new ModelViewSynchronizer<ChildRegistration>(
                ChildRegistration.class, getMetaData(), rootView,
                SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS);

        displayData();
        setButtonClicks();

        return false;
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getClass().getName() , "----------onActivityResult()");

        if (requestCode == 1) {
            populateListView(app.getEnrolmentForm().getChildRegistration());
        } else if(requestCode == 0) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                        context, getActivity().getLayoutInflater());
                docHelper.generateUploadFileList(data, true,
                        new MasterDataListener<ServerResponse>() {
                    @Override
                    public void onMasterData(ServerResponse serverResponse) {
                        if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                            SupportingFile supportingFile = new SupportingFile();
                            supportingFile.setCode(serverResponse.getCode());
                            supportingFile.setFileName(serverResponse.getFile().getName());

                            supportingFiles.add(supportingFile);
                        } else {
                            Toast.makeText(context, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        Log.i(getClass().getName() , "----------setButtonClicks()");

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);

        setBrowseDocButtonClick();
    }

    private void setBrowseDocButtonClick() {
        Log.i(getClass().getName() , "----------setBrowseDocButtonClick()");

        LinearLayout llSupDocSection = (LinearLayout)rootView.findViewById(R.id.supporting_docs);

        Button browseButton = (Button)llSupDocSection.findViewById(R.id.btnFirstInOne);
        browseButton.setText(R.string.btn_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, 0);
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ChildRegistration childRegistration = app.getEnrolmentForm().getChildRegistration();

        if(childRegistration == null){
            childRegistration = new ChildRegistration();
        }

        //Listview - Header
        if (!isHeaderLoaded) {
            listHeaderView = inflater.inflate(
                    R.layout.section_header_row_and_instruction_row, null);
            listView.addHeaderView(listHeaderView);
            isHeaderLoaded = true;
        }

        //Listview - Footer
        LinearLayout listFooterView = (LinearLayout) inflater.inflate(
                R.layout.fragment_enrolment_other_birth_footer, null);
        if (! isFooterLoaded) {
            listView.addFooterView(listFooterView);
            isFooterLoaded = true;
        }

        //Listview - Populate Data
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child_reg_type_adoption);
        populateListView(childRegistration);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION,
                R.string.label_enrolment_fill_section_below_child_adoption, false,
                AppConstants.EMPTY_STRING);

        //Section - Questions
        childRegModelViewSynchronizer.setLabels();
        childRegModelViewSynchronizer.displayDataObject(childRegistration);

        //Section - Supporting Documents
        TextView supDocHeader = (TextView)rootView.findViewById(R.id.document_listing_section_header);
        supDocHeader.setText(R.string.label_supporting_doc);

        //Section - Supporting Documents
        WebView wvDeclarationDesc = (WebView) listFooterView.findViewById(
                R.id.wvScreenDescription);
        wvDeclarationDesc.loadData(StringHelper.getJustifiedString(context,
                R.string.label_enrolment_adoption_adult_supporting_doc_desc,
                R.color.theme_gray_default_bg), "text/html", "utf-8");

        hideUnwantedLayouts();
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        ArrayAdapter<YesNoType> yesNoTypeArrayAdapter =
                new ArrayAdapter<YesNoType>(context, android.R.layout.simple_list_item_1,
                        YesNoType.values());

        try {
            //---IS MARRIED
            viewMeta = new ModelPropertyViewMeta(ChildRegistration.FIELD_REG_IS_MARRIED);
            viewMeta.setIncludeTagId(R.id.edit_is_married);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_REG_IS_MARRIED);
            viewMeta.setDropDownAdapter(yesNoTypeArrayAdapter);

            metaDataList.add(CHILD_REG_CLASS, viewMeta);

            //---IS REGISTERED IN SINGAPORE
            viewMeta = new ModelPropertyViewMeta(ChildRegistration.FIELD_REG_IS_REG_IN_SINGAPORE);
            viewMeta.setIncludeTagId(R.id.edit_is_marriage_reg_in_singapore);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_REG_IS_MARRIAGE_REG_IN_SINGAPORE);
            viewMeta.setDropDownAdapter(yesNoTypeArrayAdapter);

            metaDataList.add(CHILD_REG_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void populateListView(ChildRegistration childRegistration) {
        adapter = new EnrolmentAdoptionChildAdapter(context, app, R.layout.layout_add_child_item,
                childRegistration.getChildren());

        if (childRegistration.getChildren().size() > 0 &&
                childRegistration.getRegistrationType().equals(ChildRegistrationType.POST_BIRTH)) {
            listView.setAdapter(adapter);
        }
    }

    private void hideUnwantedLayouts() {
        ((LinearLayout)rootView.findViewById(R.id.edit_is_married)).setVisibility(View.GONE);
        ((LinearLayout)rootView.findViewById(R.id.edit_is_marriage_reg_in_singapore)).setVisibility(View.GONE);

        ((Button) rootView.findViewById(R.id.btnFirstInOne)).setVisibility(View.GONE);
    }
}

