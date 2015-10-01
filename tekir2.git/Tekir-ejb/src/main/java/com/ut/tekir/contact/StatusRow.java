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

package com.ut.tekir.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusRow implements java.util.Comparator<StatusCell> {

    private String key;
    private Map<String, StatusCell> cells = new HashMap<String, StatusCell>();
    private List<StatusCell> dataCells = new ArrayList<StatusCell>();

    public StatusRow(String key) {
        super();
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, StatusCell> getCells() {
        return cells;
    }

    public void setCells(Map<String, StatusCell> cells) {
        this.cells = cells;
    }

    public StatusCell getCell(String key) {
        if (cells.containsKey(key)) {
            return cells.get(key);
        } else {
            StatusCell cell = new StatusCell(key);
            cells.put(key, cell);
            return cell;
        }
    }

    public void sort() {
        setDataCells(new ArrayList<StatusCell>(cells.values()));
        Collections.sort(getDataCells(),this);
    }

    public int compare(StatusCell o1, StatusCell o2) {
        if (o1.getCcy().equals("LCY")) {
            return 1;
        }
        if (o2.getCcy().equals("LCY")) {
            return -1;
        }
        return o1.getCcy().compareTo(o2.getCcy());
    }

    public List<StatusCell> getDataCells() {
        return dataCells;
    }

    public void setDataCells(List<StatusCell> dataCells) {
        this.dataCells = dataCells;
    }
}
