package kubeconf.commands;

import bobthebuildtool.pojos.buildfile.Project;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import kubeconf.core.MinimalArguments;

import java.util.HashMap;
import java.util.Map;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static kubeconf.core.Functions.*;

public enum KubeRedis {;

    private static final String
        NAME = "kube-redis",
        DESCRIPTION_REDIS = "Generate a redis service instance yaml";

    public static void installCommandKubeRedis(final Project project) {
        project.addCommand(NAME, DESCRIPTION_REDIS, newWriteTemplate(
            NAME+"-", KubeRedis::toModel, newTemplate(NAME), Arguments::new));
    }
    @CliCommand(name = NAME, description = DESCRIPTION_REDIS)
    private static class Arguments extends MinimalArguments {
        @CliOption(name = 'p', longName = "port")
        private String port;
    }

    private static Map<String, Object> toModel(final Project project, final Map<String, String> environment, final Arguments arguments) {
        final var model = new HashMap<String, Object>();

        model.put("SERVICE_PORT", "6379");
        model.put("SERVICE_NAME", project.config.name + "-redis");
        model.put("NAMESPACE", project.config.name);

        if (arguments.loadEnv) model.putAll(environment);

        if (!isNullOrEmpty(arguments.name)) model.put("SERVICE_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.port)) model.put("SERVICE_PORT", arguments.port);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);

        return model;
    }

}
