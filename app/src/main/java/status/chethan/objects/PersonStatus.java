package status.chethan.objects;

import android.util.Log;

/**
 * Created by chethan on 10/01/15.
 */
public class PersonStatus {

    private String personName;
    private String personStatusUpdate;
    private String dateOfUpdate;

    public String getDateOfUpdate() {
        return dateOfUpdate;
    }

    public void setDateOfUpdate(String dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonStatusUpdate() {
        return personStatusUpdate;
    }

    public void setPersonStatusUpdate(String personStatusUpdate) {
        this.personStatusUpdate = personStatusUpdate;
    }

    public void prettyPrint(){
        Log.d("PersonStatus","Person Name : "+personName +"\nPerson Status : "+personStatusUpdate+"\nUpdate Date : "+dateOfUpdate);
    }
}
