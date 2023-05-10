package ryan.springboot.Spring.State.Machine.domain;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)  //this tells hibernate to create a VarChar in the database for this and the actual name of the enumeration will be there
    private PaymentState state;

    private BigDecimal amount;

}
