package ryan.springboot.Spring.State.Machine.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ryan.springboot.Spring.State.Machine.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
