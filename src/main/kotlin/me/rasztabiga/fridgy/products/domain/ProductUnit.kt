package me.rasztabiga.fridgy.products.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A ProductUnit.
 */
@Entity
@Table(name = "product_unit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductUnit(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null,

    @ManyToMany(mappedBy = "productUnits")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

    @JsonIgnoreProperties(
        value = [
            "productUnits", "productCategory"
        ],
        allowSetters = true
    )
    var products: MutableSet<Product>? = mutableSetOf(),

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addProduct(product: Product): ProductUnit {
        if (this.products == null) {
            this.products = mutableSetOf()
        }
        this.products?.add(product)
        product.productUnits?.add(this)
        return this
    }

    fun removeProduct(product: Product): ProductUnit {
        this.products?.remove(product)
        product.productUnits?.remove(this)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductUnit) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductUnit{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
