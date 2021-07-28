import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static kubeconf.BobPlugin.*;

public class TestSecrets {

    public static void main(final String... args) throws IOException {
        final Map<String, Object> vars = ofEntries(
            entry("SECRETS_NAME", "the-name-of-it"),
            entry("NAMESPACE", "siptrunk"),
            entry("SECRETS", ofEntries(
                    entry("TEST", "data")))
        );

        final String output = generateAppConfiguration(TEMPLATE_KUBE_SECRETS, vars);
        System.out.println(output);
    }

}
