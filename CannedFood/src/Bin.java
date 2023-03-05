import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bin{
    private List<Can> canCollection;
    private String type;
    private int bin_id;

    public Bin(String type, int bin_id) {
        if(type!=null){
            this.type=type;
        }
        this.bin_id=bin_id;
        this.canCollection = new ArrayList<Can>();
    }

    public List<Can> getCanCollection() {
        return this.canCollection;
    }

    public String getType() {
        return this.type;
    }

    public int getBin_id() {
        return bin_id;
    }

    private boolean validType(String type){
        String canType = type.trim().toLowerCase();
        String binType = this.type.trim().toLowerCase();

        return binType.equals(canType);
    }

    public void addCan(int id, String type, LocalDate date) {
        if (this.canCollection.size() < 10) {
            if (id > 0 && type!=null && date != null) {
                if(validType(type)){
                    this.canCollection.add(new Can(id,type,date));
                    System.out.println("Added");
                }
            }
        }else {
            System.out.println("Bin is full");
        }
    }

    public Can findCan(int id){
        int canId;
        for (Can can : this.canCollection) {
            canId = can.getId();
            if (canId == id) {
                return can;
            }
        }
        return null;
    }

    public void removeCan(int id){
        Can can = findCan(id);
        if(can!=null){
            this.canCollection.remove(can);
            System.out.println("Can removed");
        }else {
            System.out.println("Can doesn't exist");
        }
    }
}
