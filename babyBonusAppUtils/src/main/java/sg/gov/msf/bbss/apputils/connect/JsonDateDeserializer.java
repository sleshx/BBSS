package sg.gov.msf.bbss.apputils.connect;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by bandaray on 18/1/2015.
 */
public class JsonDateDeserializer implements JsonDeserializer<Date> {
    private String dateFormat;

    public JsonDateDeserializer(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Date parsedDate = null;

        try {
            String dateString = json.getAsJsonPrimitive().getAsString();
            int idx1 = dateString.indexOf("(");
            int idx2 = dateString.indexOf("+");

            if (idx1 > -1 && idx2 > -1) {
                String timestamp = dateString.substring(idx1 + 1, idx2);
                parsedDate = new Date(Long.valueOf(timestamp));
            }
        }catch (Exception ex){
            throw new JsonParseException(ex);
        }

        return parsedDate;
    }
}
