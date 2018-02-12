package org.senfilecompression;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.nio.file.StandardOpenOption.*;

public class Archivage {
    static final int BYTES = 1024;
    public static int writeMetaData(String[] files, Path dst) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(dst, Charset.forName("ISO-8859-1"))) {
            Path path = Paths.get(files[0]);
            String fileName = path.getFileName().toString();
            String size = Long.toHexString(Files.size(path));
            out.write(fileName);
            out.write(':');
            out.write(size);
            int nb = fileName.length() + size.length() + 1;
            for(int i=1; i<files.length; i++){
                path = Paths.get(files[i]);
                out.write(',');
                fileName = path.getFileName().toString();
                out.write(fileName);
                out.write(':');
                size = Long.toHexString(Files.size(path));
                out.write(size);
                nb += fileName.length() + size.length() + 2;
            }
            out.newLine();
            return  nb;
        }
    }
    public static String getFileHeader(Path path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(path, Charset.forName("ISO-8859-1"))) {
            String str = in.readLine();
            return str;
        }
    }
    public static FileMetaData[] extractFileMetaData(String fileHeader) throws FileHeaderCorruptedException {
            FileMetaData[] fmd;
            Set<FileMetaData> headers = new LinkedHashSet<>();
            String fileHeaders[] = fileHeader.split(",");
            for(String details: fileHeaders)
                headers.add(new FileMetaData(details));
            fmd = new FileMetaData[headers.size()];
            headers.toArray(fmd);
            return fmd;

    }
    public static long copyFileBytes(SeekableByteChannel src, SeekableByteChannel dst, long len) throws IOException{
        int remaining = (int) (len % BYTES);
        int rounds = (int) (len / BYTES);
        ByteBuffer buf = ByteBuffer.allocate(remaining);
        src.read(buf);
        ByteBuffer bb = ByteBuffer.wrap(buf.array());
        long nb = dst.write(bb);
        buf.flip();
        buf = ByteBuffer.allocate(BYTES);
        for(int i=0; i<rounds; i++){
            src.read(buf);
            bb = ByteBuffer.wrap(buf.array());
            nb += dst.write(bb);
            buf.flip();
        }
        return  nb;
    }
    public static void archive(String[] files, String dst) throws IOException{
        Path path = Paths.get(dst);
        writeMetaData(files, path);
        try (SeekableByteChannel sbc = Files.newByteChannel(path, APPEND, WRITE)) {
            for(String file: files) {
                System.out.println(String.format("Fichier='%s'", file));
                Path src = Paths.get(file);
                try (SeekableByteChannel sbcSrc = Files.newByteChannel(src)) {
                    copyFileBytes(sbcSrc, sbc, Files.size(src));
                }
            }
        }
    }
    public static void unarchive(String archive, String dir) throws IOException, FileHeaderCorruptedException {
        Path archPath = Paths.get(archive);
        try (SeekableByteChannel sbc = Files.newByteChannel(archPath)) {
            Path dirPath = dirPath = Paths.get(dir);;
            if(dir == null || dir.isEmpty()){
                String fileName = archPath.getFileName().toString();
                fileName = fileName.substring(0, fileName.lastIndexOf(".temp"));
                dirPath = Paths.get(archPath.toAbsolutePath().getParent().toString() + '\\' + fileName);
            }
            Files.createDirectory(dirPath);//creer le repertoire de destination des fichies desarchives
            System.out.println(dirPath);
            String fileHeader = getFileHeader(archPath);
            FileMetaData[] array = extractFileMetaData(fileHeader);
            sbc.position(sbc.position() + fileHeader.length() + 2);
            for(FileMetaData fmd: array){
                System.out.println(fmd);
                Path file = Paths.get(dirPath.toString() + '\\' + fmd.fileName);
                try (SeekableByteChannel sbcFile = Files.newByteChannel(file, CREATE, WRITE, TRUNCATE_EXISTING)) {
                    long nb = copyFileBytes(sbc, sbcFile, fmd.size);
                    System.out.println("Size:"+fmd.size+" Writed:"+nb);
                }
            }
        }

    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] files ={
                "target/classes/Mini-Projet CompressionFichier.pdf",
                "target/classes/ocean.jpg",
                "target/classes/001-al-fatihah.mp3",
                "target/classes/PGDC.java",
                "target/classes/mon_fichier.txt"
        };
        String path = "target/classes/archived.temp";
        try {
            System.out.println("Archivage du fichier:");
            archive(files, path);
            System.out.println("Desarchivage des fichiers:");
            unarchive(path,  "");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileHeaderCorruptedException e) {
            System.out.println(e);
        }

    }
}
