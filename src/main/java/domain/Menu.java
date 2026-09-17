/*package domain;

import util.TextUI;

import java.io.FileNotFoundException;
import java.util.List;

public class Menu {

    List<User> users;
    List<Zone> zones;
    List<Car> cars;

    Payment payment;
    User currentUser;
    Parking parking;

    TextUI textUI;
    FileIO fileIO;

    public Menu(List<User> users, List<Zone> zones, List<Car> cars, TextUI textUI, FileIO fileIO) {

        this.users = users;
        this.zones = zones;
        this.cars = cars;
        this.textUI = textUI;
        this.fileIO = fileIO;
    }

    public void start() throws FileNotFoundException {

        if (textUI.promptBinary("Har du et login?")) {
            login();
        } else {
            registerUser();
        }
    }

    public void registerUser() throws FileNotFoundException {

        int phoneNumber =
                Integer.parseInt(textUI.promptText("Skriv dit telefon nummer:"));

        String mail =
                textUI.promptText("Skriv din mail:");

        User newUser = new User(phoneNumber, mail);

        users.add(newUser);
        fileIO.saveUsers(users);

        currentUser = newUser;

        textUI.displayMsg("Bruger oprettet!");
        showMenu();
    }

    public void registerLicense() {

        String licensePlate = textUI.promptText("Skriv din nummerplade: ");

        Car newCar = new Car(licensePlate);

        cars.add(newCar);
        currentUser.addCar(newCar);
        fileIO.saveCars(cars);

        textUI.displayMsg("Licens tilføjet til din bil! ");
        showMenu();
    }

    public void login() throws FileNotFoundException {

        int phoneNumber =
                Integer.parseInt(textUI.promptText("Skriv dit telefon nummer:"));

        String mail =
                textUI.promptText("Skriv din mail:");

        for (User user : users) {

            if (user.getPhoneNumber() == phoneNumber &&
                    user.getMail().equals(mail)) {

                currentUser = user;

                textUI.displayMsg("Login successful!");
                showMenu();
                return;
            }
        }

        textUI.displayMsg("Forkert login!");
        start();
    }

    public void showMenu() {

        boolean running = true;

        while (running) {

            textUI.displayMsg("\n--- MENU ---");
            textUI.displayMsg("1. Registrer nummerplade");
            textUI.displayMsg("2. Vis alle zoner");
            textUI.displayMsg("3. Vis ledig parkering i zone");
            textUI.displayMsg("4. Start parkering");
            textUI.displayMsg("5. Afslut parkering");
            textUI.displayMsg("6. Log ud");

            String choice = textUI.promptText("\nVælg:");

            switch (choice) {

                case "1" -> registerLicense();
                case "2" -> showAllZones();
                case "3" -> showAvailableParkingInZone();
                case "4" -> chooseParkingType();
                case "5" -> endParking();
                case "6" -> {
                    textUI.displayMsg("Logget ud");
                    running = false;
                }
            }
        }
    }

    private void showAllZones() {
        for (int i = 0; i < zones.size(); i++) {
            textUI.displayMsg((i + 1) + ". " + zones.get(i));
        }
    }


    public void showAvailableParkingInZone() {

        showAllZones();

        int choice = Integer.parseInt(
                textUI.promptText("Vælg zone:")) - 1;

        if (choice < 0 || choice >= zones.size()) {
            textUI.displayMsg("Ugyldigt valg.");
            return;
        }

        Zone zone = zones.get(choice);

        textUI.displayMsg("\n=== " + zone.getName() + " zone ===");

        int disabled = 0;
        int elCar = 0;
        int regular = 0;
        int sharing = 0;
        int privateArea = 0;
        int totalAvailable = 0;

        for (Parking spot : zone.getSpots()) {

            if (spot.isAvailable()) {

                totalAvailable++;

                switch (spot.getSpotTypes().trim()) {
                    case "Disabled Car" -> disabled++;
                    case "El Car" -> elCar++;
                    case "Regular Car" -> regular++;
                    case "Sharing Car" -> sharing++;
                    case "Private Area" -> privateArea++;
                }
            }
        }

        textUI.displayMsg("- Ledige pladser -");
        textUI.displayMsg("Total available: " + totalAvailable);
        textUI.displayMsg("Disabled: " + disabled);
        textUI.displayMsg("El: " + elCar);
        textUI.displayMsg("Regular: " + regular);
        textUI.displayMsg("Sharing: " + sharing);
        textUI.displayMsg("Private: " + privateArea);
    }

    public void chooseParkingType() {

        if (currentUser.getCars() == null || currentUser.getCars().isEmpty()) {
            textUI.displayMsg("Du skal registrere en nummerplade før du kan parkere!");
            return;
        }

        showAllZones();

        int zoneChoice = Integer.parseInt(
                textUI.promptText("Vælg zone:")) - 1;

        if (zoneChoice < 0 || zoneChoice >= zones.size()) {
            textUI.displayMsg("Ugyldigt valg.");
            return;
        }

        Zone zone = zones.get(zoneChoice);

        String type = textUI.promptText(
                "Type (Regular, El, Disabled, Sharing, Private):");

        boolean found = false;

        for (Parking spot : zone.getSpots()) {

            if (spot.isAvailable()
                    && spot.getSpotTypes().toLowerCase().contains(type.toLowerCase())) {

                spot.takeSpot();

                parking = spot;
                payment = new Payment(spot);

                payment.startPayment();

                found = true;
                break;
            }
        }

        if (!found) {
            textUI.displayMsg("Ingen ledige pladser af den type.");
        }
    }


    public void endParking() {

        if (payment == null || parking == null) {
            textUI.displayMsg("Ingen aktiv parkering.");
            return;
        }

        double price = payment.endPayment();

        parking.releaseSpot();

        textUI.displayMsg(String.format("Betalings pris: %.2f kr trækkes fra dit kort", price));

        payment = null;
        parking = null;
    }
}*/