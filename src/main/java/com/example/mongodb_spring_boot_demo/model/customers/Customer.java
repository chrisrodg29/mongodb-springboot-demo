package com.example.mongodb_spring_boot_demo.model.customers;

import com.example.mongodb_spring_boot_demo.model.customers.phone.Phone;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private int customerId;
    private String firstName;
    private String lastName;
    private List<Phone> phoneList;
    private List<Address> addressList;
    private List<Integer> accountNumbers;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Phone> getPhoneList() {
        if (this.phoneList == null) {
            this.phoneList = new ArrayList<>();
        }
        return this.phoneList;
    }

    public void setPhoneList(List<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    public List<Address> getAddressList() {
        if (this.addressList == null) {
            this.addressList = new ArrayList<>();
        }
        return this.addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<Integer> getAccountNumbers() {
        if (this.accountNumbers == null) {
            this.accountNumbers = new ArrayList<>();
        }
        return this.accountNumbers;
    }

    public void setAccountNumbers(List<Integer> accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneList=" + phoneList +
                ", addressList=" + addressList +
                ", accountNumbers=" + accountNumbers +
                '}';
    }

}
