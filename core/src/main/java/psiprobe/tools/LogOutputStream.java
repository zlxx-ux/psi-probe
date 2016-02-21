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

package psiprobe.tools;

import java.io.OutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;

/**
 * An {@code OutputStream} which writes to a commons-logging {@code Log} at a particular level.
 *
 * @author Mark Lewis
 */
public class LogOutputStream extends OutputStream {

  /** The Constant LEVEL_OFF. */
  public static final int LEVEL_OFF = 0;
  
  /** The Constant LEVEL_TRACE. */
  public static final int LEVEL_TRACE = 1;
  
  /** The Constant LEVEL_DEBUG. */
  public static final int LEVEL_DEBUG = 2;
  
  /** The Constant LEVEL_INFO. */
  public static final int LEVEL_INFO = 3;
  
  /** The Constant LEVEL_WARN. */
  public static final int LEVEL_WARN = 4;
  
  /** The Constant LEVEL_ERROR. */
  public static final int LEVEL_ERROR = 5;
  
  /** The Constant LEVEL_FATAL. */
  public static final int LEVEL_FATAL = 6;

  /** The logger. */
  private final Logger logger;
  
  /** The level. */
  private final int level;
  
  /** The buf. */
  private final StringBuffer buf = new StringBuffer();

  /**
   * Creates a {@code PrintStream} with autoFlush enabled which will write to the given {@code Log}
   * at the given level.
   * 
   * @param log the {@code Log} to which to write
   * @param level the level at which to write
   * @return a {@code PrintStream} that writes to the given log
   */
  public static PrintStream createPrintStream(Logger log, int level) {
    LogOutputStream logStream = new LogOutputStream(log, level);
    return new PrintStream(logStream, true);
  }

  /**
   * Creates a new instance of {@code LogOutputStream} which will write to a given {@code Log} at
   * the given level.
   * 
   * @param log the {@code Log} to which to write
   * @param level the level at which to write
   * @throws IllegalArgumentException if {@code log} is null
   */
  private LogOutputStream(Logger log, int level) {
    if (log == null) {
      throw new IllegalArgumentException("Log cannot be null");
    }
    this.logger = log;
    this.level = level;
  }

  /**
   * Flushes the contents of this stream to its {@link Log}.
   */
  @Override
  public void flush() {
    if (shouldWrite()) {
      String message = this.buf.toString();
      log(message);
    }
    this.buf.setLength(0);
  }

  /**
   * Writes the specified {@code byte} to this stream.
   *
   * @param out the {@code byte} to write
   */
  @Override
  public void write(int out) {
    if (shouldWrite()) {
      char chr = (char) out;
      this.buf.append(chr);
    }
  }

  /**
   * Returns the {@code Log} to which this stream writes.
   *
   * @return the {@code Log} to which this stream writes
   */
  public Logger getLog() {
    return this.logger;
  }

  /**
   * Returns the level at which this stream writes to the {@code Log}.
   *
   * @return the level at which this stream writes to the {@code Log}
   */
  public int getLevel() {
    return this.level;
  }

  /**
   * Determines if the {@code Log} is configured to accept messages at this stream's level.
   *
   * @return {@code true} if the level of the underlying {@code Log} is equal to or greater than the
   *         level assigned to this stream
   */
  private boolean shouldWrite() {
    switch (this.level) {
      case LEVEL_TRACE:
        return this.logger.isTraceEnabled();
      case LEVEL_DEBUG:
        return this.logger.isDebugEnabled();
      case LEVEL_INFO:
        return this.logger.isInfoEnabled();
      case LEVEL_WARN:
        return this.logger.isWarnEnabled();
      case LEVEL_ERROR:
        return this.logger.isErrorEnabled();
      default:
        return false;
    }
  }

  /**
   * Writes the given message to this stream's {@code Log} at this stream's level.
   * 
   * @param message the message to be written
   */
  private void log(String message) {
    if (message == null || "".equals(message)) {
      return;
    }
    switch (this.level) {
      case LEVEL_TRACE:
        this.logger.trace(message);
        break;
      case LEVEL_DEBUG:
        this.logger.debug(message);
        break;
      case LEVEL_INFO:
        this.logger.info(message);
        break;
      case LEVEL_WARN:
        this.logger.warn(message);
        break;
      case LEVEL_ERROR:
        this.logger.error(message);
        break;
      default:
        //Don't log anything
    }
  }

}
