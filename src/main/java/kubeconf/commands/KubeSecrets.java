package kubeconf.commands;

import bobthebuildtool.pojos.buildfile.Project;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import kubeconf.core.MinimalArguments;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static kubeconf.core.Functions.*;

public enum KubeSecrets {;

    private static final String
        NAME = "kube-secrets",
        DESCRIPTION = "Generates a kubernetes secrets yaml";

    public static void installCommandKubeSecrets(final Project project) {
        project.addCommand(NAME, DESCRIPTION, newWriteTemplate(
            NAME+"-", KubeSecrets::toModel, newTemplate(NAME), Arguments::new));
    }

    @CliCommand(name = NAME, description = DESCRIPTION)
    private static class Arguments extends MinimalArguments {
        @CliOption(name = 'p', longName = "properties")
        private Path properties;
    }

    private static Map<String, Object> toModel(final Project project, final Map<String, String> environment, final Arguments arguments)
            throws IOException {
        final var model = new HashMap<String, Object>();

        if (arguments.loadEnv) model.putAll(environment);

        final String name = isNullOrEmpty(arguments.name) ? project.config.name : arguments.name;
        model.computeIfAbsent("NAMESPACE", key -> name);
        model.computeIfAbsent("SECRETS_NAME", key -> name + "-environment");

        if (!isNullOrEmpty(arguments.name)) model.put("SECRETS_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);
        if (arguments.properties != null) model.put("SECRETS", loadProperties(arguments.properties));

        return model;
    }

}
