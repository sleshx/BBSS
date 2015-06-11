package sg.gov.msf.bbss.view.home.familyviewold;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.familyview.FamilyViewCashGiftArrayAdapter;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class H02FamilyViewCashGiftDetailsFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class CHILD_CLASS = Child.class;
    private static Class ADULT_CLASS = Adult.class;

    private View rootView;
    private Context context;
    private BbssApplication app;
    private FamilyViewFragmentContainerActivity fragmentContainer;

    private ListView listView;

    private ModelViewSynchronizer<Child> childModelViewSynchronizer;
    private ModelViewSynchronizer<Adult> nahModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static H02FamilyViewCashGiftDetailsFragment newInstance(int index) {
        H02FamilyViewCashGiftDetailsFragment fragment = new H02FamilyViewCashGiftDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((FamilyViewFragmentContainerActivity) context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- WIZARD NAVIGATION ------------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        if (app.getProgressDialog() != null) {
            app.getProgressDialog().dismiss();
        }

        displayData(getActivity().getLayoutInflater());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, true);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInOne, CURRENT_POSITION, true);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData(LayoutInflater inflater) {
        ChildStatement childStmt = app.getChildStatement();

        //Listview - Header
        View listHeaderView = inflater.inflate(
                R.layout.fragment_family_view_cash_gift_header, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        View listFooterView = inflater.inflate(
                R.layout.fragment_family_view_cash_gift_footer, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        listView.setAdapter(new FamilyViewCashGiftArrayAdapter(context, childStmt.getCashGiftList()));
        TextView tvHeader = (TextView) listHeaderView.findViewById(
                R.id.family_view_cg_list_view_header);
        tvHeader.setText(R.string.label_cash_gift);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView,
                R.string.title_activity_family_view_cash_gift_details);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, false);

        //---Child
        childModelViewSynchronizer = new ModelViewSynchronizer<Child>(CHILD_CLASS,
                getChildMetadata(), rootView, AppConstants.EMPTY_STRING);
        childModelViewSynchronizer.setLabels();
        childModelViewSynchronizer.setHeaderTitle(R.id.section_child_particulars,
                R.string.label_child);
        childModelViewSynchronizer.displayDataObject(childStmt.getChildItem().getChild());

        //---Nominated Account Holder
        nahModelViewSynchronizer = new ModelViewSynchronizer<Adult>(ADULT_CLASS,
                getNominatedAccountHolderMetadata(), rootView, AppConstants.EMPTY_STRING);
        nahModelViewSynchronizer.setLabels();
        nahModelViewSynchronizer.setHeaderTitle(R.id.section_nah_particulars,
                R.string.label_adult_type_nah);
        nahModelViewSynchronizer.displayDataObject(childStmt.getNominatedAccountHolder());
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getChildMetadata() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---BIRTH CERTIFICATE NO
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NRIC);
            viewMeta.setIncludeTagId(R.id.family_view_child_birth_cert);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.family_view_child_name);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTHDAY
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.family_view_child_birthday);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(CHILD_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    private ModelPropertyViewMetaList getNominatedAccountHolderMetadata() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---NRIC
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NRIC);
            viewMeta.setIncludeTagId(R.id.family_view_nah_id);
            viewMeta.setLabelResourceId(R.string.label_adult_id_no);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.family_view_nah_name);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---BIRTHDAY
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.family_view_nah_birthday);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(ADULT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

}