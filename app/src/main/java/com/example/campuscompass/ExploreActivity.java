package com.example.campuscompass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


//import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ExploreActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    Button explore;
    //TextView wifi_name;
    Spinner source, dest;
    //WifiManager wifiManager;
    int curFloor;
    int selectedLevel;
    boolean scanDone=false;
    String []places = {"Entrance","2101", "2102", "2103", "2104", "2105", "2106", "2107", "2108", "2109", "2110", "2201", "2202", "2203", "2204", "2205", "2206", "2207", "2208", "2209", "2210", "2301", "2302", "2303", "2304", "2305", "2306", "2307", "2308", "2309", "2310", "2401", "2402", "2403", "2404", "2405", "2406", "2407", "2408", "2409", "2410",};
    int []placesLevels={2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5};
    /*List <String> wifi=new ArrayList<>(Arrays.asList("AndroidAP","Redmi7"));
    List<String> wifi2 = new ArrayList<>(Arrays.asList("BT-STUDENT","BT-STAFF"));
    List<String> wifi3 = new ArrayList<>(Arrays.asList("LH310-WiFi","LH312-WiFi","CCPLAB-WiFi"));
    List<String> wifi4 = new ArrayList<>(Arrays.asList("CS STUDENT","ECSTAFF-WiFi"));
    List<String> wifi5 = new ArrayList<>(Arrays.asList("ADALAB","BT-STAFF","CCPLAB-WiFi"));
*/
    Location src=null,desti=null;
    Location newSrc;

    Deque<Location> traverse=new LinkedList<Location>();
    Deque<Location> smallest=new LinkedList<Location>();
    Button[] pills = new Button[4];
    TextToSpeech textToSpeech;

    Button btnText;

    String voiceAssistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        makeLocations();
        explore=findViewById(R.id.showPath);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),PathActivity.class);
                startActivity(i);
            }
        });
        source = findViewById(R.id.source);
        dest = findViewById(R.id.dest);

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, places);
        source.setAdapter(sourceAdapter);
        ArrayAdapter<String> destAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, places);
        dest.setAdapter(destAdapter);

        /*wifi_name=findViewById(R.id.wifi_name);
        checkLocation();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    Toast.makeText(getApplicationContext(), "WiFi scan failed!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else{
            wifiManager.startScan();

        }
         */

        source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                src=getLocation(places[i],placesLevels[i]);
                if(src !=null && desti!=null){
                    resetInRoute();
                    setRoute(src,desti);
                    Bundle bundle = new Bundle();
                    bundle.putInt("level",src.getLevel());
                    Floor f=new Floor();
                    f.setArguments(bundle);
                    replaceFragment(f);
                    CurrentPointer.current=src;
                    for (int j = 0; j < pills.length; j++) {
                        pills[j].setBackgroundResource(R.drawable.pill_tab);
                    }
                    pills[src.getLevel()-2].setBackgroundResource(R.drawable.selected_pill);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });

        dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                desti=getLocation(places[i],placesLevels[i]);
                if(src !=null && desti!=null){
                    resetInRoute();
                    setRoute(src,desti);
                    Bundle bundle = new Bundle();
                    bundle.putInt("level",src.getLevel());
                    Floor f=new Floor();
                    f.setArguments(bundle);
                    replaceFragment(f);
                    for (int j = 0; j < pills.length; j++) {
                            pills[j].setBackgroundResource(R.drawable.pill_tab);
                    }
                    pills[src.getLevel()-2].setBackgroundResource(R.drawable.selected_pill);
                    CurrentPointer.current=src;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });

        for (int i = 0; i < pills.length; i++) {
            pills[i] = findViewById(getResources().getIdentifier("pill" + (i + 1), "id", getPackageName()));
            final int index = i;
            pills[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < pills.length; j++) {
                        if (j == index) {
                            selectedLevel = index+2;
                            pills[j].setBackgroundResource(R.drawable.selected_pill);
                                Bundle bundle = new Bundle();
                                bundle.putInt("level",selectedLevel);
                                Floor f=new Floor();
                                f.setArguments(bundle);
                                replaceFragment(f);
                        } else {
                            pills[j].setBackgroundResource(R.drawable.pill_tab);
                        }
                    }
                }
            });
        }

        // ToDo: set selected_pill floor button by nearest WiFi name

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        btnText = findViewById(R.id.btnText);
        // Adding OnClickListener
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = src.getName().charAt(0)!='E' ? ("21"+src.getName().substring(2)) : src.getName();
                String d = desti.getName().charAt(0)!='E' ? ("21"+desti.getName().substring(2)) : desti.getName();
                if(src.getLevel() == desti.getLevel()) {
                    addVoiceCommands(s, d);
                    textToSpeech.speak(voiceAssistant.toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
                else {
                    String mp[] = {"Ground","First","Second","Third"};
                    String voice = "";
                    addVoiceCommands(s,"Stairs1");
                    voice += voiceAssistant.toString();
                    voice += "Get down in"+mp[desti.getLevel()-2]+"Floor";
                    addVoiceCommands("Stairs1",d);
                    voice+= voiceAssistant.toString();
                    textToSpeech.speak(voice, TextToSpeech.QUEUE_FLUSH, null);
               }
            }
        });

    }

    private void replaceFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame,f);
        ft.commit();
    }

    private void scanSuccess() {
        if(scanDone)
            return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
       /* List<ScanResult> results = wifiManager.getScanResults();
        if(!results.isEmpty()){
            wifi_name.setText(results.get(0).SSID);
        }
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult sr1, ScanResult sr2) {
                return Integer.compare(sr2.level, sr1.level);
            }
        });
        int n=3;
        if(results.toArray().length<3)
            n=results.toArray().length;
        int []floor={0,0,0,0,0,0};
        for(int i=0;i<n;i++)
        {
            String item=results.get(i).SSID;
            if (wifi2.contains(item)) {
                floor[2]++;
            }
            if (wifi3.contains(item)) {
                floor[3]++;
            }
            if (wifi4.contains(item)) {
                floor[4]++;
            }
            if (wifi5.contains(item)) {
                floor[5]++;
            }
        }

        for (int i = 1; i < floor.length; i++) {
            if (floor[i] >floor[curFloor]) {
                curFloor = i;
            }
        }
        */
        Toast.makeText(this,"CURRENT -FLOOR ="+ curFloor, Toast.LENGTH_SHORT).show();
        ScanResult bestSignal = null;
       /* for (ScanResult result : results) {
            if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
                bestSignal = result;
            }
        }
        */
//        if (bestSignal != null) {
//            String ssid = bestSignal.SSID;
//            String bssid = bestSignal.BSSID;
//            int signalStrength = WifiManager.calculateSignalLevel(bestSignal.level, 100);
////            String message = String.format("The nearest Wi-Fi network is %s (%s) with a signal strength of %d%%.", ssid, bssid, signalStrength);
//            wifi_name.setText("Floor : "+curFloor);
            if(curFloor==0)
                curFloor=2;
            Bundle bundle = new Bundle();
            bundle.putInt("level",curFloor);
            Floor f=new Floor();
            f.setArguments(bundle);
            //wifi_name.setText("Level: "+curFloor);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frame,f);
            ft.commit();
            pills[selectedLevel-2].setBackgroundResource(R.drawable.selected_pill);
////        }
//        else{
//            wifi_name.setText("Floor : "+curFloor);
//            setRoute(src,desti);
//            Bundle bundle = new Bundle();
//            bundle.putInt("level",2);
//            Floor f=new Floor();
//            f.setArguments(bundle);
//            FragmentManager fm = getSupportFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(R.id.frame,f);
//            ft.commit();
//        }

        scanDone=true;
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Wi-Fi scan
               // wifiManager.startScan();
                scanSuccess();
            } else {
                // Permission denied, show an error message
                Toast.makeText(this, "Location permission required to scan for Wi-Fi networks", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isLocationEnabled();

        if(isLocationEnabled)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires location services to function properly. Please enable location services in your device settings.")
                .setCancelable(false)
                .setPositiveButton("Settings", (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeLocations() {
        {
            String[] places = {"2101", "2102", "2103", "2104", "2105", "2106", "2107", "2108", "2109", "2110",};
            Location node0 = new Location("Entrance", new ArrayList<String>(Arrays.asList("Entrance")), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second0, 2, false, 180f, null, null, null, null, null, null, null, null,null);
            Location node1 = new Location("2109", new ArrayList<String>(Arrays.asList(places[8], "HOD CABIN",places[9])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom, PlacePosition.left,PlacePosition.top)),R.drawable.second1 , 2, false, 0f, null, null, null, null, null,null,null,null,null);
            Location node2 = new Location("2106", new ArrayList<String>(Arrays.asList(places[5], "StaffRoom",places[6])), new ArrayList<Integer>(Arrays.asList(PlacePosition.top, PlacePosition.bottom, PlacePosition.right)),R.drawable.second2, 2, false, 0f, null, null, null, null,null,null,null,null,null);
            Location node3 = new Location("2108", new ArrayList<String>(Arrays.asList(places[7])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom)), R.drawable.second3, 2, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node4 = new Location("2104", new ArrayList<String>(Arrays.asList(places[3],places[4], "StaffRoom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.top,PlacePosition.right, PlacePosition.bottomRight)), R.drawable.second4, 2, false, 135f, null, null, null, null, null,null,null,null,null);
            Location node5 = new Location("2101", new ArrayList<String>(Arrays.asList(places[1],places[0])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left, PlacePosition.bottom)), R.drawable.second3, 2, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node6 = new Location("2103", new ArrayList<String>(Arrays.asList(places[2])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second4, 2, false, 135f, null, null, null, null, null,null,null,null,null);
            Location stairs1 = new Location("Stairs1", new ArrayList<>(), new ArrayList<>(),R.drawable.stairs1, 2, true, 0f, null, null, null, null, null,null,null,null,null);
            //Location stairs2 = new Location("Stairs2", new ArrayList<>(), new ArrayList<>(), R.drawable.stairs2, 2, true, 0f, null, null, null, null, null);
            node0.setAngle(180);
            node1.setAngle(180);
            stairs1.setAngle(180);
            node3.setAngle(-90);
            makeConnections(node0,node1,node2,node3,node4,node5,node6,stairs1);

            LevelPointer.levels[2] = node0;
        }
        {
            String []places = {"2201", "2202", "2203", "2204", "2205", "2206", "2207", "2208", "2209", "2210",};
            Location node0 = new Location("Balcony", new ArrayList<String>(Arrays.asList("Balcony")), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second0, 3, false, 180f, null, null, null, null, null, null, null, null,null);
            Location node1 = new Location("2209", new ArrayList<String>(Arrays.asList(places[8], "StaffRoom",places[9])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom, PlacePosition.left,PlacePosition.top)),R.drawable.second1 , 3, false, 0f, null, null, null, null, null,null,null,null,null);
            Location node2 = new Location("2206", new ArrayList<String>(Arrays.asList(places[5], "StaffRoom",places[6])), new ArrayList<Integer>(Arrays.asList(PlacePosition.top, PlacePosition.bottom, PlacePosition.right)),R.drawable.second2, 3, false, 0f, null, null, null, null,null,null,null,null,null);
            Location node3 = new Location("2208", new ArrayList<String>(Arrays.asList(places[7])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom)), R.drawable.second3, 3, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node4 = new Location("2204", new ArrayList<String>(Arrays.asList(places[3],places[4], "StaffRoom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.top,PlacePosition.right, PlacePosition.bottomRight)), R.drawable.second4, 3, false, 135f, null, null, null, null, null,null,null,null,null);
            Location node5 = new Location("2201", new ArrayList<String>(Arrays.asList(places[1],places[0])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left, PlacePosition.bottom)), R.drawable.second3, 3, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node6 = new Location("2203", new ArrayList<String>(Arrays.asList(places[2])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second4, 3, false, 135f, null, null, null, null, null,null,null,null,null);
            Location stairs1 = new Location("Stairs1", new ArrayList<>(), new ArrayList<>(),R.drawable.stairs1, 3, true, 0f, null, null, null, null, null,null,null,null,null);
            //Location stairs2 = new Location("Stairs2", new ArrayList<>(), new ArrayList<>(), R.drawable.stairs2, 2, true, 0f, null, null, null, null, null);
            node0.setAngle(180);
            node1.setAngle(180);
            stairs1.setAngle(180);
            node3.setAngle(-90);
            makeConnections(node0,node1,node2,node3,node4,node5,node6,stairs1);

            LevelPointer.levels[3] = node0;

        }
        {
            String []places = {"2301", "2302", "2303", "2304", "2305", "2306", "2307", "2308", "2309", "2310",};
            Location node0 = new Location("Balcony", new ArrayList<String>(Arrays.asList("Balcony")), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second0, 4, false, 180f, null, null, null, null, null, null, null, null,null);
            Location node1 = new Location("2309", new ArrayList<String>(Arrays.asList(places[8], "StaffRoom",places[9])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom, PlacePosition.left,PlacePosition.top)),R.drawable.second1 , 4, false, 0f, null, null, null, null, null,null,null,null,null);
            Location node2 = new Location("2306", new ArrayList<String>(Arrays.asList(places[5], "StaffRoom",places[6])), new ArrayList<Integer>(Arrays.asList(PlacePosition.top, PlacePosition.bottom, PlacePosition.right)),R.drawable.second2, 4, false, 0f, null, null, null, null,null,null,null,null,null);
            Location node3 = new Location("2308", new ArrayList<String>(Arrays.asList(places[7])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom)), R.drawable.second3, 4, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node4 = new Location("2304", new ArrayList<String>(Arrays.asList(places[3],places[4], "StaffRoom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.top,PlacePosition.right, PlacePosition.bottomRight)), R.drawable.second4, 4, false, 135f, null, null, null, null, null,null,null,null,null);
            Location node5 = new Location("2301", new ArrayList<String>(Arrays.asList(places[1],places[0])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left, PlacePosition.bottom)), R.drawable.second3, 4, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node6 = new Location("2303", new ArrayList<String>(Arrays.asList(places[2])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second4, 4, false, 135f, null, null, null, null, null,null,null,null,null);
            Location stairs1 = new Location("Stairs1", new ArrayList<>(), new ArrayList<>(),R.drawable.stairs1, 4, true, 0f, null, null, null, null, null,null,null,null,null);
            //Location stairs2 = new Location("Stairs2", new ArrayList<>(), new ArrayList<>(), R.drawable.stairs2, 2, true, 0f, null, null, null, null, null);
            node0.setAngle(180);
            node1.setAngle(180);
            stairs1.setAngle(180);
            node3.setAngle(-90);
            makeConnections(node0,node1,node2,node3,node4,node5,node6,stairs1);

            LevelPointer.levels[4] = node0;
        }
        {
            String []places = {"2401", "2402", "2403", "2404", "2405", "2406", "2407", "2408", "2409", "2410",};
            Location node0 = new Location("Balcony", new ArrayList<String>(Arrays.asList("Balcony")), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second0, 5, false, 180f, null, null, null, null, null, null, null, null,null);
            Location node1 = new Location("2409", new ArrayList<String>(Arrays.asList(places[8], "StaffRoom",places[9])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom, PlacePosition.left,PlacePosition.top)),R.drawable.second1 , 5, false, 0f, null, null, null, null, null,null,null,null,null);
            Location node2 = new Location("2406", new ArrayList<String>(Arrays.asList(places[5], "StaffRoom",places[6])), new ArrayList<Integer>(Arrays.asList(PlacePosition.top, PlacePosition.bottom, PlacePosition.right)),R.drawable.second2, 5, false, 0f, null, null, null, null,null,null,null,null,null);
            Location node3 = new Location("2408", new ArrayList<String>(Arrays.asList(places[7])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottom)), R.drawable.second3, 5, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node4 = new Location("2404", new ArrayList<String>(Arrays.asList(places[3],places[4], "StaffRoom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.top,PlacePosition.right, PlacePosition.bottomRight)), R.drawable.second4, 5, false, 135f, null, null, null, null, null,null,null,null,null);
            Location node5 = new Location("2401", new ArrayList<String>(Arrays.asList(places[1],places[0])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left, PlacePosition.bottom)), R.drawable.second3, 5, false, 45f, null, null, null, null, null,null,null,null,null);
            Location node6 = new Location("2403", new ArrayList<String>(Arrays.asList(places[2])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left)), R.drawable.second4, 5, false, 135f, null, null, null, null, null,null,null,null,null);
            Location stairs1 = new Location("Stairs1", new ArrayList<>(), new ArrayList<>(),R.drawable.stairs1, 5, true, 0f, null, null, null, null, null,null,null,null,null);
            //Location stairs2 = new Location("Stairs2", new ArrayList<>(), new ArrayList<>(), R.drawable.stairs2, 2, true, 0f, null, null, null, null, null);
            node0.setAngle(180);
            node1.setAngle(180);
            stairs1.setAngle(180);
            node3.setAngle(-90);
            makeConnections(node0,node1,node2,node3,node4,node5,node6,stairs1);

            LevelPointer.levels[5] = node0;
        }

        //connecting stairs
        LevelPointer.levels[2].getStairs().setUp(LevelPointer.levels[3].getStairs());
        LevelPointer.levels[2].getStairs().setUpAngle(-170);

        LevelPointer.levels[3].getStairs().setDown(LevelPointer.levels[2].getStairs());
        LevelPointer.levels[3].getStairs().setDownAngle(170);

        LevelPointer.levels[3].getStairs().setUp(LevelPointer.levels[4].getStairs());
        LevelPointer.levels[3].getStairs().setUpAngle(-170);

        LevelPointer.levels[4].getStairs().setDown(LevelPointer.levels[3].getStairs());
        LevelPointer.levels[4].getStairs().setDownAngle(170);

        LevelPointer.levels[4].getStairs().setUp(LevelPointer.levels[5].getStairs());
        LevelPointer.levels[4].getStairs().setUpAngle(-170);

        LevelPointer.levels[5].getStairs().setDown(LevelPointer.levels[4].getStairs());
        LevelPointer.levels[5].getStairs().setDownAngle(170);

//        LevelPointer.levels[2].getRight().getStairs().setUp(LevelPointer.levels[3].getRight().getStairs());
//        LevelPointer.levels[2].getRight().setUpAngle(-170);
//        LevelPointer.levels[3].getRight().getStairs().setDown(LevelPointer.levels[2].getRight().getStairs());
//        LevelPointer.levels[3].getRight().getStairs().setUpAngle(170);
    }
    public static int getImageResourceId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    private void makeConnections(Location node0,Location node1,Location node2,Location node3,Location node4,Location node5,Location node6, Location stairs1){
        node0.setLeft(node1);
        node0.setRight(node5);
        node0.setBack(stairs1);

        node1.setRight(node0);
        node1.setBack(node2);
        node1.setBottomLeft(node3);

        node2.setFront(node1);
        //node2.setRight(stairs1);
        node2.setBottomRight(node3);
        node2.setRight((node4));

        node3.setRight(node6);
        node3.setTopRight(node1);
        node3.setTopLeft(node2);

        node4.setLeft(node2);
        node4.setFront(node5);
        //node4.setLeft(stairs1);
        node4.setTopLeft(node6);

        node5.setBack(node4);
        node5.setLeft(node0);
        node5.setTopRight(node6);

        node6.setLeft(node3);
        node6.setBottomLeft(node5);
        node6.setBottomRight(node4);

        node0.setStairs(stairs1);
        node2.setStairs(stairs1);
        node4.setStairs(stairs1);

        stairs1.setFront(node0);
        stairs1.setLeft(node2);
        stairs1.setRight(node4);

    }

    private Location getLocation(String src,int level){
        Location node =LevelPointer.levels[level];

        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        node=node.getLeft();
        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        Location x = node.getBottomLeft();
        if(x.getPlaces().indexOf(src)!=-1)
            return x;
        node=node.getBack();
        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        node=node.getRight();

        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        x =node.getTopLeft();

        if(x.getPlaces().indexOf(src)!=-1)
            return x;
        node = node.getFront();
        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        return null;
    }

    private void setRoute(Location src,Location desti){
        // if there are in same level
        if(src.getLevel()==desti.getLevel()){
            findSmallestRoute(src,desti);
            smallest.forEach(element->{
                element.setInRoute(true);
            });
        }
        else{
            findSmallestRouteStairs(src);
            smallest.forEach(element->{
                element.setInRoute(true);
            });
            smallest.clear();
            smallest.addFirst(null);
            smallest.addFirst(null);
            smallest.addFirst(null);
            smallest.addFirst(null);
            smallest.addFirst(null);
            smallest.addFirst(null);
            smallest.addFirst(null);
            while (newSrc.getLevel()!=desti.getLevel()){
//                Log.d("hello", "setRoute: "+newSrc.getName()+" "+newSrc.getLevel()+" "+newSrc.getDown().toString());
                if(newSrc.getLevel()<desti.getLevel()){
                    newSrc=newSrc.getUp();
                    newSrc.setInRoute(true);
                }
                else{
                    newSrc=newSrc.getDown();
                    newSrc.setInRoute(true);
                }
            }
            findSmallestRoute(newSrc,desti);
            smallest.forEach(element->{
                element.setInRoute(true);
            });
        }
    }

    private void findSmallestRoute(Location src,Location dest){

        traverse.addLast(src);
        src.setInRoute(true);

        if(src==dest){
            if(smallest.size()>traverse.size())
            {
                smallest.clear();

                traverse.forEach(element->{
                    smallest.addFirst(element);
                });
            }
            src.setInRoute(false);
            traverse.removeLast();
            return;
        }

        if(src.getLeft()!=null && !src.getLeft().getInRoute())
            findSmallestRoute(src.getLeft(),dest);

        if(src.getRight()!=null && !src.getRight().getInRoute())
            findSmallestRoute(src.getRight(),dest);

        if(src.getBack()!=null && !src.getBack().getInRoute())
            findSmallestRoute(src.getBack(),dest);

        if(src.getFront()!=null && !src.getFront().getInRoute())
            findSmallestRoute(src.getFront(),dest);
        if(src.getBottomLeft()!=null && !src.getBottomLeft().getInRoute())
            findSmallestRoute(src.getBottomLeft(),dest);

        if(src.getBottomRight()!=null && !src.getBottomRight().getInRoute())
            findSmallestRoute(src.getBottomRight(),dest);
        if(src.getTopLeft()!=null && !src.getTopLeft().getInRoute())
            findSmallestRoute(src.getTopLeft(),dest);

        if(src.getTopRight()!=null && !src.getTopRight().getInRoute())
            findSmallestRoute(src.getTopRight(),dest);


        src.setInRoute(false);
        traverse.removeLast();
        return;
    }

    private void findSmallestRouteStairs(Location srcc){
        Log.d("ksfd", "findSmallestRouteStairs: "+srcc.getName());
        traverse.addLast(srcc);
        srcc.setInRoute(true);

        if(srcc.getStairs()!=null){
            if(smallest.size()>traverse.size())
            {
                smallest.clear();
                traverse.forEach(element->{
                    smallest.addFirst(element);
                });
                smallest.addFirst(srcc.getStairs());
                newSrc=srcc.getStairs();
            }
            srcc.setInRoute(false);
            traverse.removeLast();
            return;
        }

        if(srcc.getLeft()!=null && !srcc.getLeft().getInRoute())
            findSmallestRouteStairs(srcc.getLeft());

        if(srcc.getRight()!=null && !srcc.getRight().getInRoute())
            findSmallestRouteStairs(srcc.getRight());

        if(srcc.getBack()!=null && !srcc.getBack().getInRoute())
            findSmallestRouteStairs(srcc.getBack());

        if(srcc.getFront()!=null && !srcc.getFront().getInRoute())
            findSmallestRouteStairs(srcc.getFront());
        if(srcc.getBottomLeft()!=null && !srcc.getBottomLeft().getInRoute())
            findSmallestRouteStairs(srcc.getBottomLeft());

        if(srcc.getBottomRight()!=null && !srcc.getBottomRight().getInRoute())
            findSmallestRouteStairs(srcc.getBottomRight());
        if(srcc.getTopLeft()!=null && !srcc.getTopLeft().getInRoute())
            findSmallestRouteStairs(srcc.getTopLeft());

        if(srcc.getTopRight()!=null && !srcc.getTopRight().getInRoute())
            findSmallestRouteStairs(src.getTopRight());
        srcc.setInRoute(false);
        traverse.removeLast();
        return;
    }

    void addVoiceCommands(String src,String dest){
        switch(src){
            case "Entrance":
                switch (dest) {
                    case "2101":
                        voiceAssistant = "Turn left and go straight for 1 meters and your destination is on left";
                    case "2102":
                        voiceAssistant = "Turn left and go straight for 1 meters and your destination is on left";
                        break;
                    case "2103":
                        voiceAssistant = "Turn left and go straight for 2 meters and then turn slightly turn left";
                        break;
                    case "2104":
                        voiceAssistant = "Turn left and go straight for 1 meters and your destination is on right";
                        break;
                    case "2105":
                        voiceAssistant = "Turn left and go straight for 1 meters and your destination is on right";
                        break;
                    case "2106":
                        voiceAssistant = "Turn right and go straight for 1 meters and your destination is on left";
                        break;
                    case "2107":
                        voiceAssistant = "Turn right and go straight for 1 meters and your destination is on left";
                        break;
                    case "2108":
                        voiceAssistant = "Turn right and go straight for 1 meters and then slighty turn to left";
                        break;
                    case "2109":
                        voiceAssistant = "Turn right and go straight for 1 meters and your destination is on right";
                        break;
                    case "2110":
                        voiceAssistant = "Turn right and go straight for 1 meters and your destination is on right";
                        break;
                    case "Entrance":
                        voiceAssistant = "You are at entrance";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Go straight to use lift.";
                        break;
                    default:
                        voiceAssistant = "";
                        break;
                }
                break;
            case "2101":
            case "2102":
                switch (dest) {
                    case "2102":
                        voiceAssistant = "You are at your destination";
                        break;
                    case "2103":
                        voiceAssistant = "Go straight for 1 meter and turn left";
                        break;
                    case "2104":
                        voiceAssistant = "Go straight for 2 meters and turn left";
                        break;
                    case "2105":
                        voiceAssistant = "Go straight for 2 meters and turn left and go straight for 1 meter";
                        break;
                    case "2106":
                        voiceAssistant = "Turn left and go straight for 3 meters and turn right and go straight for 3 meters";
                        break;
                    case "2107":
                        voiceAssistant = "Turn left and go straight for 3 meters and turn right and go straight for 4 meters";
                        break;
                    case "2108":
                        voiceAssistant = "Turn left and go straight for 3 meters";
                        break;
                    case "2109":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2110":
                        voiceAssistant = "Turn left and go straight for 1 meter";
                        break;
                    case "Entrance":
                        voiceAssistant = "Turn left and go straight for 1 meter";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Go straight and turn right to use stairs.";
                        break;
                   default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            case "2103":
                switch (dest) {
                    case "2101":
                        voiceAssistant = "Turn right and go straight for 1 meter and turn right";
                        break;
                    case "2102":
                        voiceAssistant = "Turn right and go straight for 1 meter";
                        break;
                    case "2104":
                        voiceAssistant = "Turn right and go straight for 1 meter and turn left and go straight for 1 meter";
                        break;
                    case "2105":
                        voiceAssistant = "Turn right and go straight for 1 meter and turn left and go straight for 2 meters";
                        break;
                    case "2106":
                        voiceAssistant = "Turn right and go straight for 3 meters and turn right and go straight for 3 meters";
                        break;
                    case "2107":
                        voiceAssistant = "Turn right and go straight for 3 meters and turn right and go straight for 4 meters";
                        break;
                    case "2108":
                        voiceAssistant = "Turn right and go straight for 3 meters";
                        break;
                    case "2109":
                        voiceAssistant = "Turn right and go straight for 2 meters";
                        break;
                    case "2110":
                        voiceAssistant = "Turn right and go straight for 1 meter";
                        break;
                    case "Entrance":
                        voiceAssistant = "Turn right and go straight for 2 meters";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Go straight to use lift.";
                        break;
                    default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            case "2104":
            case "2105":
                switch (dest){
                    case "2105":
                        voiceAssistant = "Turn left and go straight for 1 meters";
                        break;
                    case "2106":
                        voiceAssistant = "Turn left and go straight for 4 meters";
                        break;
                    case "2107":
                        voiceAssistant = "Turn left and go straight for 5 meters";
                        break;
                    case "2108":
                        voiceAssistant = "Turn left and go straight for 5 meters and turn right";
                        break;
                    case "2109":
                        voiceAssistant = "Turn left and go straight for 5 meters and turn right and go straight 2 meters";
                        break;
                    case "2110":
                        voiceAssistant = "Turn left and go straight for 5 meters and turn right and go straight 3 meters";
                        break;
                    case "2103":
                        voiceAssistant = "go straight for 2 meters";
                        break;
                    case "2102":
                        voiceAssistant="go straight for 2 meters and then turn slightly turn left walk 2 meters";
                        break;
                    case "2101":
                        voiceAssistant="go straight for 2 meters and then turn slightly turn left walk 3 meters";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Go straight to use lift.";
                        break;
                    default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            case "2106":
            case "2107":
                switch (dest){
                    case "2107":
                        voiceAssistant = "Turn left and go straight for 1 meters";
                        break;
                    case "2108":
                        voiceAssistant = "Turn left and go straight for 3 meters and turn right";
                        break;
                    case "2109":
                        voiceAssistant = "Turn left and go straight and turn right and go straight 2 meters ";
                        break;
                    case "2110":
                        voiceAssistant = "Turn left and go straight for 2 meters and turn right and go straight 3 meters";
                        break;
                    case "2103":
                        voiceAssistant = " turn right and go straight for 4 meters";
                        break;
                    case "2102":
                        voiceAssistant="go straight for 2 meters and then turn slightly turn left walk 2 meters";
                        break;
                    case "2101":
                        voiceAssistant="go straight for 1 meters and then turn slightly turn left walk 2 meters";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Go straight to use stairs or lift.";
                        break;
                    default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            case "2108":
                switch (dest) {
                    case "2101":
                        voiceAssistant = "Turn left and go straight for 3 meters";
                        break;
                    case "2102":
                        voiceAssistant = "Turn left and go straight for 3 meters";
                        break;
                    case "2103":
                        voiceAssistant = "Turn left and go straight for 3 meters";
                        break;
                    case "2104":
                        voiceAssistant = "Turn left and go straight for 3 meters";
                        break;
                    case "2105":
                        voiceAssistant = "Turn left and go straight for 3 meters";
                        break;
                    case "2106":
                        voiceAssistant = "Turn left and go straight for 3 meters and turn left and go straight for 3 meters";
                        break;
                    case "2107":
                        voiceAssistant = "Turn left and go straight for 4 meters";
                        break;
                    case "2109":
                        voiceAssistant = "Turn right and go straight for 1 meter";
                        break;
                    case "2110":
                        voiceAssistant = "Turn right and go straight for 2 meters";
                        break;
                    case "Entrance":
                        voiceAssistant = "Turn right and go straight for 3 meters";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Turn Right and move for 2 meters and then turn left Go straight to use stairs or lift.";
                        break;
                    default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            case "2109":
                switch (dest) {
                    case "2101":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2102":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2103":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2104":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2105":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2106":
                        voiceAssistant = "Turn left and go straight for 2 meters and turn left and go straight for 3 meters";
                        break;
                    case "2107":
                        voiceAssistant = "Turn left and go straight for 3 meters and turn left and go straight for 4 meters";
                        break;
                    case "2108":
                        voiceAssistant = "Turn left and go straight for 1 meter";
                        break;
                    case "2110":
                        voiceAssistant = "Turn right and go straight for 1 meter";
                        break;
                    case "Entrance":
                        voiceAssistant = "Turn right and go straight for 2 meters";
                        break;
                    case "Stairs1":
                        voiceAssistant = "Go straight for 5 meters and turn left and move 2 meters forward to use stairs or lift.";
                        break;
                    default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            case "Stairs1":
                switch (dest) {
                    case "2101":
                    case "2102":
                        voiceAssistant = "Turn right and go straight for 2 meters, then turn left and move for 5 meters";
                        break;
                    case "2103":
                        voiceAssistant = "Turn right and go straight for 2 meters, then turn left and move for 3 meters and then turn right";
                        break;
                    case "2104":
                    case "2105":
                        voiceAssistant = "Turn right and go straight for 2 meters";
                        break;
                    case "2106":
                    case "2107":
                        voiceAssistant = "Turn left and go straight for 2 meters";
                        break;
                    case "2108":
                        voiceAssistant = "Turn left and go straight for 2 meters, then turn right and move for 3 meters and then turn left";
                        break;
                    case "2109":
                    case "2110":
                        voiceAssistant = "Turn left and go straight for 2 meters, then turn right and move for 5 meters";
                        break;
                    case "Entrance":
                        voiceAssistant = "go straight for 5 meters";
                        break;
                    default:
                        voiceAssistant = "Go straight";
                        break;
                }
                break;
            default:
                voiceAssistant="Go straight";
                break;
        }
    }






    private void resetInRoute(){
        int []arr={2,3,4,5};
        traverse.clear();
        smallest.clear();
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        for(int i=0;i<arr.length;i++){
            Location node=LevelPointer.levels[arr[i]];
            if(node==null)
                continue;
            node.setInRoute(false);
            //node.getBack().setInRoute(false);
            node.getLeft().setInRoute(false);
            node.getRight().setInRoute(false);
            node.getRight().getBack().setInRoute(false);
            node.getLeft().getBack().setInRoute(false);
            node.getLeft().getBottomLeft().setInRoute(false);
            node.getRight().getTopRight().setInRoute(false);
            node.getStairs().setInRoute(false);
        }

    }
}
