package models;

/**
 * Created by joaoveras on 14/05/2015.
 */
public class Profile {

    protected String name;
    protected String picture;
    protected String genre;

    public Profile(String name, String picture, String genre){
        this.name = name;
        this.picture = picture;
        this.genre = genre;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }


}
