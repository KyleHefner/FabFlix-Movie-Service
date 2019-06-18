package edu.uci.ics.khefner.service.api_gateway.containers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Genre {
    @JsonProperty(value = "id", required = true)
    private int id;
    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public Genre( @JsonProperty("id") int id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }


    @JsonProperty(value = "id", required = true)
    public int getId() {
        return id;
    }

    @JsonProperty(value = "id", required = true)
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty(value = "name", required = true)
    public String getName() {
        return name;
    }

    @JsonProperty(value = "name", required = true)
    public void setName(String name) {
        this.name = name;
    }
}
