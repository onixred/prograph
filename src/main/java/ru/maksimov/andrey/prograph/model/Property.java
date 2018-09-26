package ru.maksimov.andrey.prograph.model;

import lombok.Data;
import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;

@Data
public class Property {
    private final String name;
    private final String shortName;
    private final PropertyType type;

    public Property(String name, PropertyType type) {
        this.name = name;
        this.type = type;
        this.shortName = Utility.tailCut(name);
    }
}
