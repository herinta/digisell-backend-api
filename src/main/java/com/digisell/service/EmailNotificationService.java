package com.digisell.service;

import com.digisell.model.OrderTransaction;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailNotificationService {

    /**
     * Sends transactional email to the buyer on behalf of the platform.
     * Sender Name: "Herin Dev via DigiSell <orders@digisell.dev>"
     * Reply-To: Seller's email / contact
     */
    public void sendOrderDeliveryEmail(OrderTransaction order, String accessPortalUrl, String directFileUrl) {
        String expiryStr = order.getDownloadExpiry() != null
                ? order.getDownloadExpiry().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                : "24 Jam ke depan";

        System.out.println("================================================================================");
        System.out.println("📧 [AUTOMATED EMAIL DISPATCHED TO BUYER]");
        System.out.println("FROM      : Herin Dev via DigiSell <orders@digisell.dev>");
        System.out.println("REPLY-TO  : support@herindev.com (Email Seller)");
        System.out.println("TO        : " + order.getCustomerEmail() + " (" + order.getCustomerName() + ")");
        System.out.println("PHONE / WA: " + order.getCustomerPhone());
        System.out.println("SUBJECT   : [DigiSell] Pembayaran Berhasil! Akses File Digital: " + order.getProductTitle());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PESAN EMAIL:");
        System.out.println("Halo " + order.getCustomerName() + ", terima kasih atas pembelianmu!");
        System.out.println("Pesananmu untuk '" + order.getProductTitle() + "' (ID: " + order.getOrderId() + ") telah selesai.");
        System.out.println("");
        System.out.println("👉 PORTAL AKSES KADALUARSA (Aktif hingga: " + expiryStr + " WIB):");
        System.out.println("   " + accessPortalUrl);
        System.out.println("");
        System.out.println("👉 TAUTAN LANGSUNG (MASTER FILE NOTION/DRIVE):");
        System.out.println("   " + directFileUrl);
        System.out.println("================================================================================");
    }
}
