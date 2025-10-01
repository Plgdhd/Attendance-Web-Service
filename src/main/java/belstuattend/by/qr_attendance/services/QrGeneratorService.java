package belstuattend.by.qr_attendance.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import belstuattend.by.qr_attendance.exceptions.QrCodeGenerationException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class QrGeneratorService {

    private static final int QR_CODE_SIZE = 500;
    private static final Random random = new Random();
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public QrGeneratorService(RedisTemplate<String, String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public BufferedImage generateUniqueQrCode(String disciplineName) {

        String code = generateRandomCode();

        //генерируем код и сохраняем в редисе на 120 минут
        //если существует уже, то удаляем
        if(redisTemplate.opsForValue().get(disciplineName) != null){
            redisTemplate.delete(disciplineName);
        }
        redisTemplate.opsForValue().set(disciplineName, code, 120, TimeUnit.MINUTES); 

        return createQrCode(generateUniqueId(code));
    }

    private static BufferedImage createQrCode(String content) {
        try {
            byte[] qrCodeBytes = generateQrCodeBytes(content);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(qrCodeBytes));

            return bufferedImage;
        } catch (IOException e) {
            throw new QrCodeGenerationException("Ошибка при генерации Qr-кода: " + e);
        }
    }

    private static byte[] generateQrCodeBytes(String content) {
        try {
            BitMatrix bitMatrix = generateQrCodeBitMatrix(content);
            return convertQrCodeToBytes(bitMatrix);
        } catch (QrCodeGenerationException e) {
            throw new QrCodeGenerationException("Ошибка при генерации BitMatrix: " + e);
        }
    }

    private static String generateRandomCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private static String generateUniqueId(String code) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return "qrcode_" + timestamp + "_" + code;
    }


    private static byte[] convertQrCodeToBytes(BitMatrix bitMatrix) {
        try {
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new QrCodeGenerationException("Ошибка конвертации: " + e);
        }
    }

    private static BitMatrix generateQrCodeBitMatrix(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            return qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);
        } catch (WriterException e) {
            throw new QrCodeGenerationException("Ошимбка при генерации в generateBitMatrix: " + e);
        }
    }
}
