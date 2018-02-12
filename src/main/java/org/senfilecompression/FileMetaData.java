package org.senfilecompression;

public class FileMetaData {
    String fileName;
    long size;
    public FileMetaData(String fileName, long size) {
        this.fileName = fileName;
        this.size = size;
    }
    public FileMetaData(String details) throws FileHeaderCorruptedException {
        String[] data = details.split(":");
        if(data.length != 2)
            throw new FileHeaderCorruptedException("Une partie des entetes du fichier a ete corrompu");
        this.fileName = data[0];
        this.size = Long.valueOf(data[1], 16);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileMetaData{" +
                "fileName='" + fileName + '\'' +
                ", size=" + size +
                '}';
    }
}
