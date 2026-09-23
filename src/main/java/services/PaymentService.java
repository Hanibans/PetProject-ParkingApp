package services;

import entities.Car;
import entities.Parking;
import entities.Payment;
import entities.User;

public class PaymentService {

    public Payment startPayment(
            User user,
            Car car,
            Parking parking) {

        Payment payment =
                new Payment(user, car, parking);

        payment.startPayment();

        parking.takeSpot();

        return payment;
    }

    public double finishPayment(Payment payment) {

        double amount = payment.endPayment();

        payment.getParking().releaseSpot();

        return amount;
    }
}
