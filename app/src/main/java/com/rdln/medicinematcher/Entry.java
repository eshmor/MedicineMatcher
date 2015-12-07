package com.rdln.medicinematcher;

/**
 * Created by matan on 25/11/2015.
 */

public class Entry {
    public final String patientName;
    public final String MedecineId;
    public final String medicineName;
    public final String IsAllowed;

    public Entry(String patientName, String IsAllowed, String MedecineId,String medicineName) {
        this.patientName = patientName;
        this.IsAllowed = IsAllowed;
        this.MedecineId=MedecineId;
        this.medicineName = medicineName;
    }
}
