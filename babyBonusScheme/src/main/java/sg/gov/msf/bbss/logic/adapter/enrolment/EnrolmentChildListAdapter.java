package sg.gov.msf.bbss.logic.adapter.enrolment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.util.AddChildListItem;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.ChildListItemType;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.model.entity.people.Child;
import sg.gov.msf.bbss.view.enrolment.main.EnrolmentFragmentContainerActivity;
import sg.gov.msf.bbss.view.enrolment.sub.EnrolmentChildActivity;

/**
 * Created by bandaray
 */
public class EnrolmentChildListAdapter extends ArrayAdapter<Child> {

    private static Class CHILD_CLASS = Child.class;

    private Context context;
    private BbssApplication app;

    private int itemLayoutResource;
    private ArrayList<Child> childData;
    private ChildRegistrationType childRegistrationType;
    private LayoutInflater inflater = null;

    private LinearLayout llChildName;
    private LinearLayout llBornOverseas;
    private LinearLayout llBirthCert;

    private LinearLayout llEditViewButton;
    private LinearLayout llDeleteButton;

    private View.OnClickListener listButtonClickListener;
    private Integer[] validationErrorIndexes;

    public EnrolmentChildListAdapter(Context context, BbssApplication app,
                                     ChildRegistrationType childRegistrationType,
                                     int itemLayoutResource, ArrayList<Child> childData,
                                     View.OnClickListener listButtonClickListener) {
        super(context, itemLayoutResource, childData);

        this.context = context;
        this.app = app;
        this.childRegistrationType = childRegistrationType;
        this.itemLayoutResource = itemLayoutResource;
        this.listButtonClickListener = listButtonClickListener;
        this.childData = childData;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.validationErrorIndexes = new Integer[0];
    }

    public void setValidationErrorIndexes(Integer[] validationErrorIndexes){
        this.validationErrorIndexes = validationErrorIndexes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

//TODO: Edit function behaves in a strange way and hence commented this to see if view holder creates teh problem.
//TODO: Double check and re-use view holder again

//        if (convertView == null) {
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            convertView = inflater.inflate(itemLayoutResource, null, false);
//
//            llChildName = viewHolder.get(convertView, R.id.added_child_name);
//            llBornOverseas = viewHolder.get(convertView, R.id.added_child_other_detail);
//            llBirthCert = viewHolder.get(convertView, R.id.added_child_birth_cert);
//
//            llEditViewButton = viewHolder.get(convertView, R.id.edit_view_button_section);
//            llDeleteButton = viewHolder.get(convertView, R.id.delete_button_section);
//
//            if (childRegistrationType != ChildRegistrationType.CITIZENSHIP) {
//                llBornOverseas.setVisibility(View.GONE);
//            } else {
//                llBornOverseas.setVisibility(View.VISIBLE);
//            }
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(itemLayoutResource, null, false);

        llChildName = (LinearLayout)convertView.findViewById(R.id.added_child_name);
        llBornOverseas = (LinearLayout)convertView.findViewById(R.id.added_child_other_detail);
        llBirthCert = (LinearLayout)convertView.findViewById(R.id.added_child_birth_cert);

        llEditViewButton = (LinearLayout)convertView.findViewById(R.id.edit_view_button_section);
        llDeleteButton = (LinearLayout)convertView.findViewById(R.id.delete_button_section);

        if (childRegistrationType != ChildRegistrationType.CITIZENSHIP) {
            llBornOverseas.setVisibility(View.GONE);
        } else {
            llBornOverseas.setVisibility(View.VISIBLE);
        }

        if(Arrays.asList(validationErrorIndexes).contains(position)){
            convertView.setBackgroundColor(convertView.getContext().getResources()
                    .getColor(sg.gov.msf.bbss.apputils.R.color.field_error));
        }

        displayButtons(position);
        displayChildList(context, convertView, viewHolder, childData.get(position));
        return convertView;
    }

    //----------------------------------------------------------------------------------------------

    private void displayButtons(final int position) {
        Button viewEditButton = (Button) llEditViewButton.findViewById(R.id.imageButton);
        Button deleteButton = (Button) llDeleteButton.findViewById(R.id.imageButton);

        //--- Delete Button
        deleteButton.setText(R.string.btn_delete);
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(
                BabyBonusConstants.BUTTON_DELETE, 0, 0, 0);

        AddChildListItem addChildListItem = new AddChildListItem(position, ChildListItemType.DELETE);
        addChildListItem.setChild(getItem(position));
        deleteButton.setTag(addChildListItem);
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
        //deleteButton.setOnClickListener(listButtonClickListener);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageBox.show(context,
                        StringHelper.getStringByResourceId(context, R.string.error_common_do_you_want_to_delete),
                        false, true, R.string.btn_ok, true, R.string.btn_cancel,
                        new MessageBoxButtonClickListener() {
                            @Override
                            public void onClickPositiveButton(DialogInterface dialog, int id) {
                                childData.remove(position);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            @Override
                            public void onClickNegativeButton(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            }
        });

        //viewEditButton.setOnClickListener(listButtonClickListener);
        viewEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnrolmentChildActivity.class);
                intent.putExtra(BabyBonusConstants.ENROLMENT_CHILD_REGISTRATION_TYPE,
                        childRegistrationType);
                intent.putExtra(BabyBonusConstants.ENROLMENT_IS_VIEW_EDIT_MODE,
                        true);
                intent.putExtra(BabyBonusConstants.ENROLMENT_SELECTED_LIST_POSITION,
                        position);

                if (childRegistrationType == ChildRegistrationType.POST_BIRTH) {
                    ((EnrolmentFragmentContainerActivity) context).startActivityForResult(intent,
                            EnrolmentChildActivity.POST_BIRTH_REQUEST_CODE);
                } else if (childRegistrationType == ChildRegistrationType.CITIZENSHIP) {
                    ((EnrolmentFragmentContainerActivity) context).startActivityForResult(intent,
                            EnrolmentChildActivity.CITIZENSHIP_REQUEST_CODE);
                }
            }
        });
    }

    private void displayChildList(Context context, View convertView,
                                  ViewHolder viewHolder, Child child){
        ModelPropertyViewMetaList metaDataList = null;
        ModelPropertyViewMeta viewMeta = null;

        try {

            metaDataList = new ModelPropertyViewMetaList(context);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.added_child_name);
            viewMeta.setEditable(false);
            viewMeta.setBold(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BORN OVERSEAS
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_IS_BORN_OVERSEAS);
            viewMeta.setIncludeTagId(R.id.added_child_other_detail);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_IS_BORN_OVERSEAS);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTH CERT NO
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTH_CERT_NO);
            viewMeta.setIncludeTagId(R.id.added_child_birth_cert);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_BIRTH_CERT_NO);

            metaDataList.add(CHILD_CLASS, viewMeta);

            ModelViewSynchronizer<Child> childModelViewSynchronizer =
                    new ModelViewSynchronizer<Child>(CHILD_CLASS, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            childModelViewSynchronizer.setLabels();
            childModelViewSynchronizer.displayDataObject(child, viewHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
