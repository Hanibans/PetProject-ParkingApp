package domain;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class Payment {

     private Parking parking;
     private LocalTime startTime;
     private LocalTime endTime;

    public Payment(Parking parking) {
        this.parking = parking;
    }

    public void startPayment() {
        startTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        System.out.println("Parkering startet kl: " + startTime);

    }

    public double endPayment() {
            endTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);

            Duration duration = Duration.between(startTime, endTime);
            long minutes = duration.toMinutes();
            long seconds = duration.toSecondsPart();
            long totalSeconds = Duration.between(startTime, LocalTime.now()).toSeconds();

            System.out.println("Parkering afsluttet efter: " + minutes +
                    " min og " + seconds + " sek");

            return (totalSeconds / 3600.0) * parking.getPricePerHour();
        }


}