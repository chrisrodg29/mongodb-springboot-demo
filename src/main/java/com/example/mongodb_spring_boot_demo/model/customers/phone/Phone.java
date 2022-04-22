package com.example.mongodb_spring_boot_demo.model.customers.phone;

public class Phone {

    private String phoneNumber;
    private PhoneUsageType usageType;
    private boolean isPrimary;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneUsageType getUsageType() {
        return usageType;
    }

    public void setUsageType(PhoneUsageType usageType) {
        this.usageType = usageType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    @Override
    public String toString() {
        return "Phone{" +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", usageType=" + usageType +
                ", isPrimary=" + isPrimary +
                '}';
    }
}
