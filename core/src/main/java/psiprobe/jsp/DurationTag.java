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

package psiprobe.jsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Silly JSP tag to display duration in milliseconds as hours:minutes:seconds.milliseconds
 * 
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class DurationTag extends TagSupport {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(DurationTag.class);

  /** The value. */
  private long value;

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(long value) {
    this.value = value;
  }

  @Override
  public int doStartTag() throws JspException {
    try {
      this.pageContext.getOut().write(duration(this.value));
    } catch (IOException e) {
      logger.debug("Exception writing duration to JspWriter", e);
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Duration.
   *
   * @param value the value
   * @return the string
   */
  public static String duration(long value) {
    long millis = value % 1000;
    long sec = value / 1000;
    long mins = sec / 60;
    long hours = mins / 60;

    sec = sec % 60;
    mins = mins % 60;

    return hours + ":" + long2Str(mins) + ":" + long2Str(sec) + "." + long3Str(millis);
  }

  /**
   * Long2 str.
   *
   * @param value the value
   * @return the string
   */
  private static String long2Str(long value) {
    return value < 10 ? "0" + value : Long.toString(value);
  }

  /**
   * Long3 str.
   *
   * @param value the value
   * @return the string
   */
  private static String long3Str(long value) {
    if (value < 10) {
      return "00" + value;
    } else if (value < 100) {
      return "0" + value;
    } else {
      return Long.toString(value);
    }
  }

}
