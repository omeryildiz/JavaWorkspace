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

package com.ut.tekir.invoice;

import com.ut.tekir.entities.InvoiceItem;
import com.ut.tekir.entities.InvoiceServiceItem;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.ShipmentItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Hem İrsaliye hem de hizmet satırlarını karşılamak için bir ara model...
 * @author haky
 */
public class InvoiceItemModel {

    public enum ItemType { ServiceItem, ShipmentItem, InvoiceShipmentItem, InvoiceDiscount, TransportShipmentItem    }
    
    private ItemType type;
    private ShipmentItem shipmentItem;
    private InvoiceServiceItem serviceItem;
    private DiscountLineModel discount;
    private List<DiscountLineModel> discounts = new ArrayList<DiscountLineModel>();

    public InvoiceItemModel() {
        type = ItemType.ServiceItem;
        serviceItem = new InvoiceServiceItem();
    }

    public InvoiceItemModel(ItemType type) {
        this.type = type;

        if (type == ItemType.ServiceItem) {
            serviceItem = new InvoiceServiceItem();
        } else if (type == ItemType.InvoiceDiscount) {
            discount = new DiscountLineModel();
        } else {
            shipmentItem = new ShipmentItem();
        }
    }

    public InvoiceItemModel(InvoiceServiceItem si) {
        this.type = ItemType.ServiceItem;
        serviceItem = si;
    }

    public InvoiceItemModel(ShipmentItem sh) {
        this.type = ItemType.InvoiceShipmentItem;
        shipmentItem = sh;
    }
    
    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ShipmentItem getShipmentItem() {
        return shipmentItem;
    }

    public void setShipmentItem(ShipmentItem shipmentItem) {
        this.shipmentItem = shipmentItem;
    }

