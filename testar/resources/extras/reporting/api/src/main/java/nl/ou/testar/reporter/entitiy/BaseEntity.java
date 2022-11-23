package nl.ou.testar.reporter.entitiy;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BaseEntity() {
    }

    public BaseEntity(Integer id) {
        this.id = id;
    }
}
