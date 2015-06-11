package sg.gov.msf.bbss.logic.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.logic.type.IdentificationType;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by bandaray on 10/6/2015.
 */
public class IdentificationTypeSelectionChangeListener
        implements AdapterView.OnItemSelectedListener {
    private ArrayAdapter<IdentificationType> adapter;
    private ModelViewSynchronizer<Adult> adultModelViewSynchronizer;

    public IdentificationTypeSelectionChangeListener(ArrayAdapter<IdentificationType> adapter,
                                                     ModelViewSynchronizer<Adult> adultModelViewSynchronizer) {
        this.adapter = adapter;
        this.adultModelViewSynchronizer = adultModelViewSynchronizer;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        IdentificationType identificationType = adapter.getItem(position);
        boolean isMandatory = (identificationType == IdentificationType.FOREIGN_PASSPORT ||
                identificationType == IdentificationType.FOREIGN_ID);
        adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_IDENTIFICATION_NO, isMandatory);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}