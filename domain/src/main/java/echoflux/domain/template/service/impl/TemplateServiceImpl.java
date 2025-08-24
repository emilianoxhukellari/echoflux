package echoflux.domain.template.service.impl;

import echoflux.domain.template.service.SaveTemplateCommand;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.template.service.RenderTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import echoflux.template.renderer.TemplateRenderer;

import static echoflux.domain.jooq.Tables.TEMPLATE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final DSLContext ctx;
    private final TemplateRenderer templateRenderer;

    @Override
    public String render(RenderTemplateCommand command) {
        var content = ctx.select(TEMPLATE.CONTENT)
                .from(TEMPLATE)
                .where(TEMPLATE.NAME.eq(command.getName()))
                .fetchSingleInto(String.class);

        return templateRenderer.renderFromString(content, command.getDataModel());
    }

    @Transactional
    @Override
    public Long save(SaveTemplateCommand command) {
        var record = command.getId() != null
                ? ctx.fetchSingle(TEMPLATE, TEMPLATE.ID.eq(command.getId()))
                : ctx.newRecord(TEMPLATE);

        record.setName(command.getName());
        record.setContent(command.getContent());
        record.store();

        return record.getId();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        ctx.deleteFrom(TEMPLATE)
                .where(TEMPLATE.ID.eq(id))
                .execute();
    }

}
