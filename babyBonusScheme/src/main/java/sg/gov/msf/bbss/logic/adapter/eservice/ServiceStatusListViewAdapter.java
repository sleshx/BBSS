package sg.gov.msf.bbss.logic.adapter.eservice;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.model.entity.ServiceStatus;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class ServiceStatusListViewAdapter extends BaseAdapter {

    private static final Class SERVICE_STATUS_CLASS = ServiceStatus.class;

    private Context context;
    private List<ServiceStatus> serviceStatusList;
    private LayoutInflater inflater = null;

    public ServiceStatusListViewAdapter(Context context,
                                        List<ServiceStatus> serviceStatusList) {
        this.context = context;
        this.serviceStatusList = serviceStatusList;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return serviceStatusList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceStatusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.listitem_2rows_header_multi_choice, null, false);
        convertView.findViewById(R.id.checkableChoice).setVisibility(View.GONE);

        if (position % 2 == 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.theme_gray));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        String serviceId = StringHelper.isStringNullOrEmpty(
                serviceStatusList.get(position).getAppId()) ? AppConstants.EMPTY_STRING :
                serviceStatusList.get(position).getAppId();

        ((TextView)convertView.findViewById(R.id.tvListRowHeader)).setText(serviceId);

        ModelPropertyViewMetaList metaDataList;
        ModelPropertyViewMeta viewMeta;

        try{
            metaDataList = new ModelPropertyViewMetaList(context);

            //STATUS
            viewMeta = new ModelPropertyViewMeta(ServiceStatus.FIELD_SERVICE_APP_STATUS);
            viewMeta.setIncludeTagId(R.id.listview_row_1);
            viewMeta.setLabelString(serviceStatusList.get(position).getAppType().getStatusLabel(context));
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(SERVICE_STATUS_CLASS, viewMeta);

            //DATE
            viewMeta = new ModelPropertyViewMeta(ServiceStatus.FIELD_SERVICE_APP_DATE);
            viewMeta.setIncludeTagId(R.id.listview_row_2);
            viewMeta.setLabelString(serviceStatusList.get(position).getAppStatusType().getDateLabel());
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(SERVICE_STATUS_CLASS, viewMeta);

            //Adding to synchronizer
            ModelViewSynchronizer<ServiceStatus> serviceStatusModelViewSynchronizer =
                    new ModelViewSynchronizer<ServiceStatus>(ServiceStatus.class, metaDataList,
                            convertView, AppConstants.EMPTY_STRING);
            serviceStatusModelViewSynchronizer.setLabels();
            serviceStatusModelViewSynchronizer.displayDataObject(serviceStatusList.get(position));

        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    public void setText(TextView textView,String s){
        if (s!=null&&!"".equals(s))
        {
            textView.setText(s);
        }
    }
}
