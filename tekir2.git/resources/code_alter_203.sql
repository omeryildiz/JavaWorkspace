/* Tekir 2.0.2 -> 2.0.3 Mysql */
/* belge kodu buyumesi */

ALTER TABLE BANK_TO_ACCOUNT_TRANSFER  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE BANK_TO_BANK_TRANSFER  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE BANK_TO_CONTACT_TRANSFER  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE BOND_PURCHASE_SALE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_COLL_AT_BANK_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_FROM_CONTACT_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_TO_ACC_COLL_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_TO_ACC_PAY_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_TO_BANK_ASS_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_TO_BANK_PAY_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_TO_BANK_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE CHEQUE_TO_CONTACT_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE COUNT_NOTE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE DEBIT_CREDIT_NOTE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE DEBIT_CREDIT_VIREMENT  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE DEPOSIT_ACCOUNT  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE EXPENSE_NOTE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE TKR_SWIFT  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE FUND_TRANSFER  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE INVOICE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE ORDER_NOTE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PORTF_TO_PORTF_TRANSFER  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PRODUCT_TRANSFER  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PRODUCT_VIREMENT  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROM_COLL_AT_BANK_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROM_FROM_CONT_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROM_TO_ACC_COL_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROM_TO_ACC_PAY_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PRO_TO_BANK_ASS_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROMISSORY_TO_BANK_PAY_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROMISSORY_TO_BANK_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PROMISSORY_TO_CONTACT_PAYROLL  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE REPO  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE SHIPMENT_NOTE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE SPENDING_NOTE  MODIFY COLUMN CODE VARCHAR(15);

ALTER TABLE TEKIR_INVOICE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE TEKIR_ORDER_NOTE  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE TENDER  MODIFY COLUMN CODE VARCHAR(15);

ALTER TABLE FOUNDATION_TXN  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE POS_TXN  MODIFY COLUMN CODE VARCHAR(15);

ALTER TABLE FINANCE_TXN  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE PRODUCT_TXN  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE ACCOUNT_TXN  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE BANK_TXN  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE BOND_TXN  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE TAX_TXN  MODIFY COLUMN CODE VARCHAR(15);

ALTER TABLE PAYMENT  MODIFY COLUMN CODE VARCHAR(15);
ALTER TABLE TEKIR_SHIPMENT_NOTE  MODIFY COLUMN CODE VARCHAR(15);

/* Avans tipi atama : normal işlem */
UPDATE FINANCE_TXN SET PROCESS_TYPE = 0 WHERE PROCESS_TYPE is null;
UPDATE ACCOUNT_TXN SET PROCESS_TYPE = 0 WHERE PROCESS_TYPE is null;
UPDATE BANK_TXN SET PROCESS_TYPE = 0 WHERE PROCESS_TYPE is null;

