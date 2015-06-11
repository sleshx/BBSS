package sg.gov.msf.bbss.logic.type;

import sg.gov.msf.bbss.R;

/**
 * Created by bandaray
 * Modified to change the layout IDs by chuahe
 */
public enum ChildListType {

    FAMILY_VIEW(R.layout.listitem_4rows_header_single_choice,
            R.layout.listitem_4rows_header_single_choice),
    CHANGE_NAH(R.layout.listitem_2rows_header_multi_choice,
            R.layout.listitem_2rows_header),
    CHANGE_NAN(R.layout.listitem_1rows_header_multi_choice,
            R.layout.listitem_1rows_header),
    CHANGE_CDAT(R.layout.listitem_2rows_header_multi_choice,
            R.layout.listitem_2rows_header),
    CHANGE_CDAB(R.layout.listitem_2rows_header_multi_choice,
            R.layout.listitem_2rows_header),
    CDA_TO_PSEA(R.layout.listitem_2rows_header_multi_choice,
            R.layout.listitem_2rows_header),
    CHANGE_BO(R.layout.layout_service_change_bo_listview_item,
            R.layout.listitem_2rows_header),
    TERMS_AND_COND(R.layout.listitem_2rows_header_multi_choice,
            R.layout.listitem_2rows_header),
    OPEN_CDA(R.layout.listitem_2rows_header_multi_choice,
            R.layout.listitem_2rows_header);

    private int selectableLayoutResourceId;
    private int viewableLayoutResourceId;

    ChildListType(int selectableLayoutResourceId, int viewableLayoutResourceId) {
        this.selectableLayoutResourceId = selectableLayoutResourceId;
        this.viewableLayoutResourceId = viewableLayoutResourceId;
    }

    public int getSelectableLayoutResourceId() {
        return selectableLayoutResourceId;
    }

    public int getViewableLayoutResourceId() {
        return viewableLayoutResourceId;
    }
}
