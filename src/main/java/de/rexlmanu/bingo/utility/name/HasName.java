package de.rexlmanu.bingo.utility.name;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class HasName {
    private String name;
}
