/*
 * Copyright © EMC Corporation. All rights reserved.
 */
package radl.gradle


/**
 * Gradle extension for RADL specific properties.
 */
class RadlExtension {

  def radlCoreVersion = '1.0.10'
  def radlDirName = 'src/main/radl'
  def serviceName
  def packagePrefix
  def docsDir = 'docs/rest'
  def extraSourceDir
  def extraClasspath
  def skipClasspath = false
  def extraProcessors
  def keepArgumentsFile = false
  def scm = 'default'
  
  def preExtracts = []
  def preExtract(clos) {
    preExtracts += clos
  }
    
}
