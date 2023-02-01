/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.jvm.sandbox.moonbox.module.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link SPILoader} load spi
 * <p>
 *
 * @author zhaoyb1990
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SPILoader {

    public static <T> List<T> loadSPI(Class<T> spiType, ClassLoader classLoader) {
        ServiceLoader<T> loaded = ServiceLoader.load(spiType, classLoader);
        Iterator<T> spiIterator = loaded.iterator();
        List<T> target = Lists.newArrayList();
        while (spiIterator.hasNext()) {
            try {
                target.add(spiIterator.next());
            } catch (Throwable e) {
                log.error("Error load spi {} >>> ", spiType.getCanonicalName(), e);
            }
        }
        return target;
    }

}