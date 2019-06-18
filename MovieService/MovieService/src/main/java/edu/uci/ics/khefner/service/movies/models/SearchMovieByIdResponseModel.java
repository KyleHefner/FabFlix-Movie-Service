package edu.uci.ics.khefner.service.movies.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.khefner.service.movies.core.FullMovie;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchMovieByIdResponseModel {

    private int resultCode;
    private String message;
    private FullMovie movie;

    public SearchMovieByIdResponseModel(int resultCode, String message, FullMovie movie) {
        this.resultCode = resultCode;
        this.message = message;
        this.movie = movie;
        this.movie = new FullMovie(movie);
        //this.movie.copyMovie(movie);


//        this.movie.setDirector(movie.getDirector());
//        this.movie.setMovieid(movie.getMovieid());
//        this.movie.setTitle(movie.getTitle());
//        this.movie.setYear(movie.getYear());
//        this.movie.setBudget(movie.getBudget());
//        this.movie.setBackdrop_path(movie.getBackdrop_path());
//        this.movie.setOverview(movie.getOverview());
//        this.movie.setPoster_path(movie.getPoster_path());
//        this.movie.setRevenue(movie.getRevenue());
//        this.movie.setRating(movie.getRating());
//        this.movie.setNumVotes(movie.getNumVotes());
//        this.movie.setGenres(movie.getGenres());
//        this.movie.setStars(movie.getStars());


    }

    public SearchMovieByIdResponseModel(int resultCode, String message){
        this.resultCode = resultCode;
        this.message = message;

        this.movie = null;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public FullMovie getMovie() {
        return movie;
    }

    public void setMovie(FullMovie movie) {
        this.movie = new FullMovie(movie);
    }
}
