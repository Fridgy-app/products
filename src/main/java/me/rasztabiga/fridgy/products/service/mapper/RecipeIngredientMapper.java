package me.rasztabiga.fridgy.products.service.mapper;

import me.rasztabiga.fridgy.products.domain.*;
import me.rasztabiga.fridgy.products.service.dto.RecipeIngredientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RecipeIngredient} and its DTO {@link RecipeIngredientDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, ProductUnitMapper.class, RecipeMapper.class })
public interface RecipeIngredientMapper extends EntityMapper<RecipeIngredientDTO, RecipeIngredient> {
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    @Mapping(target = "productUnit", source = "productUnit", qualifiedByName = "id")
    @Mapping(target = "recipe", source = "recipe", qualifiedByName = "id")
    RecipeIngredientDTO toDto(RecipeIngredient s);
}
