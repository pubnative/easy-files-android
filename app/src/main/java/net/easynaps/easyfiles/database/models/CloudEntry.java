package net.easynaps.easyfiles.database.models;

import net.easynaps.easyfiles.utils.OpenMode;

public class CloudEntry {

    private int _id;
    private OpenMode serviceType;
    private String persistData;

    public CloudEntry() {}

    public CloudEntry(OpenMode serviceType, String persistData) {
        this.serviceType = serviceType;
        this.persistData = persistData;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public int getId() {
        return this._id;
    }

    public void setPersistData(String persistData) {
        this.persistData = persistData;
    }

    public String getPersistData() {
        return this.persistData;
    }

    /**
     * Set the service type
     * Support values from {@link net.pubnative.easyfiles.utils.OpenMode}
     * @param openMode
     */
    public void setServiceType(OpenMode openMode) {
        this.serviceType = openMode;
    }

    /**
     * Returns ordinal value of service from {@link net.pubnative.easyfiles.utils.OpenMode}
     * @return
     */
    public OpenMode getServiceType() {
        return this.serviceType;
    }
}
