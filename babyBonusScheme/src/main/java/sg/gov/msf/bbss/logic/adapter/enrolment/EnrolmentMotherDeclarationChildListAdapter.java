package sg.gov.msf.bbss.logic.adapter.enrolment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.util.AddChildListItem;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.ChildListItemType;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.model.entity.childdata.ChildDeclaration;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class EnrolmentMotherDeclarationChildListAdapter extends ArrayAdapter<ChildDeclaration> {

    private static Class CHILD_CLASS = Child.class;
    private static Class CHILD_DECLARATION_CLASS = ChildDeclaration.class;

    private Context context;
    private BbssApplication app;

    private int itemLayoutResource;
    private List<ChildDeclaration> childDeclarations;
    private ChildRegistrationType childRegistrationType;
    private LayoutInflater inflater = null;

    private LinearLayout llChildName;
    private LinearLayout llDeclarationType;
    private LinearLayout llBirthday;
    private LinearLayout llBirthCert;

    private LinearLayout llEditViewButton;
    private LinearLayout llDeleteButton;

    private View.OnClickListener listButtonClickListener;

    public EnrolmentMotherDeclarationChildListAdapter(Context context, BbssApplication app,
                                                      int itemLayoutResource,
                                                      List<ChildDeclaration> declarations,
                                                      View.OnClickListener listButtonClickListener) {
        super(context, itemLayoutResource, declarations);

        this.context = context;
        this.app = app;
        this.itemLayoutResource = itemLayoutResource;
        this.childDeclarations = declarations;
        this.listButtonClickListener = listButtonClickListener;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(itemLayoutResource, null, false);

            llChildName = viewHolder.get(convertView, R.id.added_child_declaration_name);
            llDeclarationType = viewHolder.get(convertView, R.id.added_child_declaration_type);
            llBirthday = viewHolder.get(convertView, R.id.added_child_declaration_birthday);
            llBirthCert = viewHolder.get(convertView, R.id.added_child_declaration_birth_cert);

            llEditViewButton = viewHolder.get(convertView, R.id.edit_view_button_section);
            llDeleteButton = viewHolder.get(convertView, R.id.delete_button_section);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        displayButtons(position);
        displayChildList(context, convertView, viewHolder, childDeclarations.get(position));
        return convertView;
    }

    //----------------------------------------------------------------------------------------------

    private void displayButtons(int position) {
        Button viewEditButton = (Button) llEditViewButton.findViewById(R.id.imageButton);
        Button deleteButton = (Button) llDeleteButton.findViewById(R.id.imageButton);

        //--- Delete Button
        deleteButton.setText(R.string.btn_delete);
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(
                BabyBonusConstants.BUTTON_DELETE, 0, 0, 0);
        deleteButton.setTag(new AddChildListItem(position, ChildListItemType.DELETE));
        deleteButton.setVisibility(app.getEnrolmentForm().isPrePopulated() ?
                View.GONE : View.VISIBLE);

        //--- View/Edit Button
        if (app.getEnrolmentForm().isPrePopulated()) {
            viewEditButton.setText(R.string.btn_view);
            viewEditButton.setCompoundDrawablesWithIntrinsicBounds(
                    BabyBonusConstants.BUTTON_VIEW, 0, 0, 0);
            viewEditButton.setTag(new AddChildListItem(position, ChildListItemType.VIEW));
        } else {
            viewEditButton.setText(R.string.btn_edit);
            viewEditButton.setCompoundDrawablesWithIntrinsicBounds(
                    BabyBonusConstants.BUTTON_EDIT, 0, 0, 0);
            viewEditButton.setTag(new AddChildListItem(position, ChildListItemType.EDIT));
        }

        //--- Set Click Listeners
        deleteButton.setOnClickListener(listButtonClickListener);
        viewEditButton.setOnClickListener(listButtonClickListener);

    }

    private void displayChildList(Context context, View convertView,
                                  ViewHolder viewHolder, ChildDeclaration childDeclaration){
        ModelPropertyViewMetaList metaDataList = null;
        ModelPropertyViewMeta viewMeta = null;

        try {

            //--- Child ---
            metaDataList = new ModelPropertyViewMetaList(context);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.added_child_declaration_name);
            viewMeta.setEditable(false);
            viewMeta.setBold(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTHDAY
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.added_child_declaration_birthday);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_BIRTHDAY);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTH CERT NO
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTH_CERT_NO);
            viewMeta.setIncludeTagId(R.id.added_child_declaration_birth_cert);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_BIRTH_CERT_NO);

            metaDataList.add(CHILD_CLASS, viewMeta);

            ModelViewSynchronizer<Child> childModelViewSynchronizer =
                    new ModelViewSynchronizer<Child>(CHILD_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            childModelViewSynchronizer.setLabels();
            childModelViewSynchronizer.displayDataObject(childDeclaration.getChild(), viewHolder);

            //--- Child Declaration ---
            metaDataList = new ModelPropertyViewMetaList(context);

            //---DECLARATION TYPE
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_TYPE);
            viewMeta.setIncludeTagId(R.id.added_child_declaration_type);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_DEC_TYPE);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);

            ModelViewSynchronizer<ChildDeclaration> childDecModelViewSynchronizer =
                    new ModelViewSynchronizer<ChildDeclaration>(CHILD_DECLARATION_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            childDecModelViewSynchronizer.setLabels();
            childDecModelViewSynchronizer.displayDataObject(childDeclaration, viewHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
