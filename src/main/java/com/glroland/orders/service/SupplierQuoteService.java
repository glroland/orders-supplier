package com.glroland.orders.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.glroland.orders.dto.SupplierQuote;
import com.glroland.orders.util.Constants;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/supplier")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SupplierQuoteService {
    private static final Logger log = LoggerFactory.getLogger(SupplierQuoteService.class);

    @POST
    @Path("/quote")
    public SupplierQuote quoteProduct(SupplierQuote supplierQuote)
    {
        if (supplierQuote == null)
        {
            String msg = "Incoming supplierQuote is null.  Cannot quote product sale.";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        if (supplierQuote.getQuantity() == null)
        {
            String msg = "Quantity for incoming supplier quote is null.  Cannot quote product sale.";
            supplierQuote.setStatus(Constants.SupplierRequestStatus.ERROR);
            log.error(msg);
            throw new RuntimeException(msg);
        }

        double costPerUnit = roundPennies(Math.random() * 100.0);
        supplierQuote.setUnitCost(costPerUnit);

        double subtotal = costPerUnit * supplierQuote.getQuantity();
        supplierQuote.setSubtotalCost(subtotal);

        supplierQuote.setDateQuoted(new Date());

        return supplierQuote;
    }

    @POST
    @Path("/tax")
    public SupplierQuote calculateTax(SupplierQuote supplierQuote)
    {
        if (supplierQuote == null)
        {
            String msg = "Incoming supplierQuote is null";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        if (supplierQuote.getSubtotalCost() == null)
        {
            String msg = "Subtotal Cost for supplier quote is null.  Cannot calculate tax.";
            supplierQuote.setStatus(Constants.SupplierRequestStatus.ERROR);
            log.error(msg);
            throw new RuntimeException(msg);
        }

        double tax = supplierQuote.getSubtotalCost() * (double)0.06;
        supplierQuote.setTax(roundPennies(tax));

        return supplierQuote;
    }

    @POST
    @Path("/shipCost")
    public SupplierQuote calculateShipping(SupplierQuote supplierQuote)
    {
        if (supplierQuote == null)
        {
            String msg = "Incoming supplierQuote is null";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        if (supplierQuote.getQuantity() == null)
        {
            String msg = "Quantity for incoming supplier quote is null";
            supplierQuote.setStatus(Constants.SupplierRequestStatus.ERROR);
            log.error(msg);
            throw new RuntimeException(msg);
        }

        double shippingPerUnit = Math.random() * 15.0;
        int quantity = supplierQuote.getQuantity().intValue();
        double shipping = shippingPerUnit * (double)quantity;

        supplierQuote.setShipping(roundPennies(shipping));

        return supplierQuote;
    }

    private double roundPennies(double amount)
    {
        return Math.round(amount * 100.0) / 100.0;
    }

    @POST
    @Path("/shipDate")
    public SupplierQuote estimateShipDate(SupplierQuote supplierQuote)
    {
        if (supplierQuote == null)
        {
            String msg = "Incoming supplierQuote is null";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        int rand = (int)(Math.random() * 5.0);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, rand);
        supplierQuote.setEstimatedShipDate(cal.getTime());

        return supplierQuote;
    }

    @POST
    @Path("/finalize")
    public SupplierQuote finalizeQuote(SupplierQuote supplierQuote)
    {
        if (supplierQuote == null)
        {
            String msg = "Incoming supplierQuote is null.  Cannot finalize supplier quote.";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        double subtotal = 0;
        if (supplierQuote.getSubtotalCost() != null)
        {
            subtotal = supplierQuote.getSubtotalCost();
        }

        double tax = 0;
        if (supplierQuote.getTax() != null)
        {
            tax = supplierQuote.getTax();
        }

        double shipping = 0;
        if (supplierQuote.getShipping() != null)
        {
            shipping = supplierQuote.getShipping();
        }

        double total = subtotal + tax + shipping;
        supplierQuote.setTotalCost(roundPennies(total));

        supplierQuote.setStatus(Constants.SupplierRequestStatus.APPROVED);

        return supplierQuote;
    }
}
