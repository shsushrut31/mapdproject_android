package remotedoorway.byteme.com.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by shsus_000 on 17/04/2017.
 */

@IgnoreExtraProperties
public class DoorsForSpinner implements Serializable {

    private String DoorId;
    private String DoorName;
    private String AccessDate;
    private String InstallDate;
    private String CurrentStatus;
    private String OwnerName;


    public DoorsForSpinner() {
    }

    public DoorsForSpinner(String doorId, String doorName, String accessDate, String installDate, String currentStatus, String ownerName) {
        DoorId = doorId;
        DoorName = doorName;
        AccessDate = accessDate;
        InstallDate = installDate;
        CurrentStatus = currentStatus;
        OwnerName = ownerName;
    }

    public String getDoorId() {
        return DoorId;
    }

    public void setDoorId(String doorId) {
        DoorId = doorId;
    }

    public String getDoorName() {
        return DoorName;
    }

    public void setDoorName(String doorName) {
        DoorName = doorName;
    }

    public String getAccessDate() {
        return AccessDate;
    }

    public void setAccessDate(String accessDate) {
        AccessDate = accessDate;
    }

    public String getInstallDate() {
        return InstallDate;
    }

    public void setInstallDate(String installDate) {
        InstallDate = installDate;
    }

    public String getCurrentStatus() {
        return CurrentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        CurrentStatus = currentStatus;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    @Override
    public String toString() {
        return getDoorName();
    }
}
