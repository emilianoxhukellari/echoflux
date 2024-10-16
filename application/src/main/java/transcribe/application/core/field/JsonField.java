package transcribe.application.core.field;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.customfield.CustomField;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.json.JsonMapper;

public class JsonField extends CustomField<JsonNode> {

    private final JsonMapper jsonMapper;

    public JsonField() {
        this.jsonMapper = SpringContext.getBean(JsonMapper.class);
    }

    @Override
    protected JsonNode generateModelValue() {
        return jsonMapper.toNode("{}");
    }

    @Override
    protected void setPresentationValue(JsonNode newPresentationValue) {
    }

}
