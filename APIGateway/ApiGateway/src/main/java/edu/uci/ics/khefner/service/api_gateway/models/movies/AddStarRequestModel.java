package edu.uci.ics.khefner.service.api_gateway.models.movies;

import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class AddStarRequestModel extends RequestModel {

    private String name;
    private Integer birthYear;

    public AddStarRequestModel() {
    }

    public AddStarRequestModel(String name, Integer birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

}
