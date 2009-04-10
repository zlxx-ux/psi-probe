/**
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://probe.jstripe.com/d/license.shtml
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.jstripe.tomcat.probe.beans;

import org.jstripe.tomcat.probe.model.DataSourceInfo;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * DBCP datasource abstraction layer.
 *
 * Author: Vlad Ilyushchenko
 */
public class DbcpDatasourceAccessor implements DatasourceAccessor {
    public DataSourceInfo getInfo(Object resource) throws Exception {
        DataSourceInfo dataSourceInfo = null;
        if (canMap(resource)) {
            BasicDataSource source = (BasicDataSource) resource;
            dataSourceInfo = new DataSourceInfo();
            dataSourceInfo.setBusyConnections(source.getNumActive());
            dataSourceInfo.setEstablishedConnections(source.getNumIdle() + source.getNumActive());
            dataSourceInfo.setMaxConnections(source.getMaxActive());
            dataSourceInfo.setJdbcURL(source.getUrl());
            dataSourceInfo.setUsername(source.getUsername());
            dataSourceInfo.setResettable(false);
        }
        return dataSourceInfo;
    }

    public boolean reset(Object resource) throws Exception {
        return false;
    }

    public boolean canMap(Object resource) {
        return "org.apache.commons.dbcp.BasicDataSource".equals(resource.getClass().getName()) && resource instanceof BasicDataSource;
    }
}