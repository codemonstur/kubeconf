import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static kubeconf.core.Functions.*;

public enum TestSecrets {;

    public static void main(final String... args) throws IOException {
        final Map<String, Object> vars = ofEntries(
            entry("SECRETS_NAME", "the-name-of-it"),
            entry("NAMESPACE", "some-namespace"),
            entry("SECRETS", ofEntries(
                    entry("TEST", "data")))
        );

        final String output = generateAppConfiguration(newTemplate("kube-secrets"), vars);
        System.out.println(output);
    }

}
