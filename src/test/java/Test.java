import java.io.IOException;
import java.util.Map;

import static kubeconf.BobPlugin.TEMPLATE_KUBE_APP;
import static kubeconf.BobPlugin.generateAppConfiguration;

public class Test {

    public static void main(final String... args) throws IOException {
        final Map<String, Object> vars = Map.ofEntries(
            Map.entry("CERTNAME", "siptrunk-certificate"),
            Map.entry("DOCKER_IMAGE", "dockerregistry.xelion.com/siptrunk:latest"),
            Map.entry("HOSTNAME", "siptrunk-defaults.xelion.com"),
            Map.entry("NAMESPACE", "siptrunk"),
            Map.entry("SERVICE_NAME", "siptrunkservice"),
            Map.entry("SERVICE_PORT", "8080"),
            Map.entry("SECRETS_APP", "siptrunk"),
            Map.entry("SECRETS_DOCKER", "dockerregistry-xelion")
        );
        final String output = generateAppConfiguration(TEMPLATE_KUBE_APP, vars);
        final String expected = new String(Test.class.getResourceAsStream("/siptrunk.yaml").readAllBytes());

        final boolean result = output.equals(expected);
        if (!result) {
            System.out.println("Invalid output\n");
            System.out.println(output);
        }
    }

}
