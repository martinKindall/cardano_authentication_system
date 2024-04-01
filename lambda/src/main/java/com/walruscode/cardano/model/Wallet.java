package com.walruscode.cardano.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public record Wallet(
        @DynamoDbPartitionKey
        @DynamoDbAttribute("stake_address")
        String stakeAddress,
        @DynamoDbAttribute("nonce")
        String nonce
) {}
