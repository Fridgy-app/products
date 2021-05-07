package me.rasztabiga.fridgy.products.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "ean_code")
    var eanCode: String? = null,

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_product__product_unit",
        joinColumns = [
            JoinColumn(name = "product_id")
        ],
        inverseJoinColumns = [
            JoinColumn(name = "product_unit_id")
        ]
    )
    @JsonIgnoreProperties(
        value = [
            "products"
        ],
        allowSetters = true
    )
    var productUnits: MutableSet<ProductUnit>? = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties(
        value = [
            "products"
        ],
        allowSetters = true
    )
    var productCategory: ProductCategory? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addProductUnit(productUnit: ProductUnit): Product {
        if (this.productUnits == null) {
            this.productUnits = mutableSetOf()
        }
        this.productUnits?.add(productUnit)
        productUnit.products?.add(this)
        return this
    }

    fun removeProductUnit(productUnit: ProductUnit): Product {
        this.productUnits?.remove(productUnit)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Product{" +
        "id=$id" +
        ", name='$name'" +
        ", eanCode='$eanCode'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
