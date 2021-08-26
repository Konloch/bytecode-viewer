package the.bytecode.club.bytecodeviewer.resources;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceTreeNode;
import the.bytecode.club.bytecodeviewer.util.LazyNameUtil;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Represents a loaded file in the form of a resource container
 * with all of the contents inside of it.
 *
 * @author Konloch
 */

public class ResourceContainer
{
    public File file;
    public String name;
    public File APKToolContents;
    public ResourceTreeNode treeNode;
    
    public Map<String, byte[]> resourceFiles = new LinkedHashMap<>();
    public Map<String, byte[]> resourceClassBytes = new LinkedHashMap<>();
    public Map<String, ClassNode> resourceClasses = new LinkedHashMap<>();
    
    public ResourceContainer(File f)
    {
        this(f, f.getName());
    }

    public ResourceContainer(File f, String name)
    {
        this.file = f;
        this.name = LazyNameUtil.applyNameChanges(name);
    }
    
    /**
     * Returns the ClassNode resource for the specified resource key (full name path)
     */
    public ClassNode getClassNode(String resourceName)
    {
        //fallback incase the resource contains the file extension
        if(resourceClassBytes.containsKey(resourceName))
            return resourceClasses.get(FilenameUtils.removeExtension(resourceName));
        
        //TODO check if this is even being called, it's probably not
        return resourceClasses.get(resourceName);
    }
    
    /**
     * Returns the unique 'working' name for container + resource look up.
     * This is used to look up a specific resource inside of this specific
     * container when you need to iterate through all opened containers
     */
    public String getWorkingName(String resourceName)
    {
        return file.getAbsolutePath() + ">" + resourceName;
    }
    
    /**
     * Returns the resource bytes for the specified resource key (full name path)
     */
    public byte[] getFileContents(String resourceName)
    {
        if(resourceClassBytes.containsKey(resourceName))
            return resourceClassBytes.get(resourceName);
        else
            return resourceFiles.get(resourceName);
    }
    
    /**
     * Updates the ClassNode reference on the resourceClass list and resourceClassBytes list
     */
    public ResourceContainer updateNode(String resourceKey, ClassNode newNode)
    {
        String classNodeKey = FilenameUtils.removeExtension(resourceKey);
        
        //update all classnode references for ASM
        if (resourceClasses.containsKey(classNodeKey))
        {
            resourceClasses.remove(classNodeKey);
            resourceClasses.put(classNodeKey, newNode);
        }
        
        //update the resource bytes
        if(resourceClassBytes.containsKey(resourceKey))
        {
            resourceClassBytes.remove(resourceKey);
            resourceClassBytes.put(resourceKey, ASMUtil.nodeToBytes(newNode));
        }
        return this;
    }
    
    /**
     * Clear this container's resources
     */
    public ResourceContainer clear()
    {
        resourceFiles.clear();
        resourceClassBytes.clear();
        resourceClasses.clear();
        return this;
    }
    
    /**
     * Updates this container's class node byte[] map
     */
    public ResourceContainer updateClassNodeBytes()
    {
        resourceClassBytes.clear();
        resourceClasses.forEach((s, cn) ->
                    resourceClassBytes.put(s+".class", ASMUtil.nodeToBytes(cn)));
        return this;
    }
    
    /**
     * Copy a resource container's resources into this container
     */
    public ResourceContainer copy(ResourceContainer copyFrom)
    {
        resourceFiles.putAll(copyFrom.resourceFiles);
        resourceClassBytes.putAll(copyFrom.resourceClassBytes);
        resourceClasses.putAll(copyFrom.resourceClasses);
        return this;
    }
}
