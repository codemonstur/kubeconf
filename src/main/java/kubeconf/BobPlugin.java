package kubeconf;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.pojos.error.VersionTooOld;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static bobthebuildtool.services.Update.requireBobVersion;

public enum BobPlugin {;

    private static final PebbleEngine ENGINE = new PebbleEngine.Builder()
            .loader(new ClasspathLoader()).newLineTrimming(false).build();

    public static final String
        DESCRIPTION_APP = "Generates a standard kubernetes app yaml",
        DESCRIPTION_CERT = "Generates a standard kubernetes certificate yaml",
        DESCRIPTION_SECRETS = "Generates a standard kubernetes secrets yaml",
        DESCRIPTION_REDIS = "Generate a redis service instance yaml";

    public static final PebbleTemplate
        TEMPLATE_KUBE_APP = ENGINE.getTemplate("templates/kube-app.yaml"),
        TEMPLATE_KUBE_CERT = ENGINE.getTemplate("templates/kube-cert.yaml"),
        TEMPLATE_KUBE_SECRETS = ENGINE.getTemplate("templates/kube-secrets.yaml"),
        TEMPLATE_KUBE_REDIS = ENGINE.getTemplate("templates/kube-redis.yaml");

    public static void installPlugin(final Project project) throws VersionTooOld {
        requireBobVersion("5");
        project.addCommand("kube-app", DESCRIPTION_APP, KubeApp::run);
        project.addCommand("kube-cert", DESCRIPTION_CERT, KubeCertificate::run);
        project.addCommand("kube-secrets", DESCRIPTION_SECRETS, KubeSecrets::run);
        project.addCommand("kube-redis", DESCRIPTION_REDIS, KubeRedis::run);
    }

    public static String generateAppConfiguration(final PebbleTemplate template, final Map<String, Object> vars) throws IOException {
        try (final var writer = new StringWriter()) {
            template.evaluate(writer, vars);
            return writer.toString();
        }
    }

}
