package transcribe.application.operation;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.operation.data.OperationEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationJpaDtoMapper extends JpaDtoMapper<OperationJpaDto, OperationEntity> {

    @Override
    OperationJpaDto toDto(OperationEntity entity);

    @Override
    OperationEntity toEntity(OperationJpaDto dto);

    @Override
    OperationEntity updateEntity(@MappingTarget OperationEntity entity, OperationJpaDto dto);

    @Override
    default Class<OperationJpaDto> getBeanType() {
        return OperationJpaDto.class;
    }

}
