package ryan.springboot.Spring.State.Machine.services;

import org.springframework.statemachine.StateMachine;
import ryan.springboot.Spring.State.Machine.domain.Payment;
import ryan.springboot.Spring.State.Machine.domain.PaymentEvent;
import ryan.springboot.Spring.State.Machine.domain.PaymentState;

public interface PaymentsService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);

}
