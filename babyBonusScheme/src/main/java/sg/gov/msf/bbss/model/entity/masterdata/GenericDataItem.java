package sg.gov.msf.bbss.model.entity.masterdata;

/**
 * Created by bandaray
 */
public class GenericDataItem {

    public static final String LIST_ITEM_ID = "id";
    public static final String LIST_ITEM_NAME = "name";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    private String id;

    private String name;

    //----------------------------------------------------------------------------------------------

    public GenericDataItem () {

    }

    public GenericDataItem (String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return name;
    }
}