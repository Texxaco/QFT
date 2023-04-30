package net.contexx.qft.freezer.model;

import java.util.*;
import java.util.stream.Collectors;

public class Directory {
    public static final String DELIMITER = "/";

    protected Directory(Directory parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //attributes

    private Directory parent;

    public Directory getParent() {
        return parent;
    }

//    private void detach() {
//        parent = null;
//    }

    //__________________________________________________________________________________________________________
    //name

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    //__________________________________________________________________________________________________________
    //comment

    private String coment;

    public String getComment() {
        return coment;
    }

    public void setComment(String aComment) {
        coment = aComment;
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // model logic

    //__________________________________________________________________________________________________________
    // childs

    private final Map<String, Directory> childDirs = new HashMap<>();

    public Directory addDirectory(String name) {
        if(name == null || name.isEmpty()) throw new RuntimeException("No name is provided.");
        if(name.contains(DELIMITER)) throw new RuntimeException("Unsupported name '+name+' with delimiter ('"+ DELIMITER +"') is unsupported");

        final Directory result = new Directory(this, name);
        childDirs.put(name, result);
        return result;
    }

    public Collection<Directory> getDirectorys() {
        return childDirs.values();
    }

    public Directory getDirectory(String aName) {
        return childDirs.get(aName);
    }

    public boolean remove(Directory child) {
        for (Iterator<Directory> iterator = childDirs.values().iterator(); iterator.hasNext(); ) {
            if(iterator.next() == child) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    //__________________________________________________________________________________________________________
    // content

    private final Map<String, Content> contentElements = new HashMap<>();

    public Content addContent(String name) {
        final Content result = new Content(this, name);
        contentElements.put(name, result);
        return result;
    }

    public Collection<Content> getContentElements() {
        return contentElements.values();
    }

    public Content getContent(String name) {
        return contentElements.get(name);
    }

    public boolean remove(Content content) {
        for (Iterator<Content> iterator = contentElements.values().iterator(); iterator.hasNext(); ) {
            if(iterator.next() == content) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // path convenience

    public Directory locate(String subPath) {
        return find(subPath, false);
    }

    public Directory computeIfAbsent(String subPath) {
        return find(subPath, true);
    }

    private Directory find(String subPath, boolean createPathIfNessesary) {
        if(subPath == null || subPath.trim().isEmpty()) return this;

        List<String> pathElems = Arrays.stream(subPath.split(DELIMITER)).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        Directory actualDir = this;
        for (String pathElem : pathElems) {
            Directory child = actualDir.getDirectory(pathElem);
            if (child == null) {
                if (createPathIfNessesary) {
                    child = actualDir.addDirectory(pathElem);
                } else return null;
            }
            actualDir = child;
        }
        return actualDir;
    }

    public String toPath() {
        final String parentPath = parent.toPath();

        return (parentPath.isEmpty()) ? getName() : parentPath + DELIMITER + getName();
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // equals, hashcode and toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Directory)) return false;
        Directory directory = (Directory) o;
        return name.equals(directory.name) && Objects.equals(coment, directory.coment) && childDirs.equals(directory.childDirs) && contentElements.equals(directory.contentElements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coment, childDirs, contentElements);
    }
}
