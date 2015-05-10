package gwtEntity.common.objects;

import java.io.Serializable;

/**
 *
 * @author jtymel
 */
public class LabelDto implements Serializable {
    private Long id;
    private String name;

    public LabelDto() {
    }

    public LabelDto(Long id, String name) {
        this.id = id;
        this.name = name;
    } 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
