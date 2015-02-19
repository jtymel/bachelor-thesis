package gwtEntity.client;

import java.io.Serializable;

/**
 *
 * @author jtymel
 */
public class CategorizationDto implements Serializable {
    private Long id;
    private String name;

    public CategorizationDto() {
    }

    public CategorizationDto(Long id, String name) {
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
