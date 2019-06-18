package edu.uci.ics.khefner.service.movies.models.SearchStar;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;

public class SearchStarRequestModel {
    private String name;
    private Integer birthYear;
    private String movieTitle;


    @JsonProperty(value = "offset")
    private int offset;
    @JsonProperty(value = "limit")
    private int limit;
    @JsonProperty(value = "direction")
    private String direction;
    @JsonProperty(value = "orderBy")
    private String orderBy;

    public SearchStarRequestModel(@JsonProperty("name") String name,
                                  @JsonProperty("birthYear")Integer birthYear,
                                  @JsonProperty("movieTitle") String movieTitle,
                                  @JsonProperty("offset") int offset,
                                  @JsonProperty("limit") int limit,
                                  @JsonProperty("direction") String direction,
                                  @JsonProperty("orderBy") String orderBy) {
        this.name = name;
        this.birthYear = birthYear;
        this.movieTitle = movieTitle;
        this.offset = offset;
        this.limit = limit;
        this.direction = direction;
        this.orderBy = orderBy;

        if(limit!= 10 && limit != 25 && limit != 50 && limit != 100){
            ServiceLogger.LOGGER.info("Invalid Limit...setting to default");
            this.limit = 10;
        }
        if(offset < 0 ){
            ServiceLogger.LOGGER.info("Invalid Offset(negative)...setting to default");
            this.offset = 0;
        }
        else if(offset%limit != 0){
            ServiceLogger.LOGGER.info("Invalid Offset(non-multiple of limit)...setting to default");
            this.offset = 0;
        }

        if(!orderBy.equals("name") && !orderBy.equals("birthYear")){
            ServiceLogger.LOGGER.info("Invalid orderby...setting to default");
            this.orderBy = "name";
        }

        if(!direction.equals("asc") && !direction.equals("desc")){
            ServiceLogger.LOGGER.info("Invalid direction...setting to default");
            this.direction = "asc";
        }
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

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
