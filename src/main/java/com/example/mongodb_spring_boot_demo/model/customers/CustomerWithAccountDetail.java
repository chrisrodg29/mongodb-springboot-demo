package com.example.mongodb_spring_boot_demo.model.customers;

import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.customers.phone.Phone;

import java.util.ArrayList;
import java.util.List;

public class CustomerWithAccountDetail {

    private int customerId;
    private String firstName;
    private String lastName;
    private List<Phone> phoneList;
    private List<Address> addressList;
    private List<Account> accounts;

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

    public List<Account> getAccountNumbers() {
        if (this.accounts == null) {
            this.accounts = new ArrayList<>();
        }
        return this.accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneList=" + phoneList +
                ", addressList=" + addressList +
                ", accounts=" + accounts +
                '}';
    }

}
