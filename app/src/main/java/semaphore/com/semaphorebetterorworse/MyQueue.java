package semaphore.com.semaphorebetterorworse;

import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by ziggypop on 3/5/16.
 */
public class MyQueue  {
    LinkedList<String> queue;
    int size;


    //don't make less than 2
    public MyQueue(int size){
        queue = new LinkedList<>();
        this.size = size;
    }

    public void add(String s){
        if (queue.size() >= size){
            queue.removeFirst();
        }
        queue.add(s);
    }

    public boolean isHomogeneous(){
        boolean isSame = true;
        String strarr[] = (String[]) queue.toArray();
        for (int i = 1; i < size; i++){
            if (! strarr[i-1].equals(strarr[i])){
                isSame = false;
            }
        }
        if (queue.size()<size){
            isSame = false;
        }
        return isSame;
    }

}
