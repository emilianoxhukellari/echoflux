package transcribe.core.core.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldProperty {

    private Field field;
    private Field parentField;
    private String name;
    private String attributeName;

}
