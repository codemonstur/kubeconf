package kubeconf.core;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.services.commands.Command;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static jcli.CliParser.parseCommandLineArguments;

public enum Functions {;

    private static final PebbleEngine PEBBLE = new PebbleEngine.Builder().newLineTrimming(false).build();

    public static PebbleTemplate newTemplate(final String name) {
        return PEBBLE.getTemplate("templates/" + name + ".yaml");
    }

    public static <T extends MinimalArguments> Command newWriteTemplate(final String prefix, final ModelSupplier<T> modelSupplier
            , final PebbleTemplate defaultTemplate, final Supplier<T> argumentsSupplier) {
        return (project, environment, args) -> {
            final var arguments = parseCommandLineArguments(args, argumentsSupplier);
            final var model = modelSupplier.toTemplateModel(project, environment, arguments);
            final var template = isNullOrEmpty(arguments.templateFile) ? PEBBLE.getTemplate(arguments.templateFile) : defaultTemplate;
            writeTemplateToFile(template, model, findOutputFile(prefix, project, arguments.outputFile));
            return 0;
        };
    }

    public static Path findOutputFile(final String prefix, final Project project, final Path outputFile) {
        final var filename = outputFile == null
                ? prefix + project.config.name + ".yaml"
                : outputFile.toString();
        return project.getBuildTarget().resolve(filename);
    }

    public static Map<String, String> loadProperties(final Path propertiesFile) throws IOException {
        final var map = new HashMap<String, String>();

        final var properties = new Properties();
        try (final var reader = newBufferedReader(propertiesFile)) {
            properties.load(reader);
        }
        properties.forEach((o, o2) -> map.put(o.toString(), o2.toString()));

        return map;
    }

    public static void writeTemplateToFile(final PebbleTemplate template, final Map<String, Object> vars, final Path file)
            throws IOException {
        Files.writeString(file, generateAppConfiguration(template, vars), CREATE, TRUNCATE_EXISTING);
    }

    public static String generateAppConfiguration(final PebbleTemplate template, final Map<String, Object> vars) throws IOException {
        try (final var writer = new StringWriter()) {
            template.evaluate(writer, vars);
            return writer.toString();
        }
    }

}
