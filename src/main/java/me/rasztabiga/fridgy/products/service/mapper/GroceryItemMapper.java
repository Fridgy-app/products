package me.rasztabiga.fridgy.products.service.mapper;

import me.rasztabiga.fridgy.products.domain.*;
import me.rasztabiga.fridgy.products.service.dto.GroceryItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GroceryItem} and its DTO {@link GroceryItemDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ProductMapper.class, ProductUnitMapper.class })
public interface GroceryItemMapper extends EntityMapper<GroceryItemDTO, GroceryItem> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    @Mapping(target = "unit", source = "unit", qualifiedByName = "id")
    GroceryItemDTO toDto(GroceryItem s);
}
