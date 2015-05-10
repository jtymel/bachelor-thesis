package gwtEntity.common.objects;

import java.io.Serializable;

/**
 *
 * @author jtymel
 */
public class CategoryDto implements Serializable {
    private Long id;
    private String name;
    private String categorization;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name) {
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

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(String categorization) {
        this.categorization = categorization;
    }

}
