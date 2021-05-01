package me.rasztabiga.fridgy.products.service.mapper;

import java.util.Set;
import me.rasztabiga.fridgy.products.domain.*;
import me.rasztabiga.fridgy.products.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductUnitMapper.class, ProductCategoryMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "productUnits", source = "productUnits", qualifiedByName = "idSet")
    @Mapping(target = "productCategory", source = "productCategory", qualifiedByName = "id")
    ProductDTO toDto(Product s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoId(Product product);

    @Mapping(target = "removeProductUnit", ignore = true)
    Product toEntity(ProductDTO productDTO);
}
