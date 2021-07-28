package kubeconf.core;

import bobthebuildtool.pojos.buildfile.Project;

import java.util.Map;

public interface ModelSupplier<T extends MinimalArguments> {
    Map<String, Object> toTemplateModel(Project project, Map<String, String> environment, T args) throws Exception;
}
