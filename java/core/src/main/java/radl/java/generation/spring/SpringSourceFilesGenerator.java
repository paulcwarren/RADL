/*
 * Copyright © EMC Corporation. All rights reserved.
 */
package radl.java.generation.spring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Document;

import radl.core.code.Code;
import radl.core.code.GeneratedSourceFile;
import radl.core.code.SourceFile;
import radl.core.generation.CodeGenerator;
import radl.core.generation.SourceFilesGenerator;
import radl.java.code.JavaCode;


/**
 * * Generates Java source files for the Spring framework from a RADL document.
 */
public class SpringSourceFilesGenerator implements SourceFilesGenerator {

  private final CodeGenerator codeGenerator;
  private final String generatedSourceSetDir;
  private final String mainSourceSetDir;

  public SpringSourceFilesGenerator(String packagePrefix, String generatedSourceSetDir, String mainSourceSetDir) {
    this(new SpringCodeGenerator(packagePrefix), generatedSourceSetDir, mainSourceSetDir);
  }

  SpringSourceFilesGenerator(CodeGenerator codeGenerator, String generatedSourceSetDir, String mainSourceSetDir) {
    this.codeGenerator = codeGenerator;
    this.generatedSourceSetDir = toDir(generatedSourceSetDir);
    this.mainSourceSetDir = toDir(mainSourceSetDir);
  }

  private String toDir(String path) {
    return path.endsWith(File.separator) ? path : path + File.separator;
  }

  @Override
  public Iterable<SourceFile> generateFrom(Document radl, File baseDir) {
    Collection<SourceFile> result = new ArrayList<SourceFile>();
    for (Code code : codeGenerator.generateFrom(radl)) {
      result.add(newSourceFileFor(baseDir, (JavaCode)code));
    }
    return result;
  }

  private SourceFile newSourceFileFor(File baseDir, JavaCode code) {
    String path = codeToPath(baseDir, code);
    return isGenerated(code) ? new GeneratedSourceFile(path, code) : new SourceFile(path, code);
  }

  private String codeToPath(File baseDir, JavaCode code) {
    try {
      return new File(baseDir, directoryFor(code) + File.separator + fileFor(code)).getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String directoryFor(JavaCode code) {
    String packageName = code.packageName();
    String result = packageName.isEmpty() ? "." : packageName.replaceAll("\\.", "\\" + File.separator);
    return getSourceSetDir(code) + result;
  }

  private String getSourceSetDir(JavaCode code) {
    return isGenerated(code) ? generatedSourceSetDir : mainSourceSetDir;
  }

  private boolean isGenerated(JavaCode code) {
    String type = code.typeName();
    return type.endsWith("Controller") || "Api".equals(type) || "Uris".equals(type);
  }

  private String fileFor(JavaCode code) {
    return String.format("%s.java", code.typeName());
  }

}
