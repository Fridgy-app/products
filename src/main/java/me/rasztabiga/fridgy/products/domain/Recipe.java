package me.rasztabiga.fridgy.products.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Recipe.
 */
@Entity
@Table(name = "recipe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "instructions_body", nullable = false)
    private String instructionsBody;

    @OneToMany(mappedBy = "recipe")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "productUnit", "recipe" }, allowSetters = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recipe id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Recipe name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructionsBody() {
        return this.instructionsBody;
    }

    public Recipe instructionsBody(String instructionsBody) {
        this.instructionsBody = instructionsBody;
        return this;
    }

    public void setInstructionsBody(String instructionsBody) {
        this.instructionsBody = instructionsBody;
    }

    public Set<RecipeIngredient> getRecipeIngredients() {
        return this.recipeIngredients;
    }

    public Recipe recipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        this.setRecipeIngredients(recipeIngredients);
        return this;
    }

    public Recipe addRecipeIngredients(RecipeIngredient recipeIngredient) {
        this.recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
        return this;
    }

    public Recipe removeRecipeIngredients(RecipeIngredient recipeIngredient) {
        this.recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
        return this;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        if (this.recipeIngredients != null) {
            this.recipeIngredients.forEach(i -> i.setRecipe(null));
        }
        if (recipeIngredients != null) {
            recipeIngredients.forEach(i -> i.setRecipe(this));
        }
        this.recipeIngredients = recipeIngredients;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }
        return id != null && id.equals(((Recipe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recipe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", instructionsBody='" + getInstructionsBody() + "'" +
            "}";
    }
}
