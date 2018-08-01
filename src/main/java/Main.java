/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sanctum.ir.Configuration;
import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.mapreduce.MapReducer;

/**
 *
 * @author Matt
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ThreadedDataLoader loader = new ThreadedDataLoader(5);
        boolean config = Configuration.loadConfiguration("config.cfg");
        MapReducer reducer = new MapReducer();
        
        if (config) {
            loader.loadData();
            reducer.mapreduce(loader);
        } else {
            System.out.println("Failed to load config.");
        }
    }

}
