package me.rasztabiga.fridgy.products.service.mapper

import me.rasztabiga.fridgy.products.domain.GroceryItem
import me.rasztabiga.fridgy.products.service.dto.GroceryItemDTO
import org.mapstruct.*

/**
 * Mapper for the entity [GroceryItem] and its DTO [GroceryItemDTO].
 */
@Mapper(componentModel = "spring", uses = [UserMapper::class, ProductMapper::class, ProductUnitMapper::class])
interface GroceryItemMapper :
    EntityMapper<GroceryItemDTO, GroceryItem> {

    @Mappings(
        Mapping(target = "user", source = "user", qualifiedByName = ["login"]),
        Mapping(target = "product", source = "product", qualifiedByName = ["id"]),
        Mapping(target = "unit", source = "unit", qualifiedByName = ["id"])
    )
    override fun toDto(s: GroceryItem): GroceryItemDTO
}
