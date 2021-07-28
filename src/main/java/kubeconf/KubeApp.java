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

public enum KubeApp {;

    @CliCommand(name = "kube-app", description = DESCRIPTION_APP)
    public static class Arguments {
        @CliOption(name = 'e', longName = "env")
        private boolean loadEnv;
        @CliOption(name = 'o', longName = "outputFile")
        private Path outputFile;

        @CliOption(name = 's', longName = "namespace")
        private String namespace;
        @CliOption(name = 'n', longName = "name")
        private String name;
        @CliOption(name = 'h', longName = "hostname")
        private String hostname;
        @CliOption(name = 'p', longName = "port")
        private String port;
        @CliOption(name = 'd', longName = "docker-image")
        private String dockerImage;
        @CliOption(longName = "secrets-docker")
        private String secretsDocker;
        @CliOption(longName = "secrets-app")
        private String secretsApp;
    }
    public static int run(final Project project, final Map<String, String> environment, final String[] args)
            throws InvalidCommandLine, IOException {
        final var arguments = parseCommandLineArguments(args, Arguments::new);

        final var model = new HashMap<String, Object>();
        model.put("SERVICE_PORT", "8080");

        if (arguments.loadEnv) model.putAll(environment);
        final String name = isNullOrEmpty(arguments.name) ? project.config.name : arguments.name;
        model.computeIfAbsent("CERTIFICATE_NAME", s -> name + "-certificate");
        model.computeIfAbsent("NAMESPACE", s -> name);
        model.computeIfAbsent("SERVICE_NAME", s -> name + "service");
        model.computeIfAbsent("SECRETS_NAME_APP", s-> name + "-environment");

        if (!isNullOrEmpty(arguments.name)) model.put("SERVICE_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);
        if (!isNullOrEmpty(arguments.hostname)) model.put("HOSTNAME", arguments.hostname);
        if (!isNullOrEmpty(arguments.port)) model.put("SERVICE_PORT", arguments.port);
        if (!isNullOrEmpty(arguments.secretsApp)) model.put("SECRETS_NAME_APP", arguments.secretsApp);
        if (!isNullOrEmpty(arguments.dockerImage)) model.put("DOCKER_IMAGE", arguments.dockerImage);
        if (!isNullOrEmpty(arguments.secretsDocker)) model.put("SECRETS_NAME_DOCKER", arguments.secretsDocker);

        final var file = findOutputFile(project, arguments);
        final var text = generateAppConfiguration(TEMPLATE_KUBE_APP, model);
        Files.writeString(file, text, CREATE, TRUNCATE_EXISTING);

        return 0;
    }

    private static Path findOutputFile(final Project project, final Arguments arguments) {
        final var filename = arguments.outputFile == null
                           ? "kube-app-" + project.config.name + ".yaml"
                           : arguments.outputFile.toString();
        return project.getBuildTarget().resolve(filename);
    }

}
