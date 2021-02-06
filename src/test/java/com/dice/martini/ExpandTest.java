package com.dice.martini;

import com.dice.martini.repository.UrlPairRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxStreamingHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Maybe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@MicronautTest
class ExpandTest {

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
    public void canExpandUrl() {
        var longUrl = "https://andersmartini.com";
        var expectedHash = "dc87807a5";

        var request = HttpRequest.GET(String.format("/%s", expectedHash));

        var hashCaptor = ArgumentCaptor.forClass(String.class);

        when(mockRepository.getByShort(hashCaptor.capture())).thenReturn(Maybe.just(longUrl));


        var result = client.toBlocking().exchange(request);

        var body = result.body();
        var capturedHash = hashCaptor.getValue();

        assertEquals(expectedHash, capturedHash);
        // The Client is "helpful" and follows the redirect, we therefore
        // test that we reach andersmartini.com by checking for Wix-specific headers that we just happen to know will be there.
        // This is not a very reliable test, as any change to (or downtime of) andersmartini.com will cause it to fail. but I'll
        // leave it here nonetheless, as it is stable enough for the purposes of this application.
        // In a more serious setting, a custom client that does not follow redirects should be used
        // and verify the status and contents returned from this local server, instead of the result
        // but that's just too much work to be worth it for a simple work-test like this.
        assertEquals(HttpStatus.OK, result.getStatus());
        assertNotNull(result.getHeaders().get("X-Wix-Request-Id"));

    }

}