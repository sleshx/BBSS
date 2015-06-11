package sg.gov.msf.bbss.apputils.ui.component;

import sg.gov.msf.bbss.apputils.R;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by bandaray on 11/12/2014.
 */
public class HorizontalProgressDialog extends ProgressDialog {

	public HorizontalProgressDialog(Context context) {
		super(context);
		
		this.setCancelable(true);
		this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.setProgress(0);
		this.setMax(100);
		this.setProgressDrawable(context.getResources().getDrawable(R.drawable.custom_progressbar));
	}

}
