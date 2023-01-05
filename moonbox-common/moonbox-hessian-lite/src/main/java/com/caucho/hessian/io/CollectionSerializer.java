package com.caucho.hessian.io;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Serializing a JDK 1.2 Collection.
 */
public class CollectionSerializer extends AbstractSerializer {
    private boolean _sendJavaType = true;

    /**
     * Set true if the java type of the collection should be sent.
     */
    public void setSendJavaType(boolean sendJavaType) {
        _sendJavaType = sendJavaType;
    }

    /**
     * Return true if the java type of the collection should be sent.
     */
    public boolean getSendJavaType() {
        return _sendJavaType;
    }

    public void writeObject(Object obj, AbstractHessianOutput out)
            throws IOException {
        if (out.addRef(obj))
            return;

        Collection list = (Collection) obj;

        Class cl = obj.getClass();
        boolean hasEnd;

        if (cl.equals(ArrayList.class)
                || !_sendJavaType
                || !Serializable.class.isAssignableFrom(cl))
            hasEnd = out.writeListBegin(list.size(), null);
        else
            hasEnd = out.writeListBegin(list.size(), obj.getClass().getName());

        /**
         * 修改序列化过程丢失属性的bug, 对继承自Collection并扩展了新属性的类，对其新增属性序列化。
        /** begin **/
        try {
            Class clasz = list.getClass();

            //记录已经写过的子类属性，以防被同名父类属性覆盖
            Set<String> fieldNameSet = new HashSet<String>();

            // 从当前自定义List子类逐层向上处理，对各层属性进行序列化
            for (; !clasz.getName().startsWith("java."); clasz = clasz.getSuperclass()) {

                // 如果当前类直接实现了List或Set接口，则不对其元素进行读写
                boolean impListOrSet = false;
                for (Class c : clasz.getInterfaces()) {
                    if (List.class.equals(c) | Set.class.equals(c) | SortedSet.class.equals(c) | Collection.class.equals(c)) {
                        impListOrSet = true;
                        break;
                    }
                }
                if (impListOrSet) {
                    continue;
                }

                // 如果当前类直接继承AbstractCollection/AbstractList/ABstractSet类，则不对其元素进行读写
                Class sc = clasz.getSuperclass();
                if (AbstractList.class.equals(sc) | AbstractSet.class.equals(sc) | AbstractCollection.class.equals(sc)) {
                    continue;
                }

                //获得所有的字段
                Field[] fields = clasz.getDeclaredFields();
                for (Field field : fields) {

                    // 子类属性已被写入，不再写入同名父属性
                    if (fieldNameSet.contains(field.getName())) {
                        continue;
                    }
                    //transient和静态属性不读写
                    if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    boolean isAccessible = field.isAccessible();
                    //设置为可访问
                    if (!isAccessible) {
                        field.setAccessible(true);
                    }
                    //获得字段值
                    Object val = field.get(list);

                    out.writeObject(val);
                    field.setAccessible(isAccessible);

                    // 记录已写过的属性
                    fieldNameSet.add(field.getName());
                }
            }

            fieldNameSet.clear();

        } catch (IllegalAccessException e) {
            throw new IOException(e.getMessage());
        }
        //写出的格式是这样的 先写类名  然后写当前类的字段值，然后父类字段值 (和子类同名的字段不写) 最后是列表值
        /** end **/


        //写列表值
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Object value = iter.next();

            out.writeObject(value);
        }

        if (hasEnd)
            out.writeListEnd();
    }
}
