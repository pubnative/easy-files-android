package net.easynaps.easyfiles.adapters.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.ui.icons.Icons;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.Utils;

import java.io.File;
import java.util.Calendar;

public class LayoutElementParcelable implements Parcelable {

    private static final String CURRENT_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    public final int filetype;
    public final IconDataParcelable iconData;
    public final String title;
    public final String desc;
    public final String permissions;
    public final String symlink;
    public final String size;
    public final boolean isDirectory;
    public final long date, longSize;
    public final String date1;
    public final boolean header;

    //same as hfile.modes but different than openmode in Main.java
    private OpenMode mode = OpenMode.FILE;

    public LayoutElementParcelable(String title, String path, String permissions,
                                   String symlink, String size, long longSize, boolean header,
                                   String date, boolean isDirectory, boolean useThumbs) {
        filetype = Icons.getTypeOfFile(path, isDirectory);
        @DrawableRes int fallbackIcon = Icons.loadMimeIcon(path, isDirectory);

        if(useThumbs) {
            if (filetype == Icons.IMAGE || filetype == Icons.VIDEO || filetype == Icons.APK) {
                this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_FROMFILE, path, fallbackIcon);
            } else {
                this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_RES, fallbackIcon);
            }
        } else {
            this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_RES, fallbackIcon);
        }

        this.title = title;
        this.desc = path;
        this.permissions = permissions.trim();
        this.symlink = symlink.trim();
        this.size = size;
        this.header = header;
        this.longSize=longSize;
        this.isDirectory = isDirectory;
        if (!date.trim().equals("")) {
            this.date = Long.parseLong(date);
            this.date1 = Utils.getDate(this.date, CURRENT_YEAR);
        } else {
            this.date = 0;
            this.date1 = "";
        }
    }

    public LayoutElementParcelable(String path, String permissions, String symlink,
                                   String size, long longSize, boolean isDirectory, boolean header,
                                   String date, boolean useThumbs) {
        this(new File(path).getName(), path, permissions, symlink, size, longSize, header, date, isDirectory, useThumbs);

    }

    public OpenMode getMode() {
        return mode;
    }

    public void setMode(OpenMode mode) {
        this.mode = mode;
    }

    public HybridFileParcelable generateBaseFile() {
        HybridFileParcelable baseFile=new HybridFileParcelable(desc, permissions, date, longSize, isDirectory);
        baseFile.setMode(mode);
        baseFile.setName(title);
        return baseFile;
    }

    public boolean hasSymlink() {
        return symlink != null && symlink.length() != 0;
    }

    @Override
    public String toString() {
        return title + "\n" + desc;
    }

    public LayoutElementParcelable(Parcel im) {
        filetype = im.readInt();
        iconData = im.readParcelable(IconDataParcelable.class.getClassLoader());
        title = im.readString();
        desc = im.readString();
        permissions = im.readString();
        symlink = im.readString();
        int j = im.readInt();
        date = im.readLong();
        int i = im.readInt();
        header = i != 0;
        isDirectory = j != 0;
        date1 = im.readString();
        size = im.readString();
        longSize=im.readLong();
    }

    @Override
    public int describeContents() {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p1, int p2) {
        p1.writeInt(filetype);
        p1.writeParcelable(iconData, 0);
        p1.writeString(title);
        p1.writeString(desc);
        p1.writeString(permissions);
        p1.writeString(symlink);
        p1.writeInt(isDirectory?1:0);
        p1.writeLong(date);
        p1.writeInt(header ? 1 : 0);
        p1.writeString(date1);
        p1.writeString(size);
        p1.writeLong(longSize);
    }

    public static final Parcelable.Creator<LayoutElementParcelable> CREATOR =
            new Parcelable.Creator<LayoutElementParcelable>() {
                public LayoutElementParcelable createFromParcel(Parcel in) {
                    return new LayoutElementParcelable(in);
                }

                public LayoutElementParcelable[] newArray(int size) {
                    return new LayoutElementParcelable[size];
                }
            };

}
