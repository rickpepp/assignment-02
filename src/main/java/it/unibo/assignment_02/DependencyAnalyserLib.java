package it.unibo.assignment_02;

import io.vertx.core.Promise;

public interface DependencyAnalyserLib {
    Promise<ClassDepsReport> getClassDependencies(String filePath);
    Promise<PackageDepsReport> getPackageDependencies();
    Promise<ProjectDepsReport> getProjectDependencies();
}
