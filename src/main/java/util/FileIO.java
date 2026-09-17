/*package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import domain.*;

public class FileIO {

    static final String userHeader = "phoneNumber, mail";
    private static final String userFile = "Data/UserData";
    private static final String carFile = "Data/carData";
    private static final String zoneFile = "Data/zoneData.csv";

    public static List<User> loadUsers(String path) {
        List<User> users = new ArrayList<>();

        try (Scanner scan = new Scanner(new File(path))) {

            if (scan.hasNextLine()) scan.nextLine();

            while (scan.hasNextLine()) {

                String[] parts = scan.nextLine().split(",");

                String phoneNumber = parts[0].trim();
                String mail = parts[1].trim();

                users.add(new User(phoneNumber, mail));
            }

        } catch (Exception e) {
            System.out.println("Fejl i users: " + e.getMessage());
        }

        return users;
    }

    public void saveUsers(List<User> users) {
        try (FileWriter writer = new FileWriter(userFile)) {

            writer.write(userHeader + "\n");

            for (User user : users) {
                writer.write(user.getPhoneNumber() + "," + user.getMail() + "\n");
            }

        } catch (IOException e) {
            System.out.println("Fejl ved saveUsers");
        }
    }

    public static List<Car> loadCars(String path) {
        List<Car> cars = new ArrayList<>();

        try (Scanner scan = new Scanner(new File(path))) {

            if (scan.hasNextLine()) scan.nextLine();

            while (scan.hasNextLine()) {

                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                String licensePlate = parts[0].trim();

                cars.add(new Car(licensePlate));
            }

        } catch (Exception e) {
            System.out.println("Fejl i cars: " + e.getMessage());
        }

        return cars;
    }

    public void saveCars(List<Car> cars) {
        try (FileWriter writer = new FileWriter(carFile)) {

            writer.write("licensePlate\n");

            for (Car car : cars) {
                writer.write(car.getLicensePlate() + "\n");
            }

        } catch (IOException e) {
            System.out.println("Fejl ved saveCars");
        }
    }

    public static List<Zone> loadZones(String path) {
        List<Zone> zones = new ArrayList<>();

        try (Scanner scan = new Scanner(new File(path))) {

            if (scan.hasNextLine()) scan.nextLine();

            while (scan.hasNextLine()) {

                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",");

                Zone z = new Zone(
                        p[0].trim(),
                        Double.parseDouble(p[1].trim()),
                        Integer.parseInt(p[2].trim()),
                        Integer.parseInt(p[3].trim()),
                        Integer.parseInt(p[4].trim()),
                        Integer.parseInt(p[5].trim()),
                        Integer.parseInt(p[6].trim()),
                        Integer.parseInt(p[7].trim())
                );

                zones.add(z);
            }

        } catch (Exception e) {
            System.out.println("Fejl i zones: " + e.getMessage());
        }

        return zones;
    }
}*/