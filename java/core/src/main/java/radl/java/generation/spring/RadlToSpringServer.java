/*
 * Copyright © EMC Corporation. All rights reserved.
 */
package radl.java.generation.spring;

import java.io.File;

import org.w3c.dom.Document;

import radl.common.xml.Xml;
import radl.core.Log;
import radl.core.cli.Application;
import radl.core.cli.Arguments;
import radl.core.cli.Cli;
import radl.core.code.SourceFile;
import radl.core.enforce.Desired;
import radl.core.enforce.Enforcer;
import radl.core.enforce.Reality;
import radl.core.generation.DesiredSourceFiles;
import radl.core.generation.RealSourceFiles;
import radl.core.scm.ScmFactory;
import radl.core.scm.SourceCodeManagementSystem;
import radl.java.code.Java;


/**
 * Generates (and re-generates) code for a Spring-based server that implements a RADL document.
 */
public class RadlToSpringServer implements Application {

  public static void main(String[] args) {
    Cli.run(RadlToSpringServer.class, args);
  }

  /**
   * The following arguments are supported.<ul>
   * <li>[Required] The RADL file</li>
   * <li>[Optional] The base directory. Defaults to the current directory</li>
   * <li>[Optional] The prefix for packages. Defaults to <code>radl.sample</code></li>
   * <li>[Optional] The directory inside the base directory in which to store generated source.
   * Defaults to <code>build/src/java</code></li>
   * <li>[Optional] The directory inside the base directory in which to store skeleton code.
   * Defaults to <code>src/main/java</code></li>
   * <li>[Optional] The source code management system to use. Defaults to <code>default</code></li>
   * </ul>
   */
  @Override
  public int run(Arguments arguments) {
    if (!arguments.hasNext()) {
      Log.error("Usage: " + RadlToSpringServer.class.getSimpleName()
          + " radlFile [baseDir [packagePrefix [generatedSourceDir [mainSourceDir [scm]]]]]");
      return -1;
    }
    File radlFile = arguments.file();
    File baseDir = arguments.file(".");
    String packagePrefix = arguments.next("radl.sample");
    String generatedSourceSetDir = arguments.next("build/src/java");
    String mainSourceSetDir = arguments.next("src/main/java");
    String scmId = arguments.next("default");
    SourceCodeManagementSystem scm = ScmFactory.newInstance(scmId);
    new RadlToSpringServer().generate(radlFile, baseDir, packagePrefix, generatedSourceSetDir, mainSourceSetDir, scm);
    return 0;
  }

  void generate(File radlFile, File baseDir, String packagePrefix, String generatedSourceSetDir,
      String mainSourceSetDir, SourceCodeManagementSystem scm) {
    Document radlDocument = Xml.parse(radlFile);
    Desired<String, SourceFile> desired = new DesiredSourceFiles(radlDocument,
        new SpringSourceFilesGenerator(packagePrefix, generatedSourceSetDir, mainSourceSetDir), baseDir);
    Reality<String, SourceFile> reality = new RealSourceFiles(baseDir, generatedSourceSetDir, mainSourceSetDir,
        Java.packageToDir(packagePrefix), scm);
    new Enforcer<String, SourceFile>().enforce(desired, reality);
  }

}
