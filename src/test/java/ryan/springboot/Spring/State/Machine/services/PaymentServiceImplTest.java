package ryan.springboot.Spring.State.Machine.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import ryan.springboot.Spring.State.Machine.domain.Payment;
import ryan.springboot.Spring.State.Machine.domain.PaymentEvent;
import ryan.springboot.Spring.State.Machine.domain.PaymentState;
import ryan.springboot.Spring.State.Machine.repository.PaymentRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentsService paymentsService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentsService.newPayment(payment);

        System.out.println( "Should be NEW");
        System.out.println(savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm =  paymentsService.preAuth(savedPayment.getId());

        Optional<Payment> preAuthedPayment = paymentRepository.findById(savedPayment.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
        System.out.println( sm.getState().getId());

        System.out.println(preAuthedPayment.get());
    }

    @Transactional
    @RepeatedTest(10)
    void testAuth() {
        Payment savedPayment = paymentsService.newPayment(payment);


        StateMachine<PaymentState, PaymentEvent> preAuthSM =  paymentsService.preAuth(savedPayment.getId());

        if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH){
            System.out.println("Payment is Pre Authorized");
            StateMachine<PaymentState, PaymentEvent> authSM =  paymentsService.authorizePayment(savedPayment.getId());
            System.out.println("Result of Auth: " + authSM.getState().getId());
        }
        else {
            System.out.println("Payment failed pre-auth...");
        }

    }
}