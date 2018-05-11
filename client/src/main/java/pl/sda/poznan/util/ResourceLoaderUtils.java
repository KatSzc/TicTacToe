package pl.sda.poznan.util;

import java.net.URL;

public class ResourceLoaderUtils {

  public static URL getResource(String path){
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    return classloader.getResource(path);
  }

}
