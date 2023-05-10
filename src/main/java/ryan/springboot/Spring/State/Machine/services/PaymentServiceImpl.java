package ryan.springboot.Spring.State.Machine.services;

import lombok.RequiredArgsConstructor;
//import org.aspectj.bridge.Message;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import ryan.springboot.Spring.State.Machine.domain.Payment;
import ryan.springboot.Spring.State.Machine.domain.PaymentEvent;
import ryan.springboot.Spring.State.Machine.domain.PaymentState;
import ryan.springboot.Spring.State.Machine.repository.PaymentRepository;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentsService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTHORIZE);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTHORIZE);
        return sm;
    }

    @Deprecated //not needed
    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    //So now we want to work on restoring the state machine from the DB, we will persist the state machine and then retrieve it from the DB
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {

        //1st we get a payment from the db
        Optional<Payment> payment = paymentRepository.findById(paymentId);


        //below we are asking the sm factory for an instance of the sm
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.get().getId()));

        //we stop our sm
        sm.stop();

        //we get a sm accessor, and we use it to get our sm and the set it to an instance of the sm which we got from the db or the payment from the db
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    //as we go thru events, we will send an event which will go through a state machine and the Interceptor will then kick in. Go to interceptor for more info
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    //on the line below we are telling the sm to stop and we setting it to the specific state of the sm which we retrieved from the db
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.get().getState(), null, null, null));
                });

        sm.start();

        return sm;
        //SO BASICALLY THIS IS THE COMMON METHOD WE ARE GOING TO USE WITH OUR METHODS WHICH WORK WITH THE STATE MACHINE
    }

    // Now we are going to configure a message which we will use to send events to a Statemachine
    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
       Message msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();

        sm.sendEvent(msg);


    }


}
