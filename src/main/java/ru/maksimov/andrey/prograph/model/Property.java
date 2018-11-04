package ru.maksimov.andrey.prograph.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.maksimov.andrey.prograph.component.PropertyType;

/**
 * Модель данных свойсто
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Property extends BaseProperty {

    @Setter(lombok.AccessLevel.PROTECTED)
    private String value;

    public Property(String name, PropertyType type, String value) {
        super(name, type);
        this.value = value;
    }

}
