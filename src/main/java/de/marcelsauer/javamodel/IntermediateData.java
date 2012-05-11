package de.marcelsauer.javamodel;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author msauer
 */
class IntermediateData {

    String packageName;
    File file;
    String classOrInterface;

    Set<String> importLines = new HashSet<String>();
    Set<String> usesTypes = new HashSet<String>();
    Set<String> implementedInterfaces = new HashSet<String>();
    Set<String> annotations = new HashSet<String>();

    void addImport(String name) {
        this.importLines.add(name);
    }

    void usesType(String type) {
        this.usesTypes.add(type);
    }

    void setClassOrInterface(String classOrInterface) {
        this.classOrInterface = classOrInterface;
    }

    void addAnnotation(String annotation) {
        this.annotations.add(annotation);
    }

    void addImplementedInterface(String interfaceName) {
        this.implementedInterfaces.add(interfaceName);
    }

    @Override
    public String toString() {
        return "IntermediateData{" +
                "packageName='" + packageName + '\'' +
                ", file=" + file +
                ", classOrInterface='" + classOrInterface + '\'' +
                ", importLines=" + importLines +
                ", usesTypes=" + usesTypes +
                ", implementedInterfaces=" + implementedInterfaces +
                ", annotations=" + annotations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntermediateData that = (IntermediateData) o;

        if (!file.equals(that.file)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
