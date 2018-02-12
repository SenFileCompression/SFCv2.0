package org.senfilecompression;

public class FileHeaderCorruptedException extends Exception {
    public FileHeaderCorruptedException(String message) {
        super(message);
    }
}
