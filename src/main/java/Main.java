/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sanctum.ir.Configuration;
import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.mapreduce.MapReducer;
import java.io.IOException;

/**
 *
 * @author Matt
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        ThreadedDataLoader loader = new ThreadedDataLoader(10);
        boolean config = Configuration.loadConfiguration("config.cfg");
        MapReducer reducer = new MapReducer(0, 0, 5);
        
        if (config) {
            loader.loadData();
            reducer.mapreduce(loader);
            reducer.merge();
        } else {
            System.out.println("Failed to load config.");
        }
    }

}
