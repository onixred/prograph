package ru.maksimov.andrey.prograph.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;

/**
 * Модель данных свойство
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Property {
    @Setter(lombok.AccessLevel.PROTECTED)
    private String name;
    @Setter(lombok.AccessLevel.PROTECTED)
    private String shortName;
    private final PropertyType type;

    public Property(String name, PropertyType type) {
        this.name = name;
        this.type = type;
        shortName = Utility.tailCut(name);
    }
}
