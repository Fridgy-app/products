package me.rasztabiga.fridgy.products.service.mapper;

import me.rasztabiga.fridgy.products.domain.*;
import me.rasztabiga.fridgy.products.service.dto.ProductCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductCategory} and its DTO {@link ProductCategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductCategoryMapper extends EntityMapper<ProductCategoryDTO, ProductCategory> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductCategoryDTO toDtoId(ProductCategory productCategory);
}
