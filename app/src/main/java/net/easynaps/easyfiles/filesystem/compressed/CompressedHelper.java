package net.easynaps.easyfiles.filesystem.compressed;

import android.content.Context;

import net.easynaps.easyfiles.filesystem.compressed.extractcontents.Extractor;
import net.easynaps.easyfiles.filesystem.compressed.extractcontents.helpers.GzipExtractor;
import net.easynaps.easyfiles.filesystem.compressed.extractcontents.helpers.RarExtractor;
import net.easynaps.easyfiles.filesystem.compressed.extractcontents.helpers.SevenZipExtractor;
import net.easynaps.easyfiles.filesystem.compressed.extractcontents.helpers.TarExtractor;
import net.easynaps.easyfiles.filesystem.compressed.extractcontents.helpers.ZipExtractor;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.Decompressor;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers.GzipDecompressor;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers.RarDecompressor;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers.SevenZipDecompressor;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers.TarDecompressor;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers.ZipDecompressor;
import net.easynaps.easyfiles.utils.Utils;

import java.io.File;

public class CompressedHelper {

    /**
     * Path separator used by all Decompressors and Extractors.
     * e.g. rar internally uses '\' but is converted to "/" for the app.
     */
    public static final String SEPARATOR = "/";

    public static final String fileExtensionZip = "zip", fileExtensionJar = "jar", fileExtensionApk = "apk";
    public static final String fileExtensionTar = "tar";
    public static final String fileExtensionGzipTar = "tar.gz";
    public static final String fileExtensionRar = "rar";
    public static final String fileExtension7zip = "7z";

    /**
     * To add compatibility with other compressed file types edit this method
     */
    public static Extractor getExtractorInstance(Context context, File file, String outputPath,
                                                 Extractor.OnUpdate listener) {
        Extractor extractor;
        String type = getExtension(file.getPath());

        if (isZip(type)) {
            extractor = new ZipExtractor(context, file.getPath(), outputPath, listener);
        } else if (isRar(type)) {
            extractor = new RarExtractor(context, file.getPath(), outputPath, listener);
        } else if(isTar(type)) {
            extractor = new TarExtractor(context, file.getPath(), outputPath, listener);
        } else if(isGzippedTar(type)) {
            extractor = new GzipExtractor(context, file.getPath(), outputPath, listener);
        } else if (is7zip(type)) {
            extractor = new SevenZipExtractor(context, file.getPath(), outputPath, listener);
        } else {
            return null;
        }

        return extractor;
    }

    /**
     * To add compatibility with other compressed file types edit this method
     */
    public static Decompressor getCompressorInstance(Context context, File file) {
        Decompressor decompressor;
        String type = getExtension(file.getPath());

        if (isZip(type)) {
            decompressor = new ZipDecompressor(context);
        } else if (isRar(type)) {
            decompressor = new RarDecompressor(context);
        } else if(isTar(type)) {
            decompressor = new TarDecompressor(context);
        } else if(isGzippedTar(type)) {
            decompressor = new GzipDecompressor(context);
        } else if (is7zip(type)) {
            decompressor = new SevenZipDecompressor(context);
        } else {
            return null;
        }

        decompressor.setFilePath(file.getPath());
        return decompressor;
    }

    public static boolean isFileExtractable(String path) {
        String type = getExtension(path);

        return isZip(type) || isTar(type) || isRar(type) || isGzippedTar(type) || is7zip(type);
    }

    /**
     * Gets the name of the file without compression extention.
     * For example:
     * "s.tar.gz" to "s"
     * "s.tar" to "s"
     */
    public static String getFileName(String compressedName) {
        compressedName = compressedName.toLowerCase();
        if(isZip(compressedName) || isTar(compressedName) || isRar(compressedName) || is7zip(compressedName)) {
            return compressedName.substring(0, compressedName.lastIndexOf("."));
        } else if (isGzippedTar(compressedName)) {
            return compressedName.substring(0,
                    Utils.nthToLastCharIndex(2, compressedName, '.'));
        } else {
            return compressedName;
        }
    }

    private static boolean isZip(String type) {
        return type.endsWith(fileExtensionZip) || type.endsWith(fileExtensionJar)
                || type.endsWith(fileExtensionApk);
    }

    private static boolean isTar(String type) {
         return type.endsWith(fileExtensionTar);
    }

    private static boolean isGzippedTar(String type) {
         return type.endsWith(fileExtensionGzipTar);
    }

    private static boolean isRar(String type) {
        return type.endsWith(fileExtensionRar);
    }

    private static boolean is7zip(String type) {
        return type.endsWith(fileExtension7zip);
    }

    private static String getExtension(String path) {
        return path.substring(path.indexOf('.')+1, path.length()).toLowerCase();
    }

}
