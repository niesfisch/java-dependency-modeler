package de.marcelsauer.javamodel;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * @author msauer
 */
public class JavaModelGeneratorIntegrationTest {

    private Model model;
    private JavaModel oneJavaModel;
    private Set<String> imports;
    private Set<String> usesTypes;
    private Set<String> implementedInterfaces;
    private Set<String> annotations;
    private String packageName;

    @Test
    public void thatModelCanBeBuilt() throws URISyntaxException {
        // given
        File root = getRootOfFilesToBeProcessed();

        assertTrue(root.exists());
        assertTrue(root.isDirectory());

        JavaModelGenerator modelGenerator = new JavaModelGenerator(root, TrueFileFilter.INSTANCE, JavaModelGenerator.JAVA_SUFFIX_FILE_FILTER, new TypeFilter() {

            @Override
            public boolean accept(String fullQualifiedType) {
                return fullQualifiedType.startsWith("de.marcelsauer");
            }
        });

        // when
        this.model = modelGenerator.generate();

        // then
        assertEquals(9, this.model.size());

        assertFilesCorrectlyProcessed();
    }

    private void assertFilesCorrectlyProcessed() {
        boolean foundA = false;
        boolean foundB = false;
        boolean foundC = false;
        boolean foundDomainService1Impl = false;
        boolean foundDomainService1 = false;
        boolean foundDomainService2 = false;
        boolean foundBusinessService = false;
        boolean foundAInterface = false;
        boolean foundSomeOtherInterface = false;

        for (String fullQualifiedType : this.model.fullQualifiedTypes()) {
            builtState(fullQualifiedType);

            if (fullQualifiedType.endsWith("A")) {
                foundA = true;
                assertA();
            } else if (fullQualifiedType.endsWith("B")) {
                foundB = true;
                assertB();
            } else if (fullQualifiedType.endsWith("C")) {
                foundC = true;
                assertC();
            } else if (fullQualifiedType.endsWith("DomainService1")) {
                foundDomainService1 = true;
                assertDomainService1();
            } else if (fullQualifiedType.endsWith("DomainService1Impl")) {
                foundDomainService1Impl = true;
                assertDomainService1Impl();
            } else if (fullQualifiedType.endsWith("DomainService2")) {
                foundDomainService2 = true;
                assertDomainService2();
            } else if (fullQualifiedType.endsWith("BusinessService")) {
                foundBusinessService = true;
                assertBusinessService();
            } else if (fullQualifiedType.endsWith("AInterface")) {
                foundAInterface = true;
            } else if (fullQualifiedType.endsWith("SomeOtherInterface")) {
                foundSomeOtherInterface = true;
            }
        }

        assertTrue(foundA);
        assertTrue(foundB);
        assertTrue(foundC);
        assertTrue(foundDomainService1);
        assertTrue(foundDomainService1Impl);
        assertTrue(foundDomainService2);
        assertTrue(foundBusinessService);
        assertTrue(foundAInterface);
        assertTrue(foundSomeOtherInterface);
    }

    private File getRootOfFilesToBeProcessed() {
        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource(".").getPath();
        return new File(path.substring(0, path.indexOf("target")) + "src/test/files");
    }

    private void builtState(String fullQualifiedType) {
        this.oneJavaModel = this.model.getModelForType(fullQualifiedType);
        this.imports = this.oneJavaModel.getImports();
        this.usesTypes = this.oneJavaModel.getUsesTypes();
        this.implementedInterfaces = this.oneJavaModel.getImplementedInterfaces();
        this.annotations = this.oneJavaModel.getAnnotations();
        this.packageName = this.oneJavaModel.getPackageName();
    }

