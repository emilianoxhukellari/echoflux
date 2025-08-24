package echoflux.domain.core.jooq.converter;

import com.fasterxml.jackson.databind.JsonNode;
import echoflux.core.core.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.JSONB;

public class JSONBToJsonNodeConverter implements Converter<JSONB, JsonNode> {

    @Override
    public JsonNode from(JSONB databaseObject) {
        return databaseObject == null ? null : JsonMapper.toNode(databaseObject.data());
    }

    @Override
    public JSONB to(JsonNode userObject) {
        return userObject == null ? null : JSONB.valueOf(JsonMapper.toString(userObject));
    }

    @NotNull
    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

    @NotNull
    @Override
    public Class<JsonNode> toType() {
        return JsonNode.class;
    }

}
