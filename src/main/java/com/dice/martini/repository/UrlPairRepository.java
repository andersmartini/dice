package com.dice.martini.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dice.martini.model.UrlPair;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UrlPairRepository {

    private final AmazonDynamoDBAsync dynamo;
    private static final String SHORT_COLLUMN = "shortUrl";
    private static final String LONG_COLLUMN = "longUrl";
    private static final String TABLE_NAME = "urlShort";

    @Inject
    public UrlPairRepository(AmazonDynamoDBAsync dynamo) {
        this.dynamo = dynamo;
    }


    public Single<String> saveUrl(UrlPair urlPair) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(SHORT_COLLUMN, new AttributeValue().withS(urlPair.getHash()));
        item.put(LONG_COLLUMN, new AttributeValue().withS(urlPair.getLongUrl()));

        var putRequest = new PutItemRequest()
                .withTableName(TABLE_NAME)
                .withItem(item);

        return Single.fromFuture(dynamo.putItemAsync(putRequest))
                .subscribeOn(Schedulers.io())
                .map(r -> urlPair.getHash());
    }

    public Maybe<String> getByShort(String shortUrl) {
        Map<String, AttributeValue> searchCriteria = new HashMap<>();
        searchCriteria.put(SHORT_COLLUMN, new AttributeValue().withS(shortUrl));

        var getRequest = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(searchCriteria)
                .withAttributesToGet(LONG_COLLUMN);

        return Maybe.fromFuture(dynamo.getItemAsync(getRequest))
                .subscribeOn(Schedulers.io())
                .map(r -> r.getItem().get(LONG_COLLUMN).getS());
    }

    @PostConstruct
    private void createTableUnlessExists() {
        if(!tableExists()){
            var keyDefinitions = new KeySchemaElement()
                    .withAttributeName(SHORT_COLLUMN)
                    .withKeyType(KeyType.HASH);

            var shortUrlAttributeDefinition = new AttributeDefinition()
                    .withAttributeName(SHORT_COLLUMN)
                    .withAttributeType(ScalarAttributeType.S);

            var request = new CreateTableRequest()
                    .withTableName(TABLE_NAME)
                    .withKeySchema(keyDefinitions)
                    .withAttributeDefinitions(shortUrlAttributeDefinition)
                    .withBillingMode(BillingMode.PAY_PER_REQUEST);
            dynamo.createTable(request);
        }
    }


    private boolean tableExists() {
        var listTablesRequest = new ListTablesRequest()
                .withExclusiveStartTableName(TABLE_NAME);
        var result = dynamo.listTables();
        return result.getTableNames().contains(TABLE_NAME);
    }


}
