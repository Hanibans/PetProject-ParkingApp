import domain.Car;
import domain.Menu;
import domain.User;
import domain.Zone;
import util.FileIO;
import util.TextUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {



        try {
            BufferedImage myPicture = ImageIO.read(new File("src/Map.png"));


            JLabel picLabel = new JLabel(new ImageIcon(myPicture));


            JFrame frame = new JFrame("Image Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(picLabel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileIO fileIO = new FileIO();
        TextUI textUI = new TextUI();

        List<User> users = FileIO.loadUsers("Data/UserData");
        List<Zone> zones = fileIO.loadZones("Data/zoneData");
        List<Car> cars = fileIO.loadCars("Data/carData");

        textUI.displayMsg("Velkommen til Parkeringsservice S&N");
        Menu menu = new Menu(users, zones, cars, textUI, fileIO);

        menu.start();
    }





}