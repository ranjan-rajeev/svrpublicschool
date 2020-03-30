package com.svrpublicschool.models;

public class LeadDetailModel {
    long leadId;
    String leadName;
    String leadMobileNo;
    String leadEmail;

    public long getLeadId() {
        return leadId;
    }

    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadMobileNo() {
        return leadMobileNo;
    }

    public void setLeadMobileNo(String leadMobileNo) {
        this.leadMobileNo = leadMobileNo;
    }

    public String getLeadEmail() {
        return leadEmail;
    }

    public void setLeadEmail(String leadEmail) {
        this.leadEmail = leadEmail;
    }
}
