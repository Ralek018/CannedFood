import java.time.LocalDate;

public class Can {
    private int id;
    private String type;
    private LocalDate expiry_date;

    public Can(int id, String type, LocalDate expiry_date){
        this.id = id;
        this.type = type;
        this.expiry_date = expiry_date;
    }

    public int getId() {

        return id;
    }

    public String getType() {

        return type;
    }


    public LocalDate getExpiry_date() {

        return expiry_date;
    }

    public String toString() {
        return this.id + "," + this.type + "," + this.expiry_date;
    }
}
