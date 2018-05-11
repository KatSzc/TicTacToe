package pl.sda.poznan;

import java.util.*;

public class Main2 {
    public static void main(String[] args) {
        List integer = Arrays.asList( new Integer[]{1, 2, 3} );
        List ints = Arrays.asList( new int[]{1, 2, 3} );
        System.out.println( ints.size() == integer.size() );
        Object object = new Object();

        TreeSet set = new TreeSet();
        set.add( "one" );
        set.add( "two" );
        set.add( "three" );
        set.add( "four" );
        set.add( "one" );

        Iterator it = set.iterator();
        while (it.hasNext()){
            System.out.println(it.next()+ "");
        }
    }
}
