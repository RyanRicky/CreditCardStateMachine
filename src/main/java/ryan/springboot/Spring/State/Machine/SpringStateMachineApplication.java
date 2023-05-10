package ryan.springboot.Spring.State.Machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringStateMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringStateMachineApplication.class, args);
	}

}
