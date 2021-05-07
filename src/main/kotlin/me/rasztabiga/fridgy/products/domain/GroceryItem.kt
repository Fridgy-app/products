package me.rasztabiga.fridgy.products.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

/**
 * A GroceryItem.
 */
@Entity
@Table(name = "grocery_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class GroceryItem(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @Column(name = "quantity")
    var quantity: Double? = null,

    @Column(name = "description")
    var description: String? = null,

    @ManyToOne var user: User? = null,

    @ManyToOne
    @JsonIgnoreProperties(
        value = [
            "productUnits", "productCategory"
        ],
        allowSetters = true
    )
    var product: Product? = null,

    @ManyToOne
    @JsonIgnoreProperties(
        value = [
            "products"
        ],
        allowSetters = true
    )
    var unit: ProductUnit? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroceryItem) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "GroceryItem{" +
        "id=$id" +
        ", quantity=$quantity" +
        ", description='$description'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
