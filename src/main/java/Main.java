import Controllers.UserController;
import configuration.ThymeleafConfig;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    public static void main(String[] args) {

        var app = Javalin.create(config -> {
            UserController.setRoutes(config);
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));

        }).start(7070);







        /*try {
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
    }*/


    }

}