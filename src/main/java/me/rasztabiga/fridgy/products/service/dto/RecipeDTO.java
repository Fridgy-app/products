package me.rasztabiga.fridgy.products.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link me.rasztabiga.fridgy.products.domain.Recipe} entity.
 */
public class RecipeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String instructionsBody;

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

    public String getInstructionsBody() {
        return instructionsBody;
    }

    public void setInstructionsBody(String instructionsBody) {
        this.instructionsBody = instructionsBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeDTO)) {
            return false;
        }

        RecipeDTO recipeDTO = (RecipeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, recipeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", instructionsBody='" + getInstructionsBody() + "'" +
            "}";
    }
}
