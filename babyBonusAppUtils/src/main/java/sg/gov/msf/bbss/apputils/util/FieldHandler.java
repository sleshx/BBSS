package sg.gov.msf.bbss.apputils.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bandaray on 11/12/2014.
 */
public class FieldHandler {

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        Class currentClass = type;

        do {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);

        return fields;
    }

    public static List<Field> getAllNonStaticFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        Class currentClass = type;

        for (Field field : getAllFields(currentClass)) {
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }

        return fields;
    }

    public static Field getField(Class<?> type, String fieldName) {
        Class currentClass = type;
        Field field = null;

        do {
            try {
                if (currentClass.getDeclaredField(fieldName) != null) {
                    field = currentClass.getDeclaredField(fieldName);
                    break;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);

        return field;
    }
}
