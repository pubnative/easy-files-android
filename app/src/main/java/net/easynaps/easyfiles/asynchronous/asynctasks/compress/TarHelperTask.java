package net.easynaps.easyfiles.asynchronous.asynctasks.compress;

import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.utils.OnAsyncTaskFinished;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static net.easynaps.easyfiles.filesystem.compressed.CompressedHelper.SEPARATOR;

public class TarHelperTask extends CompressedHelperTask {

    private String filePath, relativePath;

    public TarHelperTask(String filePath, String relativePath, boolean goBack,
                         OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> l) {
        super(goBack, l);
        this.filePath = filePath;
        this.relativePath = relativePath;
    }

    @Override
    void addElements(ArrayList<CompressedObjectParcelable> elements) {
        TarArchiveInputStream tarInputStream = null;
        try {
            tarInputStream = new TarArchiveInputStream(new FileInputStream(filePath));

            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(SEPARATOR)) name = name.substring(0, name.length() - 1);

                boolean isInBaseDir = relativePath.equals("") && !name.contains(SEPARATOR);
                boolean isInRelativeDir = name.contains(SEPARATOR)
                        && name.substring(0, name.lastIndexOf(SEPARATOR)).equals(relativePath);

                if (isInBaseDir || isInRelativeDir) {
                    elements.add(new CompressedObjectParcelable(entry.getName(),
                            entry.getLastModifiedDate().getTime(), entry.getSize(), entry.isDirectory()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