    private void assertBusinessService() {
        String expectedPackageName = "de.marcelsauer.test_files";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".BusinessService", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("BusinessService", this.oneJavaModel.getClassOrInterface());

        assertEquals(1, this.imports.size());

        assertEquals(2, this.usesTypes.size());

        assertOneServiceAnnotationUsed();

        boolean found1 = false;
        boolean found2 = false;
        for (String usedType : this.usesTypes) {
            JavaModel type = this.model.getModelForType(usedType);
            if ("DomainService1".equals(type.getClassOrInterface())) {
                found1 = true;
                assertEquals("de.marcelsauer.test_files.DomainService1", type.getFullyQualifiedType());
            }
            if ("DomainService2".equals(type.getClassOrInterface())) {
                found2 = true;
                assertEquals("de.marcelsauer.test_files.DomainService2", type.getFullyQualifiedType());
            }
        }
        assertTrue(found1 && found2);
    }

    private void assertDomainService2() {
        String expectedPackageName = "de.marcelsauer.test_files";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".DomainService2", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("DomainService2", this.oneJavaModel.getClassOrInterface());

        assertEquals(1, this.imports.size());
        assertEquals(0, this.implementedInterfaces.size());
        assertOneServiceAnnotationUsed();
    }

    private void assertDomainService1() {
        String expectedPackageName = "de.marcelsauer.test_files";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".DomainService1", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("DomainService1", this.oneJavaModel.getClassOrInterface());

        assertEquals(0, this.implementedInterfaces.size());
        assertEquals(0, this.imports.size());
    }

    private void assertDomainService1Impl() {
        String expectedPackageName = "de.marcelsauer.test_files";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".DomainService1Impl", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("DomainService1Impl", this.oneJavaModel.getClassOrInterface());

        assertEquals(1, this.imports.size());
        assertEquals(1, this.implementedInterfaces.size());
        assertOneServiceAnnotationUsed();
    }

    private void assertC() {
        String expectedPackageName = "de.marcelsauer.test_files";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".C", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("C", this.oneJavaModel.getClassOrInterface());

        assertEquals(1, this.imports.size());
        assertEquals(0, this.implementedInterfaces.size());
        assertEquals(1, this.usesTypes.size());

        boolean foundA = false;
        for (String usedType : this.usesTypes) {
            JavaModel type = this.model.getModelForType(usedType);
            if ("A".equals(type.getClassOrInterface())) {
                foundA = true;
                assertEquals("de.marcelsauer.test_files.package_a.A", type.getFullyQualifiedType());
            }
        }
        assertTrue(foundA);
    }

    private void assertB() {
        String expectedPackageName = "de.marcelsauer.test_files.package_b";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".B", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("B", this.oneJavaModel.getClassOrInterface());

        assertEquals(0, this.imports.size());
    }

    private void assertA() {
        String expectedPackageName = "de.marcelsauer.test_files.package_a";
        assertEquals(expectedPackageName, this.packageName);
        assertEquals(expectedPackageName + ".A", this.oneJavaModel.getFullyQualifiedType());
        assertEquals("A", this.oneJavaModel.getClassOrInterface());

        assertEquals(4, this.imports.size());
        assertThat(this.imports, hasItem("de.marcelsauer.test_files.package_b.B"));
        assertThat(this.imports, hasItem("org.springframework.stereotype.Service"));

        assertEquals(2, this.usesTypes.size());

        boolean foundC = false;
        boolean foundB = false;
        for (String usedType : this.usesTypes) {
            JavaModel type = this.model.getModelForType(usedType);
            if ("B".equals(type.getClassOrInterface())) {
                foundB = true;
                assertEquals("de.marcelsauer.test_files.package_b.B", type.getFullyQualifiedType());
            }
            if ("C".equals(type.getClassOrInterface())) {
                foundC = true;
                assertEquals("de.marcelsauer.test_files.C", type.getFullyQualifiedType());
            }
        }

        assertOneServiceAnnotationUsed();

        assertTrue(foundC && foundB);
        assertEquals(2, this.implementedInterfaces.size());
        assertEquals("de.marcelsauer.test_files.package_a.AInterface", this.implementedInterfaces.iterator().next());
    }

    private void assertOneServiceAnnotationUsed() {
        assertEquals(1, this.annotations.size());
        assertEquals("org.springframework.stereotype.Service", this.annotations.iterator().next());
    }
}
