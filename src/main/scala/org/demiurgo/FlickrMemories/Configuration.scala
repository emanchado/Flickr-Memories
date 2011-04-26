import org.yaml.snakeyaml.Yaml

package org.demiurgo.FlickrMemories {
  class Configuration(var file: String = null) {
    var configuration : java.util.LinkedHashMap
                            [String, java.util.LinkedHashMap[String,
                                                             String]] =
                                                               new java.util.LinkedHashMap

    if (file != null)
      configuration = readFromFile(file)

    def readFromFile(file: String) : java.util.LinkedHashMap[String, java.util.LinkedHashMap[String, String]] = {
      readFromString(scala.io.Source.fromFile(file).mkString)
    }

    def readFromString(yamlString: String) : java.util.LinkedHashMap[String, java.util.LinkedHashMap[String, String]] = {
      val yaml = new Yaml
      return yaml.load(yamlString).
                    asInstanceOf[java.util.LinkedHashMap
                                 [String, java.util.LinkedHashMap[String,
                                                                  String]]]
    }

    def get(key: String, subkey: String) : String = {
      configuration.get(key).get(subkey)
    }
  }
}
