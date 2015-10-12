package ru.isa.ai.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

/**
 * Author: Aleksandr Panov
 * Date: 23.10.2014
 * Time: 11:37
 */
public class MNISTDatasetReader {
    private byte[] labels;
    private byte[][] images;
    private String[] fileNames = {"train-images.idx3-ubyte", "train-labels.idx1-ubyte"};

    private String directoryPath;

    public MNISTDatasetReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public byte[][] readData(boolean withMagic) throws IOException {
        BufferedInputStream labelStream = new BufferedInputStream(
                new FileInputStream(directoryPath + File.separator + fileNames[1]));
        byte[] info = new byte[withMagic ? 8 : 4];
        labelStream.read(info, 0, withMagic ? 8 : 4);
        ByteBuffer infoBuffer = ByteBuffer.wrap(info);
        if (withMagic)
            infoBuffer.getInt(); // Magic nuamber
        int itemsAmount = infoBuffer.getInt();
        System.out.println(String.format("Number of items: %d", itemsAmount));

        labels = new byte[itemsAmount];
        int readed = labelStream.read(labels, 0, itemsAmount);
        System.out.println(String.format("Readed: %d", readed));
        if (readed != itemsAmount) {
            throw new IOException("Could not read info");
        }
        labelStream.close();

        BufferedInputStream imageStream = new BufferedInputStream(
                new FileInputStream(directoryPath + File.separator + fileNames[0]));
        byte[] imageInfo = new byte[withMagic ? 16 : 12];
        imageStream.read(imageInfo, 0, withMagic ? 16 : 12);
        ByteBuffer imageInfoBuffer = ByteBuffer.wrap(imageInfo);
        if (withMagic)
            imageInfoBuffer.getInt(); // Magic nuamber
        int imageAmount = imageInfoBuffer.getInt();
        if (itemsAmount != imageAmount) {
            throw new IOException("Other amount of items: " + imageAmount);
        }
        int rowsAmount = imageInfoBuffer.getInt();
        int columnsAmount = imageInfoBuffer.getInt();
        System.out.println(String.format("Number of items: %d, rows: %d, columns: %d", itemsAmount, rowsAmount, columnsAmount));
        System.out.println(String.format("Readed: %d", readed));

        images = new byte[itemsAmount][];
        for (int i = 0; i < itemsAmount; i++) {
            byte[] imageData = new byte[rowsAmount * columnsAmount];
            readed = imageStream.read(imageData, 0, rowsAmount * columnsAmount);
            if (readed != rowsAmount * columnsAmount) {
                throw new IOException("Could not read image " + i + " of " + readed);
            }
            images[i] = imageData;
        }
        imageStream.close();

        return images;
    }

    public byte[] getLabels() {
        return labels;
    }

    public byte[][] getImages() {
        return images;
    }
}
