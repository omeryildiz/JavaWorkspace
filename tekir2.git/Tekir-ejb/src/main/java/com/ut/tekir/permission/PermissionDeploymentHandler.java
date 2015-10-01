/*
 * Copyleft 2007-2011 Ozgur Yazilim A.S.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * www.tekir.com.tr
 * www.ozguryazilim.com.tr
 *
 */

package com.ut.tekir.permission;

import org.jboss.seam.deployment.AbstractDeploymentHandler;
import org.jboss.seam.deployment.DeploymentMetadata;

/**
 * Seam Custom Resource Handler for .perm.xml files
 * @author Hakan Uygun
 */
public class PermissionDeploymentHandler extends AbstractDeploymentHandler{

    private static DeploymentMetadata PERM_METADATA = new DeploymentMetadata()
    {
        public String getFileNameSuffix() {
            return ".perm.xml";
        }
    };


    public String getName() {
        return "permDeploymentHandler";
    }

    public DeploymentMetadata getMetadata() {
        return PERM_METADATA;
    }

}
