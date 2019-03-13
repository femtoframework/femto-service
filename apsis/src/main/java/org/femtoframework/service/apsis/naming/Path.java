package org.femtoframework.service.apsis.naming;


import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import javax.naming.InvalidNameException;
import javax.naming.Name;

/**
 * 文件路径或者其它简单分隔符的名字<br>
 * 它是对<code>javax.naming.Name</code>的简单实现<br>
 * 比如： /org/bolango/jade/naming/Path<br>
 * org.bolango.jade.naming.Path<br>
 *
 * @author fengyun
 * @see javax.naming.Name
 */
public class Path implements Name
{
    /**
     * 起始级别
     */
    private transient int off;

    /**
     * 拥有级别的数目,不等同于<code>groups.length</code><br>
     * <br>
     * /org/bolango/jade/naming/Path --> 是4<br>
     * org/bolango/jade/naming/ --> 是3<br>
     */
    private transient int size;

    /**
     * 格式化后产生的数组<br>
     * <p/>
     * 比如:   /org/bolango/jade/naming/Path <br>
     * --> {"", "naisa", "jade", "naming", "Path" }<br>
     */
    private transient String[] groups;

    /**
     * 是否带有前缀 sep
     */
    private transient boolean hasPrefixSep = false;

    /**
     * 是否带有后缀 sep
     */
    private transient boolean hasSuffixSep = false;

    /**
     * 分隔符('.' 或 '/' 等)
     */
    private char sep;

    /**
     * 实际的路径
     */
    private String path;

    /**
     * 根据数组和分隔符构造
     *
     * @param groups 数组
     * @param sep    分隔符
     * @param path   最初的路径
     */
    protected Path(String[] groups, char sep, String path)
    {
        this(groups, 0, groups.length, sep, path, false, false);
    }

    /**
     * 根据数组和分隔符构造
     *
     * @param groups       数组
     * @param off          起始级别
     * @param size         级数
     * @param sep          分隔符
     * @param path         最初的路径
     * @param hasPrefixSep 是否拥有分隔符前缀
     * @param hasSuffixSep 是否拥有分隔符后缀
     */
    protected Path(String[] groups, int off, int size,
                   char sep, String path,
                   boolean hasPrefixSep, boolean hasSuffixSep)
    {
        this.groups = groups;
        this.sep = sep;
        this.path = path;
        this.off = off;
        this.size = size;
        this.hasPrefixSep = hasPrefixSep;
        this.hasSuffixSep = hasSuffixSep;
    }

    /**
     * 根据字符串构造
     *
     * @param path 路径
     * @param sep  分隔符
     * @throws NullPointerException 如果<code>path == null</code>
     */
    public Path(String path, char sep)
    {
        if (path == null) {
            throw new NullPointerException("Path is null");
        }
        this.path = path;
        this.sep = sep;
        parse0(path, sep);
    }

    /**
     * 解析路径
     *
     * @param path 路径
     * @param sep  分隔符
     * @
     */
    protected void parse0(String path, char sep)
    {
        if (path.length() == 0) {
            this.groups = DataUtil.EMPTY_STRING_ARRAY;
            this.off = this.size = 0;
        }
        else {
            this.groups = DataUtil.toStrings(path, sep);
            this.hasPrefixSep = path.charAt(0) == sep;
            this.hasSuffixSep = path.charAt(path.length() - 1) == sep;
            this.off = hasPrefixSep ? 1 : 0;
            this.size = groups.length - (hasSuffixSep ? off + 1 : off);
        }
    }

    /**
     * 返回路径
     *
     * @return String 路径
     */
    public String toString()
    {
        return path;
    }

    /**
     * 判断是否等价
     *
     * @param obj 与之判断的对象
     * @return [true|false] 是否等价
     */
    public boolean equals(Object obj)
    {
        return (obj != null &&
                obj instanceof Path &&
                path.equals(((Path)obj).path));
    }

    /**
     * 返回哈希码
     *
     * @return int 哈希码
     */
    public int hashCode()
    {
        return path.hashCode();
    }

