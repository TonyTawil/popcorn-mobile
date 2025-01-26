package com.example.popcorn.DTOs;

import java.util.List;

public class MovieResponse {
    private boolean adult;
    private String backdropPath;
    private Collection belongsToCollection;
    private long budget;
    private List<Genre> genres;
    private String homepage;
    private int movieId;
    private String imdbId;
    private List<String> originCountry;
    private String originalLanguage;
    private String originalTitle;
    private String overview;
    private double popularity;
    private String poster_path;
    private List<ProductionCompany> productionCompanies;
    private List<ProductionCountry> productionCountries;
    private String releaseDate;
    private long revenue;
    private int runtime;
    private List<SpokenLanguage> spokenLanguages;
    private String status;
    private String tagline;
    private String title;
    private boolean video;
    private double voteAverage;
    private int voteCount;

    public boolean isAdult() { return adult; }
    public void setAdult(boolean adult) { this.adult = adult; }

    public String getBackdropPath() { return backdropPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    public Collection getBelongsToCollection() { return belongsToCollection; }
    public void setBelongsToCollection(Collection belongsToCollection) { this.belongsToCollection = belongsToCollection; }

    public long getBudget() { return budget; }
    public void setBudget(long budget) { this.budget = budget; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }

    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }

    public int getId() { return movieId; }
    public void setId(int movieId) { this.movieId = movieId; }

    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public List<String> getOriginCountry() { return originCountry; }
    public void setOriginCountry(List<String> originCountry) { this.originCountry = originCountry; }

    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }

    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public double getPopularity() { return popularity; }
    public void setPopularity(double popularity) { this.popularity = popularity; }

    public String getPosterPath() { return poster_path; }
    public void setPosterPath(String posterPath) { this.poster_path = posterPath; }

    public List<ProductionCompany> getProductionCompanies() { return productionCompanies; }
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) { this.productionCompanies = productionCompanies; }

    public List<ProductionCountry> getProductionCountries() { return productionCountries; }
    public void setProductionCountries(List<ProductionCountry> productionCountries) { this.productionCountries = productionCountries; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public long getRevenue() { return revenue; }
    public void setRevenue(long revenue) { this.revenue = revenue; }

    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }

    public List<SpokenLanguage> getSpokenLanguages() { return spokenLanguages; }
    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) { this.spokenLanguages = spokenLanguages; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isVideo() { return video; }
    public void setVideo(boolean video) { this.video = video; }

    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }

    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    public static class Genre {
        private int id;
        private String name;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Collection {
        private int id;
        private String name;
        private String posterPath;
        private String backdropPath;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPosterPath() { return posterPath; }
        public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

        public String getBackdropPath() { return backdropPath; }
        public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    }

    public static class ProductionCompany {
        private int id;
        private String logoPath;
        private String name;
        private String originCountry;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getLogoPath() { return logoPath; }
        public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getOriginCountry() { return originCountry; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
    }

    public static class ProductionCountry {
        private String iso_3166_1;
        private String name;

        public String getIso_3166_1() { return iso_3166_1; }
        public void setIso_3166_1(String iso_3166_1) { this.iso_3166_1 = iso_3166_1; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class SpokenLanguage {
        private String iso_639_1;
        private String name;
        private String englishName;

        public String getIso_639_1() { return iso_639_1; }
        public void setIso_639_1(String iso_639_1) { this.iso_639_1 = iso_639_1; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEnglishName() { return englishName; }
        public void setEnglishName(String englishName) { this.englishName = englishName; }
    }
}
