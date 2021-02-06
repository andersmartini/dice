package com.dice.martini.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsAsyncClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.env.Environment;

@Factory
public class Config {

    @Bean
    AmazonDynamoDBAsync dynamoDBAsync(Environment environment){
        var keyId = environment.get("aws.keyId", String.class).orElseThrow();
        var accessKey = environment.get("aws.accessKey", String.class).orElseThrow();
        var dynamoEndpoint = environment.get("aws.dynamoUrl", String.class).orElseThrow();

        var credentials = new BasicAWSCredentials(keyId, accessKey);

        var clientBuilder = AmazonDynamoDBAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoEndpoint, null));

        return clientBuilder.build();
    }


}
