package fr.julienj.carnetentretien.data;

import java.util.ArrayList;

/**
 * Created by JulienJ on 16/07/2017.
 */

public class ContainerData {
    private static final ContainerData ourInstance = new ContainerData();


    public double myLatitude;
    public double myLongitude;
    public String codeScanned;
    public boolean isInternetAlive;


    public static ContainerData getInstance() {
        return ourInstance;
    }

    private ContainerData() {
        myLatitude=0;
        myLongitude=0;
        codeScanned="";
        isInternetAlive=false;


    }

}
