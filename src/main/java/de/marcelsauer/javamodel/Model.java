package de.marcelsauer.javamodel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author msauer
 */
public class Model {

    // e.g. de.marcelsauer.A -> model
    private final Map<String, JavaModel> fqTypeToJavaModel;

    public Model(Map<String, JavaModel> model) {
        this.fqTypeToJavaModel = model;
    }

    public int size() {
        return this.fqTypeToJavaModel.size();
    }

    public Set<String> fullQualifiedTypes() {
        return this.fqTypeToJavaModel.keySet();

    }

    public JavaModel getModelForType(String fullQualifiedType) {
        return this.fqTypeToJavaModel.get(fullQualifiedType);
    }

    public Collection<JavaModel> getJavaModels() {
        return fqTypeToJavaModel.values();
    }
}
