import java.util.*;  // ✅ Add this import
import java.util.function.Function;
import java.util.stream.Collectors;

public class run {
    public static void main(String[] args){
        Function<String,String> run = (name) -> "My Name is :" + name;
        String s = run.apply("Devesh");
        System.out.println(s);
        List<String>names = Arrays.asList("jsjdj","Sss","dkkdjjd");
        List<String>result = names.stream()
                .filter(n -> n.length() > 3)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        result.forEach(name -> System.out.println(name));
    }


}
