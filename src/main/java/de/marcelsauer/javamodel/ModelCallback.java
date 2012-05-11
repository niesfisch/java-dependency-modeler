package de.marcelsauer.javamodel;

import japa.parser.ast.type.Type;

/**
 * @author msauer
 */
interface ModelCallback {

    void foundPackage(String packageName);

    void foundImport(String name);

    void foundFieldType(Type type);

    void foundClassOrInterface(String string);

    void foundAnnotation(String annotation);

    void foundImplementedInterface(String interfaceName);
}
