package sg.gov.msf.bbss.logic.type;

/**
 * Created by bandaray
 */
public enum ServiceAppStatusType {
    COMPLETED("Completed", "Completed", "Date Processed"),
    IN_PROGRESS("SUBMITTED", "In-Progress", "Date Submitted");  //In-Progress

    private String code;
    private String status;
    private String dateLabel;

    ServiceAppStatusType(String code, String status, String dateLabel) {
        this.code = code;
        this.status = status;
        this.dateLabel = dateLabel;
    }

    @Override
    public String toString() {
        return code;
    }

    public String getStatus(){
        return status;
    }

    public String getDateLabel(){
        return dateLabel;
    }

    public static ServiceAppStatusType parseType(String value){
        for (ServiceAppStatusType statusType : ServiceAppStatusType.values()) {
            if(statusType.toString().equals(value)) {
                return statusType;
            }
        }

        return null;
    }

}
