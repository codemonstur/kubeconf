package kubeconf.core;

import jcli.annotations.CliOption;

import java.nio.file.Path;

public abstract class MinimalArguments {
    @CliOption(name = 'o', longName = "outputFile")
    public Path outputFile;
    @CliOption(name = 'e', longName = "env")
    public boolean loadEnv;
    @CliOption(name = 's', longName = "namespace")
    public String namespace;
    @CliOption(name = 'n', longName = "name")
    public String name;

    @CliOption(name = 't', longName = "template")
    public String templateFile;
}