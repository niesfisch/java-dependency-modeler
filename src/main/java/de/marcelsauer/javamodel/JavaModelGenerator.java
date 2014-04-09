package de.marcelsauer.javamodel;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author msauer
 */
public class JavaModelGenerator {

    public static final IOFileFilter JAVA_SUFFIX_FILE_FILTER = new SuffixFileFilter(".java");

    private final static Logger log = Logger.getLogger(JavaModelGenerator.class);

    private final File baseDir;
    private final Map<String, IntermediateData> tempModel = new HashMap<String, IntermediateData>();
    private final IOFileFilter dirFilter;
    private final IOFileFilter fileFilter;
    private final TypeFilter typeFilter;

    public JavaModelGenerator(File baseDir) {
        this(baseDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE, TypeFilter.ACCEPTING_TYPE_FILTER);
    }

    public JavaModelGenerator(File baseDir, IOFileFilter dirFilter, IOFileFilter fileFilter, TypeFilter typeFilter) {
        this.baseDir = baseDir;
        this.dirFilter = dirFilter;
        this.fileFilter = fileFilter;
        this.typeFilter = typeFilter;
    }

    public Model generate() {

        buildTempModelFromFiles();

        Map<String, JavaModel> sortedModel = buildRealModel();
        Model model = new Model(sortedModel);

        log.info(String.format("model created with %d entries", model.getJavaModels().size()));

        return model;

    }

    private Map<String, JavaModel> buildRealModel() {
        Map<String, JavaModel> sortedModel = new HashMap<String, JavaModel>();
        for (IntermediateData intermediate : this.tempModel.values()) {

            JavaModel model = createMetaDataFromTempData(intermediate);
            String fullQualifiedTypeProcessed = intermediate.packageName + "." + intermediate.classOrInterface;

            if (this.typeFilter.accept(fullQualifiedTypeProcessed)) {
                sortedModel.put(fullQualifiedTypeProcessed, model);
                fullQualifyTypesUsed(intermediate, model, fullQualifiedTypeProcessed);
                fullQualifyImplementedInterfaces(intermediate, model, fullQualifiedTypeProcessed);
                fullQualifyUsedAnnotations(intermediate, model, fullQualifiedTypeProcessed);
            } else {
                log.info(String.format("filter skipped type '%s'", fullQualifiedTypeProcessed));
            }
        }
        return sortedModel;
    }

    private void fullQualifyTypesUsed(IntermediateData intermediate, final JavaModel model, String fullQualifiedTypeProcessed) {
        fullQualifyCandidates(intermediate, fullQualifiedTypeProcessed, intermediate.usesTypes, new TypeCallback() {
            @Override
            public void foundFullQualifiedType(String type) {
                model.usesType(type);
            }
        });
    }

    private void fullQualifyImplementedInterfaces(IntermediateData intermediate, final JavaModel model, String fullQualifiedTypeProcessed) {
        fullQualifyCandidates(intermediate, fullQualifiedTypeProcessed, intermediate.implementedInterfaces, new TypeCallback() {
            @Override
            public void foundFullQualifiedType(String type) {
                model.addImplementedInterface(type);
            }
        });
    }

    private void fullQualifyUsedAnnotations(IntermediateData intermediate, final JavaModel model, String fullQualifiedTypeProcessed) {
        fullQualifyCandidates(intermediate, fullQualifiedTypeProcessed, intermediate.annotations, new TypeCallback() {
            @Override
            public void foundFullQualifiedType(String type) {
                model.usesAnnotation(type);
            }
        });
    }

    private void fullQualifyCandidates(IntermediateData intermediate, String fullQualifiedTypeProcessed, Set<String> candidatesToQualify, TypeCallback callback) {
        for (String candidate : candidatesToQualify) {
            String fullQualifiedCandidate = findFullQualifiedType(candidate, intermediate);
            if (fullQualifiedCandidate == null) {
                log.debug(String.format("'%s' in type '%s' ignored because no meta data available. file '%s' (classpath libs are currently not supported)!", candidate, fullQualifiedTypeProcessed, intermediate.file));
            } else {
                callback.foundFullQualifiedType(fullQualifiedCandidate);
            }
        }
    }

