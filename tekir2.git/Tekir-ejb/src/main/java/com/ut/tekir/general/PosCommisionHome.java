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

package com.ut.tekir.general;

/**
 *
 * @author rustem
 */

import com.ut.tekir.entities.PosCommision;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

@Local
public interface PosCommisionHome<E> extends IEntityBase<E>  {

    void init();
    void manualFlush();

    void createNewLine();
    void deleteLine(PosCommisionDetail pcd);
    void deleteLine(Integer ix);

    PosCommision getPosCommision();
    void setPosCommision(PosCommision posCommision);
    void print();



}
