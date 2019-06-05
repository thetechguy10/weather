package com.jaykapadia.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class adapter extends RecyclerView.Adapter<adapter.holder> {
    private Context context;
    private JSONArray array;

    adapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new holder(inflater.inflate(R.layout.temp_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        holder.position = i;
        try {
            holder.i1.setImageResource(context.getResources().getIdentifier(updateWeatherIcon(array.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id")),"drawable",context.getPackageName()));
            holder.t1.setText(date(String.valueOf(array.getJSONObject(i).getInt("dt"))));
            holder.t2.setText(String.format("%s%sC", String.valueOf(array.getJSONObject(i).getJSONObject("temp").getDouble("day")), (char) 0x00B0));
            holder.t3.setText(String.format("%s%sC", String.valueOf(array.getJSONObject(i).getJSONObject("temp").getDouble("night")), (char) 0x00B0));
            holder.t4.setText(array.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));
            holder.t5.setText(String.format("Speed: %s m/s", array.getJSONObject(i).getDouble("speed")));
            holder.t6.setText(String.format("Clouds: %s%%", String.valueOf(array.getJSONObject(i).getInt("clouds"))));
            holder.t7.setText(String.format("Pressure: %s hpa", array.getJSONObject(i).getDouble("pressure")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String date(String x) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(new Date(Long.parseLong(x) * 1000));
    }

    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    class holder extends RecyclerView.ViewHolder {
        TextView t1, t2, t3, t4, t5, t6, t7;
        int position;
        ImageView i1;
        holder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.date);
            t2 = itemView.findViewById(R.id.day);
            t3 = itemView.findViewById(R.id.night);
            t4 = itemView.findViewById(R.id.desc);
            t5 = itemView.findViewById(R.id.speed);
            t6 = itemView.findViewById(R.id.cloud);
            t7 = itemView.findViewById(R.id.pressure);
            i1 = itemView.findViewById(R.id.icon);
        }
    }
}
