package sg.gov.msf.bbss.logic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.logic.adapter.util.ChildListArrayUtil;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;

/**
 * Created by bandaray
 * Modified to add more methods by chuanhe
 */
public class DisplayChildArrayAdapter extends ArrayAdapter<ChildItem> {

    private static Class CHILD_ITEM_CLASS = ChildItem.class;

    private Context context;
    private Adapter adapter;
    private List<ChildItem> childData;
    private ChildListType childListType;
    private int resourceId;
    private LayoutInflater inflater = null;
    private ArrayList<Integer> colorPositions;

    public DisplayChildArrayAdapter(Context context, List<ChildItem> childItemList,
                                    ChildListType childListType) {
        super(context, childListType.getSelectableLayoutResourceId(), 0, childItemList);

        this.context = context;
        this.adapter = this;
        this.childData = childItemList;
        this.childListType = childListType;
        this.resourceId = childListType.getViewableLayoutResourceId();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null) {
            convertView = inflater.inflate(resourceId, null, false);
        }

        ChildItem childItem = childData.get(position);

        switch (childListType) {
            case CHANGE_NAH:
                ChildListArrayUtil.displayNominatedAccHolderChildList(context, convertView,
                        viewHolder, childItem);
                break;
            case CHANGE_NAN:
                ChildListArrayUtil.displayNominatedAccNumberChildList(context, convertView,
                        viewHolder, childItem);
                break;
            case CHANGE_CDAT:
                ChildListArrayUtil.displayCdaTrusteeChildList(context, convertView,
                        viewHolder, childItem);
                break;
            case CHANGE_CDAB:
                ChildListArrayUtil.displayCdaBankChildList(context, convertView,
                        viewHolder, childItem);
                break;
            case CDA_TO_PSEA:
                ChildListArrayUtil.displayTransferPseaChildList(context, convertView,
                        viewHolder, childItem);
                break;
            case CHANGE_BO:
                ChildListArrayUtil.displayTransferPseaChildList(context, convertView,
                        viewHolder, childItem);
                break;
            case TERMS_AND_COND:
                ChildListArrayUtil.displayAcceptanceOfCdaBankTAndC(context, convertView,
                        viewHolder, childItem);
                break;
            case OPEN_CDA:
                ChildListArrayUtil.displayOpenCdaChildList(context, convertView,
                        viewHolder, childItem);
                break;
            default:
                //Nothing
        }

        if(colorPositions != null && colorPositions.contains(position)) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.field_error));
        }

        return convertView;
    }

    public void setColorPositions(ArrayList<Integer> colorPositions){
        this.colorPositions = colorPositions;
        notifyDataSetChanged();
    }
}
