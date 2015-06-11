package sg.gov.msf.bbss.apputils.meta;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.apputils.util.FieldHandler;

/**
 * Created by bandaray on 15/12/2014.
 */
public class ModelPropertyViewMetaList {
    private Context context;

    private Map<String, ModelPropertyViewMeta> viewMetaMap =  new HashMap<String, ModelPropertyViewMeta>();

    public ModelPropertyViewMetaList(Context context) {
        this.context = context;
    }

    public void add(Class objectClass, ModelPropertyViewMeta viewMeta) throws Exception {
        if (viewMetaMap.containsKey(viewMeta.getPropertyName())){
            viewMetaMap.remove(viewMeta.getPropertyName());
        }

        Field field = FieldHandler.getField(objectClass, viewMeta.getPropertyName());

        if (field != null) {
            if (viewMeta.getLabelResourceId() == 0) {
                viewMeta.setLabelResourceId(field.getAnnotation(DisplayNameId.class).value());
            }
            if (viewMeta.getTagNameId() == 0) {
                viewMeta.setTagNameId(field.getAnnotation(DisplayNameId.class).value());
            }
            viewMetaMap.put(viewMeta.getPropertyName(), viewMeta);
        }
    }

    //----------------------------------------------------------------------------------------------

    public ModelPropertyViewMeta getByPropertyName(String propertyName){
        if (viewMetaMap.containsKey(propertyName)) {
            return viewMetaMap.get(propertyName);
        }

        return null;
    }

    public ModelPropertyViewMeta getBySerialName(String serialName){
        for (ModelPropertyViewMeta viewMeta : viewMetaMap.values()){
            if(viewMeta.getSerialName().equals(serialName)) {
                return viewMeta;
            }
        }

        return null;
    }

    public String[] getByPropertyNames() {
        return viewMetaMap.keySet().toArray(new String[0]);
    }
}
