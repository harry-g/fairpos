/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.discount;


import com.openbravo.format.Formats;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
//import com.openbravo.pos.ticket.JPanelTicket;
//import com.openbravo.pos.ticket.TicketProductInfo; 
import com.openbravo.pos.ticket.TicketTaxInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Vilem
 */
public class DiscountManager {
    private double discountVal = 0.00f;
    private double discountTotal = 0.00f;
    
    public void setDiscountValue(double val) {
        if (val > 100)
            val = 100;
        this.discountVal=val;
    }
    
    public String getDiscountValueText() {
        return (""+ discountVal*100.0f + " %").replaceAll("\\.0\\d*", "");
    }
    
    public void discountRowsClear(TicketInfo ticket) {
        List<TicketLineInfo> lineList = ticket.getLines();        
        Iterator<TicketLineInfo> it = lineList.iterator();
        
        List<Integer> toDelete = new ArrayList();
        
        int j=-1;
        while (it.hasNext()) {
            j++;
            TicketLineInfo line = it.next();
            
            String test;
            test = line.getProductName();
            if (test.startsWith("## Discount "))
                //ticket.removeLine(j);
                toDelete.add(j);
        }
        
        int shift = 0;
        for (Integer index: toDelete) {            
            ticket.removeLine(index-shift);
            shift++;
        }        
    }
    
    /*
     * Update discount for ticket
     *  - adds row with discount
     *  - called from JPanelTicket.java on such a places that this row is not visible (but it is there & its values are included in price totals)
     *  - deletes old discount lines & creates new
     *  
     */
    
    public void discountRowsUpdate(TicketInfo ticket) {
 
        discountRowsClear(ticket);

        TicketTaxInfo[] taxes = ticket.getTaxLines();  
        
        
        this.discountTotal = 0;
        
        for (int i = 0; i < taxes.length; i++) {  
            TicketTaxInfo taxline = taxes[i];  
            
            ticket.insertLine(ticket.getLinesCount(),
                new TicketLineInfo(
                    "## Discount " + 100*discountVal + "% of "  + Double.toString(taxline.getTax() + taxline.getSubTotal()) , //+ " " + taxline.printTax() + " 1+tax * " + taxline.printSubTotal() + ") ,   
                    taxline.getTaxInfo().getTaxCategoryID(),          
                    1.0, 
                    -taxline.getSubTotal() * discountVal,
                    taxline.getTaxInfo()));  
            
            this.discountTotal += - (taxline.getTax() + taxline.getSubTotal()) * discountVal;
        } 

    }    
    
    
         
        
    public double getDiscountTotal() {         
        return this.discountTotal;
    }
    
    public String printDiscountTotal() {                         
        return Formats.CURRENCY.formatValue(new Double(getDiscountTotal()));
    }
    
    
}

/*

import com.openbravo.format.Formats;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.ticket.TicketProductInfo; 
import java.util.Properties;

discountrate = 0.5;  

total = ticket.getTotal();  
if (total > 0.0) {  
    sdiscount = Formats.PERCENT.formatValue(discountrate);  
 
    taxes = ticket.getTaxLines();  
    for (int i = 0; i < taxes.length; i++) {  
        taxline = taxes[i];  
        ticket.insertLine(ticket.getLinesCount(),
            new TicketLineInfo(
                "Discount " + sdiscount + " of " + taxline.printSubTotal(),   
                taxline.getTaxInfo().getTaxCategoryID(),          
                1.0, 
                -taxline.getSubTotal() * discountrate,
                taxline.getTaxInfo()));  
    }  
    sales.setSelectedIndex(ticket.getLinesCount() - 1);
} else {  
    java.awt.Toolkit.getDefaultToolkit().beep();  
}*/