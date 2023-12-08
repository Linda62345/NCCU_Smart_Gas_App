package glotech.smartgasapp;

public class Company {
    private String companyId;
    private String companyName;

    public Company(String companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String toString() {
        return companyName;
    }
}

