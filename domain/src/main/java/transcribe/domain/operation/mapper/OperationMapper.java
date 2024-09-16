package transcribe.domain.operation.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationStatus;

import java.time.LocalDateTime;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationMapper {

    OperationEntity asEntity(@MappingTarget OperationEntity entity,
                             LocalDateTime endedAt,
                             OperationStatus status,
                             String error);

    OperationEntity asEntity(@MappingTarget OperationEntity entity,
                             LocalDateTime endedAt,
                             OperationStatus status);

}
