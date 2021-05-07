package me.rasztabiga.fridgy.products.service.mapper

import me.rasztabiga.fridgy.products.domain.ProductCategory
import me.rasztabiga.fridgy.products.service.dto.ProductCategoryDTO
import org.mapstruct.*

/**
 * Mapper for the entity [ProductCategory] and its DTO [ProductCategoryDTO].
 */
@Mapper(componentModel = "spring", uses = [])
interface ProductCategoryMapper :
    EntityMapper<ProductCategoryDTO, ProductCategory> {

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(productCategory: ProductCategory): ProductCategoryDTO
}
