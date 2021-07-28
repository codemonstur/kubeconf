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

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static jcli.CliParser.parseCommandLineArguments;
import static kubeconf.BobPlugin.*;

public enum KubeCertificate {;

    @CliCommand(name = "kube-cert", description = DESCRIPTION_REDIS)
    public static class Arguments {
        @CliOption(name = 'e', longName = "env")
        private boolean loadEnv;
        @CliOption(name = 'o', longName = "outputFile")
        private Path outputFile;

        @CliOption(name = 'n', longName = "name")
        private String name;
        @CliOption(name = 's', longName = "namespace")
        private String namespace;
        @CliOption(name = 'h', longName = "hostname")
        private String hostname;
    }
    public static int run(final Project project, final Map<String, String> environment, final String[] args)
            throws InvalidCommandLine, IOException {
        final var arguments = parseCommandLineArguments(args, Arguments::new);

        final var model = new HashMap<String, Object>();
        model.put("CERTIFICATE_NAME", project.config.name + "-certificate");

        if (arguments.loadEnv) model.putAll(environment);

        if (!isNullOrEmpty(arguments.name)) model.put("CERTIFICATE_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);
        if (!isNullOrEmpty(arguments.hostname)) model.put("HOSTNAME", arguments.hostname);

        final var file = findOutputFile(project, arguments);
        final var text = generateAppConfiguration(TEMPLATE_KUBE_APP, model);
        Files.writeString(file, text, CREATE, TRUNCATE_EXISTING);

        return 0;
    }

    private static Path findOutputFile(final Project project, final Arguments arguments) {
        final var filename = arguments.outputFile == null
                ? "kube-cert-" + project.config.name + ".yaml"
                : arguments.outputFile.toString();
        return project.getBuildTarget().resolve(filename);
    }

}
