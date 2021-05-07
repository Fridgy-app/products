package me.rasztabiga.fridgy.products.service.mapper

import me.rasztabiga.fridgy.products.domain.Recipe
import me.rasztabiga.fridgy.products.service.dto.RecipeDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Recipe] and its DTO [RecipeDTO].
 */
@Mapper(componentModel = "spring", uses = [])
interface RecipeMapper :
    EntityMapper<RecipeDTO, Recipe> {

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoId(recipe: Recipe): RecipeDTO
}