    /**
     * 创建一份拷贝
     *
     * @return [Object] 路径的拷贝
     */
    public Object clone()
    {
        return (new Path(groups, sep, path));
    }

    /**
     * 比较是否等价
     *
     * @param obj
     * @return 如果 <code> this.equals(obj)</code> 返回<code>0</code>
     *         否则返回<code>-1</code>
     */
    public int compareTo(Object obj)
    {
        return equals(obj) ? 0 : -1;
    }

    /**
     * 返回级数
     *
     * @return 级数
     */
    public int size()
    {
        return size;
    }

    /**
     * 返回分隔符
     *
     * @return 分隔符
     */

    public char getSeparator()
    {
        return sep;
    }

    /**
     * 是否为零级
     *
     * @return [true|flase] <code> size == 0 </code>
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * 返回目录名的列举
     *
     * @return java.util.Enumeration 列举
     */
    public Enumeration<String> getAll()
    {
        return ArrayUtil.enumerate(groups, off, size);
    }

    /**
     * 返回给定位置的目录名
     *
     * @param level 级别  [0,size)
     * @throws ArrayIndexOutOfBoundsException 如果level越界
     */
    public String get(int level)
    {
        ArrayUtil.indexCheck(0, size, level);
        return groups[off + level];
    }

    /**
     * 创建以 0 - Level 级的前缀
     *
     * @param level 级别，[0,size)
     * @return 包含前缀的路径
     * @throws ArrayIndexOutOfBoundsException 如果level越界
     */
    public Name getPrefix(int level)
    {
        ArrayUtil.indexCheck(0, size, level);
        int sz = level + 1;
        String prefix = StringUtil.toString(groups, off, sz, sep);
        boolean preSep = hasPrefixSep;
        boolean sufSep = level == size && hasSuffixSep;
        if (preSep) {
            prefix = sep + prefix;
        }
        if (sufSep) {
            prefix = prefix + sep;
        }
        return new Path(groups, off, sz, sep, prefix, preSep, sufSep);
    }

    /**
     * 创建以 Level - size()级的后缀
     *
     * @param level 级别，[0,size)
     * @return 包含后缀的路径
     * @throws ArrayIndexOutOfBoundsException 如果level越界
     */
    public Name getSuffix(int level)
    {
        ArrayUtil.indexCheck(0, size, level);
        int sz = size - level;
        String suffix = StringUtil.toString(groups, off + level, sz, sep);
        boolean preSep = level == 0 ? hasPrefixSep : false;
        boolean sufSep = hasSuffixSep;
        if (preSep) {
            suffix = sep + suffix;
        }
        if (sufSep) {
            suffix = suffix + sep;
        }
        return new Path(groups, off + level, sz, sep, suffix, preSep, sufSep);
    }

    /**
     * 判断路径是否已给定的名字为前缀
     *
     * @param name 前缀
     * @return true 如果给定的名字是<code>Path</code>
     *         并且是当前路径的前缀返回<code>true</code>
     *         如果名字参数是<code>null<code>返回<code>false</code>
     */
    public boolean startsWith(Name name)
    {
        if (name == null) {
            return false;
        }

        if (name instanceof Path) {
            Path p = (Path)name;
            int sz = p.size();
            boolean start = sep == p.sep && size >= sz;
            if (start) {
                start = ArrayUtil.matches(groups, off, p.groups,
                        p.off, sz);
            }
            return start;
        }
        else {
            return false;
        }
    }

    /**
     * 判断路径是否已给定的名字为前缀
     *
     * @param name 前缀名字
     * @return true|false 如果是当前路径的前缀返回<code>true</code>
     *         如果参数是<code>null<code>返回<code>false</code>
     */
    public boolean startsWith(String name)
    {
        if (name == null) {
            return false;
        }
        return startsWith(new Path(name, sep));
    }


