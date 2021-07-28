package kubeconf;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.pojos.error.VersionTooOld;

import static bobthebuildtool.services.Update.requireBobVersion;
import static kubeconf.commands.KubeApp.installCommandKubeApp;
import static kubeconf.commands.KubeCertificate.installCommandKubeCertificate;
import static kubeconf.commands.KubeRedis.installCommandKubeRedis;
import static kubeconf.commands.KubeSecrets.installCommandKubeSecrets;

public enum BobPlugin {;

    public static void installPlugin(final Project project) throws VersionTooOld {
        requireBobVersion("5");

        installCommandKubeApp(project);
        installCommandKubeCertificate(project);
        installCommandKubeRedis(project);
        installCommandKubeSecrets(project);
    }

}
