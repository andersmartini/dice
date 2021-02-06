package com.dice.martini;

import com.dice.martini.model.UrlPair;
import com.dice.martini.repository.UrlPairRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxStreamingHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@MicronautTest
class ShortenTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxStreamingHttpClient client;

    private static UrlPairRepository mockRepository;


    @BeforeEach
    public void setup() {
        mockRepository = Mockito.mock(UrlPairRepository.class);
        application.getApplicationContext().registerSingleton(mockRepository);
    }

    @Test
    public void canShortenUrl() {
        var longUrl = "https://andersmartini.com";
        var expectedHash = "dc87807a58";
        var request = HttpRequest.POST("/shorten", longUrl);

        var urlPairCaptor = ArgumentCaptor.forClass(UrlPair.class);
        when(mockRepository.saveUrl(urlPairCaptor.capture())).thenReturn(Single.just(expectedHash));

        var result = client.toBlocking().retrieve(request);

        var capturedPair = urlPairCaptor.getValue();
        assertEquals(String.format("localhost:8080/%s",expectedHash), result);
        assertEquals(expectedHash, capturedPair.getHash());
        assertEquals(longUrl, capturedPair.getLongUrl());
    }
}