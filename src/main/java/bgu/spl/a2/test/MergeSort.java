/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {
    	if(array.length >1){
	    	int[] arr1 = new int[array.length /2 ];
	    	int[] arr2 = new int[array.length - arr1.length];
	    	for(int i = 0 ; i < array.length ; i++){
	    		if(i < arr1.length){
	    			arr1[i] = array[i];
	    		}else{
	    			arr2[i-arr1.length] = array[i];
	    		}
	    	}
	    	MergeSort task1= new MergeSort(arr1);
	    	MergeSort task2= new MergeSort(arr2);
	    	ArrayList<MergeSort> tasks = new ArrayList<MergeSort>();
	    	tasks.add(task1);
	    	tasks.add(task2);
	        this.whenResolved(tasks, ()->{
	        	int a1 [] = tasks.get(0).getResult().get();
	        	int a2 [] = tasks.get(1).getResult().get();
	        	int j = 0;
	        	int k = 0;
	        	for(int i = 0 ; i< array.length ; i++){
	        		if(k >= a2.length || (j<a1.length && a1[j]<a2[k])){
	        			array[i] = a1[j];
	        			j++;
	        		}
	        		else{
	        			array[i] = a2[k];
	        			k++;
	        		}
	        	}
	        	
	        	this.complete(array);
	        });
	    	spawn(task1,task2);
	    }else{
	    	this.complete(array);
	    }
    }

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 10000; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();
        MergeSort task = new MergeSort(array);
        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });

        l.await();
        pool.shutdown();
        
        //Tests
        /*boolean sorted = true;
        int i = 0;
        while(i<array.length-1 && sorted){
        	if(array[i]>array[i+1]){
        		sorted = false;
        	}
        	i++;
        }
        System.out.println(array.length);
        System.out.println(sorted);*/
    }

}
