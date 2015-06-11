package sg.gov.msf.bbss.logic.adapter.enrolment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class EnrolmentAdoptionChildAdapter extends ArrayAdapter<Child> {

    private static Class CHILD_CLASS = Child.class;

    private Context context;
    private BbssApplication app;

    private int itemLayoutResource;
    private ArrayList<Child> childData;
    private LayoutInflater inflater = null;

    private LinearLayout llChildName;
    private LinearLayout llEditViewButton;
    private LinearLayout llDeleteButton;

    public EnrolmentAdoptionChildAdapter(Context context, BbssApplication app,
                                         int itemLayoutResource, ArrayList<Child> childData) {
        super(context, itemLayoutResource, childData);

        this.context = context;
        this.app = app;
        this.itemLayoutResource = itemLayoutResource;
        this.childData = childData;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        LinearLayout llAdoptionDate;
        LinearLayout llBirthCert;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(itemLayoutResource, null, false);

            llChildName = viewHolder.get(convertView, R.id.added_child_name);
            llAdoptionDate = viewHolder.get(convertView, R.id.added_child_other_detail);
            llBirthCert = viewHolder.get(convertView, R.id.added_child_birth_cert);

            llEditViewButton = viewHolder.get(convertView, R.id.edit_view_button_section);
            llDeleteButton = viewHolder.get(convertView, R.id.delete_button_section);

            llEditViewButton.setVisibility(View.GONE);
            llDeleteButton.setVisibility(View.GONE);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        displayChildList(context, convertView, viewHolder, childData.get(position));
        return convertView;
    }

    //----------------------------------------------------------------------------------------------

    private void displayChildList(Context context, View convertView,
                                  ViewHolder viewHolder, Child child){
        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        ViewHolder holder = viewHolder;

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

            //---DATE OF ADOPTION
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_DATE_OF_ADOPTION);
            viewMeta.setIncludeTagId(R.id.added_child_other_detail);
            viewMeta.setEditable(false);
            viewMeta.setBold(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_DATE_OF_ADOPTION);

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
