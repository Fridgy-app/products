package me.rasztabiga.fridgy.products.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A ProductCategory.
 */
@Entity
@Table(name = "product_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null,

    @OneToMany(mappedBy = "productCategory")
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

    fun addProduct(product: Product): ProductCategory {
        if (this.products == null) {
            this.products = mutableSetOf()
        }
        this.products?.add(product)
        product.productCategory = this
        return this
    }

    fun removeProduct(product: Product): ProductCategory {
        this.products?.remove(product)
        product.productCategory = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductCategory) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductCategory{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
