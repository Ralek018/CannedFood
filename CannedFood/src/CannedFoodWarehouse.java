import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class CannedFoodWarehouse {
    //kolekcija binova preko koje vrsim sve funkcije u main-u
    private static List<Bin> bins = new ArrayList<Bin>();

    public static void main(String[] args) throws IOException {
        //falj ucitavam preko strimova
        try(BufferedReader br = new BufferedReader(new FileReader("test.csv"))){
            br.lines()
                    .skip(1)
                    .map(CannedFoodWarehouse::getCan)
                    .forEach(CannedFoodWarehouse::add);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        /*Path path = Path.of("src","test.csv");
        Files.lines(path)
                .skip(1)
                .map(CannedFoodWarehouse::getCan)
                .forEach(CannedFoodWarehouse::add);*/


        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        int action;
        while (flag) {
            System.out.println("\nSelect action:");
            printActions();
            action = scanner.nextInt();
            scanner.nextLine();

                switch (action) {
                    case 0:
                        System.out.println("Saving...\n" +
                                "Exiting the menu... ");
                        writing(bins);
                        flag = false;
                        break;
                    case 1:
                        printAll();
                        break;
                    case 2:
                        printOneBin();
                        break;
                    case 3:
                        addFood();
                        break;
                    case 4:
                        removeFood();
                        break;
                    case 5:
                        removeExpired();
                        break;
                }
        }
    }

    public static void printActions() {
        System.out.println("1 - List all bins\n" +
                "2 - List a single bin\n" +
                "3 - Add food\n" +
                "4 - Remove food\n" +
                "5 - Remove all expired food\n" +
                "0 - Save and Exit");
    }
    //u slucaju dodavanja fante nije dobar format stampanja, u svakom drugom jeste
    //idem kroz sve binove, u svaki bin zasebno prolazim prolazim kroz sve can-ove i stampam trazene vrednosti
    public static void printAll() {
        if (bins.isEmpty()) {
            System.out.println("No canned food found");
        } else {
            System.out.print("Bin No.   | " + "Type" + "\t\t\t" + " | " + "No. Items |" + " Min. Expiry date");
            for (int i = 0; i < bins.size(); i++) {
                Bin bin = bins.get(i);
                System.out.printf("\n%d" + "\t\t  | " + "%s" + "\t | " + "%d" + "\t\t | " + "%s",
                        bin.getBin_id(), bin.getType(), bin.getCanCollection().size(),min(bin.getBin_id()-1));
            }
        }
    }

    //na osnovu id-a nadjem konkretan bin i stampam sve potrebne vrednosti
    //format stampe za fantu nece biti dobar
    private static void printOneBin() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the bin_id: ");
        int id;
        if(sc.hasNextInt()) {
            id = sc.nextInt();
            if(id>0 && id<=bins.size()) {
                System.out.print("Canned Food ID" + " |" + " Type" + "\t\t\t\t | " + "Expiration Date");
                Bin checkedBin = bins.get(id - 1);
                for (int i = 0; i < checkedBin.getCanCollection().size(); i++) {
                    Can can = checkedBin.getCanCollection().get(i);
                    System.out.printf("\n%d" + "\t\t\t   | " + "%s" + "\t | " + "%s",
                            can.getId(), can.getType(), can.getExpiry_date() );
                }
            }else {
                printOneBin();
            }
        }else {
            printOneBin();
        }
    }
    //od korisnika trazim id, i proizvod koji, a na osnovu proizvoda
    //dajem korisniku izbog da izabere proizvodjaca (ukoliko ih ima vise)
    private static void addFood() {
        Scanner sc = new Scanner(System.in);
        int id;
        String product, productBrand, type, dateToFormat;
        LocalDate expiration_date;


        do {
            System.out.print("Enter the id of the product: ");
            while (!sc.hasNextInt()) {
                System.out.print("Invalid input, try again:");
                sc.nextLine();
            }
            id = sc.nextInt();
        } while (id < 0 || !free(id));

        do {
            System.out.print("Enter the name of the product (soft drink, sardine, beans or soup): ");
            while (!sc.hasNext("[A-Za-z]+")) {
                System.out.println("Invalid input");
                sc.nextLine();
            }
            product = sc.nextLine().trim().toLowerCase();
            product = product.replaceAll("\\s", "");
        } while (!(productValidation(product)));
        //productValidation je niz vrednosti na osnovu kojih proveravam tacnost inputa korisnika
        //firstCapital vraca isti string samo sa vecim prvim slovom a ukoliko je soft drink input
        //vratice vrednost Soft_drink
        product = firstLetterCapital(product);
        productBrand = findBrand(product);
        type = String.join("_", product, productBrand);

        do {
            System.out.println("Enter the date of expiration in the form of yyyy-MM-dd: ");
            while (sc.hasNext("[A-Za-z]*")) {
                System.out.println("Invalid input");
                sc.nextLine();
            }
            dateToFormat = sc.nextLine();

        } while (!((dateToFormat.length() == 10) && (validDate(dateToFormat))));
        //provera formata datuma
        expiration_date = date(dateToFormat);
        add(new Can(id,type,expiration_date));
    }

    //prolazim kroz ceo niz i trazim odgovarajuci can sa id-om korisnika
    private static void removeFood() {
        int id;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("Enter the id of the product: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input");
                sc.nextLine();
            }
            id = sc.nextInt();
        } while (id < 0);

        for (int i = 0 ; i < bins.size();i++) {
            Bin bin = bins.get(i);
            List<Can> cans = bins.get(i).getCanCollection();
            for (int j = 0; j < cans.size(); j++) {
                int checked_Id = cans.get(j).getId();
                if (checked_Id == id) {
                    bin.removeCan(id);
                    if(cans.isEmpty()){
                        bins.remove(bin);
                    }
                }
            }
        }
    }
    //brise sve proizvode kojima je istekao rok
    public static void removeExpired() {
        if (!bins.isEmpty()) {
            for (int i = 0; i < bins.size(); i++) {
                Bin bin = bins.get(i);
                for (int j = 0; j < bin.getCanCollection().size(); j++) {
                    Can can = bin.getCanCollection().get(j);
                    if (isExpired(can.getExpiry_date().toString())) {
                        bin.getCanCollection().remove(can);
                    }
                }
            }
        }
    }
    //vraca mi bin na osnovu tipa
    private static Bin findBin(String type) {
        for (int i = 0; i < bins.size(); i++) {
            Bin checkBin = bins.get(i);
            if (checkBin.getType().equals(type)) {
                return checkBin;
            }
        }
        return null;
    }
    //niz koji sadrzi imena svih proizvoda, dodao sam samo one koje sam video na githubu vezane za projekat
    private static boolean productValidation(String s) {
        List<String> validProducts = List.of("softdrink", "sardine", "beans", "soup");
        for (int i = 0; i < validProducts.size(); i++) {
            if (validProducts.get(i).equals(s)) {
                System.out.println("Match found");
                return true;
            }
        }
        System.out.println("No match found");
        return false;
    }
    //na osnovu proizvoda, funkcija vraca brand proizvoda, ukoliko ih ima vise nudim izbog korisniku
    private static String findBrand(String s) {
        Scanner input = new Scanner(System.in);
        int action;
        if (s.equals("Soft_drink")) {
            System.out.println("Choose brand: ");
            System.out.println("1 - Cola\n" +
                    "2 - Fanta");
            action = input.nextInt();
            switch (action) {
                case 1:
                    return "Cola";
                case 2:
                    return "Fanta";
            }

        }else if(s.equals("Sardine")){
            System.out.println("Choose brand: ");
            System.out.println("1 - Eva\n" +
                    "2 - Spain");
            action = input.nextInt();
            switch (action) {
                case 1:
                    return "Eva";
                case 2:
                    return "Spain";
            }
        }else {

            return "Tetovo";
        }
        return null;
    }
    //vraca prvo veliko slovo istog stringa, ukoliko je soft drink unos vratice Soft_drink
    private static String firstLetterCapital(String s){
        if(s.equals("softdrink")){
            return "Soft_drink";
        }else {
            String cap = s;
            cap = cap.substring(0, 1).toUpperCase() + cap.substring(1).toLowerCase();
            return cap;
        }
    }
    //proveravam da li je forma unosa za datum tacna
    private static boolean validDate(String date){
        try {
            LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }catch (DateTimeParseException e){
            System.out.println("Invalid date entered by the user, " + e.getMessage());
            return false;
        }
        return true;
    }
    //funkcija koja mi pravi LocalDate fajl na osnovu prosledjenog stringa i u isto vreme provera forme
    private static LocalDate date(String date) {
        if(validDate(date)){
            return LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else {
            return null;
        }
    }
    //provera roka hrane
    private static boolean isExpired(String s){
        LocalDate now = LocalDate.now();
        LocalDate ld = date(s);
        if(ld!=null) {
            return ld.isBefore(now);
        }else{
            return false;
        }
    }
    //metoda koja vraca minimalni rok
    private static String min(int id) {
        if (id >= 0) {
            LocalDate min = LocalDate.MAX;
            Bin bin = bins.get(id);
            if (bin != null) {
                for (int i = 0; i < bin.getCanCollection().size(); i++) {
                    Can checkedCan = bin.getCanCollection().get(i);
                    if (checkedCan.getExpiry_date().isBefore(min)) {
                        min = checkedCan.getExpiry_date();
                    }
                }
                return min.toString();

            } else {
                System.out.println("Bin is empty");
                return null;
            }
        }else {
            System.out.println("Invalid ID");
            return null;
        }
    }
    //metoda koja proverava da li je id slobodan za dodelu novih proizvoda
    private static boolean free(int id){
        if (!bins.isEmpty()) {
            for (int i = 0; i < bins.size(); i++) {
                Bin bin = bins.get(i);
                for (int j = 0; j < bin.getCanCollection().size(); j++) {
                    int validatingID = bin.getCanCollection().get(j).getId();
                    if (validatingID == id) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    //metoda koju koristim za strim, citam vrednosti i vracam can spreman za dodavanje u niz
    private static Can getCan(String line){
        String[] fields = line.split(",");
        return new Can((Integer.parseInt(fields[0])),fields[1].replaceAll("^\"|\"$", ""),
                    date(fields[2]));
    }
    //funkcija za dodavanje konzervi u niz, ukoliko je prazan niz
    //pravim bin sa istim tipom kao i proizvod i dodajem konzervu
    //ukoliko postoji bin sa tim tipom dodajem u taj bin
    //ukoliko ne postoji pravim bin sa istim tipom i dodajem konzervu
    private static void add(Can can){
        int id = can.getId();
        String type = can.getType();
        LocalDate expiration_date = can.getExpiry_date();

        if (bins.isEmpty()) {
            Bin newBin = new Bin(can.getType(), bins.size() + 1);
            bins.add(newBin);
            newBin.addCan(id, type, expiration_date);
        } else if (findBin(type) != null) {
            Bin checkedBin = findBin(type);
            if (checkedBin != null) {
                checkedBin.addCan(id, type, expiration_date);
            }
        } else {
            if (bins.size() <= 10) {
                Bin newBin = new Bin(type, bins.size() + 1);
                bins.add(newBin);
                newBin.addCan(id, type, expiration_date);
            } else {
                System.out.println("No available bin to store the canned food");
            }
        }
    }
    //funkcija za dodavanje vrednosti u string koji koristim za dodavanje u fajl
    private static String createRows(List<Bin> bins,int i, int j) {
        Bin bin = bins.get(i);
        Can can = bin.getCanCollection().get(j);
        String s = can.toString();
        return String.join(",",s,String.valueOf(bin.getBin_id()));
    }

    //metoda za upisivanje vrednosti u fajl, prolazim kroz ceo niz i stampam sve vrednosti
    private static void writing(List<Bin> bins) throws IOException {

        List <String> rows = new ArrayList<String>();
        for(int i = 0; i < bins.size();i++) {
            for (int j = 0; j < bins.get(i).getCanCollection().size(); j++) {
                rows.add(createRows(bins,i,j));
            }
        }

        FileWriter fw = new FileWriter("test.csv");
        fw.append("id");
        fw.append(",");
        fw.append("type");
        fw.append(",");
        fw.append("expiry_date");
        fw.append(",");
        fw.append("bin_id");
        fw.append("\n");

        for(String rowData : rows){
            fw.append(rowData);
            fw.append("\n");
        }

        fw.flush();
        fw.close();

    }
}
