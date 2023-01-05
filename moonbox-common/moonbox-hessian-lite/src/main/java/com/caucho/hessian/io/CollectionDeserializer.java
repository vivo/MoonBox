package com.caucho.hessian.io;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


public class CollectionDeserializer extends AbstractListDeserializer {

    private Class _type;

    public CollectionDeserializer(Class type) {
        _type = type;
    }

    public Class getType() {
        return _type;
    }

    public Object readList(AbstractHessianInput in, int length)
            throws IOException {
        Collection list = createList();

        in.addRef(list);

        /**
         * 解决序列化过程丢失属性的bug，对继承自Collection并扩展了新属性的类，对其新增属性反序列化。
         */
        /** begin **/
        try {
            Class clasz = list.getClass();

            //记录已经读过的子类属性，以防被同名父类属性覆盖
            Set<String> fieldNameSet = new HashSet<String>();

            // 从当前自定义List子类逐层向上处理，对各层属性进行反序列化
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

                Field[] fields = clasz.getDeclaredFields();
                for (Field field : fields) {
                    // 子类属性已被读取，不再读取同名父属性
                    if (fieldNameSet.contains(field.getName())) {
                        continue;
                    }
                    if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    boolean isAccessible = field.isAccessible();
                    if (!isAccessible) {
                        field.setAccessible(true);
                    }

                    Object val = in.readObject();
                    field.set(list, val);
                    field.setAccessible(isAccessible);

                    // 记录已记取的属性
                    fieldNameSet.add(field.getName());
                }
            }// end

            fieldNameSet.clear();

        } catch (IllegalAccessException e) {
            throw new IOException(e.getMessage());
        }
        /** end **/


        while (!in.isEnd())
            list.add(in.readObject());

        in.readEnd();

        return list;
    }

    public Object readLengthList(AbstractHessianInput in, int length)
            throws IOException {
        //先实例化
        Collection list = createList();

        in.addRef(list);

        /**
         * 解决序列化过程丢失属性的bug，对继承自Collection并扩展了新属性的类，对其新增属性反序列化。
         */
        /** begin **/
        try {
            Class clasz = list.getClass();

            //记录已经读过的子类属性，以防被同名父类属性覆盖
            Set<String> fieldNameSet = new HashSet<String>();

            // 从当前自定义List子类逐层向上处理，对各层属性进行反序列化
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
                //反射获得所有声明的字段
                Field[] fields = clasz.getDeclaredFields();
                for (Field field : fields) {

                    // 子类属性已被读取，不再读取同名父属性
                    if (fieldNameSet.contains(field.getName())) {
                        continue;
                    }
                    if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    boolean isAccessible = field.isAccessible();
                    if (!isAccessible) {
                        field.setAccessible(true);
                    }
                    //读取字段值
                    Object val = in.readObject();
                    //然后反射设置值
                    field.set(list, val);
                    field.setAccessible(isAccessible);

                    // 记录已记取的属性
                    fieldNameSet.add(field.getName());
                }
            }// end

            fieldNameSet.clear();

        } catch (IllegalAccessException e) {
            throw new IOException(e.getMessage());
        }
        /** end **/

        //写出的格式是这样的 先写类名  然后写当前类的字段值，然后父类字段值 (和子类同名的字段不写) 最后是列表值
        //所以读取的顺序是 先实例化,然后写入当前类的字段值，然后写入父类的字段值，最后写入列表值
        for (; length > 0; length--)
            list.add(in.readObject());

        return list;
    }

    private Collection createList()
            throws IOException {
        Collection list = null;

        if (_type == null)
            list = new ArrayList();
        else if (!_type.isInterface()) {
            try {
                list = (Collection) _type.newInstance();
            } catch (Exception e) {
            }
        }

        if (list != null) {
        } else if (SortedSet.class.isAssignableFrom(_type))
            list = new TreeSet();
        else if (Set.class.isAssignableFrom(_type))
            list = new HashSet();
        else if (List.class.isAssignableFrom(_type))
            list = new ArrayList();
        else if (Collection.class.isAssignableFrom(_type))
            list = new ArrayList();
        else {
            try {
                list = (Collection) _type.newInstance();
            } catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }
        }

        return list;
    }
}