    /**
     * 判断路径是否已给定的名字为后缀
     *
     * @param name 后缀名字
     * @return true 如果给定的名字是<code>Path</code>
     *         并且是当前路径的后缀返回<code>true</code>
     *         如果名字参数是<code>null<code>返回<code>false</code>
     */
    public boolean endsWith(Name name)
    {
        if (name == null) {
            return false;
        }

        if (name instanceof Path) {
            Path p = (Path)name;
            int sz = p.size();
            boolean start = sep == p.sep && size >= sz;
            if (start) {
                start = ArrayUtil.matches(groups, off + (size - sz), p.groups,
                        p.off, sz);
            }
            return start;
        }
        else {
            return false;
        }
    }

    /**
     * 判断路径是否已给定的名字为后缀
     *
     * @param name 后缀
     * @return true|false 如果是当前路径的后缀返回<code>true</code>
     *         如果参数是<code>null<code>返回<code>false</code>
     */
    public boolean endsWith(String name)
    {
        if (name == null) {
            return false;
        }
        return endsWith(new Path(name, sep));
    }

    /**
     * 将当前路径跟<code>n</code>组成新的路径<br>
     * 如果给定的分隔符跟当前的相等，则产生相应级别的路径<br>
     * 否则，将<code>n</code>作为一级来看待<br>
     * 比如：当前的路径是 "/org/bolango/jade"<br>
     * <code> #addAll(n["naming"]); // return /org/bolango/jade/naming</code><br>
     * <code> #addAll(n["naming.properties"]); // return /org/bolango/jade/naming.properties 分隔符是'/'</code><br>
     *
     * @param suffix 名字
     */
    public Name addAll(Name suffix) throws InvalidNameException
    {
        return addAll(size - 1, suffix);
    }

    /**
     * 将当前路径前level级跟<code>n</code>组成新的路径<br>
     * 如果给定的分隔符跟当前的相等，则产生相应级别的路径<br>
     * 否则，将<code>n</code>作为一级来看待<br>
     * 比如：当前的路径是 "/org/bolango/jade"<br>
     * <code> #addAll(1, n["naming"]); // return /org/bolango/jade/naming</code><br>
     * <code> #addAll(1, n["naming.properties"]); // return /org/bolango/jade/naming.properties 分隔符是'/'</code><br>
     *
     * @param level 级别
     * @param n     名字
     */
    public Name addAll(int level, Name n)
            throws InvalidNameException
    {
        if (!(n instanceof Path)) {
            throw new InvalidNameException("n is not a Path");
        }

        ArrayUtil.indexCheck(0, size, level);

        Path p = (Path)n;
        boolean sameSep = sep == p.sep;
        int sz = level + 1 + (sameSep ? p.size() : 1);
        String[] grps = new String[sz];
        System.arraycopy(groups, off, grps, 0, level + 1);
        if (sameSep) {
            System.arraycopy(p.groups, p.off, grps, level + 1, p.size());
        }
        else {
            grps[sz - 1] = p.toString();
        }

        String prefix = StringUtil.toString(grps, 0, sz, sep);
        boolean preSep = hasPrefixSep;
        boolean sufSep = sameSep ? p.hasSuffixSep : false;
        if (preSep) {
            prefix = sep + prefix;
        }
        if (sufSep) {
            prefix = prefix + sep;
        }
        return new Path(grps, 0, sz, sep, prefix, preSep, sufSep);
    }

    /**
     * 将当前路径跟<code>group</code>组成新的路径<br>
     * 比如：当前的路径是 "/org/bolango/jade"<br>
     * <code> #addAll("naming"); // return /org/bolango/jade/naming</code><br>
     * <code> #addAll("naming.properties"); // return /org/bolango/jade/naming.properties 分隔符是'/'</code><br>
     *
     * @param group 单级字符串
     */
    public Name add(String group) throws InvalidNameException
    {
        return add(size - 1, group);
    }

