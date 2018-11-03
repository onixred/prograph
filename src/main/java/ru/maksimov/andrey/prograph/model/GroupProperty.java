package ru.maksimov.andrey.prograph.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Модель данных группа свойств
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GroupProperty extends Property {

    @Getter(lombok.AccessLevel.NONE)
    private final Map<String, Property> likeShortName2Property = new HashMap<>();

    public GroupProperty(Property property) {
        super(property.getName(), property.getType());
        likeShortName2Property.put(property.getShortName(), property);
    }

    public Set<String> getLikeShortNames() {
        return likeShortName2Property.keySet();
    }

    public Collection<Property> getLikeProperties() {
        return likeShortName2Property.values();
    }

    public void addLikeProperty(Property likeProperty) {
        String shortName = likeProperty.getShortName();
        if (shortName.length() < getShortName().length()) {
            setName(likeProperty.getName());
            setShortName(shortName);
        }
        likeShortName2Property.put(shortName, likeProperty);
    }
}
