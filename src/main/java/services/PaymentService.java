package services;

import controllers.PaymentController;
import entities.Car;
import entities.Parking;
import entities.Payment;
import entities.User;
import persistence.ConnectionPool;
import persistence.PaymentMapper;

public class PaymentService {

    private ConnectionPool connectionPool;
    private final PaymentMapper paymentMapper;

    public PaymentService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.paymentMapper = new PaymentMapper(connectionPool);
    }

    public Payment startPayment(User user, Car car, Parking parking) {

        Payment payment = new Payment(user, car, parking);

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
