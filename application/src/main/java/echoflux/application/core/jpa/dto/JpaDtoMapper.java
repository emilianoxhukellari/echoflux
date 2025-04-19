package echoflux.application.core.jpa.dto;

import echoflux.core.core.bean.BeanTypeAware;

public interface JpaDtoMapper<DTO, ENTITY> extends BeanTypeAware<DTO> {

    DTO toDto(ENTITY entity);

    ENTITY toEntity(DTO dto);

    ENTITY updateEntity(ENTITY entity, DTO dto);

}
