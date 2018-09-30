package ru.maksimov.andrey.prograph.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

import ru.maksimov.andrey.prograph.component.PropertyType;

/**
 * Модель данных файл свойств
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Data
@AllArgsConstructor
public class File {
	private final String name;
	private final PropertyType type;
	private final Set<GroupProperty> GroupProperties = new HashSet<>();
}
