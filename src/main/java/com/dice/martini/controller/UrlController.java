package com.dice.martini.controller;

import com.dice.martini.exception.InvalidUrlException;
import com.dice.martini.service.UrlService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Controller
@Singleton
public class UrlController {

    private UrlService urlService;

    @Inject
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @Post("/shorten")
    public Single<HttpResponse<String>> shorten(@Body String url) {
        return urlService.shortenUrl(url)
                .map(this::shortUrlResponse)
                .onErrorReturn(this::handleError);
    }

    @Get("/bad")
    public Single<HttpResponse> badReq(){
        return Single.just(HttpResponse.badRequest());
    }

    @Get("/{hash}")
    public Maybe<HttpResponse> expand(String hash) {
        return urlService.expandUrl(hash)
                .map(this::toRedirectResponse)
                .defaultIfEmpty(HttpResponse.notFound());
    }

    private HttpResponse<String> shortUrlResponse(String shortUrl) {
        return HttpResponse.ok(shortUrl);
    }

    private HttpResponse toRedirectResponse(String url) {
        return HttpResponse.redirect(URI.create(url));
    }

    private HttpResponse<String> handleError(Throwable e) {
        if (e instanceof InvalidUrlException) {
            return HttpResponse.badRequest("invalid URL. please provide a valid url." +
                    " A valid Url looks like this: https://andersmartini.com");
        }
        return HttpResponse.serverError(e.getMessage());
    }
}
