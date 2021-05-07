package me.rasztabiga.fridgy.products.service.mapper

import me.rasztabiga.fridgy.products.domain.RecipeIngredient
import me.rasztabiga.fridgy.products.service.dto.RecipeIngredientDTO
import org.mapstruct.*

/**
 * Mapper for the entity [RecipeIngredient] and its DTO [RecipeIngredientDTO].
 */
@Mapper(componentModel = "spring", uses = [ProductMapper::class, ProductUnitMapper::class, RecipeMapper::class])
interface RecipeIngredientMapper :
    EntityMapper<RecipeIngredientDTO, RecipeIngredient> {

    @Mappings(
        Mapping(target = "product", source = "product", qualifiedByName = ["id"]),
        Mapping(target = "productUnit", source = "productUnit", qualifiedByName = ["id"]),
        Mapping(target = "recipe", source = "recipe", qualifiedByName = ["id"])
    )
    override fun toDto(s: RecipeIngredient): RecipeIngredientDTO
}
