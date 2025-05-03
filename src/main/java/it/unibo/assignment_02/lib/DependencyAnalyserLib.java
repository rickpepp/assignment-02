package it.unibo.assignment_02.lib;

import io.vertx.core.Promise;

public interface DependencyAnalyserLib {
    Promise<ClassDepsReport> getClassDependencies(String filePath);
    Promise<PackageDepsReport> getPackageDependencies(String packagePath);
    Promise<ProjectDepsReport> getProjectDependencies(String projectPath);
}
