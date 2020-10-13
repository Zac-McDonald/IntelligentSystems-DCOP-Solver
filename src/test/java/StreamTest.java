import java.util.*;
import java.util.stream.Collectors;

public class StreamTest {
    public static void main (String[] args) throws Exception {
        HashMap<Integer, Integer> map = new HashMap<>();
        ArrayList<Integer> values = new ArrayList<>();
        values.add(0); values.add(1); values.add(2); values.add(3); values.add(4);

        process(map, values);

        values.add(5);
        process(map, values);

        values.remove(1);
        process(map, values);
    }

    public static void process (HashMap<Integer, Integer> map, Collection<Integer> values) {
        List<Integer> a = values.stream().map(x -> {
            // Do stuff
            if (!map.containsKey(x)) {
                // Discovered
                System.out.println("\tDiscovered " + x);
            }

            map.put(x, x*2);

            return x;
        }).collect(Collectors.toList());

        System.out.println(map);
        map.keySet().removeIf(x -> {
            if (!values.contains(x)) {
                System.out.println("\tDropped " + x);
                return true;
            }
            return false;
        });
        System.out.println(map);
    }
}
