package edu.uci.ics.khefner.service.movies.models.AddStar;

public class AddStarRequestModel {

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
