package sg.gov.msf.bbss.model.wizardbase.eservice;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Map;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;

/**
 * Created by chuanhe
 * Modified by bandaray
 */
public class ServiceChangeBo extends WizardBase {

    public static final String FIELD_CHANGE_REASON = "relationshipType";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private String changeReason;

    private Map<String,Bitmap> bitmaps;

    private SupportingFile[] supportingFiles = new SupportingFile[0];

    private boolean isDeclared;


    //----------------------------------------------------------------------------------------------

    public ServiceChangeBo() {

    }

    public ServiceChangeBo(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
        super();
        this.childItems = childItems;
        this.childIds = childIds;
    }

    //----------------------------------------------------------------------------------------------


    public Map<String, Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(Map<String, Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public String getReason() {
        return changeReason;
    }

    public void setReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public ArrayList<ChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(ArrayList<ChildItem> childItems) {
        this.childItems = childItems;
    }

    public ArrayList<String> getChildIds() {
        return childIds;
    }

    public void setChildIds(ArrayList<String> childIds) {
        this.childIds = childIds;
    }


    public SupportingFile[] getSupportingFiles() {
        return supportingFiles;
    }

    public void setSupportingFiles(SupportingFile[] supportingFiles) {
        this.supportingFiles = supportingFiles;
    }

    public boolean isDeclared() {
        return isDeclared;
    }

    public void setDeclared(boolean isDeclared) {
        this.isDeclared = isDeclared;
    }


}
