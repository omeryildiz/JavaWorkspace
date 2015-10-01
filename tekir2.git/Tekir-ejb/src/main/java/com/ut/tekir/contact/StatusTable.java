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

import com.ut.tekir.entities.FinanceAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusTable implements java.util.Comparator<StatusRow> {

    
    private Map<String, StatusRow> rows = new HashMap<String, StatusRow>();
    private List<StatusRow> dataRows = null;

    public List<StatusRow> getDataRows() {
        return dataRows;
    }

    public StatusRow getRow(String rowKey) {
        if (rows.containsKey(rowKey)) {
            return rows.get(rowKey);
        } else {
            StatusRow row = new StatusRow(rowKey);
            rows.put(rowKey, row);
            return row;
        }
    }

    private void addData(String rowKey, String ccy, FinanceAction action, Double amt, Double localAmt) {
        StatusRow row = getRow(rowKey);
        StatusCell cell = row.getCell(ccy);
        if (action == FinanceAction.Credit) {
            cell.addCredit(amt);
        } else {
            cell.addDebit(amt);
        }
        cell = row.getCell("LCY");
        if (action == FinanceAction.Credit) {
            cell.addCredit(localAmt);
        } else {
            cell.addDebit(localAmt);
        }
    }

    public void addRow(String rowKey, String ccy, FinanceAction action, Double amt, Double localAmt) {
        addData(rowKey, ccy, action, amt, localAmt);
        addData("TOTAL", ccy, action, amt, localAmt);
    }

    @SuppressWarnings("unchecked")
	public void sort() {
        dataRows = new ArrayList(rows.values());
        Collections.sort(dataRows, this);
        for (StatusRow r : dataRows) {
            r.sort();
        }
    }

    public int compare(StatusRow o1, StatusRow o2) {
        if (o1.getKey().equals("OPEN")) {
            return -1;
        }
        if (o2.getKey().equals("OPEN")) {
            return 1;
        }
        if (o1.getKey().equals("TOTAL")) {
            return 1;
        }
        if (o2.getKey().equals("TOTAL")) {
            return -1;
        }
        return o1.getKey().compareTo(o2.getKey());
    }
}
