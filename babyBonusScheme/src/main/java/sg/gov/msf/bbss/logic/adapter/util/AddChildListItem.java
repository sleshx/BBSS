package sg.gov.msf.bbss.logic.adapter.util;

import sg.gov.msf.bbss.logic.type.ChildListItemType;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class AddChildListItem {

    private int listPosition;
    private ChildListItemType type;
    private Child child;

    public AddChildListItem(int AddChildListItem, ChildListItemType type) {
        this.listPosition = listPosition;
        this.type = type;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public ChildListItemType getType() {
        return type;
    }

    public void setType(ChildListItemType type) {
        this.type = type;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }
}
