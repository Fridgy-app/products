package me.rasztabiga.fridgy.products.service.mapper

import me.rasztabiga.fridgy.products.domain.ProductUnit
import me.rasztabiga.fridgy.products.service.dto.ProductUnitDTO
import org.mapstruct.*

/**
 * Mapper for the entity [ProductUnit] and its DTO [ProductUnitDTO].
 */
@Mapper(componentModel = "spring", uses = [])
interface ProductUnitMapper :
    EntityMapper<ProductUnitDTO, ProductUnit> {

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(productUnit: ProductUnit): ProductUnitDTO

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoIdSet(productUnit: Set<ProductUnit>): Set<ProductUnitDTO>
}
