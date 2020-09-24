package wolox.training.models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("professor")
@Setter
@Getter
@NoArgsConstructor
public class Professor extends User {
  @Column
  private String subject;
}
