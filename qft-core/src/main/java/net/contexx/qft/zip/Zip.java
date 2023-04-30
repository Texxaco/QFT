package net.contexx.qft.zip;

import net.contexx.qft.freezer.model.Content;
import net.contexx.qft.freezer.model.Directory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip extends Directory {

    private static final String ZIP_DELIM = "/";

    public Zip() {
        super(null, "");
    }

    @Override
    public String toPath() {
        return "";
    }

//    //__________________________________________________________________________________________________________________
//    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//    //save
//
//    public void save(OutputStream out) throws IOException {
//        final ZipOutputStream zout = new ZipOutputStream(out);
//        try {
//            zout.setLevel(9);
//            if(this.getComment() != null) zout.setComment(this.getComment());
//
//            save("", this, zout);
//        } finally {
//            zout.close();
//        }
//    }
//
//    private void save(String aParent, Directory aParentDir, ZipOutputStream zout) throws IOException {
//        for (Directory theSubDir : aParentDir.getDirectorys()) {
//            final ZipEntry entry = new ZipEntry(getDirName(aParent, theSubDir));
//            if(theSubDir.getComment() != null) entry.setComment(theSubDir.getComment());
//            entry.setTime(System.currentTimeMillis());
//            zout.putNextEntry(entry);
//        }
//
//        saveFiles(aParent, aParentDir, zout);
//
//        for (Directory theSubDir : aParentDir.getDirectorys()) {
//            save(getDirName(aParent, theSubDir), theSubDir, zout);//recurse
//        }
//    }
//
//    private String getDirName(String aParent, Directory aDir) {
//        return aParent +  encodeName(aDir.getName()) + ZIP_DELIM;
//    }
//
//    private void saveFiles(String aParent, Directory aDir, ZipOutputStream zout) throws IOException {
//        for (Content theContent : aDir.getContentElements()) {
//            String theContentName = aParent + encodeName(theContent.getName());
//            final byte[] theBytes = theContent.content();
//            final ZipEntry entry = new ZipEntry(theContentName);
//            if(theContent.getComment() != null) entry.setComment(theContent.getComment());
//            zout.putNextEntry(entry);
//            zout.write(theBytes);
//            zout.closeEntry();
//        }
//    }
//    //__________________________________________________________________________________________________________________
//    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//    //read
//
//    public void read(InputStream in) throws IOException {
//        final ZipInputStream zin = new ZipInputStream(in);
//
//        for (ZipEntry entry = zin.getNextEntry(); entry != null; entry = zin.getNextEntry()) {
//            final String name = entry.getName();
//            if (!name.endsWith(ZIP_DELIM)) {
//                final int theIndex = name.lastIndexOf(ZIP_DELIM);
//                final String filename = name.substring(theIndex+1);
//                if (name.contains(ZIP_DELIM)) {
//
//                    final String path = name.substring(0, theIndex);
//                    final StringBuilder decodePath = new StringBuilder(name.length());
//                    final StringTokenizer tokenizer = new StringTokenizer(path, ZIP_DELIM, false);
//                    while(tokenizer.hasMoreTokens()) {
//                        decodePath.append(DELIM).append(decodeName(tokenizer.nextToken()));
//                    }
//                    locate(decodePath.toString(), true).addContent(decodeName(filename)).setData(readContent(zin));
//                } else {
//                    this.addContent(filename).setData(readContent(zin));
//                }
//            }
//        }
//    }
//
//    private final byte[] buffer = new byte[4048];
//    private byte[] readContent(ZipInputStream in) throws IOException {
//        final ByteArrayOutputStream theResult = new ByteArrayOutputStream();
//
//        try {
//            for (int len;(len = in.read(buffer)) > 0;) theResult.write(buffer, 0, len);
//        } finally {
//            theResult.close();
//        }
//
//        return theResult.toByteArray();
//    }
//
//    //__________________________________________________________________________________________________________________
//    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//    //name conversion
//
//    protected String encodeName(String aName) {
//        final StringBuilder theResult = new StringBuilder(aName.length());
//        char[] theCharArray = aName.toCharArray();
//        for (char c : theCharArray) {
//            switch (c) {
//                case '_': theResult.append("__"); break;
//                case '/': theResult.append("_d"); break;
//                case '\\': theResult.append("_b"); break;
//                default: theResult.append(c);
//            }
//        }
//
//        return theResult.toString();
//    }
//
//    protected String decodeName(String aName) {
//        final StringBuilder theResult = new StringBuilder(aName.length());
//
//        final char[] cha = aName.toCharArray();
//        for (int i = 0; i < cha.length; i++) {
//            char c = cha[i];
//            switch (c) {
//                case '_':
//                    if (i == 0) break;
//                    if(cha[i-1] == '_') {
//                        theResult.append('_');
//                        cha[i] = '#';
//                    }
//                    break;
//                case 'd':
//                    if(i > 0 && cha[i-1] == '_') {
//                        theResult.append('/');
//                        cha[i] = '#';
//                    } else {
//                        theResult.append(c);
//                    }
//                    break;
//                case 'b':
//                    if(i > 0 && cha[i-1] == '_') {
//                        theResult.append('\\');
//                        cha[i] = '#';
//                    } else {
//                        theResult.append(c);
//                    }
//                    break;
//                default : theResult.append(c);
//            }
//        }
//
//        return theResult.toString();
//    }
}
