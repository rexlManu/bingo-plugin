package de.rexlmanu.bingo.shared.flag;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.rexlmanu.bingo.game.flags.Flag;
import de.rexlmanu.bingo.game.flags.advancement.FlagAdvancement;
import de.rexlmanu.bingo.game.flags.item.FlagItem;
import de.rexlmanu.bingo.shared.flag.adapters.FlagAdvancementAdapter;
import de.rexlmanu.bingo.shared.flag.adapters.FlagItemAdapter;
import de.rexlmanu.bingo.utility.gson.RuntimeTypeAdapterFactory;
import de.rexlmanu.bingo.utility.interfaces.Reloadable;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public class FlagTemplateProvider implements Reloadable {

    private static final FlagTemplate ITEM_FLAG_TEMPLATE = new FlagTemplate("example-item", FlagType.ITEM,
            Collections.singletonList(new FlagItem(Material.DIRT)));
    private static final FlagTemplate ADVANCEMENT__FLAG_TEMPLATE = new FlagTemplate("example-advancement", FlagType.ADVANCEMENT,
            Collections.singletonList(new FlagAdvancement("story/root")));

    private static final JsonParser PARSER = new JsonParser();
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(FlagItem.class, new FlagItemAdapter())
            .registerTypeAdapter(FlagAdvancement.class, new FlagAdvancementAdapter())
            .setPrettyPrinting()
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory
                            .of(Flag.class, "type")
                            .registerSubtype(FlagItem.class)
                            .registerSubtype(FlagAdvancement.class)
            )
            .create();

    private static final Type FLAG_TEMPLATE_TYPE_TOKEN = TypeToken.get(FlagTemplate.class).getType();

    @Getter
    private List<FlagTemplate> loadedTemplates;

    private final Path templateDirectory;

    public FlagTemplateProvider(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        this.templateDirectory = dataFolder.toPath().resolve("template");
        if (!Files.isDirectory(this.templateDirectory)) {
            try {
                Files.createDirectory(this.templateDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Arrays.stream(FlagType.values()).forEach(type -> {
            try {
                Path directory = this.templateDirectory.resolve(type.directory());
                if (!Files.isDirectory(directory)) {
                    Files.createDirectory(directory);
                    Files.write(directory.resolve("example-" + type.name().toLowerCase() + ".json"),
                            GSON.toJson(type.equals(FlagType.ITEM) ? ITEM_FLAG_TEMPLATE : ADVANCEMENT__FLAG_TEMPLATE).getBytes(),
                            StandardOpenOption.CREATE_NEW);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.reload();
    }

    public void saveTemplate(FlagTemplate flagTemplate) {
        try {
            Path filePath = this.templateDirectory.resolve(flagTemplate.type().directory()).resolve(flagTemplate.fileName());
            Files.deleteIfExists(filePath);
            Files.write(filePath,
                    GSON.toJson(flagTemplate).getBytes(),
                    StandardOpenOption.CREATE_NEW);

            this.loadedTemplates.add(flagTemplate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        this.loadedTemplates = Lists.newArrayList();

        Arrays.stream(FlagType.values()).forEach(type -> {
            try {
                Files.list(this.templateDirectory.resolve(type.directory()))
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            try {
                                String content = new String(Files.readAllBytes(path));
                                FlagTemplate template = GSON.fromJson(content, FLAG_TEMPLATE_TYPE_TOKEN);
                                template.fileName(path.getFileName().toString());
                                this.loadedTemplates.add(template);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<FlagTemplate> getTemplates(FlagType type) {
        return this.loadedTemplates.stream().filter(flagTemplate -> flagTemplate.type().equals(type)).collect(Collectors.toList());
    }

    public Optional<FlagTemplate> findByName(String name) {
        return this.loadedTemplates.stream().filter(template -> template.name().equals(name)).findFirst();
    }
}
