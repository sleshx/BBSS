package sg.gov.msf.bbss.model.entity.people;

import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class Person {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NRIC = "nric";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_BIRTHDAY = "dateOfBirth";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_common_id)
    private String id;

	@DisplayNameId(R.string.label_person_nric)
	private String nric;

	@DisplayNameId(R.string.label_person_name)
	private String name;

	@DisplayNameId(R.string.label_person_birthday)
	private Date dateOfBirth;

    //----------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
