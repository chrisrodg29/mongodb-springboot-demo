package com.example.mongodb_spring_boot_demo.spring_data_example.repository;

import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataAccountsRepository extends MongoRepository<Account, String>, CustomAccountsRepository {

    Account getByAccountNumber(int accountNumber);

    List<Account> getByAccountType(AccountType accountType);

    int deleteByAccountNumber(int accountNumber);

    void deleteAll();

}
