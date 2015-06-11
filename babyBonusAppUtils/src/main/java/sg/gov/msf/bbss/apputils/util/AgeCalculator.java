package sg.gov.msf.bbss.apputils.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by bandaray on 5/1/2015.
 * Fixed bugs by chuanhe
 */
public class AgeCalculator {

    public static int getAgeByBirthMonthAndYear2(Date birthDate) {

        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        if (age == 0) {
            age = -(12 + today.get(Calendar.MONTH) - dob.get(Calendar.MONTH));
        }

        return age;
    }

    public static int getAgeByBirthMonthAndYear(Date birthDate) {

        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthDate);

        Calendar nowCal = Calendar.getInstance();

        int ageByYear = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
        int ageByYearMonth = birthCal.get(Calendar.MONTH) >= nowCal.get(Calendar.MONTH) ?
                ageByYear : (ageByYear - 1);
//        int ageByYearMonthDate =  (birthCal.getByPropertyName(Calendar.MONTH) == nowCal.getByPropertyName(Calendar.MONTH) &&
//                birthCal.getByPropertyName(Calendar.DAY_OF_MONTH) > nowCal.getByPropertyName(Calendar.DAY_OF_MONTH)) ?
//                ageByYear : (ageByYear - 1);


        if ((ageByYearMonth - 1)==0){
            if (nowCal.get(Calendar.YEAR)>birthCal.get(Calendar.YEAR)){
                return -(nowCal.get(Calendar.MONTH)+12-birthCal.get(Calendar.MONTH));
            }else {
                return -(nowCal.get(Calendar.MONTH)-birthCal.get(Calendar.MONTH));
            }
        }

        return (ageByYearMonth - 1);
    }

}
