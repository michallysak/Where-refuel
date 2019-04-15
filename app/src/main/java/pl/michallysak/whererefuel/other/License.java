package pl.michallysak.whererefuel.other;

public class License {
    private String name;
    private String message;
    private String url;

    public License(){}

    public License(String name, String message, String url){
        this.name = name;
        this.message = message;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

}
