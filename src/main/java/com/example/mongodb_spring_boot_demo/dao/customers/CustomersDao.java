package com.example.mongodb_spring_boot_demo.dao.customers;

import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.example.mongodb_spring_boot_demo.model.customers.CustomerWithAccountDetail;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

@Repository
public class CustomersDao {

    private static final String COLLECTION = "customers";

    private final MongoDatabase db;
    private final MongoCollection<Customer> customerCollection;

    public CustomersDao(MongoDatabase database) {
        this.db = database;
        this.customerCollection = this.db.getCollection(COLLECTION, Customer.class);
    }

    public ArrayList<Customer> getAllCustomers() {
        return customerCollection.find().into(new ArrayList<>());
    }

    public Customer getCustomerById(int customerId) {
        Bson query = eq("customerId", customerId);
        return customerCollection.find(query).first();
    }

    public List<Customer> getCustomersByAccountNumber(int accountNumber) {
        Bson query = eq("accountNumbers", accountNumber);
        return customerCollection.find(query).into(new ArrayList<>());
    }

    public CustomerWithAccountDetail getCustomerWithAccountDetailById(int customerId) {
        MongoCollection<CustomerWithAccountDetail> customCollection =
                db.getCollection(COLLECTION, CustomerWithAccountDetail.class);

        Bson match = Aggregates.match(eq("customerId", customerId));
        Bson lookup = Aggregates.lookup(
                "accounts",
                "accountNumbers",
                "accountNumber",
                "accounts"
        );
        Bson project = Aggregates.project(eq("accountNumbers", 0));
        List<Bson> aggregationPipeline = Arrays.asList(
                match,
                lookup,
                project
        );
        return customCollection.aggregate(aggregationPipeline).first();
    }

    public boolean insertCustomers(List<Customer> customerList) {
        InsertManyResult result = customerCollection.insertMany(customerList);
        return result.wasAcknowledged();
    }

    public UpdateResult replaceCustomer(Customer customer) {
        Bson query = eq("customerId", customer.getCustomerId());
        return customerCollection.replaceOne(query, customer);
    }

    public UpdateResult addAccountNumberToCustomer(int customerId, int accountNumber) {
        Bson query = eq("customerId", customerId);
        Bson update = Updates.addToSet("accountNumbers", accountNumber);
        return customerCollection.updateMany(query, update);
    }

    public UpdateResult removeAccountFromCustomer(int customerId, int accountNumber) {
        Bson query = eq("customerId", customerId);
        Bson update = Updates.pull("accountNumbers", accountNumber);
        return customerCollection.updateMany(query, update);
    }

    public boolean removeAccountFromAllCustomers(int accountNumber) {
        Bson query = eq("accountNumbers", accountNumber);
        Bson update = Updates.pull("accountNumbers", accountNumber);
        UpdateResult updateResult = customerCollection.updateMany(query, update);

        return updateResult.getModifiedCount() > 0;
    }

    public boolean removeAllAccountsFromCustomers() {
        Bson query = exists("accountNumbers.0");
        Bson update = Updates.set("accountNumbers", new ArrayList<>());
        UpdateResult result = customerCollection.updateMany(query, update);
        return result.wasAcknowledged();
    }

    public boolean deleteAllCustomers() {
        DeleteResult deleteResult = customerCollection.deleteMany(new Document());
        return deleteResult.wasAcknowledged();
    }

}
