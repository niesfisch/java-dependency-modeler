package de.marcelsauer.javamodel;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * @author msauer
 */
class CapturingVoidVisitorAdapter extends VoidVisitorAdapter<Object> {

    private final ModelCallback modelCallback;

    CapturingVoidVisitorAdapter(ModelCallback modelCallback) {
        this.modelCallback = modelCallback;
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration, Object o) {
        super.visit(enumDeclaration, o);
        this.modelCallback.foundClassOrInterface(enumDeclaration.getName());
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Object o) {
        super.visit(classOrInterfaceDeclaration, o);
        this.modelCallback.foundClassOrInterface(classOrInterfaceDeclaration.getName());

        List<AnnotationExpr> annotations = classOrInterfaceDeclaration.getAnnotations();
        if (annotations != null) {
            for (AnnotationExpr annotationExpr : annotations) {
                this.modelCallback.foundAnnotation(annotationExpr.getName().getName());
            }
        }

        List<ClassOrInterfaceType> implementedInterfaces = classOrInterfaceDeclaration.getImplements();
        if (implementedInterfaces != null) {
            for (ClassOrInterfaceType decl : implementedInterfaces) {
                this.modelCallback.foundImplementedInterface(decl.toString());
            }
        }
    }

    @Override
    public void visit(ImportDeclaration importDeclaration, Object arg) {
        super.visit(importDeclaration, arg);
        // a bit ugly using toString() here, find a better way
        String strippedImport = importDeclaration.toString().replaceFirst("import ", "");
        String plainImport = strippedImport.replace(";", "").replaceAll("\\s", "");
        this.modelCallback.foundImport(plainImport);
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
        this.modelCallback.foundFieldType(n.getType());
        List<AnnotationExpr> annotations = n.getAnnotations();
        if (annotations != null) {
            for (AnnotationExpr annotationExpr : annotations) {
                this.modelCallback.foundAnnotation(annotationExpr.getName().getName());
            }
        }

    }

    @Override
    public void visit(PackageDeclaration packageDeclaration, Object arg) {
        super.visit(packageDeclaration, arg);
        String simplePackageName = packageDeclaration.getName().toString().replace("package ", "").replace(";", "");
        this.modelCallback.foundPackage(simplePackageName);
    }
}
