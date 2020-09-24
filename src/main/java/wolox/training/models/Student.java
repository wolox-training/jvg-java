package wolox.training.models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("student")
@Setter
@Getter
@NoArgsConstructor
public class Student extends User {
  @Column
  private String year;
}
