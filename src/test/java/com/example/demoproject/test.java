package com.example.demoproject;

import cn.hutool.core.lang.func.VoidFunc0;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * https://blog.csdn.net/boweiqiang/article/details/109860950
 */
@Slf4j
public class test {
    public  String encAESmain (String password) {
        try {

            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec sks = new SecretKeySpec("abcdefghijklmn11".getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec("abcdefghijklmn11".getBytes());
            c.init(Cipher.ENCRYPT_MODE, sks , ivParameterSpec);
            // 加密
            byte[] bytes = c.doFinal(password.getBytes());
            Base64.encode(bytes);
            System.out.println(new String(Base64.encode(bytes)));
            return new String(bytes);
        } catch (Exception e) {
            //log.info(e.getMessage());
        }
        return "";
    }
    @Test
    public void  test (){
        try{
//            ImmutableMultimap.Builder<String, String> groupInfoBuilder = ImmutableMultimap.builder();
//            groupInfoBuilder.put("a","value");
//            Multimap multimap = groupInfoBuilder.build();
//
//            new ArrayList<String>(multimap.get("a")).forEach(a->{
//                for (String s : args[0].split(":")) {
//                    log.info("组信息：{}",s);
//                }
//            });
//            List<Integer> list = Arrays.asList(1,2,3,4,5,6);
//            list.stream().anyMatch((predicate)->{
//                if (predicate<=3){
//                    log.info("流信息{}",String.valueOf(predicate));
//                }
//                return false;
//            });
//            VoidFunc0 voidFunc0 = () ->list.parallelStream().filter(predicate->predicate>2).collect(Collectors.toList());
//            voidFunc0.call();

            List<String> list = Arrays.asList("123","456","789","1101","212121121","asdaa","3e3e3e","2321eew");
            Map<String,String> map = list.stream().collect(toImmutableMap(e -> e.substring(0,1),e -> e));
            System.out.println(map.toString() + "\n");


        }catch (Exception e){

        }

    }
    public  <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper) {
        final Supplier<ImmutableMap.Builder<K, V>> supplier = ImmutableMap.Builder::new;

        final BiConsumer<ImmutableMap.Builder<K, V>, T> accumulator = (b, t) -> b
                .put(keyMapper.apply(t), valueMapper.apply(t));


        final BinaryOperator<ImmutableMap.Builder<K, V>> combiner = (r, l) -> l
                .putAll(r.build());

        final Function<ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> finisher = ImmutableMap.Builder::build;

        return Collector.of(supplier, accumulator, combiner, finisher);
    }

    public  <T, P, C, V> Collector<T, Map<P, ImmutableListMultimap.Builder<C, V>>, ImmutableMap<P, ImmutableMultimap<C, V>>> toPartitionedImmutableMultimap(
            Function<? super T, ? extends P> parentKeyMapper,
            Function<? super T, ? extends C> childKeyMapper,
            Function<? super T, ? extends V> valueMapper) {
        final Supplier<Map<P, ImmutableListMultimap.Builder<C, V>>> supplier = HashMap::new;

        final BiConsumer<Map<P, ImmutableListMultimap.Builder<C, V>>, T> accumulator = (map, element)
                -> map.computeIfAbsent(
                                        parentKeyMapper.apply(element),x -> ImmutableListMultimap.builder()
                                       ).put(
                                            childKeyMapper.apply(element), valueMapper.apply(element)
                                            );

        final BinaryOperator<Map<P, ImmutableListMultimap.Builder<C, V>>> combiner = (
                l, r) -> {
            l.putAll(r);
            return l;
        };

        final Function<Map<P, ImmutableListMultimap.Builder<C, V>>, ImmutableMap<P, ImmutableMultimap<C, V>>> finisher = map -> map
                .entrySet()
                .stream()
                .collect(
                        toImmutableMap(Map.Entry::getKey, e -> e.getValue()
                                .build()));

        return Collector.of(supplier, accumulator, combiner, finisher);
    }
}
