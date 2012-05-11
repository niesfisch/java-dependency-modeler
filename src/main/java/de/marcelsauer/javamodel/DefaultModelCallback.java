package de.marcelsauer.javamodel;

import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.Type;
import org.apache.log4j.Logger;

/**
 * @author msauer
 */
class DefaultModelCallback implements ModelCallback {

    private final static Logger log = Logger.getLogger(DefaultModelCallback.class);
    private final IntermediateData metaData;

    public DefaultModelCallback(IntermediateData metaData) {
        this.metaData = metaData;
    }

    @Override
    public void foundPackage(String packageName) {
        this.metaData.packageName = packageName;
    }

    @Override
    public void foundImport(String name) {
        this.metaData.addImport(name);
    }

    @Override
    public void foundFieldType(Type type) {
        // TODO handle promitive types!
        if (!(type instanceof PrimitiveType)) {
            this.metaData.usesType(type.toString());
        } else {
            log.debug(String.format("ignoring primitive type '%s'", type));
        }
    }

    @Override
    public void foundClassOrInterface(String classOrInterface) {
        this.metaData.setClassOrInterface(classOrInterface);
    }

    @Override
    public void foundAnnotation(String annotation) {
        this.metaData.addAnnotation(annotation);
    }

    @Override
    public void foundImplementedInterface(String interfaceName) {
        this.metaData.addImplementedInterface(interfaceName);
    }
}
