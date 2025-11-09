package com.group3.application.common.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Utility class for generating QR codes
 */
public class QRCodeGenerator {
    
    /**
     * Generate QR code bitmap from content string
     * @param content Content to encode in QR code
     * @param width Width of the QR code in pixels
     * @param height Height of the QR code in pixels
     * @return Bitmap of QR code or null if generation fails
     */
    public static Bitmap generate(String content, int width, int height) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generate QR code with default size (512x512)
     */
    public static Bitmap generate(String content) {
        return generate(content, 512, 512);
    }
    
    /**
     * Generate QR code for table with table ID
     */
    public static Bitmap generateTableQR(String tableId, int size) {
        String content = "TABLE_ID:" + tableId;
        return generate(content, size, size);
    }
    
    /**
     * Generate QR code for table with default size
     */
    public static Bitmap generateTableQR(String tableId) {
        return generateTableQR(tableId, 512);
    }
}
