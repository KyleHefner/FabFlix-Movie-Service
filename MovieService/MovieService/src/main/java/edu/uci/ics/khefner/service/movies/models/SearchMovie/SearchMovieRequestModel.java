package edu.uci.ics.khefner.service.movies.models.SearchMovie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchMovieRequestModel {


    private String title;
    private String genre;
    private int year;
    private String director;
    private boolean hidden;

    @JsonProperty(value = "offset")
    private int offset;
    @JsonProperty(value = "limit")
    private int limit;
    @JsonProperty(value = "direction")
    private String direction;
    @JsonProperty(value = "orderBy")
    private String orderBy;

    public SearchMovieRequestModel(){}

    @JsonCreator
    public SearchMovieRequestModel(
            @JsonProperty("email") String email,
            @JsonProperty("title") String title,
            @JsonProperty("genre") String genre,
            @JsonProperty("year") int year,
            @JsonProperty("director") String director,
            @JsonProperty("hidden") boolean hidden,
            @JsonProperty("offset")int offset,
            @JsonProperty("limit")int limit,
            @JsonProperty("direction")String direction,
            @JsonProperty("orderBy")String orderBy) {


        this.title = title;
        this.genre = genre;
        this.year = year;
        this.director = director;
        this.hidden = hidden;
        this.offset = offset;
        this.limit = limit;
        this.direction = direction.toLowerCase();
        this.orderBy = orderBy.toLowerCase();

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

        if(!orderBy.equals("rating") && !orderBy.equals("title")){
            ServiceLogger.LOGGER.info("Invalid orderby...setting to default");
            this.orderBy = "rating";
        }

        if(!direction.equals("asc") && !direction.equals("desc")){
            ServiceLogger.LOGGER.info("Invalid direction...setting to default");
            this.direction = "desc";
        }

    }






    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("genre")
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @JsonProperty("year")
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @JsonProperty("director")
    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @JsonProperty("hidden")
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @JsonProperty("offset")
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @JsonProperty("limit")
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @JsonProperty("orderBy")
    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }




}
