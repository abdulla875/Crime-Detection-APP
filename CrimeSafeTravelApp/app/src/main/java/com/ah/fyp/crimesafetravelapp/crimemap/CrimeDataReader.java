package com.ah.fyp.crimesafetravelapp.crimemap;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ah.fyp.crimesafetravelapp.model.CrimeAmount;
import com.ah.fyp.crimesafetravelapp.model.CrimeCategory;
import com.ah.fyp.crimesafetravelapp.model.CrimeType;
import com.ah.fyp.crimesafetravelapp.model.DashboardData;
import com.ah.fyp.crimesafetravelapp.model.Location;
import com.ah.fyp.crimesafetravelapp.model.MostCrime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public class CrimeDataReader {

    private List<Location> items = new ArrayList<Location>();
    private int roadCrimeAmount;

    public List<Location> returnData(JSONArray jsonArray) throws JSONException, IOException {
        List<Location> items = new ArrayList<Location>();
        List<CrimeAmount> lcs = convertJsonArrayToCrimeAmountLists(jsonArray);
        addCrimeAmountToItemList(items, lcs);
        return items;

    }

    public List<Location> filteredCrimeData(JSONArray jsonArray) throws JSONException, IOException {
        List<Location> items = new ArrayList<Location>();
        List<CrimeAmount> crimeAmounts = convertJsonArrayToCrimeAmountLists(jsonArray);
        List<CrimeAmount> crimeAmountList = extractCrimeFromOriginalCrimeDataList(crimeAmounts);
        addCrimeAmountToItemList(items, crimeAmountList);
        return items;

    }


    private void addCrimeAmountToItemList(List<Location> items, List<CrimeAmount> lcs) {
        for (int i = 0; i < lcs.size(); i++) {
            String title = null;
            String snippet = null;
            double lat = Double.parseDouble(lcs.get(i).getLatitude());
            double lng = Double.parseDouble(lcs.get(i).getLongitude());
            if (!lcs.get(i).getArea().isEmpty()) {
                title = lcs.get(i).getArea();
            }
            roadCrimeAmount = lcs.get(i).getAmountOfCrime();

            List<CrimeType> crimeTypeList = new ArrayList<>();
            for(int x = 0; x < lcs.get(i).getCrimeType().size(); x++){
                crimeTypeList.add(lcs.get(i).getCrimeType().get(x));
                int amount = lcs.get(i).getCrimeType().get(x).getAmount();
                snippet= Arrays.toString(crimeTypeList.toArray());
            }


            items.add(new Location(lat, lng, title, snippet,crimeTypeList, roadCrimeAmount));
        }
    }

    public List<CrimeAmount> extractCrimeFromOriginalCrimeDataList(List<CrimeAmount> lcs) {
        List<CrimeAmount> newCrimeAmountList = new ArrayList<>(lcs);

        for(int i = 0; i< lcs.size(); i++) {
            for(int j = 0; j< lcs.get(i).getCrimeType().size(); j++)
            {
                if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type1.name)){
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                } else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type2.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                } else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type3.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                }else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type4.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                }else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type5.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                }else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type6.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                }else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type7.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                }else if(lcs.get(i).getCrimeType().get(j).getCtype().equals(CrimeCategory.type8.name)) {
                    newCrimeAmountList.get(i).getCrimeType().remove(j);
                    j--;
                }
            }
        }
        for(int i =0;i<newCrimeAmountList.size(); i++) {
            if(newCrimeAmountList.get(i).getCrimeType().isEmpty()) {
                newCrimeAmountList.remove(i);
                i--;
            }

        }
        return newCrimeAmountList;
    }


    private List<CrimeAmount> convertJsonArrayToCrimeAmountLists(JSONArray jsonArray) {
        Type collectionType = new TypeToken<List<CrimeAmount>>() {
        }.getType();
        return new Gson()
                .fromJson(String.valueOf((jsonArray)), collectionType);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DashboardData rodeWithHighestCrime(List<Location> newitems) {
        String highestCrimeStreet = "";
        String lowestCrimeStreet = "";
        int maxCrimeRoadAmount;
        int totalAmountOfCrime;
        int mainCrimeRoadAmount;

        totalAmountOfCrime =  newitems.stream().mapToInt(Location::getmRoadCrimeAmount).sum();
        OptionalInt maxCrimeRoad = newitems.stream().mapToInt(Location::getmRoadCrimeAmount).max();
        maxCrimeRoadAmount = maxCrimeRoad.getAsInt();

        for(int i =0; i<newitems.size(); i++){
            if(maxCrimeRoadAmount == newitems.get(i).getmRoadCrimeAmount()){
                highestCrimeStreet = newitems.get(i).getTitle();
            }
        }
        OptionalInt minCrimeRoad = newitems.stream().mapToInt(Location::getmRoadCrimeAmount).min();
        mainCrimeRoadAmount = minCrimeRoad.getAsInt();
        for(int i =0; i<newitems.size(); i++){
            if(mainCrimeRoadAmount == newitems.get(i).getmRoadCrimeAmount()){
                lowestCrimeStreet = newitems.get(i).getTitle();
            }
        }





        DashboardData dashboardData = new DashboardData();
        dashboardData.setHighestAmountOfCrime(maxCrimeRoadAmount);
        dashboardData.setTotalAmountOfCrime(totalAmountOfCrime);
        dashboardData.setHighestCrimeStreet(highestCrimeStreet);
        dashboardData.setLowestAmountOfCrime(mainCrimeRoadAmount);
        dashboardData.setLowestCrimeStreet(lowestCrimeStreet);

        return dashboardData;
    }



//        List<String> top = new ArrayList<>();
//        for (CrimeAmount crimeAmount : lcs) {
//            for (int j = 0; j < crimeAmount.getCrimeType().size(); j++) {
//                String ct = crimeAmount.getCrimeType().get(j).getCtype();
//                top.add(ct);
//            }}
//        Set<MostCrime> mostCrimeList = new HashSet<>();
//        for(int i=0;i<top.size(); i++) {
//            int freq = Collections.frequency(top, top.get(i));
//            MostCrime mostCrime = new MostCrime();
//            mostCrime.setCrimeName(top.get(i));
//            mostCrime.setNumberOfCrimeOccurrence(freq);
//            mostCrimeList.add(mostCrime);
//        }
}
