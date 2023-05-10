package ryan.springboot.Spring.State.Machine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import ryan.springboot.Spring.State.Machine.domain.Payment;
import ryan.springboot.Spring.State.Machine.domain.PaymentEvent;
import ryan.springboot.Spring.State.Machine.domain.PaymentState;
import ryan.springboot.Spring.State.Machine.repository.PaymentRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
//So basically this class will be used to listen to the transitions between States and persist those changes to the database
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    //So this will go and get that payment out of the database, set it to the state of the event and then persist it back to the DB

    private static  PaymentRepository paymentRepository;


        @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg->{
           Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER,-1L)))
                   .ifPresent(paymentId->{
                       Optional<Payment> payment = paymentRepository.findById(paymentId);
                       payment.get().setState(state.getId());
                       paymentRepository.save(payment.get());

                   });
        });
    }



}
