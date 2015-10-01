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

import com.ut.tekir.entities.Option;
import com.ut.tekir.options.OptionKey;
import java.util.List;

/**
 *
 * @author haky
 */
public interface OptionManager {
    void destroy();

    Option getOption(String key);
    public Option getOption( String key, String defval );
    public Option getOption( String key, String defval, boolean foruser );
    public void updateOption( Option o );
    public void updateOptions(List<Option> ls );
    void initManager();
    void loadOptions();
    
    void updateOption(OptionKey key);
    void updateOption(String optionKey);
    Option getOption(OptionKey key);
    Option getOption(OptionKey key, boolean create);
}
