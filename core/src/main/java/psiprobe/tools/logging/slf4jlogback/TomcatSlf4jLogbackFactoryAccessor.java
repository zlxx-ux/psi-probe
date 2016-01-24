/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */

package psiprobe.tools.logging.slf4jlogback;

import org.apache.commons.beanutils.MethodUtils;

import psiprobe.tools.logging.DefaultAccessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps a TomcatSlf4jLogback logger factory from a given web application class loader.
 * 
 * <p>
 * All TomcatSlf4jLogback classes are loaded via the given class loader and not via psi-probe's own
 * class loader. For this reasons, all methods on TomcatSlf4jLogback objects are invoked via
 * reflection.
 * </p>
 * <p>
 * This way, we can even handle different versions of TomcatSlf4jLogback embedded in different WARs.
 * </p>
 * 
 * @author Jeremy Landis
 */
public class TomcatSlf4jLogbackFactoryAccessor extends DefaultAccessor {

  /**
   * Attempts to initialize a TomcatSlf4jLogback logger factory via the given class loader.
   *
   * @param cl the ClassLoader to use when fetching the factory
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws InvocationTargetException the invocation target exception
   */
  public TomcatSlf4jLogbackFactoryAccessor(ClassLoader cl) throws ClassNotFoundException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    // Get the singleton SLF4J binding, which may or may not be Logback, depending on the binding.
    Class clazz = cl.loadClass("org.apache.juli.logging.org.slf4j.impl.StaticLoggerBinder");
    Method getSingleton = MethodUtils.getAccessibleMethod(clazz, "getSingleton", new Class[0]);
    Object singleton = getSingleton.invoke(null);
    Method getLoggerFactory = MethodUtils
        .getAccessibleMethod(clazz, "getLoggerFactory", new Class[0]);
    
    Object loggerFactory = getLoggerFactory.invoke(singleton);

    // Check if the binding is indeed Logback
    Class loggerFactoryClass =
        cl.loadClass("org.apache.juli.logging.ch.qos.logback.classic.LoggerContext");
    if (!loggerFactoryClass.isInstance(loggerFactory)) {
      throw new RuntimeException("The singleton SLF4J binding was not Logback");
    }
    setTarget(loggerFactory);
  }

  /**
   * Returns the TomcatSlf4jLogback root logger.
   * 
   * @return the root logger
   */
  public TomcatSlf4jLogbackLoggerAccessor getRootLogger() {
    /*
     * TomcatSlf4jLogback has no dedicated getRootLogger() method, so we simply access the root
     * logger by its well-defined name.
     */
    return getLogger("ROOT");
  }

  /**
   * Returns the TomcatSlf4jLogback logger with a given name.
   *
   * @param name the name
   * @return the Logger with the given name
   */
  public TomcatSlf4jLogbackLoggerAccessor getLogger(String name) {
    try {
      Class clazz = getTarget().getClass();
      Method getLogger = MethodUtils
          .getAccessibleMethod(clazz, "getLogger", new Class[] {String.class});
      
      Object logger = getLogger.invoke(getTarget(), new Object[] {name});
      if (logger == null) {
        throw new NullPointerException(getTarget() + ".getLogger(\"" + name + "\") returned null");
      }
      TomcatSlf4jLogbackLoggerAccessor accessor = new TomcatSlf4jLogbackLoggerAccessor();
      accessor.setTarget(logger);
      accessor.setApplication(getApplication());
      return accessor;

    } catch (Exception e) {
      logger.error(getTarget() + ".getLogger(\"" + name + "\") failed", e);
    }
    return null;
  }

  /**
   * Returns a list of wrappers for all TomcatSlf4jLogback appenders that have an associated logger.
   * 
   * @return a list of {@link TomcatSlf4jLogbackAppenderAccessor}s representing all appenders that
   *         are in use
   */
  public List<TomcatSlf4jLogbackAppenderAccessor> getAppenders() {
    List<TomcatSlf4jLogbackAppenderAccessor> appenders =
        new ArrayList<TomcatSlf4jLogbackAppenderAccessor>();
    try {
      Class clazz = getTarget().getClass();
      Method getLoggerList = MethodUtils
          .getAccessibleMethod(clazz, "getLoggerList", new Class[0]);
      
      List<Object> loggers = (List<Object>) getLoggerList.invoke(getTarget());
      Iterator<Object> it = loggers.iterator();
      while (it.hasNext()) {
        TomcatSlf4jLogbackLoggerAccessor accessor = new TomcatSlf4jLogbackLoggerAccessor();
        accessor.setTarget(it.next());
        accessor.setApplication(getApplication());

        appenders.addAll(accessor.getAppenders());
      }
    } catch (Exception e) {
      logger.error(getTarget() + ".getLoggerList() failed", e);
    }
    return appenders;
  }

}
