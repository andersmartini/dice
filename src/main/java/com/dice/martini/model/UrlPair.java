package com.dice.martini.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "UrlPairBuilder")
@JsonSerialize
@JsonDeserialize
public class UrlPair {
    private String longUrl;
    private String hash;


    @JsonPOJOBuilder(withPrefix = "")
    public static class UrlPairBuilder{}

}
