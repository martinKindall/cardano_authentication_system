package com.walruscode.cardano.repositories;

import com.walruscode.cardano.model.Wallet;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.time.Instant;

public class WalletRepository implements AutoCloseable {

    private final DynamoDbClient client;
    private final DynamoDbEnhancedClient dbClient;
    private final DynamoDbTable<Wallet> table;

    public WalletRepository() {
        String dynamoDbUrl = System.getenv("DYNAMO_URL");

        client = DynamoDbClient
                .builder()
                .region(Region.EU_CENTRAL_1)
                .endpointOverride(URI.create(dynamoDbUrl))
                .build();

        dbClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();

        table = dbClient.table("wallet", TableSchema.fromBean(Wallet.class));
    }

    public void saveWallet(String stakeAddress, String nonce, Instant instant) {
        var request = PutItemEnhancedRequest
                .builder(Wallet.class)
                .item(new Wallet(stakeAddress, nonce))
                .build();

        table.putItem(request);
    }

    @Override
    public void close() throws Exception {
        if (client != null) client.close();
    }
}
