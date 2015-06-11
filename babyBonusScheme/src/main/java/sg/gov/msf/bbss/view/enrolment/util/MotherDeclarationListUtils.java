package sg.gov.msf.bbss.view.enrolment.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.adapter.enrolment.EnrolmentMotherDeclarationArrayAdapter;
import sg.gov.msf.bbss.logic.type.ChildDeclarationType;
import sg.gov.msf.bbss.model.entity.childdata.ChildDeclaration;

/**
 * Created by bandaray
 */
public class MotherDeclarationListUtils {

    private View rootView;
    private Context context;
    private Activity activity;
    private BbssApplication app;

    private LayoutInflater inflater;
    private ListView listView;

    private EnrolmentMotherDeclarationArrayAdapter adapter;
    private ArrayList<ChildDeclaration> childDeclarations;
    private ChildDeclaration childDeclaration;
    private boolean isLoaded;

    public MotherDeclarationListUtils(Activity activity) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
    }

    //--- CREATION ---------------------------------------------------------------------------------

    public View onCreateView() {
        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = activity;
        app = (BbssApplication) activity.getApplication();

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        adapter = new EnrolmentMotherDeclarationArrayAdapter(context,
                R.layout.listitem_1rows_multi_choice, ChildDeclarationType.values());

        return rootView;

    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    public boolean onPauseFragment(boolean isValidationRequired) {
        if (isValidationRequired && childDeclaration == null) {
            createNoOptionsSelectedMessageBox();
            return true;
        }

        if(!isLoaded) {
            childDeclarations.add(childDeclaration);
            app.getEnrolmentForm().setChildDeclarations(childDeclarations);
            isLoaded = true;
        }

        return false;
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData() {
        childDeclarations = app.getEnrolmentForm().getChildDeclarations();

        if(childDeclarations == null){
            childDeclarations = new ArrayList<ChildDeclaration>();
        }

        //Listview - Header
        View listHeader = inflater.inflate(R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeader);
        }

        //Listview - Footer
        View  listFooter = inflater.inflate(R.layout.fragment_enrolment_mother_dec_list_footer, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooter);
        }

        //Listview - Populate Data
        populateListViewData();

        TextView tvHeader = (TextView) listHeader.findViewById(R.id.section_header);
        tvHeader.setText(StringHelper.getStringByResourceId(context, R.string.label_child_dec_type));

        WebView wvFooter = (WebView) listFooter.findViewById(R.id.screen_message);
        wvFooter.loadDataWithBaseURL(null, StringHelper.getJustifiedString(context,
                        R.string.label_enrolment_mother_dec_list_footer, R.color.theme_gray_default_bg),
                "text/html", "utf-8", null);
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    public void populateListViewData() {
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                childDeclaration = new ChildDeclaration(adapter.getItem(position - 1));
            }
        });
    }

    public void createNoOptionsSelectedMessageBox() {
        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.error_enrolment_select_one_option),
                false, true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickPositiveButton()");

                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickNegativeButtons()");

                        dialog.dismiss();
                    }
                });
    }
}
