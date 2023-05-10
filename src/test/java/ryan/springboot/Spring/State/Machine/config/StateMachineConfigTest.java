package ryan.springboot.Spring.State.Machine.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import ryan.springboot.Spring.State.Machine.domain.PaymentEvent;
import ryan.springboot.Spring.State.Machine.domain.PaymentState;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StateMachineConfigTest {


    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentState, PaymentEvent> sm= factory.getStateMachine(UUID.randomUUID());

        sm.start();
        System.out.println( sm.getState().toString());


        sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        System.out.println(sm.getState().toString());


        sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        System.out.println(sm.getState().toString());

        sm.sendEvent(PaymentEvent.PRE_AUTH_DECLINED);
        System.out.println(sm.getState().toString());
    }








}