    /**
     * 将当前路径前level级跟<code>group</code>组成新的路径<br>
     * 比如：当前的路径是 "/org/bolango/jade"<br>
     * <code> #addAll("naming"); // return /org/bolango/jade/naming</code><br>
     * <code> #addAll("naming.properties"); // return /org/bolango/jade/naming.properties 分隔符是'/'</code><br>
     *
     * @param group 单级字符串
     */
    public Name add(int level, String group) throws InvalidNameException
    {
        ArrayUtil.indexCheck(0, size, level);
        if (group == null || group.length() == 0) {
            return (Name)this.clone();
        }

        int sz = level + 2;
        String[] grps = new String[sz];
        System.arraycopy(groups, off, grps, 0, level + 1);
        grps[level + 1] = group;
        String prefix = concat(path, sep, group);
        boolean preSep = hasPrefixSep;
        if (preSep) {
            prefix = sep + prefix;
        }
        return new Path(grps, 0, sz, sep, prefix, preSep, false);
    }

    /**
     * 暂时不支持
     *
     * @throws InvalidNameException 暂时不支持
     */
    public Object remove(int level) throws InvalidNameException
    {
        throw new InvalidNameException("Unsupported");
    }

    /**
     * 输出
     *
     * @param oos
     * @see java.io.Serializable
     */
    private void writeObject(ObjectOutputStream oos)
            throws IOException
    {
        oos.writeUTF(path);
        oos.writeChar(sep);
    }

    /**
     * 输入
     *
     * @param ois
     * @see java.io.Serializable
     */
    private void readObject(ObjectInputStream ois)
            throws IOException
    {
        path = ois.readUTF();
        sep = ois.readChar();
        parse0(path, sep);
    }

    /**
     * 解析路径
     *
     * @param path 要解析的路径
     * @param sep  分隔符
     * @throws NullPointerException 如果<code>path == null</code>
     */
    public static Path parse(String path, char sep)
    {
        if (path == null) {
            throw new NullPointerException("Path is null");
        }

        Path name = new Path(path, sep);
        return name;
    }

    /**
     * 将两个路径合并起来
     *
     * @param path      路径
     * @param separator 分隔符
     * @param file      文件
     */
    public static String concat(String path, String separator, String file)
    {
        StringBuilder sb = new StringBuilder(path.length()
                + separator.length()
                + file.length());
        sb.append(path).append(separator);
        sb.append(file);
        return sb.toString();
    }

    /**
     * 将两个路径合并起来
     *
     * @param path 路径
     * @param sep  分隔符
     * @param file 文件
     */
    public static String concat(String path, char sep, String file)
    {
        StringBuilder sb = new StringBuilder(path.length() + file.length() + 1);
        sb.append(path).append(sep);
        sb.append(file);
        return sb.toString();
    }
//
///*
//   下面是与标准的Name的比较
//*/
//    public static void main(String[] args) throws Exception
//    {
//        Path path = new Path(args[0], args[1].charAt(0));
//        System.out.println("Path=" + path);
//        System.out.println("Group[0]=" + path.get(1));
//        System.out.println("Prefix[0]=" + path.getPrefix(0));
//        System.out.println("Prefix[1]=" + path.getPrefix(1));
//        System.out.println("Suffix[0]=" + path.getSuffix(0));
//        System.out.println("Suffix[1]=" + path.getSuffix(1));
//        System.out.println("Start With=" + path.startsWith(args[2]));
//
//        Properties props = new Properties();
//        props.setProperty("jndi.syntax.separator", args[1]);
//        props.setProperty("jndi.syntax.direction", "left_to_right");
//        Name name = new javax.naming.CompoundName(args[0], props);
//
//        System.out.println("Path=" + name);
//        System.out.println("Group[0]=" + name.get(1));
//        System.out.println("Prefix[0]=" + name.getPrefix(0));
//        System.out.println("Prefix[1]=" + name.getPrefix(1));
//        System.out.println("Suffix[0]=" + name.getSuffix(0));
//        System.out.println("Suffix[1]=" + name.getSuffix(1));
//        System.out.println("Start With="
//                           + name.startsWith(new javax.naming.CompoundName(args[2], props)));
//    }
}

