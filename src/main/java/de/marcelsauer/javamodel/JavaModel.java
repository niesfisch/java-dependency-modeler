package de.marcelsauer.javamodel;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author msauer
 */
public class JavaModel {

    private String packageName;
    private File file;
    private String classOrInterface;

    private Set<String> imports = new HashSet<String>();
    private Set<String> usesTypes = new HashSet<String>();
    private Set<String> annotations = new HashSet<String>();
    private Set<String> implementedInterfaces = new HashSet<String>();

    public void setClassOrInterface(String classOrInterface) {
        this.classOrInterface = classOrInterface;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public File getFile() {
        return this.file;
    }

    public Set<String> getImports() {
        return this.imports;
    }

    public void usesType(String fullQualifiedType) {
        this.usesTypes.add(fullQualifiedType);
    }

    public Set<String> getUsesTypes() {
        return this.usesTypes;
    }

    public String getClassOrInterface() {
        return this.classOrInterface;
    }

    public void classOrInterface(String classOrInterface) {
        this.classOrInterface = classOrInterface;
    }

    public String getFullyQualifiedType() {
        return this.getPackageName() + "." + this.getClassOrInterface();
    }

    public Set<String> getAnnotations() {
        return this.annotations;
    }

    public void addImplementedInterface(String fullQualifiedInterface) {
        this.implementedInterfaces.add(fullQualifiedInterface);
    }

    public void usesAnnotation(String fullQualifiedAnnotation) {
        this.annotations.add(fullQualifiedAnnotation);
    }

    public Set<String> getImplementedInterfaces() {
        return this.implementedInterfaces;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("JavaModel");
        sb.append("{ classOrInterface='").append(classOrInterface).append('\'');
        sb.append(", packageName='").append(packageName).append('\'');
        sb.append(", file=").append(file);
        sb.append(", imports=").append(imports);
        sb.append(", usesTypes=").append(usesTypes);
        sb.append(", annotations=").append(annotations);
        sb.append(", implementedInterfaces=").append(implementedInterfaces);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaModel model = (JavaModel) o;

        if (this.file != null ? !this.file.equals(model.file) : model.file != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return this.file != null ? this.file.hashCode() : 0;
    }
}