    private interface TypeCallback {
        void foundFullQualifiedType(String type);

    }

    private String findFullQualifiedType(String unqualifiedType, IntermediateData intermediate) {

        String foundFullQualifiedType = null;
        boolean foundMemberTypeInImportedPackages = false;

        // check if direct import, e.g. de.marcelsauer.test_files.package_b.B
        for (String importLine : intermediate.importLines) {
            boolean directlyImported = importLine.endsWith("." + unqualifiedType);
            if (directlyImported/* && this.tempModel.containsKey(importLine)*/) {
                foundFullQualifiedType = importLine;
                foundMemberTypeInImportedPackages = true;
                break;
            }
        }

        // resolve * import, e.g. de.marcelsauer.test_files.package_b.*
        if (!foundMemberTypeInImportedPackages) {

            // e.g. de.marcelsauer.test_files.package_b.B
            Set<String> possibleResolveableFullQualifiedTypes = new HashSet<String>();
            for (String importLine : intermediate.importLines) {
                // e.g. de.marcelsauer.test_files.package_b.
                String importLineWithoutWildcard = importLine.substring(0, importLine.length() - 1);
                // e.g. de.marcelsauer.test_files.package_b.B
                String fullQualifiedCandidateType = importLineWithoutWildcard + unqualifiedType;
                if (this.tempModel.containsKey(fullQualifiedCandidateType)) {
                    possibleResolveableFullQualifiedTypes.add(fullQualifiedCandidateType);
                }
            }

            if (possibleResolveableFullQualifiedTypes.size() > 1) {
                throw new IllegalStateException(unqualifiedType + " not resolveable: choices: " + possibleResolveableFullQualifiedTypes);
            } else if (possibleResolveableFullQualifiedTypes.size() == 1) {
                String fullQualifiedTypeResolved = possibleResolveableFullQualifiedTypes.iterator().next();
                foundFullQualifiedType = fullQualifiedTypeResolved;
                foundMemberTypeInImportedPackages = true;
            }
        }

        if (!foundMemberTypeInImportedPackages) {
            String fullQualTypeInSamePackage = intermediate.packageName + "." + unqualifiedType;
            if (this.tempModel.containsKey(fullQualTypeInSamePackage)) {
                foundFullQualifiedType = fullQualTypeInSamePackage;
                foundMemberTypeInImportedPackages = true;
            }
        }

        return foundFullQualifiedType;
    }

    private JavaModel createMetaDataFromTempData(IntermediateData intermediate) {
        JavaModel model = new JavaModel();
        model.setClassOrInterface(intermediate.classOrInterface);
        model.setPackageName(intermediate.packageName);
        model.setFile(intermediate.file);
        model.setImports(intermediate.importLines);
        return model;
    }

    private void buildTempModelFromFiles() {
        Collection<File> files = FileUtils.listFiles(this.baseDir, this.fileFilter, this.dirFilter);
        log.info(String.format("found %d files to analyse", files.size()));
        for (File file : files) {
            log.debug(String.format("processing file: '%s'", file));
            buildTempModelFromFile(file);
        }
    }

    private void buildTempModelFromFile(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            CompilationUnit cu = JavaParser.parse(in);

            final IntermediateData metaData = new IntermediateData();
            metaData.file = file;

            // this is where the magic happens
            CapturingVoidVisitorAdapter capturingVoidVisitorAdapter = new CapturingVoidVisitorAdapter(new DefaultModelCallback(metaData));
            capturingVoidVisitorAdapter.visit(cu, null);

            String fullQualifiedType = metaData.packageName + "." + metaData.classOrInterface;
            this.tempModel.put(fullQualifiedType, metaData);

        } catch (ParseException e) {
            error(file, e);
        } catch (FileNotFoundException e) {
            error(file, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private void error(File file, Exception e) {
        throw new RuntimeException("could not process file + " + file, e);
    }
}
