package com.example.yiska.project_part2.model.entities;

public class Driver {
    protected String password;
    protected String fullName;
    protected String id;
    protected String phoneNumber;
    protected String EMail;
    protected String creditCardDebit;


    public Driver() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEMail() {
        return EMail;
    }

    public void seteMail(String EMail) {
        this.EMail = EMail;
    }

    public String getCreditCardDebit() {
        return creditCardDebit;
    }

    public void setCreditCardDebit(String creditCardDebit) {
        this.creditCardDebit = creditCardDebit;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Driver{" +
                ", password='" + password + '\'' +
                ", familyName='" + fullName + '\'' +
                ", id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", EMail='" + EMail + '\'' +
                ", creditCardDebit='" + creditCardDebit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return creditCardDebit == driver.creditCardDebit &&
                fullName.equals(driver.fullName)
                && phoneNumber.equals(driver.phoneNumber) &&
                EMail.equals(driver.EMail)&&
                password.equals(driver.password);
    }
}
