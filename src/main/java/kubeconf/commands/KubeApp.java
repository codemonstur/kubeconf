package kubeconf.commands;

import bobthebuildtool.pojos.buildfile.Project;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import kubeconf.core.MinimalArguments;

import java.util.HashMap;
import java.util.Map;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static kubeconf.core.Functions.*;

public enum KubeApp {;

    private static final String
        NAME = "kube-app",
        DESCRIPTION = "Generates a kubernetes app yaml";

    public static void installCommandKubeApp(final Project project) {
        project.addCommand(NAME, DESCRIPTION, newWriteTemplate(
            NAME+"-", KubeApp::toModel, newTemplate(NAME), Arguments::new));
    }

    @CliCommand(name = NAME, description = DESCRIPTION)
    private static class Arguments extends MinimalArguments {
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

    private static Map<String, Object> toModel(final Project project, final Map<String, String> environment, final Arguments arguments) {
        final var model = new HashMap<String, Object>();

        model.put("SERVICE_PORT", "8080");

        if (arguments.loadEnv) model.putAll(environment);
        final String name = isNullOrEmpty(arguments.name) ? project.config.name : arguments.name;
        model.computeIfAbsent("CERTIFICATE_NAME", key -> name + "-certificate");
        model.computeIfAbsent("NAMESPACE", key -> name);
        model.computeIfAbsent("SERVICE_NAME", key -> name + "service");
        model.computeIfAbsent("SECRETS_NAME_APP", key-> name + "-environment");

        if (!isNullOrEmpty(arguments.name)) model.put("SERVICE_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);
        if (!isNullOrEmpty(arguments.hostname)) model.put("HOSTNAME", arguments.hostname);
        if (!isNullOrEmpty(arguments.port)) model.put("SERVICE_PORT", arguments.port);
        if (!isNullOrEmpty(arguments.secretsApp)) model.put("SECRETS_NAME_APP", arguments.secretsApp);
        if (!isNullOrEmpty(arguments.dockerImage)) model.put("DOCKER_IMAGE", arguments.dockerImage);
        if (!isNullOrEmpty(arguments.secretsDocker)) model.put("SECRETS_NAME_DOCKER", arguments.secretsDocker);

        return model;
    }

}
