package me.rasztabiga.fridgy.products.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Recipe.
 */
@Entity
@Table(name = "recipe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @get: NotNull
    @Column(name = "instructions_body", nullable = false)
    var instructionsBody: String? = null,

    @OneToMany(mappedBy = "recipe")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

    @JsonIgnoreProperties(
        value = [
            "product", "productUnit", "recipe"
        ],
        allowSetters = true
    )
    var recipeIngredients: MutableSet<RecipeIngredient>? = mutableSetOf(),

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addRecipeIngredients(recipeIngredient: RecipeIngredient): Recipe {
        if (this.recipeIngredients == null) {
            this.recipeIngredients = mutableSetOf()
        }
        this.recipeIngredients?.add(recipeIngredient)
        recipeIngredient.recipe = this
        return this
    }

    fun removeRecipeIngredients(recipeIngredient: RecipeIngredient): Recipe {
        this.recipeIngredients?.remove(recipeIngredient)
        recipeIngredient.recipe = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Recipe) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Recipe{" +
        "id=$id" +
        ", name='$name'" +
        ", instructionsBody='$instructionsBody'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
