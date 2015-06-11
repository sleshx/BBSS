package sg.gov.msf.bbss.apputils.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import sg.gov.msf.bbss.apputils.ui.helper.ActionBarCreator;

public class BbssActionBarActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarCreator actionBarCreator = new ActionBarCreator(this, this.getSupportActionBar());
        actionBarCreator.setupActionBarOther(this.getTitle().toString());
    }
}
