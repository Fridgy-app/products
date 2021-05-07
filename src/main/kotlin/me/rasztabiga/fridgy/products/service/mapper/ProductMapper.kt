package me.rasztabiga.fridgy.products.service.mapper

import me.rasztabiga.fridgy.products.domain.Product
import me.rasztabiga.fridgy.products.service.dto.ProductDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Product] and its DTO [ProductDTO].
 */
@Mapper(componentModel = "spring", uses = [ProductUnitMapper::class, ProductCategoryMapper::class])
interface ProductMapper :
    EntityMapper<ProductDTO, Product> {

    @Mappings(
        Mapping(target = "productUnits", source = "productUnits", qualifiedByName = ["idSet"]),
        Mapping(target = "productCategory", source = "productCategory", qualifiedByName = ["id"])
    )
    override fun toDto(s: Product): ProductDTO

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(product: Product): ProductDTO

    @Mappings(
        Mapping(target = "removeProductUnit", ignore = true)
    )
    override fun toEntity(productDTO: ProductDTO): Product
}