    public InvoiceServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(InvoiceServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public InvoiceItem getInvoiceItem() {

        if (type == ItemType.ServiceItem) {
            return serviceItem;
        } else {
            return shipmentItem;
        }

    }

    public Product getProduct() {
        if (type == ItemType.ServiceItem) {
            return null;
        } else {
            return getShipmentItem().getProduct();
        }
    }

    public void setProduct(Product product) {
        if (type != ItemType.ServiceItem) {
            getShipmentItem().setProduct(product);
        }
    }

    public Product getService() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getService();
        } else {
            return null;
        }
    }

    public void setService(Product service) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setService(service);
        }
    }

    public Long getId() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getId();
        } else {
            return getShipmentItem().getId();
        }
    }

    public void setId(Long id) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setId(id);
        } else {
            getShipmentItem().setId(id);
        }
    }

    public String getInfo() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getInfo();
        } else {
            return getShipmentItem().getInfo();
        }
    }

    public void setInfo(String info) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setInfo(info);
        } else {
            getShipmentItem().setInfo(info);
        }
    }

    public String getLineCode() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getLineCode();
        } else {
            return getShipmentItem().getLineCode();
        }
    }

    public void setLineCode(String lineCode) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setLineCode(lineCode);
        } else {
            getShipmentItem().setLineCode(lineCode);
        }
    }

    public Quantity getQuantity() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getQuantity();
        } else {
            return getShipmentItem().getQuantity();
        }
    }

    public void setQuantity(Quantity quantity) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setQuantity(quantity);
        } else {
            getShipmentItem().setQuantity(quantity);
        }
    }
    
    
    public MoneySet getUnitPrice() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getUnitPrice();
        } else {
            return getShipmentItem().getUnitPrice();
        }
    }

    public void setUnitPrice(MoneySet unitPrice) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setUnitPrice(unitPrice);
        } else {
            getShipmentItem().setUnitPrice(unitPrice);
        }
    }

    public MoneySet getAmount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getAmount();
        } else {
            return getShipmentItem().getAmount();
        }
    }

    public void setAmount(MoneySet amount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setAmount(amount);
        } else {
            getShipmentItem().setAmount(amount);
        }
    }

    
    public BigDecimal getTaxRate() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getTaxRate();
        } else {
            return getShipmentItem().getTaxRate();
        }
    }

    public void setTaxRate(BigDecimal taxRate) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setTaxRate(taxRate);
        } else {
            getShipmentItem().setTaxRate(taxRate);
        }
    }

    public MoneySet getTaxAmount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getTaxAmount();
        } else {
            return getShipmentItem().getTaxAmount();
        }
    }

    public void setTaxAmount(MoneySet taxAmount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setTaxAmount(taxAmount);
        } else {
            getShipmentItem().setTaxAmount(taxAmount);
        }
    }

    
    public Money getTotalAmaount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getTotalAmaount();
        } else {
            return getShipmentItem().getTotalAmount();
        }
    }

    public void setTotalAmaount(Money totalAmount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setTotalAmaount(totalAmount);
        } else {
            getShipmentItem().setTotalAmount(totalAmount);
        }
    }
    
    public MoneySet getTaxExcludedTotalAmount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getTaxExcludedTotalAmount();
        } else {
            return getShipmentItem().getTaxExcludedTotalAmount();
        }
    }

    public void setTaxExcludedTotalAmount(MoneySet taxExcludedTotalAmount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setTaxExcludedTotalAmount(taxExcludedTotalAmount);
        } else {
            getShipmentItem().setTaxExcludedTotalAmount(taxExcludedTotalAmount);
        }
    }

    public BigDecimal getLineDiscount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getLineDiscount();
        } else {
            return getShipmentItem().getLineDiscount();
        }
    }

    public void setLineDiscount(BigDecimal lineDiscount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setLineDiscount(lineDiscount);
        } else {
            getShipmentItem().setLineDiscount(lineDiscount);
        }
    }

    public BigDecimal getShipmentDiscount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getShipmentDiscount();
        } else {
            return getShipmentItem().getShipmentDiscount();
        }
        
    }

    public void setShipmentDiscount(BigDecimal shipmentDiscount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setShipmentDiscount(shipmentDiscount);
        } else {
            getShipmentItem().setShipmentDiscount(shipmentDiscount);
        }
    }

    public BigDecimal getInvoiceDiscount() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getInvoiceDiscount();
        } else {
            return getShipmentItem().getInvoiceDiscount();
        }
    }

    public void setInvoiceDiscount(BigDecimal invoiceDiscount) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setInvoiceDiscount(invoiceDiscount);
        } else {
            getShipmentItem().setInvoiceDiscount(invoiceDiscount);
        }
    }

    public BigDecimal getLineExpense() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getLineExpense();
        } else {
            return getShipmentItem().getLineExpense();
        }
    }

    public void setLineExpense(BigDecimal lineExpense) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setLineExpense(lineExpense);
        } else {
            getShipmentItem().setLineExpense(lineExpense);
        }
    }

    public BigDecimal getShipmentExpense() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getShipmentExpense();
        } else {
            return getShipmentItem().getShipmentExpense();
        }
    }

    public void setShipmentExpense(BigDecimal shipmentExpense) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setShipmentExpense(shipmentExpense);
        } else {
            getShipmentItem().setShipmentExpense(shipmentExpense);
        }
    }

    public BigDecimal getInvoiceExpense() {
        if (type == ItemType.ServiceItem) {
            return getServiceItem().getInvoiceExpense();
        } else {
            return getShipmentItem().getInvoiceExpense();
        }
    }

    public void setInvoiceExpense(BigDecimal invoiceExpense) {
        if (type == ItemType.ServiceItem) {
            getServiceItem().setInvoiceExpense(invoiceExpense);
        } else {
            getShipmentItem().setInvoiceExpense(invoiceExpense);
        }
    }
    
    
    
    public List<DiscountLineModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountLineModel> discounts) {
        this.discounts = discounts;
    }

    public DiscountLineModel getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountLineModel discount) {
        this.discount = discount;
    }

}
