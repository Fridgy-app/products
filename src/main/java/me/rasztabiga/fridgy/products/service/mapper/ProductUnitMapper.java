package me.rasztabiga.fridgy.products.service.mapper;

import java.util.Set;
import me.rasztabiga.fridgy.products.domain.*;
import me.rasztabiga.fridgy.products.service.dto.ProductUnitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductUnit} and its DTO {@link ProductUnitDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductUnitMapper extends EntityMapper<ProductUnitDTO, ProductUnit> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductUnitDTO toDtoId(ProductUnit productUnit);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ProductUnitDTO> toDtoIdSet(Set<ProductUnit> productUnit);
}
