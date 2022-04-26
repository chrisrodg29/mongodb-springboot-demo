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

@Repository
public class AccountsSummaryDao {

    private static final String COLLECTION = "accounts";
    private final MongoDatabase db;
    private final MongoCollection<AccountTotalsSummary> collectionWithAccountTotalsSummaryCodec;

    public AccountsSummaryDao(MongoDatabase database) {
        this.db = database;
        collectionWithAccountTotalsSummaryCodec =
                this.db.getCollection(COLLECTION, AccountTotalsSummary.class);
    }

    public List<AccountTotalsSummary> getAccountTotalsSummaryListV1() {
        List<Bson> aggregationPipeline = new ArrayList<>();

        Bson groupStage = group(
                "$accountType",
                Accumulators.sum("numberOfAccounts", 1),
                Accumulators.sum("balancesTotal", "$balance")
        );
        aggregationPipeline.add(groupStage);

        return collectionWithAccountTotalsSummaryCodec.aggregate(aggregationPipeline)
                .into(new ArrayList<>());
    }

    public List<Document> getAccountTotalsSummaryListV2() {
        MongoCollection<Document> collectionWithoutCodec = db.getCollection(COLLECTION);
        List<Bson> aggregationPipeline = new ArrayList<>();

        Bson groupStage = group(
                "$accountType",
                Accumulators.sum("numberOfAccounts", 1),
                Accumulators.sum("balancesTotal", "$balance")
        );
        aggregationPipeline.add(groupStage);

        List<Document> documentList =
                collectionWithoutCodec.aggregate(aggregationPipeline).into(new ArrayList<>());
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
