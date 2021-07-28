import java.io.IOException;
import java.util.Map;

import static kubeconf.core.Functions.*;

public enum TestApp {;

    public static void main(final String... args) throws IOException {
        final Map<String, Object> vars = Map.ofEntries(
            Map.entry("CERTNAME", ""),
            Map.entry("DOCKER_IMAGE", ""),
            Map.entry("HOSTNAME", ""),
            Map.entry("NAMESPACE", ""),
            Map.entry("SERVICE_NAME", ""),
            Map.entry("SERVICE_PORT", ""),
            Map.entry("SECRETS_APP", ""),
            Map.entry("SECRETS_DOCKER", "")
        );
        final String output = generateAppConfiguration(newTemplate("kube-app"), vars);
        final String expected = new String(TestApp.class.getResourceAsStream("/template.yaml").readAllBytes());

        final boolean result = output.equals(expected);
        if (!result) {
            System.out.println("Invalid output\n");
            System.out.println(output);
        }
    }

}
