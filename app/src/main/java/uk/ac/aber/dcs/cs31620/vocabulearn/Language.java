package uk.ac.aber.dcs.cs31620.vocabulearn;

public class Language {
//hard code language defined so spinner is never epmty, hack to avoid empty spinner handling
    public static final int  FRENCH = 1;

    private int id;
    private String name;

    @Override
    //adapter will return name of category and not class name
    public String toString() {
        return getName();
    }
    public Language() {
    }

    public Language(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
