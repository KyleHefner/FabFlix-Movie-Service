package edu.uci.ics.khefner.service.movies.models.AddGenre;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddGenreRequestModel {

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public AddGenreRequestModel(@JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
