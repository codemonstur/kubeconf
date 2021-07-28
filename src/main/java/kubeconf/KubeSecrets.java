package kubeconf;

import bobthebuildtool.pojos.buildfile.Project;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.errors.InvalidCommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static jcli.CliParser.parseCommandLineArguments;
import static kubeconf.BobPlugin.*;

public enum KubeSecrets {;

    @CliCommand(name = "kube-secrets", description = DESCRIPTION_SECRETS)
    private static class Arguments {
        @CliOption(name = 'o', longName = "outputFile")
        public Path outputFile;
        @CliOption(name = 's', longName = "namespace")
        public String namespace;
        @CliOption(name = 'n', longName = "name")
        public String name;
        @CliOption(name = 'e', longName = "env")
        public boolean loadEnv;
        @CliOption(name = 'p', longName = "properties")
        public Path properties;
    }

    public static int run(final Project project, final Map<String, String> environment, final String[] args)
            throws InvalidCommandLine, IOException {
        final var arguments = parseCommandLineArguments(args, Arguments::new);

        final var model = new HashMap<String, Object>();
        if (arguments.loadEnv) model.putAll(environment);

        final String name = isNullOrEmpty(arguments.name) ? project.config.name : arguments.name;
        model.put("NAMESPACE", name);
        model.put("SECRETS_NAME", name + "-environment");

        if (!isNullOrEmpty(arguments.name)) model.put("SECRETS_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);
        if (arguments.properties != null) model.put("SECRETS", loadProperties(arguments.properties));

        final var file = findOutputFile(project, arguments);
        final var text = generateAppConfiguration(TEMPLATE_KUBE_APP, model);
        Files.writeString(file, text, CREATE, TRUNCATE_EXISTING);

        return 0;
    }

    private static Map<String, String> loadProperties(final Path properties) throws IOException {
        final var map = new HashMap<String, String>();
        try (final var reader = Files.newBufferedReader(properties)) {
            final var props = new Properties();
            props.load(reader);
            props.forEach((o, o2) -> map.put(o.toString(), o2.toString()));
        }
        return map;
    }

    private static Path findOutputFile(final Project project, final Arguments arguments) {
        final var filename = arguments.outputFile == null
                ? "kube-secrets-" + project.config.name + ".yaml"
                : arguments.outputFile.toString();
        return project.getBuildTarget().resolve(filename);
    }

}
