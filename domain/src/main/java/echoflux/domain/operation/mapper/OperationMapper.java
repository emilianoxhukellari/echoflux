package echoflux.domain.operation.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import echoflux.domain.operation.data.OperationEntity;
import echoflux.domain.operation.data.OperationProjection;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    OperationProjection toProjection(OperationEntity entity);

}
