import com.google.common.collect.Lists;
import com.kangpan.model.PageInfo;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * java8 进阶学习
 *
 * @author Kangpan
 * @date 2020/10/31 10:20
 */
public class Java8 {

    static List<People> list = Lists.newArrayList(
            new People(1L, "周杰伦", "男", 30, 12),
            new People(2L, "汪峰", "男", 30, 10),
            new People(3L, "刘亦菲", "女", 20, 15),
            new People(4L, "蔡依林", "女", 18, 18),
            new People(5L, "张杰", "男", 30, 20),
            new People(6L, "薛之谦", "男", 28, 30)
    );


    /**
     * 排序
     * 1. 按照薪资降序
     * 2. 按照年龄降序薪资升序
     * 3. 考虑存在 NULL 的数据
     * 4. 使用 Collections 排序
     */
    @Test
    public void ex1() {
        System.out.println("1. 按照薪资降序-------------------------------------------");
        {
            list.stream().sorted(Comparator.comparing(People::getSalary).reversed());
            list.forEach(System.out::println); // 没有改变原list的顺序
        }
        System.out.println("2. 按照年龄降序薪资升序-------------------------------------------");
        {
            list.stream().sorted(Comparator.comparing(People::getAge).reversed().thenComparing(People::getSalary));
        }
        System.out.println("3. 考虑存在 NULL 的数据-------------------------------------------");
        {
            list.add(null);
            list.sort(Comparator.nullsFirst(Comparator.comparing(People::getSalary))); // null 数据排前面
            list.sort(Comparator.nullsLast(Comparator.comparing(People::getSalary)));
            list.forEach(System.out::println); // 改变原list的顺序
        }
        System.out.println("4. 使用 Collections 排序-------------------------------------------");
        {
            Collections.sort(list.stream().filter(Objects::nonNull).collect(Collectors.toList()), Comparator.comparing(People::getSalary));
            list.forEach(System.out::println); // 改变原list的顺序
        }
    }

    /**
     * 分组
     * 1. map (男生集合 女生集合) 数量
     * 2. 男生的平均薪资
     * 3. 总数 平均 最大 最小 数量
     * 4. 不同年龄男生女生集合
     * 5. map(薪资最高的男生 薪资最高的女生 按薪资升序排序取最后一个)
     * 6. map(年龄的)
     */
    @Test
    public void ex2() {
        System.out.println("1. map (男生集合 女生集合) 数量-------------------------------------------");
        {
            Map<String, List<People>> map = list.stream().collect(Collectors.groupingBy(People::getSex));
            map.forEach((k, v) -> System.out.println(k + ":" + v));
            System.out.println(map.get("女").size());
            System.out.println(map.get("男").stream().count());
            Map<String, Long> map1 = list.stream().collect(Collectors.groupingBy(People::getSex, Collectors.counting()));
            map1.forEach((k, v) -> System.out.println(k + ":" + v));
        }
        System.out.println("2. 男生的平均薪资-------------------------------------------");
        {
            double avg = list.stream().filter(p -> p.getSex().equals("男")).collect(Collectors.averagingDouble(People::getSalary));
            System.out.println(avg);
        }
        System.out.println("3. 总数 平均 最大 最小 数量-------------------------------------------");
        {
            DoubleSummaryStatistics statistics = list.stream().collect(Collectors.summarizingDouble(People::getSalary));
            System.out.println(statistics.getAverage());
            System.out.println(statistics.getMax());
            System.out.println(statistics.getMin());
            System.out.println(statistics.getCount());
            System.out.println(statistics.getSum());
            list.stream().map(People::getSalary).reduce((s1, s2) -> s1 + s2).ifPresent(System.out::println);
            list.stream().map(People::getSalary).reduce(BinaryOperator.maxBy(Double::compareTo)).ifPresent(System.out::println);
            list.stream().map(People::getSalary).reduce(BinaryOperator.minBy(Double::compareTo)).ifPresent(System.out::println);
            list.stream().map(People::getSalary).max(Double::compareTo).ifPresent(System.out::println);
            list.stream().map(People::getSalary).min(Double::compareTo).ifPresent(System.out::println);

        }
        System.out.println("4. 不同年龄男生女生集合-------------------------------------------");
        {
            Map<Boolean, Map<Integer, List<People>>> map = list.stream().collect(
                    Collectors.partitioningBy(p -> p.getSex().equals("男"), Collectors.groupingBy(People::getAge)));
            map.get(true).get(30).forEach(System.out::println);
            map.get(false).get(20).forEach(System.out::println);

            Map<String, Map<Integer, List<People>>> map1 = list.stream().collect(
                    Collectors.groupingBy(People::getSex, Collectors.groupingBy(People::getAge)));
            map1.get("男").get(30).forEach(System.out::println);
        }
        System.out.println("5. map(薪资最高的男生 薪资最高的女生  按薪资升序排序取最后一个)-------------------------------------------");
        {
            Map<String, People> map = list.stream().sorted(Comparator.comparing(People::getSalary, Double::compareTo))
                    .collect(Collectors.toMap(People::getSex, Function.identity(), (p1, p2) -> p2));
            System.out.println(map.get("女"));
            System.out.println(map.get("男"));
        }
    }
}


class People {
    public Long id;
    public String name;
    public String sex;
    public int age;
    public double salary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public People(Long id, String name, String sex, int age, double salary) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "People{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                '}';
    }
}