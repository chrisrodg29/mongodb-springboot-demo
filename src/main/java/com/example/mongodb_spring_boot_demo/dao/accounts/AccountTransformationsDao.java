package com.example.mongodb_spring_boot_demo.dao.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.AccountTotalsSummary;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.BucketOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.bucket;
import static com.mongodb.client.model.Aggregates.group;

/**
 * Each method in this class transforms Account documents, returning the data
 * in some other form.
 */
@Repository
public class AccountTransformationsDao {

    private static final String COLLECTION = "accounts";
    private final MongoDatabase db;
    private final MongoCollection<Document> collection;

    public AccountTransformationsDao(MongoDatabase database) {
        this.db = database;
        // Because the final POJO is going to be different for each method,
        // we don't bother setting a default POJO class for the collection.
        collection = this.db.getCollection(COLLECTION);
    }

    /**
     * This method aggregates Account documents into AccountTotalsSummary documents,
     * and specifies the class to be found in the aggregation call.
     */
    public List<AccountTotalsSummary> getAccountTotalsSummaryListV1() {
        List<Bson> aggregationPipeline = new ArrayList<>();

        Bson groupStage = group(
                "$accountType",
                Accumulators.sum("numberOfAccounts", 1),
                Accumulators.sum("balancesTotal", "$balance")
        );
        aggregationPipeline.add(groupStage);

        return collection.aggregate(aggregationPipeline, AccountTotalsSummary.class)
                .into(new ArrayList<>());
    }

    /**
     * This method makes the exact same call as getAccountTotalsSummaryListV1, but
     * returns raw documents, leaving transformation to be done in the service layer.
     * It includes print statements for examining the raw document structure.
     */
    public List<Document> getAccountTotalsSummaryListV2() {
        List<Bson> aggregationPipeline = new ArrayList<>();

        Bson groupStage = group(
                "$accountType",
                Accumulators.sum("numberOfAccounts", 1),
                Accumulators.sum("balancesTotal", "$balance")
        );
        aggregationPipeline.add(groupStage);

        List<Document> documentList =
                collection.aggregate(aggregationPipeline).into(new ArrayList<>());
        examineDocumentList(documentList);
        return documentList;
    }

    private void examineDocumentList(List<Document> documentList) {
        System.out.println(documentList);

        Document summary = documentList.get(0);
        System.out.println(summary.get("_id").getClass());
        System.out.println(summary.get("numberOfAccounts").getClass());
        System.out.println(summary.get("balancesTotal").getClass());
    }

    /**
     * The returned documents from this method will not match our AccountBucket pojo.
     * Transforming the documents at the service layer is the most efficient
     * transformation available to us.
     */
    public List<Document> getAccountBucketsByBoundaries(Integer[] boundaries) {
        MongoCollection<Document> collection =
                db.getCollection(COLLECTION);

        List<Bson> pipeline = new ArrayList<>();

        Bson bucketStage = buildBucketStage(boundaries);
        pipeline.add(bucketStage);

        ArrayList<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
        System.out.println(results);
        return results;
    }

    private Bson buildBucketStage(Integer[] boundaries) {
        BucketOptions options = new BucketOptions();
        BsonField numberOfAccounts = new BsonField("numberOfAccounts", new Document("$sum", 1));
        options.output(numberOfAccounts);

        return bucket("$balance", Arrays.asList(boundaries), options);
    }


}
