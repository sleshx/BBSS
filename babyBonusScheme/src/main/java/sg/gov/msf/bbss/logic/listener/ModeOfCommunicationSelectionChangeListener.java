package sg.gov.msf.bbss.logic.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.logic.type.CommunicationType;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by bandaray on 10/6/2015.
 */
public class ModeOfCommunicationSelectionChangeListener
        implements AdapterView.OnItemSelectedListener {
    private ArrayAdapter<CommunicationType> adapter;
    private ModelViewSynchronizer<Adult> adultModelViewSynchronizer;

    public ModeOfCommunicationSelectionChangeListener(ArrayAdapter<CommunicationType> adapter,
                                                      ModelViewSynchronizer<Adult> adultModelViewSynchronizer) {
        this.adapter = adapter;
        this.adultModelViewSynchronizer = adultModelViewSynchronizer;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CommunicationType communicationType = adapter.getItem(position);

        if (communicationType.equals(CommunicationType.EMAIL)) {
            adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_EMAIL_ADDR, true);
            adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_MOBILE_NO, false);
        } else if (communicationType.equals(CommunicationType.SMS)) {
            adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_EMAIL_ADDR, false);
            adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_MOBILE_NO, true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}