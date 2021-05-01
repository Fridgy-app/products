package me.rasztabiga.fridgy.products.service.mapper;

import me.rasztabiga.fridgy.products.domain.*;
import me.rasztabiga.fridgy.products.service.dto.RecipeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Recipe} and its DTO {@link RecipeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RecipeMapper extends EntityMapper<RecipeDTO, Recipe> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RecipeDTO toDtoId(Recipe recipe);
}
