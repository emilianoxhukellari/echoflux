package echoflux.application.operation;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import echoflux.application.core.jpa.dto.JpaDtoMapper;
import echoflux.domain.operation.data.OperationEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationJpaDtoMapper extends JpaDtoMapper<OperationJpaDto, OperationEntity> {

    @Override
    OperationJpaDto toDto(OperationEntity entity);

    @Override
    OperationEntity toEntity(OperationJpaDto dto);

    @Override
    @Mapping(target = "version", ignore = true)
    OperationEntity updateEntity(@MappingTarget OperationEntity entity, OperationJpaDto dto);

    @Override
    default Class<OperationJpaDto> getBeanType() {
        return OperationJpaDto.class;
    }

}
