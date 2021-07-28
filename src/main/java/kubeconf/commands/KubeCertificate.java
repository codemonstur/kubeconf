package kubeconf.commands;

import bobthebuildtool.pojos.buildfile.Project;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import kubeconf.core.MinimalArguments;

import java.util.HashMap;
import java.util.Map;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static kubeconf.core.Functions.*;

public enum KubeCertificate {;

    private static final String
        NAME = "kube-cert",
        DESCRIPTION = "Generates a kubernetes certificate yaml";

    public static void installCommandKubeCertificate(final Project project) {
        project.addCommand(NAME, DESCRIPTION, newWriteTemplate(
            NAME+"-", KubeCertificate::toModel, newTemplate(NAME), Arguments::new));
    }

    @CliCommand(name = NAME, description = DESCRIPTION)
    private static class Arguments extends MinimalArguments {
        @CliOption(name = 'h', longName = "hostname")
        private String hostname;
    }

    private static Map<String, Object> toModel(final Project project, final Map<String, String> environment, final Arguments arguments) {
        final var model = new HashMap<String, Object>();

        model.put("CERTIFICATE_NAME", project.config.name + "-certificate");

        if (arguments.loadEnv) model.putAll(environment);

        if (!isNullOrEmpty(arguments.name)) model.put("CERTIFICATE_NAME", arguments.name);
        if (!isNullOrEmpty(arguments.namespace)) model.put("NAMESPACE", arguments.namespace);
        if (!isNullOrEmpty(arguments.hostname)) model.put("HOSTNAME", arguments.hostname);

        return model;
    }

}
