package page.admin.financial.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TaxType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tax_seq_generator")
    @SequenceGenerator(name = "tax_seq_generator", sequenceName = "tax_seq", allocationSize = 1)
    private Long taxCode;

    private String taxType;
    private String description;
}
