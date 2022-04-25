package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.model.customers.Address;
import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import com.example.mongodb_spring_boot_demo.model.customers.phone.Phone;
import com.example.mongodb_spring_boot_demo.model.customers.phone.PhoneUsageType;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FakerService {

    private final Faker faker = new Faker();

    public List<Account> getNewAccounts(int numberOfAccounts) {
        List<Account> accountList = new ArrayList<>();
        int count = 0;
        while (count < numberOfAccounts) {
            accountList.add(getNewAccount());
            count++;
        }
        return accountList;
    }

    private Account getNewAccount() {
        Account account = new Account();
        account.setAccountNumber(
                faker.number().numberBetween(10000001, 100000000)
        );
        account.setAccountType(getRandomEnumValue(AccountType.class));
        account.setBalance(BigDecimal.valueOf(
                faker.number().randomDouble(2, 0, 1000000)
        ));
        return account;
    }

    public List<Customer> getNewCustomers(List<Account> accounts) {
        List<Customer> customerList = new ArrayList<>();
        for (Account account : accounts) {
            customerList.add(getNewCustomer(account));
        }
        return customerList;
    }

    private Customer getNewCustomer(Account account) {
        Customer customer = new Customer();
        customer.setCustomerId(faker.number().numberBetween(30001, 40000));
        customer.setFirstName(faker.name().firstName());
        customer.setLastName(faker.name().lastName());
        customer.getAccountNumbers().add(account.getAccountNumber());
        customer.getAddressList().add(getNewAddress());
        customer.getPhoneList().add(getNewPhone());
        return customer;
    }

    private Phone getNewPhone() {
        Phone phone = new Phone();
        phone.setPhoneNumber(faker.phoneNumber().phoneNumber());
        phone.setUsageType(getRandomEnumValue(PhoneUsageType.class));
        phone.setPrimary(true);
        return phone;
    }

    private Address getNewAddress() {
        Address address = new Address();
        address.setAddressLine1(faker.address().streetAddress());
        address.setCity(faker.address().city());
        address.setStateCode(faker.address().stateAbbr());
        address.setPostalCode(faker.address().zipCode());
        address.setCountryCode("US");
        return address;
    }

    private <T extends Enum<T>> T getRandomEnumValue(Class<T> type) {
        int random = faker.number().numberBetween(0, type.getEnumConstants().length);
        return type.getEnumConstants()[random];
    }

}
