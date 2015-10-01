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

package com.ut.tekir.framework;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{

    public Hash()
    {
    }

    public String md5(String data)
        throws NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data.getBytes());
        byte b[] = digest.digest();
        char hexChars[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
            'A', 'B', 'C', 'D', 'E', 'F'
        };
        String hex = "";
        int lsb = 0;
        for(int i = 0; i < b.length; i++)
        {
            int msb = (b[i] & 0xff) / 16;
            lsb = (b[i] & 0xff) % 16;
            hex = (new StringBuilder()).append(hex).append(hexChars[msb]).append(hexChars[lsb]).toString();
        }

        return hex;
    }
}
