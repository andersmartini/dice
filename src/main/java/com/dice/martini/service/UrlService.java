package com.dice.martini.service;

import com.dice.martini.exception.InvalidUrlException;
import com.dice.martini.model.UrlPair;
import com.dice.martini.repository.UrlPairRepository;
import io.micronaut.context.annotation.Value;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.UrlValidator;

import javax.inject.Singleton;

@Singleton
public class UrlService {

    private final UrlValidator urlValidator;
    private final UrlPairRepository repository;
    private final String baseUrl;

    public UrlService(UrlPairRepository urlPairRepository,
                      @Value("${app.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        repository = urlPairRepository;
        //Will accept http, https and ftp links
        urlValidator = new UrlValidator();
    }


    public Single<String> shortenUrl(String url) {
        if (urlValidator.isValid(url)) {
            var urlPair = UrlPair.builder()
                    .longUrl(url)
                    .hash(shortenString(url))
                    .build();
            return repository.saveUrl(urlPair)
                    .map(this::toShortUrl);
        } else {
            return Single.error(InvalidUrlException::new);
        }
    }

    public Maybe<String> expandUrl(String hash) {
        return repository.getByShort(hash);
    }


    private String toShortUrl(String hash) {
        return String.format("%s/%s", baseUrl, hash);
    }


    private String shortenString(String s) {
        return DigestUtils.md5Hex(s).substring(0, 10);
    }


}
