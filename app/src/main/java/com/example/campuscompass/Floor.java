package com.example.campuscompass;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class Floor extends Fragment {
    View v;
    //String []places = {"LH301", "LH302", "BTL10", "BTL07", "BT HOD Cabin", "LH210", "LH211", "LH212", "Dept of Physical Education", "BT Staffroom", "Biokinetics Lab", "Instrumentation and Project Lab"};
    TextView[][] nodes=new TextView[7][8];
    TextView[] mainNodes=new TextView[7];
    View[] lines = new View[13];
    int[][] ids={
        {R.id.topleftNode0,R.id.topNode0,R.id.toprightNode0,R.id.rightNode0,R.id.bottomrightNode0,R.id.bottomNode0,R.id.bottomleftNode0,R.id.leftNode0},
        {R.id.topleftNode1,R.id.topNode1,R.id.toprightNode1,R.id.rightNode1,R.id.bottomrightNode1,R.id.bottomNode1,R.id.bottomleftNode1,R.id.leftNode1},
        {R.id.topleftNode2,R.id.topNode2,R.id.toprightNode2,R.id.rightNode2,R.id.bottomrightNode2,R.id.bottomNode2,R.id.bottomleftNode2,R.id.leftNode2},
        {R.id.topleftNode3,R.id.topNode3,R.id.toprightNode3,R.id.rightNode3,R.id.bottomrightNode3,R.id.bottomNode3,R.id.bottomleftNode3,R.id.leftNode3},
        {R.id.topleftNode4,R.id.topNode4,R.id.toprightNode4,R.id.rightNode4,R.id.bottomrightNode4,R.id.bottomNode4,R.id.bottomleftNode4,R.id.leftNode4},
        {R.id.topleftNode5,R.id.topNode5,R.id.toprightNode5,R.id.rightNode5,R.id.bottomrightNode5,R.id.bottomNode5,R.id.bottomleftNode5,R.id.leftNode5},
            {R.id.topleftNode6,R.id.topNode6,R.id.toprightNode6,R.id.rightNode6,R.id.bottomrightNode6,R.id.bottomNode6,R.id.bottomleftNode6,R.id.leftNode6}
    };

    HashMap<Integer ,Integer>placesPositionMapping=new HashMap<>();
    Location locations[];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_floor, container, false);

        placesPositionMapping.put(9,0);
        placesPositionMapping.put(8,1);
        placesPositionMapping.put(10,2);
        placesPositionMapping.put(2,3);
        placesPositionMapping.put(6,4);
        placesPositionMapping.put(4,5);
        placesPositionMapping.put(5,6);
        placesPositionMapping.put(1,7);
        Bundle bundle = getArguments();

        Location node0 = LevelPointer.levels[bundle.getInt("level")];
        Location node1 = node0.getLeft();
        Location node2 = node1.getBack();
        Location node3 = node2.getBottomRight();
        Location stairs1=node0.getStairs();
        Location node4 = stairs1.getRight();
        Location node5 = node4.getFront();
        Location node6 = node3.getRight();
        //Location stairs2=node5.getStairs();
        mainNodes[0]=v.findViewById(R.id.Node0);
        mainNodes[1]=v.findViewById(R.id.Node1);
        mainNodes[2]=v.findViewById(R.id.Node2);
        mainNodes[3]=v.findViewById(R.id.Node3);
        mainNodes[4]=v.findViewById(R.id.Node4);
        mainNodes[5]=v.findViewById(R.id.Node5);
        mainNodes[6]=v.findViewById(R.id.Node6);

        lines[0]=v.findViewById(R.id.line01);
        lines[1]=v.findViewById(R.id.line12s);
        lines[2]=v.findViewById(R.id.line23);
        lines[3]=v.findViewById(R.id.line13);
        lines[4]=v.findViewById(R.id.line50);

        lines[5]=v.findViewById(R.id.line02);
        lines[6]=v.findViewById(R.id.line3s);

        lines[7]=v.findViewById(R.id.line5s);
        lines[8]=v.findViewById(R.id.line6s);
        lines[9]=v.findViewById(R.id.line13s);
        lines[10]=v.findViewById(R.id.line03);
        lines[11]=v.findViewById(R.id.line04);
        lines[12]=v.findViewById(R.id.line24);

        locations=new Location[]{node0,node1,node2,node3,node4,node5,node6};
        for(int i=0;i<7;i++){
            for(int j=0;j<8;j++){
                nodes[i][j] = v.findViewById(ids[i][j]);
            }
        }

        if(node0.getInRoute() && node1.getInRoute()){
            lines[0].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node1.getInRoute() && node2.getInRoute()){
            lines[1].setBackgroundColor(Color.rgb(0,255,0));
            lines[9].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node1.getInRoute() && node3.getInRoute()){
            lines[1].setBackgroundColor(Color.rgb(0,255,0));
            lines[5].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node2.getInRoute() && node3.getInRoute()){
            lines[9].setBackgroundColor(Color.rgb(0,255,0));
            lines[5].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node2.getInRoute() && node4.getInRoute()){
            lines[2].setBackgroundColor(Color.rgb(0,255,0));
            lines[12].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node4.getInRoute() && node5.getInRoute()){
            lines[7].setBackgroundColor(Color.rgb(0,255,0));
            lines[8].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node4.getInRoute() && node6.getInRoute()){
            lines[10].setBackgroundColor(Color.rgb(0,255,0));
            lines[8].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node6.getInRoute() && node5.getInRoute()){
            lines[7].setBackgroundColor(Color.rgb(0,255,0));
            lines[10].setBackgroundColor(Color.rgb(0,255,0));
        }
        if(node5.getInRoute() && node0.getInRoute()){
            lines[4].setBackgroundColor(Color.rgb(0,255,0));
        }

        if(node3.getInRoute() && node6.getInRoute()){
            lines[10].setBackgroundColor(Color.rgb(0,255,0));
            lines[11].setBackgroundColor(Color.rgb(0,255,0));
            lines[5].setBackgroundColor(Color.rgb(0,255,0));
        }

        if((node0.getInRoute() && stairs1.getInRoute())){
            lines[6].setBackgroundColor(Color.rgb(0,255,0));
            lines[3].setBackgroundColor(Color.rgb(0,255,0));
        }
        if((node2.getInRoute() && stairs1.getInRoute())){
            lines[2].setBackgroundColor(Color.rgb(0,255,0));
            lines[3].setBackgroundColor(Color.rgb(0,255,0));
        }
        if((node4.getInRoute() && stairs1.getInRoute())){
            lines[12].setBackgroundColor(Color.rgb(0,255,0));
            lines[3].setBackgroundColor(Color.rgb(0,255,0));
        }

        /*if((node5.getInRoute() && stairs2.getInRoute())||(node5.getInRoute() && node4.getInRoute())){
            lines[8].setBackgroundColor(Color.rgb(0,255,0));
        }
        if((node4.getInRoute() && stairs2.getInRoute())||(node5.getInRoute() && node4.getInRoute())){
            lines[9].setBackgroundColor(Color.rgb(0,255,0));
        }
        if((node5.getInRoute() && stairs2.getInRoute())||(node4.getInRoute() && stairs2.getInRoute())){
            lines[10].setBackgroundColor(Color.rgb(0,255,0));
        }*/

        for(int i=0;i<7;i++){
            ArrayList<String> places=locations[i].getPlaces();
            ArrayList<Integer> placePositions=locations[i].getPlacesPositions();
            if(locations[i].getInRoute()){
                mainNodes[i].setBackgroundColor(Color.rgb(0,255,0));
            }
            for(int j=0;j<places.size();j++){
//                Log.d("slkdf", "onCreateView: "+placesPositionMapping.get(placePositions.get(j))+" "+j);
                nodes[i][placesPositionMapping.get(placePositions.get(j))].setText(places.get(j).toString());

            }
        }

        if(stairs1.getInRoute()){
            v.findViewById(R.id.stairs1).setBackgroundColor(Color.rgb(0,255,0));
        }
        /*if(stairs2.getInRoute()){
            v.findViewById(R.id.stairs2).setBackgroundColor(Color.rgb(0,255,0));
        }*/

        return v;
    }

    public static int getImageResourceId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}