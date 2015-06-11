package sg.gov.msf.bbss.apputils.wizard;

/**
 * Created by bandaray on 29/1/2015.
 */
public interface FragmentWizard {

    public boolean onPauseFragment(boolean isValidationRequired);
    public boolean onResumeFragment();
}
